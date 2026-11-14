import Link from "next/link";
import {
  ChartBarBig,
  ChevronRight,
  CircleCheck,
  LayoutList,
  MessageSquare,
  Zap,
  type LucideIcon,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { JoinMeeting } from "@/components/join-meeting";
import { Navbar } from "@/components/navbar";

const features: { icon: LucideIcon; title: string; desc: string }[] = [
  {
    icon: LayoutList,
    title: "Multiple Choice",
    desc: "Single or multi-select from host-defined options. Results tally in real time.",
  },
  {
    icon: MessageSquare,
    title: "Long Answer",
    desc: "Open text responses for qualitative ideas and freeform input.",
  },
  {
    icon: ChartBarBig,
    title: "Ranked Voting",
    desc: "Participants drag-rank a priority list. Surface group consensus instantly.",
  },
  {
    icon: Zap,
    title: "Rating Scale",
    desc: "1–5 or 1–10 numeric rating shown as a live aggregate across the room.",
  },
  {
    icon: CircleCheck,
    title: "Yes / No",
    desc: "Live pulse check. Instant thumbs up/down read on any question.",
  },
];

const steps: { step: string; title: string; desc: string }[] = [
  {
    step: "01",
    title: "Build your session",
    desc: "Create a meeting, add questions in any order and format, and set your participant cap. Schedule it ahead or launch immediately.",
  },
  {
    step: "02",
    title: "Share the room",
    desc: "Send a link or room code. Participants join instantly!",
  },
  {
    step: "03",
    title: "Run it live",
    desc: "Open questions one at a time, watch responses stream in, and reveal results when you're ready. You control the pace.",
  },
  {
    step: "04",
    title: "Get the summary",
    desc: "End the session and receive an AI-generated PDF summarizing every response, idea, and vote from the room.",
  },
];

export default function HomePage() {
  return (
    <>
      <Navbar />
      <main className="bg-background text-foreground">
        <section className="relative flex min-h-[88vh] flex-col items-center justify-center px-6 pt-12 pb-24 text-center overflow-hidden">
          <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
            <div className="h-150 w-150 rounded-full bg-primary/5 blur-3xl" />
          </div>

          <h1 className="relative max-w-3xl text-5xl leading-[1.08] tracking-tight text-foreground sm:text-6xl lg:text-7xl">
            Craftmeet
          </h1>
          <p className="relative max-w-3xl text-3xl leading-[1.15] tracking-tight text-foreground/80 sm:text-4xl lg:text-5xl">
            Meetings that{" "}
            <span className="inline-block font-black text-primary">
              extract
            </span>{" "}
            great ideas.
          </p>

          <p className="relative mt-6 max-w-xl text-base leading-relaxed text-muted-foreground sm:text-lg">
            Craftmeet lets you host real-time structured sessions with flexible
            question formats, live participant responses, and an AI-generated
            summary delivered the moment your meeting ends.
          </p>

          <div className="relative mt-10 flex flex-wrap items-center justify-center gap-3">
            <Button
              render={<Link href="/login" />}
              className="h-10 rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90"
            >
              Get started free
            </Button>
            <Button
              variant="outline"
              render={<Link href="#join-meeting" />}
              className="h-10 rounded-full border-border bg-transparent px-6 text-sm font-medium text-foreground/80 transition-all hover:bg-secondary"
            >
              Join a meeting
              <ChevronRight className="ml-1 h-3.5 w-3.5" />
            </Button>
          </div>

          <p className="relative mt-8 text-xs text-muted-foreground/60">
            No credit card required &nbsp;·&nbsp; Free tier available
            &nbsp;·&nbsp; No account needed to join a meeting
          </p>
        </section>

        <section id="features" className="border-t border-border px-6 py-24">
          <div className="mx-auto max-w-6xl">
            <div className="mb-16 text-center">
              <p className="mb-3 text-xs uppercase tracking-widest text-muted-foreground">
                Features
              </p>
              <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
                Every format your session needs
              </h2>
              <p className="mt-4 max-w-lg mx-auto text-sm leading-relaxed text-muted-foreground">
                Five question types built for real facilitation.
              </p>
            </div>

            <div className="grid grid-cols-1 gap-px rounded-xl overflow-hidden border border-border bg-border sm:grid-cols-2 lg:grid-cols-5">
              {features.map((feature) => (
                <div
                  key={feature.title}
                  className="group flex flex-col gap-4 bg-card p-8 transition-colors hover:bg-muted/40"
                >
                  <div className="flex h-9 w-9 items-center justify-center rounded-lg border border-border bg-background">
                    <feature.icon className="h-4 w-4 text-primary" />
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-foreground">
                      {feature.title}
                    </h3>
                    <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
                      {feature.desc}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section
          id="how-it-works"
          className="border-t border-border px-6 py-24"
        >
          <div className="mx-auto max-w-4xl">
            <div className="mb-16 text-center">
              <p className="mb-3 text-xs uppercase tracking-widest text-muted-foreground">
                How it works
              </p>
              <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
                From idea to insight in minutes
              </h2>
            </div>

            <div className="relative flex flex-col gap-0">
              <div className="absolute left-4.75 top-3 bottom-3 w-px bg-border sm:left-5.75" />
              {steps.map((item) => (
                <div
                  key={item.step}
                  className="relative flex gap-8 pb-12 last:pb-0"
                >
                  <div className="relative z-10 flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-card sm:h-12 sm:w-12">
                    <span className="text-[10px] font-medium text-muted-foreground sm:text-xs">
                      {item.step}
                    </span>
                  </div>
                  <div className="pt-2">
                    <h3 className="text-base font-medium text-foreground sm:text-lg">
                      {item.title}
                    </h3>
                    <p className="mt-2 max-w-lg text-sm leading-relaxed text-muted-foreground">
                      {item.desc}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <JoinMeeting />

        <footer className="border-t border-border px-6 py-10">
          <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 sm:flex-row">
            <span className="text-sm font-medium text-muted-foreground">
              Craftmeet
            </span>
            <div className="flex gap-6">
              <Link
                href="/privacy"
                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
              >
                Privacy
              </Link>
              <Link
                href="/terms"
                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
              >
                Terms
              </Link>
              <Link
                href="/contact"
                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
              >
                Contact
              </Link>
            </div>
            <span className="text-xs text-muted-foreground">
              © 2026 Craftmeet
            </span>
          </div>
        </footer>
      </main>
    </>
  );
}
