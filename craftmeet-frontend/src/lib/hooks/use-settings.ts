"use client";

import {
  useMutation,
  useQuery,
  useQueryClient,
  type UseMutationResult,
  type UseQueryResult,
} from "@tanstack/react-query";
import * as meetingsApi from "@/lib/api/meetings";
import * as usersApi from "@/lib/api/users";
import { useAuth } from "@/lib/auth/auth-provider";
import { meetingsKeys } from "@/lib/hooks/use-meetings";
import type { ApiError } from "@/lib/types/errors";
import type { StatLifeTime } from "@/lib/types/meeting";
import type { User } from "@/lib/types/user";

export const statsKeys = {
  all: ["stats"] as const,
};

export function useStats(): UseQueryResult<StatLifeTime, ApiError> {
  return useQuery<StatLifeTime, ApiError>({
    queryKey: statsKeys.all,
    queryFn: meetingsApi.getStats,
  });
}

export function useUpdateUsername(): UseMutationResult<
  User,
  ApiError,
  { id: string; newUsername: string }
> {
  const { updateUser } = useAuth();
  return useMutation<User, ApiError, { id: string; newUsername: string }>({
    mutationFn: ({ id, newUsername }) =>
      usersApi.updateUsername(id, newUsername),
    onSuccess: (user) => updateUser(user),
  });
}

export function useDeleteAccount(): UseMutationResult<void, ApiError, string> {
  return useMutation<void, ApiError, string>({
    mutationFn: usersApi.deleteUser,
  });
}

export function useDeleteAllMeetings(): UseMutationResult<
  void,
  ApiError,
  void
> {
  const queryClient = useQueryClient();
  return useMutation<void, ApiError, void>({
    mutationFn: meetingsApi.deleteAllMeetings,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: meetingsKeys.all });
      void queryClient.invalidateQueries({ queryKey: statsKeys.all });
    },
  });
}
