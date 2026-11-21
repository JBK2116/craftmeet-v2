"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import * as authApi from "@/lib/api/auth";
import { useAuth } from "@/lib/auth/auth-provider";

export function VerifyEmailForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const { status: authStatus } = useAuth();

  const started = useRef(false);
  const [verified, setVerified] = useState(false);

  // Fire the verification request once, then mark the outcome. The ref guards
  // against StrictMode's double-invoked effects in dev (would verify twice).
  useEffect(() => {
    if (started.current) return;
    started.current = true;

    if (!token) {
      router.replace("/login");
      return;
    }

    authApi
      .verifyEmail(token)
      .then(() => {
        toast.success("Email verified");
        setVerified(true);
      })
      .catch(() => {
        toast.error("Verification failed. Please try again.");
        router.replace("/login");
      });
  }, [token, router]);

  // Once verified and the session is known, route authenticated users to the
  // dashboard and everyone else to login.
  useEffect(() => {
    if (!verified || authStatus === "loading") return;
    router.replace(authStatus === "authenticated" ? "/dashboard" : "/login");
  }, [verified, authStatus, router]);

  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-4 py-16">
      <div className="flex flex-col items-center gap-4">
        <div className="h-8 w-8 rounded-full border-2 border-muted border-t-primary animate-spin" />
        <p className="text-sm text-muted-foreground">Verifying email…</p>
      </div>
    </main>
  );
}
