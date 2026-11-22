"use client";

import { LoaderCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

export function DangerZone({
  isDeletingMeetings,
  isDeletingAccount,
  onDeleteAllMeetings,
  onDeleteAccount,
  confirmOpen,
  onConfirmOpenChange,
}: {
  isDeletingMeetings: boolean;
  isDeletingAccount: boolean;
  onDeleteAllMeetings: () => void;
  onDeleteAccount: () => void;
  confirmOpen: boolean;
  onConfirmOpenChange: (open: boolean) => void;
}) {
  return (
    <section className="border-t border-border pt-6">
      <h2 className="font-semibold text-destructive">Danger zone</h2>

      <div className="mt-4 divide-y divide-border rounded-xl bg-card ring-1 ring-foreground/10">
        <div className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <div className="font-medium">Delete all meetings</div>
            <p className="text-sm text-muted-foreground">
              Permanently delete every meeting you have created.
            </p>
          </div>
          <Button variant="outline" onClick={() => onConfirmOpenChange(true)}>
            Delete all meetings
          </Button>
        </div>

        <div className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <div className="font-medium">Delete account</div>
            <p className="text-sm text-muted-foreground">
              Permanently delete your account and all associated data.
            </p>
          </div>
          <Button
            variant="destructive"
            aria-label="Delete account"
            disabled={isDeletingAccount}
            onClick={onDeleteAccount}
          >
            {isDeletingAccount ? (
              <>
                <LoaderCircle className="size-4 animate-spin" />
                Deleting…
              </>
            ) : (
              "Delete account"
            )}
          </Button>
        </div>
      </div>

      <Dialog open={confirmOpen} onOpenChange={onConfirmOpenChange}>
        <DialogContent showCloseButton={false}>
          <DialogHeader>
            <DialogTitle>Delete all meetings?</DialogTitle>
            <DialogDescription>
              All of your meetings, along with their questions and stats, will
              be permanently deleted. This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => onConfirmOpenChange(false)}
              disabled={isDeletingMeetings}
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={onDeleteAllMeetings}
              disabled={isDeletingMeetings}
            >
              {isDeletingMeetings ? (
                <>
                  <LoaderCircle className="size-4 animate-spin" />
                  Deleting…
                </>
              ) : (
                "Delete all"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </section>
  );
}
