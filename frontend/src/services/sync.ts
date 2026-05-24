import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080" });

export interface SyncLog {
  id: number;
  schedulerName: string;
  status: string;
  recordsProcessed: number;
  errorMessage: string;
  syncToken: string;
  executedAt: string;
}

export const syncService = {
  getLogs: () => api.get<SyncLog[]>("/api/sync/logs").then(r => r.data),
  syncAgents: () => api.post("/api/sync/agents").then(r => r.data),
  syncLocations: () => api.post("/api/sync/locations").then(r => r.data),
  syncCheckIns: () => api.post("/api/sync/check-ins").then(r => r.data),
  syncGeofences: () => api.post("/api/sync/geofences").then(r => r.data),
};