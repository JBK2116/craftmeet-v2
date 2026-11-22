"use client";

import { useState } from "react";
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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export function DeleteAccountDialog({
  open,
  email,
  isDeleting,
  onConfirm,
  onCancel,
}: {
  open: boolean;
  email: string;
  isDeleting: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  const [typedEmail, setTypedEmail] = useState("");
  // Reset the confirmation input whenever the dialog opens/closes or the email
  // being confirmed changes. Clearing during render (rather than an effect)
  // avoids the cascading render flagged by react-hooks/set-state-in-effect.
  const [prevOpenEmail, setPrevOpenEmail] = useState({ open, email });
  if (prevOpenEmail.open !== open || prevOpenEmail.email !== email) {
    setPrevOpenEmail({ open, email });
    setTypedEmail("");
  }

  const confirmed = typedEmail === email;

  return (
    <Dialog
      open={open}
      onOpenChange={(nextOpen) => {
        if (!nextOpen) onCancel();
      }}
    >
      <DialogContent showCloseButton={false}>
        <DialogHeader>
          <DialogTitle>Delete account?</DialogTitle>
          <DialogDescription>
            Your account and all of your meetings will be permanently deleted.
            This action cannot be undone.
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-1.5">
          <Label htmlFor="confirm-email">
            Type <span className="font-medium">{email}</span> to confirm
          </Label>
          <Input
            id="confirm-email"
            type="text"
            value={typedEmail}
            onChange={(event) => setTypedEmail(event.target.value)}
            autoComplete="off"
            disabled={isDeleting}
          />
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onCancel} disabled={isDeleting}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            onClick={onConfirm}
            disabled={!confirmed || isDeleting}
          >
            {isDeleting ? (
              <>
                <LoaderCircle className="size-4 animate-spin" />
                Deleting…
              </>
            ) : (
              "Delete account"
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
