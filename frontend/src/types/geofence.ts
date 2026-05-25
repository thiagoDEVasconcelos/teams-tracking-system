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