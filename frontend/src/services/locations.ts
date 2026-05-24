import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080" });

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