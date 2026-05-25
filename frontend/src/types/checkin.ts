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