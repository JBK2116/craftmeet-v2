"use client";

import Link from "next/link";
import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { LoaderCircle, MailCheck } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import * as authApi from "@/lib/api/auth";
import { ApiError, RateLimitedError } from "@/lib/types/errors";

const forgotSchema = z.object({
  email: z.string().email("Please enter a valid email address"),
});

type ForgotValues = z.infer<typeof forgotSchema>;

export default function ForgotPasswordPage() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotValues>({
    resolver: zodResolver(forgotSchema),
    defaultValues: { email: "" },
  });

  async function onSubmit(values: ForgotValues) {
    setIsSubmitting(true);
    try {
      await authApi.forgotPassword(values.email);
      setSubmittedEmail(values.email);
    } catch (err) {
      if (err instanceof RateLimitedError) {
        toast.error(err.message);
      } else if (err instanceof ApiError) {
        toast.error(
          "We could not send your reset link. There was a temporary problem with our service. Please try again in a few minutes.",
        );
      } else {
        toast.error("An unexpected error occurred. Please try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  if (submittedEmail) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
        <div className="w-full max-w-[420px] space-y-3">
          <div className="rounded-2xl border border-border bg-card px-8 py-9 text-center">
            <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-primary/10">
              <MailCheck className="h-5 w-5 text-primary" />
            </div>
            <h1 className="mb-1 text-xl font-semibold tracking-tight text-foreground">Check your inbox</h1>
            <p className="text-sm leading-relaxed text-muted-foreground">
              If <span className="font-medium text-foreground">{submittedEmail}</span> is associated with an account,
              you&apos;ll receive a reset link shortly. Be sure to check your spam folder.
            </p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
      <div className="w-full max-w-[420px] space-y-3">
        <div className="rounded-2xl border border-border bg-card px-8 py-9">
          <h1 className="mb-1 text-center text-[1.4rem] font-semibold tracking-tight text-foreground">
            Forgot your password?
          </h1>
          <p className="mb-7 text-center text-sm text-muted-foreground">
            Enter your email and we&apos;ll send you a reset link.
          </p>

          <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="email" className="text-sm font-medium text-muted-foreground">
                Email
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="you@example.com"
                disabled={isSubmitting}
                aria-invalid={!!errors.email}
                className="h-10"
                {...register("email")}
              />
              {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
            </div>

            <Button
              type="submit"
              disabled={isSubmitting}
              className="mt-1 h-10 w-full rounded-lg px-4 py-2.5 text-sm font-medium"
            >
              {isSubmitting ? (
                <>
                  <LoaderCircle className="h-4 w-4 animate-spin" />
                  Sending…
                </>
              ) : (
                "Send reset link"
              )}
            </Button>
          </form>
        </div>

        <div className="rounded-xl border border-border bg-card px-8 py-4 text-center text-sm text-muted-foreground">
          Remember your password?
          <Link
            href="/login"
            className="ml-1 font-medium text-foreground underline-offset-2 transition-colors hover:text-muted-foreground hover:underline"
          >
            Sign in
          </Link>
        </div>

        <p className="text-center text-xs text-muted-foreground">
          <Link href="/privacy" className="transition-colors hover:text-foreground">
            Privacy Policy
          </Link>
          <span className="mx-1.5">·</span>
          <Link href="/terms" className="transition-colors hover:text-foreground">
            Terms of Service
          </Link>
        </p>
      </div>
    </main>
  );
}
