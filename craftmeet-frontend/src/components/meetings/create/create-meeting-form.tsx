"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  FormProvider,
  useFieldArray,
  useForm,
  useWatch,
  type FieldPath,
  type Resolver,
} from "react-hook-form";
import { ArrowLeft } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { useCreateMeeting } from "@/lib/hooks/use-meetings";
import {
  DEFAULT_MEETING_VALUES,
  meetingFormSchema,
  newQuestion,
  toMeetingRequest,
  type MeetingFormValues,
} from "@/lib/schemas/meeting";
import type { QuestionType } from "@/lib/types/meeting";
import { FormCard } from "./form-card";
import { ProgressRail } from "./progress-rail";
import { StepBasicInfo } from "./step-basic-info";
import { StepQuestions } from "./step-questions";
import { StepReview } from "./step-review";
import { StepSettings } from "./step-settings";
import { STEPS } from "./steps-config";
import { WizardFooter } from "./wizard-footer";
import { WizardLayout } from "./wizard-layout";

/** Field paths validated on each step before advancing. */
const STEP_FIELDS: FieldPath<MeetingFormValues>[][] = [
  ["title", "description"],
  ["duration", "participantCap"],
  ["questions"],
];

export function CreateMeetingForm() {
  const router = useRouter();
  const createMeeting = useCreateMeeting();

  const [currentIndex, setCurrentIndex] = useState(0);
  const [completedIndex, setCompletedIndex] = useState(0);
  const [backendError, setBackendError] = useState<string | null>(null);
  const [showDiscardDialog, setShowDiscardDialog] = useState(false);

  const methods = useForm<MeetingFormValues>({
    // `z.coerce` makes the schema's input type (`unknown` for numeric fields)
    // differ from its output type (`MeetingFormValues`); cast so the form stays
    // typed as the output shape consumed by the step components.
    resolver: zodResolver(meetingFormSchema) as Resolver<MeetingFormValues>,
    mode: "onBlur",
    defaultValues: DEFAULT_MEETING_VALUES,
  });

  const { fields, append, remove, move } = useFieldArray({
    control: methods.control,
    name: "questions",
  });

  const { errors, isDirty } = methods.formState;
  const liveValues = useWatch<MeetingFormValues>({
    control: methods.control,
  });

  const nextDisabled =
    (currentIndex === 0 && (!!errors.title || !!errors.description)) ||
    (currentIndex === 1 && (!!errors.duration || !!errors.participantCap)) ||
    (currentIndex === 2 && !!errors.questions);

  function handleBack() {
    if (isDirty) {
      setShowDiscardDialog(true);
    } else {
      router.push("/dashboard");
    }
  }

  function handleStepBack() {
    setCurrentIndex((index) => Math.max(0, index - 1));
  }

  function handleStepSelect(index: number) {
    if (index <= completedIndex) {
      setCurrentIndex(index);
    }
  }

  async function handleNext() {
    if (currentIndex === STEPS.length - 1) {
      await methods.handleSubmit(onSubmit)();
      return;
    }

    const ok = await methods.trigger(STEP_FIELDS[currentIndex]);
    if (ok) {
      const next = currentIndex + 1;
      setCurrentIndex(next);
      setCompletedIndex((completed) => Math.max(completed, next));
      setBackendError(null);
    }
  }

  function onSubmit(values: MeetingFormValues) {
    setBackendError(null);
    createMeeting.mutate(toMeetingRequest(values), {
      onSuccess: () => {
        toast.success("Meeting created");
        router.push("/dashboard");
      },
      onError: (error) => {
        setBackendError(
          error instanceof Error
            ? error.message
            : "Something went wrong. Please try again.",
        );
      },
    });
  }

  function renderStep() {
    switch (currentIndex) {
      case 0:
        return <StepBasicInfo />;
      case 1:
        return <StepSettings />;
      case 2:
        return (
          <StepQuestions
            fields={fields}
            onAppend={(type: QuestionType) => append(newQuestion(type))}
            onRemove={(index) => remove(index)}
            onMove={(index, direction) => move(index, index + direction)}
          />
        );
      case 3:
        return <StepReview values={liveValues as MeetingFormValues} />;
      default:
        return null;
    }
  }

  return (
    <div className="space-y-6">
      <header className="flex items-center gap-3">
        <Button
          type="button"
          variant="ghost"
          size="icon"
          className="rounded-full"
          onClick={handleBack}
          aria-label="Back to dashboard"
        >
          <ArrowLeft />
        </Button>
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Create meeting</h1>
          <p className="text-sm text-muted-foreground">
            Set up your meeting and its questions.
          </p>
        </div>
      </header>

      {backendError ? (
        <div
          role="alert"
          className="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
        >
          {backendError}
        </div>
      ) : null}

      <FormProvider {...methods}>
        <WizardLayout>
          <ProgressRail
            currentIndex={currentIndex}
            completedIndex={completedIndex}
            onStepSelect={handleStepSelect}
          />
          <FormCard
            title={STEPS[currentIndex].title}
            description={STEPS[currentIndex].subtitle}
            className="max-h-[calc(100dvh-10rem)]"
            footer={
              <WizardFooter
                onBack={handleStepBack}
                backHidden={currentIndex === 0}
                onNext={handleNext}
                nextLabel={
                  currentIndex === STEPS.length - 1 ? "Create meeting" : "Next"
                }
                nextDisabled={nextDisabled}
                submitting={createMeeting.isPending}
              />
            }
          >
            {renderStep()}
          </FormCard>
        </WizardLayout>
      </FormProvider>

      <Dialog
        open={showDiscardDialog}
        onOpenChange={(open) => {
          if (!open) setShowDiscardDialog(false);
        }}
      >
        <DialogContent showCloseButton={false}>
          <DialogHeader>
            <DialogTitle>Discard changes?</DialogTitle>
            <DialogDescription>
              Your meeting draft will be lost if you leave this page.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowDiscardDialog(false)}
            >
              Cancel
            </Button>
            <Button variant="destructive" onClick={() => router.push("/dashboard")}>
              Discard
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
