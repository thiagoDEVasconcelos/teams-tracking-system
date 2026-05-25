"use client";

import { useEffect, useRef } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { API_BASE_URL } from "@/services/api";

type RealtimeEvent = {
  type: string;
  resource:
    | "agents"
    | "locations"
    | "check-ins"
    | "geofences"
    | "sync-logs"
    | "system";
  occurredAt: string;
};

const QUERY_KEYS_BY_RESOURCE: Partial<Record<RealtimeEvent["resource"], string[][]>> = {
  agents: [["agents"]],
  locations: [["agents"], ["route"]],
  "check-ins": [["check-ins"]],
  geofences: [["geofences"]],
  "sync-logs": [["sync-logs"]],
};

export function useRealtimeEvents() {
  const queryClient = useQueryClient();
  const warnedAboutConnection = useRef(false);

  useEffect(() => {
    const events = new EventSource(`${API_BASE_URL}/api/events`);

    events.addEventListener("realtime", event => {
      let payload: RealtimeEvent;

      try {
        payload = JSON.parse(event.data) as RealtimeEvent;
      } catch {
        return;
      }

      const queryKeys = QUERY_KEYS_BY_RESOURCE[payload.resource] ?? [];

      queryKeys.forEach(queryKey => {
        queryClient.invalidateQueries({ queryKey });
      });
    });

    events.onerror = () => {
      if (warnedAboutConnection.current) return;
      warnedAboutConnection.current = true;
      toast.warning("Conexão em tempo real instável. Tentando reconectar...");
    };

    return () => events.close();
  }, [queryClient]);
}
