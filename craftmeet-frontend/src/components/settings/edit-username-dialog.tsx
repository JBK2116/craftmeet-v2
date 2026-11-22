"use client";

import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { LoaderCircle } from "lucide-react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const usernameFormSchema = z.object({
  username: z
    .string()
    .trim()
    .min(3, { error: "Username must be at least 3 characters." })
    .max(30, { error: "Username must be 30 characters or fewer." })
    .regex(/^[a-zA-Z0-9]*$/, {
      error: "Username can only contain letters and numbers.",
    }),
});

type UsernameFormValues = z.infer<typeof usernameFormSchema>;

export function EditUsernameDialog({
  open,
  currentUsername,
  isSaving,
  onSave,
  onCancel,
}: {
  open: boolean;
  currentUsername: string | null;
  isSaving: boolean;
  onSave: (newUsername: string) => void;
  onCancel: () => void;
}) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<UsernameFormValues>({
    resolver: zodResolver(usernameFormSchema),
    defaultValues: { username: currentUsername ?? "" },
  });

  useEffect(() => {
    if (open) {
      reset({ username: currentUsername ?? "" });
    }
  }, [open, currentUsername, reset]);

  function onSubmit(values: UsernameFormValues) {
    onSave(values.username);
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        if (!nextOpen) onCancel();
      }}
    >
      <DialogContent showCloseButton={false}>
        <DialogHeader>
          <DialogTitle>Edit username</DialogTitle>
          <DialogDescription>
            Your username is shown on your meetings and to participants.
          </DialogDescription>
        </DialogHeader>
        <form
          id="edit-username-form"
          onSubmit={handleSubmit(onSubmit)}
          noValidate
          className="space-y-1.5"
        >
          <Label htmlFor="username">Username</Label>
          <Input
            id="username"
            type="text"
            maxLength={30}
            aria-invalid={errors.username ? true : undefined}
            aria-describedby={errors.username ? "username-error" : undefined}
            {...register("username")}
          />
          {errors.username ? (
            <p id="username-error" className="text-xs text-destructive">
              {errors.username.message}
            </p>
          ) : null}
        </form>
        <DialogFooter>
          <Button
            type="button"
            variant="ghost"
            onClick={onCancel}
            disabled={isSaving}
          >
            Cancel
          </Button>
          <Button type="submit" form="edit-username-form" disabled={isSaving}>
            {isSaving ? (
              <>
                <LoaderCircle className="size-4 animate-spin" />
                Saving…
              </>
            ) : (
              "Save"
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
