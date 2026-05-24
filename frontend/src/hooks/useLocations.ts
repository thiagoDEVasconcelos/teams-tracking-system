import { useQuery } from "@tanstack/react-query";
import { locationsService } from "@/services/locations";

export function useAgentRoute(agentId: number, date: string) {
  return useQuery({
    queryKey: ["route", agentId, date],
    queryFn: () => locationsService.getAgentRoute(agentId, date),
    enabled: !!agentId && !!date,
  });
}