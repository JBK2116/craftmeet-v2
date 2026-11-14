"use client";

import { useEffect, useRef, useState } from "react";

export type TocSection = { id: string; title: string };

export function TableOfContents({ sections }: { sections: TocSection[] }) {
  const [activeSection, setActiveSection] = useState("");
  const activeRef = useRef("");

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            activeRef.current = entry.target.id;
            setActiveSection(entry.target.id);
          } else if (activeRef.current === entry.target.id) {
            activeRef.current = "";
            setActiveSection("");
          }
        });
      },
      { rootMargin: "-20% 0px -80% 0px" },
    );

    sections.forEach((section) => {
      const el = document.getElementById(section.id);
      if (el) observer.observe(el);
    });

    return () => observer.disconnect();
  }, [sections]);

  return (
    <aside className="hidden w-64 shrink-0 lg:block">
      <div className="sticky top-24 flex flex-col gap-1">
        <p className="mb-3 text-xs uppercase tracking-widest text-muted-foreground/60">Contents</p>
        {sections.map((section) => (
          <a
            key={section.id}
            href={`#${section.id}`}
            className={`border-l py-1 pl-4 text-sm transition-all duration-200 ${
              activeSection === section.id
                ? "border-foreground font-medium text-foreground"
                : "border-border text-muted-foreground hover:border-muted-foreground/60 hover:text-foreground"
            }`}
          >
            {section.title}
          </a>
        ))}
      </div>
    </aside>
  );
}
