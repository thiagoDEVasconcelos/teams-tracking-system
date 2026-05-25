import { api } from "@/services/api";
import type { Geofence } from "@/types/geofence";

async function findAll(): Promise<Geofence[]> {
  const response = await api.get<Geofence[]>("/api/geofences");
  return response.data;
}

export const geofencesService = {
  findAll,
};