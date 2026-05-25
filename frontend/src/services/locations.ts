import { api } from "@/services/api";

export interface RoutePoint {
  id: number;
  latitude: number;
  longitude: number;
  accuracy: number;
  speed: number;
  recordedAt: string;
}

export const locationsService = {
  getAgentRoute: (agentId: number, date: string) =>
    api.get<RoutePoint[]>(`/api/locations/agent/${agentId}/route?date=${date}`).then(r => r.data),
};
