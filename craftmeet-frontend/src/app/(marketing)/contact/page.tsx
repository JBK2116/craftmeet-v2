import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function ContactPage() {
  return (
    <main className="relative min-h-screen bg-background text-foreground">
      <div className="pointer-events-none absolute inset-0 flex justify-center overflow-hidden">
        <div className="h-[800px] w-[800px] -translate-y-1/2 rounded-full bg-primary/5 blur-3xl" />
      </div>

      <div className="mx-auto flex max-w-3xl flex-col gap-12 px-6 py-24 lg:px-8">
        <header className="flex flex-col gap-4">
          <h1 className="text-4xl font-semibold tracking-tight text-foreground">Contact Us</h1>
          <p className="text-lg text-muted-foreground">
            Have a question, a piece of feedback, or need a hand with your account? We&apos;d love to hear from you.
          </p>
          <p className="text-muted-foreground">
            The only way to reach us is by email. We don&apos;t operate a phone line or a physical office, so
            emailing us directly at{" "}
            <a
              href="mailto:support@craftmeet.live"
              className="font-medium text-foreground underline underline-offset-4 hover:text-muted-foreground"
            >
              support@craftmeet.live
            </a>{" "}
            is the fastest and only way to get a response. This is the same address referenced throughout our{" "}
            <Link
              href="/privacy"
              className="font-medium text-foreground underline underline-offset-4 hover:text-muted-foreground"
            >
              Privacy Notice
            </Link>{" "}
            and{" "}
            <Link
              href="/terms"
              className="font-medium text-foreground underline underline-offset-4 hover:text-muted-foreground"
            >
              Terms and Conditions
            </Link>
            .
          </p>
        </header>

        <div className="flex flex-col gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Email Us Directly</CardTitle>
              <CardDescription>
                Email is our primary and only support channel. Whether it&apos;s a general question, a privacy
                request, a billing concern, or a legal inquiry, send it our way and we&apos;ll get back to you.
              </CardDescription>
            </CardHeader>
            <CardContent className="flex flex-col gap-4">
              <a
                href="mailto:support@craftmeet.live"
                className={buttonVariants({ variant: "default", className: "w-fit" })}
              >
                support@craftmeet.live
              </a>
              <p className="text-sm text-muted-foreground">
                We aim to respond to all legitimate requests within 30 days.
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                Mailing Address
                <Badge variant="secondary">Coming Soon</Badge>
              </CardTitle>
              <CardDescription>
                We don&apos;t currently have a physical or virtual mailing address. Once we establish one, you&apos;ll
                be able to find it here.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                In the meantime, please direct all correspondence to{" "}
                <a
                  href="mailto:support@craftmeet.live"
                  className="font-medium text-foreground underline underline-offset-4 hover:text-muted-foreground"
                >
                  support@craftmeet.live
                </a>
                .
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </main>
  );
}
