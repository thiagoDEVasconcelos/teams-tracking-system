export type Agent = {
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
};

export type AgentRequest = {
  name: string;
  role: string;
  team: string;
  phone: string;
  email: string;
};