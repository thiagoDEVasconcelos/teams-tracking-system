import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080" });

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