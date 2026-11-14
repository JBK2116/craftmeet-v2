"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { Menu, Moon, Sun } from "lucide-react";
import { Logo } from "@/components/logo";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { useAuth } from "@/lib/auth/auth-provider";

const landingLinks = [
  { title: "How it Works", href: "#how-it-works" },
  { title: "Features", href: "#features" },
  { title: "Pricing", href: "/pricing" },
  { title: "Privacy", href: "/privacy" },
  { title: "Join Meeting", href: "#join-meeting" },
];

const authRoutes = [
  "/login",
  "/signup",
  "/forgot-password",
  "/reset-password",
  "/verify-email",
];

function ThemeToggle() {
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    setIsDark(document.documentElement.classList.contains("dark"));
  }, []);

  function toggle() {
    const next = !isDark;
    setIsDark(next);
    document.documentElement.classList.toggle("dark", next);
    localStorage.setItem("theme", next ? "dark" : "light");
  }

  return (
    <Button
      variant="ghost"
      size="icon-sm"
      onClick={toggle}
      className="text-muted-foreground hover:bg-accent hover:text-accent-foreground"
      aria-label="Toggle theme"
    >
      {isDark ? <Sun /> : <Moon />}
    </Button>
  );
}

export function Navbar() {
  const pathname = usePathname();
  const { user, logout } = useAuth();

  const isLanding = pathname === "/";
  const isAuth = authRoutes.includes(pathname);

  const authButtons = user ? (
    <>
      <span className="hidden text-sm text-muted-foreground sm:inline">
        {user.username ?? user.email}
      </span>
      <Button
        variant="ghost"
        size="sm"
        onClick={() => logout()}
        className="text-muted-foreground hover:bg-accent hover:text-accent-foreground"
      >
        Logout
      </Button>
    </>
  ) : (
    <>
      <Button
        variant="ghost"
        size="sm"
        nativeButton={false} render={<Link href="/login" />}
        className="text-muted-foreground hover:bg-accent hover:text-accent-foreground"
      >
        Log in
      </Button>
      <Button size="sm" nativeButton={false} render={<Link href="/signup" />}>
        Sign up
      </Button>
    </>
  );

  return (
    <>
      <header className="fixed top-0 right-0 left-0 z-50 border-b border-border bg-background">
        <nav className="mx-auto flex h-14 max-w-7xl items-center justify-between px-6">
          <Link
            href="/"
            className="group flex shrink-0 items-center gap-2 text-foreground transition-opacity active:opacity-90"
          >
            <Logo className="h-6 w-6 text-foreground transition-colors duration-200 group-hover:text-primary" />
            <span className="text-sm font-semibold tracking-tight transition-colors duration-200 group-hover:text-foreground/90">
              Craftmeet
            </span>
          </Link>

          {isLanding && (
            <>
              <div className="hidden items-center gap-8 md:flex">
                {landingLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    className="text-sm text-muted-foreground transition-colors duration-150 hover:text-foreground"
                  >
                    {link.title}
                  </Link>
                ))}
              </div>

              <div className="hidden items-center gap-3 md:flex">
                <ThemeToggle />
                {authButtons}
              </div>

              <div className="md:hidden">
                <Sheet>
                  <SheetTrigger
                    render={
                      <Button
                        variant="ghost"
                        size="icon-sm"
                        className="text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                      />
                    }
                  >
                    <Menu />
                  </SheetTrigger>
                  <SheetContent
                    side="right"
                    className="w-72 border-border bg-background p-6"
                  >
                    <div className="mt-8 flex flex-col gap-6">
                      <div className="flex flex-col gap-4">
                        {landingLinks.map((link) => (
                          <Link
                            key={link.href}
                            href={link.href}
                            className="text-sm text-muted-foreground transition-colors duration-150 hover:text-foreground"
                          >
                            {link.title}
                          </Link>
                        ))}
                      </div>
                      <div className="flex flex-col gap-2 border-t border-border pt-4">
                        {authButtons}
                      </div>
                    </div>
                  </SheetContent>
                </Sheet>
              </div>
            </>
          )}

          {isAuth && (
            <div className="flex items-center gap-3">
              <ThemeToggle />
              {pathname === "/login" ? (
                <Button size="sm" nativeButton={false} render={<Link href="/signup" />}>
                  Sign up
                </Button>
              ) : (
                <Button
                  variant="ghost"
                  size="sm"
                  nativeButton={false} render={<Link href="/login" />}
                  className="text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                >
                  Log in
                </Button>
              )}
            </div>
          )}

          {!isLanding && !isAuth && (
            <div className="flex items-center gap-3">
              <ThemeToggle />
              {authButtons}
            </div>
          )}
        </nav>
      </header>
      <div className="h-14" />
    </>
  );
}
