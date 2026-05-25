import { api } from "@/services/api";
import type { SyncLog } from "@/types/sync";

async function getLogs(): Promise<SyncLog[]> {
  const response = await api.get<SyncLog[]>("/api/sync/logs");
  return response.data;
}

async function syncAgents() {
  const response = await api.post("/api/sync/agents");
  return response.data;
}

async function syncLocations() {
  const response = await api.post("/api/sync/locations");
  return response.data;
}

async function syncCheckIns() {
  const response = await api.post("/api/sync/check-ins");
  return response.data;
}

async function syncGeofences() {
  const response = await api.post("/api/sync/geofences");
  return response.data;
}

export const syncService = {
  getLogs,
  syncAgents,
  syncLocations,
  syncCheckIns,
  syncGeofences,
};