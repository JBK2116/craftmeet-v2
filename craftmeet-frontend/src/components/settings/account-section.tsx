"use client";

import { Mail, Pencil } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import type { User } from "@/lib/types/user";

export function AccountSection({
  user,
  onEditUsername,
}: {
  user: User;
  onEditUsername: () => void;
}) {
  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Pencil className="size-4 text-muted-foreground" />
            Username
          </CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-between gap-3">
          <span className="font-medium">{user.username ?? "Not set"}</span>
          <Button
            variant="outline"
            size="sm"
            aria-label="Edit username"
            onClick={onEditUsername}
          >
            Edit
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Mail className="size-4 text-muted-foreground" />
            Email
          </CardTitle>
          <CardDescription>{"Email can't be changed"}</CardDescription>
        </CardHeader>
        <CardContent>
          <span className="font-medium">{user.email}</span>
        </CardContent>
      </Card>
    </div>
  );
}
