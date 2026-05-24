"use client";

import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { Agent } from "@/services/agents";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

interface Props {
  agents: Agent[];
}

export function AgentMap({ agents }: Props) {
  const agentsWithLocation = agents.filter(a => a.latitude && a.longitude);
  const center = agentsWithLocation.length > 0
    ? [agentsWithLocation[0].latitude, agentsWithLocation[0].longitude] as [number, number]
    : [-23.5505, -46.6333] as [number, number];

  return (
    <MapContainer center={center} zoom={12} style={{ height: "500px", width: "100%" }}>
      <TileLayer
        attribution='&copy; OpenStreetMap contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {agentsWithLocation.map(agent => (
        <Marker key={agent.id} position={[agent.latitude, agent.longitude]}>
          <Popup>
            <div>
              <strong>{agent.name}</strong><br />
              {agent.role} — {agent.team}<br />
              Status: {agent.status}<br />
              Bateria: {agent.battery}%<br />
              {agent.currentAddress && <span>{agent.currentAddress}</span>}
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}