import { api } from "@/services/api";
import type {
  CheckIn,
  CheckInRequest,
} from "@/types/checkin";

async function findAll(): Promise<CheckIn[]> {
  const response = await api.get<CheckIn[]>("/api/check-ins");
  return response.data;
}

async function findByAgent(agentId: number): Promise<CheckIn[]> {
  const response = await api.get<CheckIn[]>(
    `/api/check-ins/agent/${agentId}`
  );

  return response.data;
}

async function create(
  data: CheckInRequest
): Promise<CheckIn> {
  const response = await api.post<CheckIn>(
    "/api/check-ins",
    data
  );

  return response.data;
}

export const checkInsService = {
  findAll,
  findByAgent,
  create,
};