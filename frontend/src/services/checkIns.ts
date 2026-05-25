import { api } from "@/services/api";

export interface CheckIn {
  id: number;
  agent: { id: number; name: string };
  type: string;
  source: string;
  latitude: number;
  longitude: number;
  address: string;
  notes: string;
  distanceFromPrevious: number | null;
  occurredAt: string;
}

export interface CheckInRequest {
  agentId: number;
  latitude: number;
  longitude: number;
  notes?: string;
  address?: string;
}

export const checkInsService = {
  findAll: () => api.get<CheckIn[]>("/api/check-ins").then(r => r.data),
  findByAgent: (agentId: number) => api.get<CheckIn[]>(`/api/check-ins/agent/${agentId}`).then(r => r.data),
  create: (data: CheckInRequest) => api.post<CheckIn>("/api/check-ins", data).then(r => r.data),
};
