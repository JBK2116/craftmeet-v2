import Link from "next/link";
import { CircleCheck } from "lucide-react";
import { Button } from "@/components/ui/button";

const features = [
  "Unlimited meetings",
  "All 5 question formats",
  "Live participant responses",
  "AI-generated summary after every session",
  "No account needed to join a meeting",
];

export default function PricingPage() {
  return (
    <main className="bg-background text-foreground">
      <section className="flex min-h-screen flex-col items-center justify-center px-6 py-24">
        <div className="mb-10 text-center">
          <p className="mb-3 text-xs uppercase tracking-widest text-muted-foreground">Pricing</p>
          <h1 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">Free With No Catch.</h1>
          <p className="mt-4 max-w-md text-sm leading-relaxed text-muted-foreground">
            Craftmeet is free while we build. No credit card required, no seat limits, no paywalled features.
          </p>
        </div>

        <div className="w-full max-w-sm rounded-2xl border border-border bg-card p-8 shadow-md">
          <div className="flex items-baseline gap-1.5">
            <span className="text-4xl font-bold tracking-tight text-foreground">$0</span>
            <span className="text-sm text-muted-foreground">/ forever</span>
          </div>

          <ul className="mt-6 space-y-3">
            {features.map((feature) => (
              <li key={feature} className="flex items-start gap-2.5 text-sm text-muted-foreground">
                <CircleCheck className="mt-0.5 h-4 w-4 shrink-0 text-primary" />
                {feature}
              </li>
            ))}
          </ul>

          <Button
            nativeButton={false}
            render={<Link href="/login" />}
            className="mt-8 h-10 w-full rounded-full bg-primary text-primary-foreground text-sm font-medium transition-colors hover:bg-primary/90"
          >
            Get started free
          </Button>
        </div>

        <p className="mt-8 text-xs text-muted-foreground/60">
          Pricing may change as we grow, early users receive notice first.
        </p>
      </section>
    </main>
  );
}
