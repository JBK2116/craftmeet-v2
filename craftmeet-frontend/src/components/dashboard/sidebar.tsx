"use client";

import Link from "next/link";
import {
  CalendarDays,
  LayoutTemplate,
  Settings,
  type LucideIcon,
} from "lucide-react";
import { cn } from "cn";
import { Logo } from "@/components/logo";
import { comingSoon } from "@/lib/coming-soon";

type NavItem = { label: string; icon: LucideIcon; href?: string };

const mainNav: NavItem[] = [
  { label: "Meetings", icon: CalendarDays, href: "/dashboard" },
  { label: "Templates", icon: LayoutTemplate },
];

const settingsItem: NavItem = { label: "Settings", icon: Settings };

function NavItemLink({
  item,
  active,
  onNavigate,
}: {
  item: NavItem;
  active: boolean;
  onNavigate?: () => void;
}) {
  const className = cn(
    "flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm transition-colors",
    active
      ? "bg-primary/10 font-semibold text-primary"
      : "text-muted-foreground hover:bg-muted hover:text-foreground",
  );
  const content = (
    <>
      <item.icon className="size-4" />
      {item.label}
    </>
  );

  if (item.href) {
    return (
      <Link
        href={item.href}
        aria-current={active ? "page" : undefined}
        className={className}
        onClick={onNavigate}
      >
        {content}
      </Link>
    );
  }
  return (
    <button
      type="button"
      className={className}
      onClick={() => {
        onNavigate?.();
        comingSoon(item.label);
      }}
    >
      {content}
    </button>
  );
}

export function SidebarNav({
  variant = "sidebar",
  onNavigate,
}: {
  variant?: "sidebar" | "sheet";
  onNavigate?: () => void;
}) {
  // In the mobile sheet Settings is grouped with the other items (no pinned
  // footer), so it isn't stranded at the bottom of a full-height panel.
  const items = variant === "sheet" ? [...mainNav, settingsItem] : mainNav;

  return (
    <div className={cn("flex flex-col", variant === "sidebar" && "h-full")}>
      <Link
        href="/"
        className="flex items-center gap-2.5 px-5 py-4 text-foreground transition-opacity hover:opacity-80"
      >
        <Logo className="size-6 text-foreground" />
        <span className="text-sm font-semibold tracking-tight">Craftmeet</span>
      </Link>

      <nav className="flex flex-col gap-0.5 px-3 py-3">
        {items.map((item) => (
          <NavItemLink
            key={item.label}
            item={item}
            active={item.label === "Meetings"}
            onNavigate={onNavigate}
          />
        ))}
      </nav>

      {variant === "sidebar" ? (
        <div className="mt-auto border-t border-border px-3 py-3">
          <NavItemLink item={settingsItem} active={false} onNavigate={onNavigate} />
        </div>
      ) : null}
    </div>
  );
}

export function Sidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 z-30 hidden w-60 flex-col border-r border-border bg-background md:flex">
      <SidebarNav />
    </aside>
  );
}
