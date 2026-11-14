"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { CircleAlert, LoaderCircle } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import * as authApi from "@/lib/api/auth";
import { ApiError, RateLimitedError } from "@/lib/types/errors";

const resetSchema = z
  .object({
    password: z
      .string()
      .min(12, "Password must be at least 12 characters")
      .max(72, "Password must be at most 72 characters"),
    confirmPassword: z.string().min(1, "Please confirm your password"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

type ResetValues = z.infer<typeof resetSchema>;

export function ResetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token") ?? "";

  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetValues>({
    resolver: zodResolver(resetSchema),
    defaultValues: { password: "", confirmPassword: "" },
  });

  async function onSubmit(values: ResetValues) {
    setIsSubmitting(true);
    try {
      await authApi.resetPassword(token, { newPassword: values.password });
      toast.success("Password updated. Please sign in.");
      router.push("/login");
    } catch (err) {
      if (err instanceof RateLimitedError) {
        toast.error(err.message);
      } else if (err instanceof ApiError) {
        toast.error("Your reset link is invalid or has expired. Please request a new one.");
      } else {
        toast.error("Unable to reset password. Please try again.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  if (!token) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
        <div className="w-full max-w-md space-y-3">
          <div className="rounded-2xl border border-border bg-card px-8 py-9 text-center">
            <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-destructive/10">
              <CircleAlert className="h-5 w-5 text-destructive" />
            </div>
            <h1 className="mb-1 text-xl font-semibold tracking-tight text-foreground">
              Invalid or expired reset link
            </h1>
            <p className="text-sm leading-relaxed text-muted-foreground">
              This link is invalid or has expired. Request a new one to try again.
            </p>
            <Button
              nativeButton={false}
              render={<Link href="/forgot-password" />}
              className="mt-6 h-10 w-full rounded-lg text-sm font-medium"
            >
              Request a new link
            </Button>
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
            Reset your password
          </h1>
          <p className="mb-7 text-center text-sm text-muted-foreground">
            Choose a strong password to secure your account.
          </p>

          <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="password" className="text-sm font-medium text-muted-foreground">
                New Password
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

            <div className="space-y-1.5">
              <Label htmlFor="confirm-password" className="text-sm font-medium text-muted-foreground">
                Confirm New Password
              </Label>
              <Input
                id="confirm-password"
                type="password"
                placeholder="Re-enter your password"
                disabled={isSubmitting}
                aria-invalid={!!errors.confirmPassword}
                className="h-10"
                {...register("confirmPassword")}
              />
              {errors.confirmPassword && (
                <p className="text-xs text-destructive">{errors.confirmPassword.message}</p>
              )}
            </div>

            <Button
              type="submit"
              disabled={isSubmitting}
              className="mt-1 h-10 w-full rounded-lg px-4 py-2.5 text-sm font-medium"
            >
              {isSubmitting ? (
                <>
                  <LoaderCircle className="h-4 w-4 animate-spin" />
                  Resetting password…
                </>
              ) : (
                "Reset password"
              )}
            </Button>
          </form>
        </div>

        <div className="rounded-xl border border-border bg-card px-8 py-4 text-center text-sm text-muted-foreground shadow-sm">
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
