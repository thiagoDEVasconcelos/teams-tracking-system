import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export interface Agent {
  id: number;
  externalId: string;
  name: string;
  role: string;
  team: string;
  phone: string;
  email: string;
  active: boolean;
  status: string;
  battery: number;
  latitude: number;
  longitude: number;
  currentAddress: string;
  lastSeen: string;
}

export interface AgentRequest {
  name: string;
  role: string;
  team: string;
  phone: string;
  email: string;
}

export const agentsService = {
  findAll: () => api.get<Agent[]>("/api/agents").then(r => r.data),
  findById: (id: number) => api.get<Agent>(`/api/agents/${id}`).then(r => r.data),
  create: (data: AgentRequest) => api.post<Agent>("/api/agents", data).then(r => r.data),
  update: (id: number, data: AgentRequest) => api.put<Agent>(`/api/agents/${id}`, data).then(r => r.data),
  delete: (id: number) => api.delete(`/api/agents/${id}`),
};