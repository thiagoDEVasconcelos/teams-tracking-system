import { api } from "@/services/api";
import type { RoutePoint } from "@/types/location";

async function getAgentRoute(
  agentId: number,
  date: string
): Promise<RoutePoint[]> {
  const response = await api.get<RoutePoint[]>(
    `/api/locations/agent/${agentId}/route?date=${date}`
  );

  return response.data;
}

export const locationsService = {
  getAgentRoute,
};