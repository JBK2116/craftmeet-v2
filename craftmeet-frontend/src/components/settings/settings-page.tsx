"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { AccountSection } from "@/components/settings/account-section";
import { DangerZone } from "@/components/settings/danger-zone";
import { DeleteAccountDialog } from "@/components/settings/delete-account-dialog";
import { EditUsernameDialog } from "@/components/settings/edit-username-dialog";
import { LifetimeStats } from "@/components/settings/lifetime-stats";
import { useAuth } from "@/lib/auth/auth-provider";
import {
  useDeleteAccount,
  useDeleteAllMeetings,
  useStats,
  useUpdateUsername,
} from "@/lib/hooks/use-settings";

export function SettingsPage() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const stats = useStats();
  const updateUsername = useUpdateUsername();
  const deleteAccount = useDeleteAccount();
  const deleteAllMeetings = useDeleteAllMeetings();
  const [usernameOpen, setUsernameOpen] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);

  if (!user) return null;

  return (
    <div className="mx-auto flex w-full max-w-3xl flex-col gap-8">
      <header className="flex flex-col gap-1">
        <h1 className="text-2xl font-bold tracking-tight">Settings</h1>
        <p className="text-muted-foreground">
          Manage your account, lifetime stats, and data.
        </p>
      </header>

      <LifetimeStats
        stats={stats.data}
        isLoading={stats.isLoading}
        isError={stats.isError}
        onRetry={() => stats.refetch()}
      />

      <AccountSection user={user} onEditUsername={() => setUsernameOpen(true)} />

      <DangerZone
        isDeletingMeetings={deleteAllMeetings.isPending}
        isDeletingAccount={deleteAccount.isPending}
        onDeleteAllMeetings={() =>
          deleteAllMeetings.mutate(undefined, {
            onSuccess: () => toast.success("All meetings deleted"),
          })
        }
        onDeleteAccount={() => setAccountOpen(true)}
      />

      <EditUsernameDialog
        open={usernameOpen}
        currentUsername={user.username}
        isSaving={updateUsername.isPending}
        onSave={(newUsername) =>
          updateUsername.mutate(
            { id: user.id, newUsername },
            {
              onSuccess: () => {
                toast.success("Username updated");
                setUsernameOpen(false);
              },
            },
          )
        }
        onCancel={() => setUsernameOpen(false)}
      />

      <DeleteAccountDialog
        open={accountOpen}
        email={user.email}
        isDeleting={deleteAccount.isPending}
        onConfirm={() =>
          deleteAccount.mutate(user.id, {
            onSuccess: async () => {
              await logout();
              router.replace("/");
            },
          })
        }
        onCancel={() => setAccountOpen(false)}
      />
    </div>
  );
}
