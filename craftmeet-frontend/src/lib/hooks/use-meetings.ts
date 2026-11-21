"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as meetingsApi from "@/lib/api/meetings";

export const meetingsKeys = {
  all: ["meetings"] as const,
  list: () => [...meetingsKeys.all, "list"] as const,
};

export function useMeetings() {
  return useQuery({
    queryKey: meetingsKeys.list(),
    queryFn: meetingsApi.getMeetings,
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
