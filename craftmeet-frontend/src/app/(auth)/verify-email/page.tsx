import { Suspense } from "react";
import { VerifyEmailForm } from "./verify-email-form";

export default function VerifyEmailPage() {
  return (
    <Suspense
      fallback={
        <div className="flex min-h-screen items-center justify-center bg-background">
          <div className="h-8 w-8 rounded-full border-2 border-muted border-t-primary animate-spin" />
        </div>
      }
    >
      <VerifyEmailForm />
    </Suspense>
  );
}
