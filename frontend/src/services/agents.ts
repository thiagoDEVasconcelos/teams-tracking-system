import { api } from "@/services/api";
import type {
  Agent, AgentRequest
} from "@/types/agent";

async function findAll(): Promise<Agent[]> {
  const response = await api.get<Agent[]>("/api/agents");
  return response.data;
}

async function findById(id: number): Promise<Agent> {
  const response = await api.get<Agent>(`/api/agents/${id}`);
  return response.data;
}

async function create(data: AgentRequest): Promise<Agent> {
  const response = await api.post<Agent>("/api/agents", data);
  return response.data;
}

async function update(
  id: number,
  data: AgentRequest
): Promise<Agent> {
  const response = await api.put<Agent>(`/api/agents/${id}`, data);
  return response.data;
}

async function remove(id: number): Promise<void> {
  await api.delete(`/api/agents/${id}`);
}

export const agentsService = {
  findAll,
  findById,
  create,
  update,
  delete: remove,
};