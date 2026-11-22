"use client";

import { useState } from "react";
import Link from "next/link";
import { CircleHelp, LogOut, Menu, Search, Settings } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { ThemeToggle } from "@/components/theme-toggle";
import { useAuth } from "@/lib/auth/auth-provider";
import { comingSoon } from "@/lib/coming-soon";
import { useSearch } from "@/lib/search/search-provider";
import { SidebarNav } from "./sidebar";

export function TopBar() {
  const { query, setQuery } = useSearch();
  const { logout } = useAuth();
  const [navOpen, setNavOpen] = useState(false);

  return (
    <header className="sticky top-0 z-20 flex h-14 items-center gap-3 border-b border-border bg-background px-4 sm:px-6">
      <div className="md:hidden">
        <Sheet open={navOpen} onOpenChange={setNavOpen}>
          <SheetTrigger
            render={
              <Button variant="ghost" size="icon-sm" aria-label="Open navigation" />
            }
          >
            <Menu />
          </SheetTrigger>
          <SheetContent side="left" className="w-full border-border bg-background p-0">
            <SheetTitle className="sr-only">Navigation</SheetTitle>
            <SidebarNav variant="sheet" onNavigate={() => setNavOpen(false)} />
          </SheetContent>
        </Sheet>
      </div>

      <div className="relative w-full min-w-0 max-w-md">
        <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-muted-foreground" />
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search meetings"
          className="h-9 w-full min-w-0 rounded-lg border border-transparent bg-muted pr-3 pl-9 text-sm text-foreground transition-colors outline-none placeholder:text-muted-foreground focus:border-ring focus:ring-2 focus:ring-ring/50"
        />
      </div>

      <div className="ml-auto flex items-center gap-1">
        <div className="hidden items-center gap-1 sm:flex">
          <Button
            variant="ghost"
            size="icon-sm"
            aria-label="Help"
            nativeButton={false}
            render={<Link href="/contact" />}
            className="text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <CircleHelp />
          </Button>
          <Button
            variant="ghost"
            size="icon-sm"
            aria-label="Settings"
            onClick={() => comingSoon("Settings")}
            className="text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <Settings />
          </Button>
        </div>
        <ThemeToggle />
        <Button
          variant="ghost"
          size="icon-sm"
          aria-label="Logout"
          onClick={() => logout()}
          className="text-muted-foreground hover:bg-muted hover:text-foreground"
        >
          <LogOut />
        </Button>
      </div>
    </header>
  );
}
