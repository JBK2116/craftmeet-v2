"use client";

import Link from "next/link";
import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { LoaderCircle, MailCheck } from "lucide-react";
import { toast } from "sonner";
import { GoogleButton } from "@/components/google-button";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import * as authApi from "@/lib/api/auth";
import { ApiError, RateLimitedError } from "@/lib/types/errors";

const signupSchema = z.object({
  username: z
    .string()
    .min(3, "Username must be at least 3 characters")
    .max(30, "Username must be at most 30 characters"),
  email: z.string().email("Please enter a valid email address"),
  password: z
    .string()
    .min(12, "Password must be at least 12 characters")
    .max(72, "Password must be at most 72 characters"),
});

type SignupValues = z.infer<typeof signupSchema>;

export default function SignupPage() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SignupValues>({
    resolver: zodResolver(signupSchema),
    defaultValues: { username: "", email: "", password: "" },
  });

  async function onSubmit(values: SignupValues) {
    setIsSubmitting(true);
    try {
      await authApi.signup(values);
      setSubmittedEmail(values.email);
    } catch (err) {
      if (err instanceof RateLimitedError) {
        toast.error(err.message);
      } else if (err instanceof ApiError) {
        if (err.code === "DUPLICATE_RESOURCE") {
          toast.error("An account with that email or username already exists.");
        } else {
          toast.error(err.message);
        }
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
        <div className="w-full max-w-md space-y-3">
          <div className="rounded-2xl border border-border bg-card px-8 py-9 text-center">
            <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-primary/10">
              <MailCheck className="h-5 w-5 text-primary" />
            </div>
            <h1 className="mb-1 text-xl font-semibold tracking-tight text-foreground">Verify your email</h1>
            <p className="text-sm leading-relaxed text-muted-foreground">
              We&apos;ve sent a verification link to <span className="font-medium text-foreground">{submittedEmail}</span>.
              Please confirm your email address to activate your account.
            </p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
      <div className="w-full max-w-md space-y-3">
        <div className="rounded-2xl border border-border bg-card px-8 py-9 shadow-sm">
          <h1 className="mb-1 text-center text-2xl font-semibold tracking-tight text-foreground">
            Create your account
          </h1>
          <p className="mb-7 text-center text-sm text-muted-foreground">Start running better meetings today.</p>

          <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="username" className="text-sm font-medium text-muted-foreground">
                Username
              </Label>
              <Input
                id="username"
                type="text"
                placeholder="yourname"
                disabled={isSubmitting}
                aria-invalid={!!errors.username}
                className="h-10"
                {...register("username")}
              />
              {errors.username && <p className="text-xs text-destructive">{errors.username.message}</p>}
            </div>

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

            <div className="space-y-1.5">
              <Label htmlFor="password" className="text-sm font-medium text-muted-foreground">
                Password
              </Label>
              <Input
                id="password"
                type="password"
                placeholder="12+ characters"
                disabled={isSubmitting}
                aria-invalid={!!errors.password}
                className="h-10"
                {...register("password")}
              />
              {errors.password && <p className="text-xs text-destructive">{errors.password.message}</p>}
            </div>

            <Button
              type="submit"
              disabled={isSubmitting}
              className="mt-1 h-10 w-full rounded-lg px-4 py-2.5 text-sm font-medium"
            >
              {isSubmitting ? (
                <>
                  <LoaderCircle className="h-4 w-4 animate-spin" />
                  Creating account…
                </>
              ) : (
                "Create account"
              )}
            </Button>
          </form>

          <div className="my-5 flex items-center gap-3">
            <div className="h-px flex-1 bg-border" />
            <span className="text-xs font-medium uppercase tracking-widest text-muted-foreground/50">or</span>
            <div className="h-px flex-1 bg-border" />
          </div>

          <GoogleButton disabled={isSubmitting} />
        </div>

        <div className="rounded-xl border border-border bg-card px-8 py-4 text-center text-sm text-muted-foreground shadow-sm">
          Already have an account?
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
