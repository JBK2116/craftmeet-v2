import type { Metadata } from "next";
import Script from "next/script";
import "@fontsource-variable/figtree";
import "./globals.css";
import { Providers } from "./providers";

export const metadata: Metadata = {
  title: "Craftmeet",
  description: "Run live meetings, polls, and Q&A.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en" className="h-full antialiased" suppressHydrationWarning>
      <body className="min-h-full flex flex-col">
        <Script id="theme-init" strategy="beforeInteractive">
          {`const t = localStorage.getItem('theme'); if (t === 'dark' || (!t && matchMedia('(prefers-color-scheme: dark)').matches)) document.documentElement.classList.add('dark');`}
        </Script>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
