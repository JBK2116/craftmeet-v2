"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as meetingsApi from "@/lib/api/meetings";
import type { MeetingRequest } from "@/lib/types/meeting";

export const meetingsKeys = {
  all: ["meetings"] as const,
  list: () => [...meetingsKeys.all, "list"] as const,
  detail: (id: string) => [...meetingsKeys.all, "detail", id] as const,
};

export function useMeetings() {
  return useQuery({
    queryKey: meetingsKeys.list(),
    queryFn: meetingsApi.getMeetings,
  });
}

export function useMeeting(id: string) {
  return useQuery({
    queryKey: meetingsKeys.detail(id),
    queryFn: () => meetingsApi.getMeeting(id),
    enabled: !!id,
  });
}

export function useDeleteMeeting() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: meetingsApi.deleteMeeting,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: meetingsKeys.all });
    },
  });
}

export function useCreateMeeting() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: meetingsApi.createMeeting,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: meetingsKeys.all });
    },
  });
}

export function useUpdateMeeting() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: MeetingRequest }) =>
      meetingsApi.updateMeeting(id, body),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: meetingsKeys.all });
    },
  });
}
