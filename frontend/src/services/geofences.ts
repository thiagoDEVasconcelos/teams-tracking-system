import { api } from "@/services/api";

export interface Geofence {
  id: number;
  externalId: string;
  name: string;
  type: string;
  coordinates: string;
  alertOnEnter: boolean;
  alertOnExit: boolean;
  teams: string;
}

export const geofencesService = {
  findAll: () => api.get<Geofence[]>("/api/geofences").then(r => r.data),
};
