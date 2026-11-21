"use client";

import { useEffect, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { LoaderCircle } from "lucide-react";
import { Sidebar } from "@/components/dashboard/sidebar";
import { TopBar } from "@/components/dashboard/top-bar";
import { useAuth } from "@/lib/auth/auth-provider";
import { SearchProvider } from "@/lib/search/search-provider";

/** Shared guard + app shell for authenticated-only pages. Static export has no
 *  middleware, so auth is enforced client-side: spinner until the session is
 *  known, then redirect unauthenticated visitors to /login. */
export default function PrivateLayout({ children }: { children: ReactNode }) {
  const { status } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (status === "unauthenticated") {
      router.replace("/login");
    }
  }, [status, router]);

  if (status === "loading") {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoaderCircle
          className="size-6 animate-spin text-muted-foreground"
          aria-label="Loading"
        />
      </div>
    );
  }

  if (status === "unauthenticated") {
    return null;
  }

  return (
    <SearchProvider>
      <div className="min-h-screen bg-background">
        <Sidebar />
        <div className="md:pl-60">
          <TopBar />
          <main className="px-4 py-6 sm:px-6 lg:px-8">{children}</main>
        </div>
      </div>
    </SearchProvider>
  );
}
