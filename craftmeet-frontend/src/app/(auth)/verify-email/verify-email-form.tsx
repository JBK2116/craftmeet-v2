"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { CircleCheck, CircleAlert, LoaderCircle } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import * as authApi from "@/lib/api/auth";

type Status = "verifying" | "success" | "error";

export function VerifyEmailForm() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token") ?? "";

  const [status, setStatus] = useState<Status>(token ? "verifying" : "error");
  const [resendEmail, setResendEmail] = useState("");
  const [resendSent, setResendSent] = useState(false);
  const [isResending, setIsResending] = useState(false);

  useEffect(() => {
    if (!token) return;
    let cancelled = false;
    authApi
      .verifyEmail(token)
      .then(() => {
        if (!cancelled) setStatus("success");
      })
      .catch(() => {
        if (!cancelled) setStatus("error");
      });
    return () => {
      cancelled = true;
    };
  }, [token]);

  async function handleResend() {
    setIsResending(true);
    try {
      await authApi.sendVerificationEmail(resendEmail);
      setResendSent(true);
    } catch {
      toast.error("Could not send a verification email. Please try again.");
    } finally {
      setIsResending(false);
    }
  }

  if (status === "verifying") {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
        <div className="flex flex-col items-center gap-4">
          <div className="h-8 w-8 rounded-full border-2 border-muted border-t-primary animate-spin" />
          <p className="text-sm text-muted-foreground">Verifying email…</p>
        </div>
      </main>
    );
  }

  if (status === "success") {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
        <div className="w-full max-w-md space-y-3">
          <div className="rounded-2xl border border-border bg-card px-8 py-9 text-center">
            <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-primary/10">
              <CircleCheck className="h-5 w-5 text-primary" />
            </div>
            <h1 className="mb-1 text-xl font-semibold tracking-tight text-foreground">Email verified!</h1>
            <p className="text-sm text-muted-foreground">You can now sign in to your account.</p>
            <Button
              nativeButton={false}
              render={<Link href="/login" />}
              className="mt-6 h-10 w-full rounded-lg text-sm font-medium"
            >
              Sign in
            </Button>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
      <div className="w-full max-w-md space-y-3">
        <div className="rounded-2xl border border-border bg-card px-8 py-9 text-center">
          <div className="mx-auto mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-destructive/10">
            <CircleAlert className="h-5 w-5 text-destructive" />
          </div>
          <h1 className="mb-1 text-xl font-semibold tracking-tight text-foreground">
            Invalid or expired verification link
          </h1>
          <p className="text-sm leading-relaxed text-muted-foreground">
            This link is invalid or has expired. Enter your email to receive a new verification link.
          </p>

          {resendSent ? (
            <p className="mt-6 rounded-lg bg-primary/10 px-4 py-3 text-sm text-foreground">
              Verification email sent. Check your inbox and spam folder.
            </p>
          ) : (
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleResend();
              }}
              className="mt-6 space-y-3 text-left"
            >
              <div className="space-y-1.5">
                <Label htmlFor="resend-email" className="text-sm font-medium text-muted-foreground">
                  Email
                </Label>
                <Input
                  id="resend-email"
                  type="email"
                  placeholder="you@example.com"
                  value={resendEmail}
                  onChange={(e) => setResendEmail(e.target.value)}
                  className="h-10"
                />
              </div>
              <Button type="submit" disabled={isResending} className="h-10 w-full rounded-lg text-sm font-medium">
                {isResending ? (
                  <>
                    <LoaderCircle className="h-4 w-4 animate-spin" />
                    Sending…
                  </>
                ) : (
                  "Resend verification email"
                )}
              </Button>
            </form>
          )}
        </div>

        <p className="text-center text-xs text-muted-foreground">
          <Link href="/login" className="transition-colors hover:text-foreground">
            Back to sign in
          </Link>
        </p>
      </div>
    </main>
  );
}
