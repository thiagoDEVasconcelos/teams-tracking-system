"use client";

import { useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup, Polyline, Polygon, Circle, useMap } from "react-leaflet";

import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { Agent } from "@/types/agent";
import { RoutePoint } from "@/types/location";
import { Geofence } from "@/types/geofence";

type LeafletDefaultIconPrototype = L.Icon.Default & {
  _getIconUrl?: unknown;
};

delete (L.Icon.Default.prototype as LeafletDefaultIconPrototype)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

function MapFocus({ agent }: { agent: Agent | null }) {
  const map = useMap();
  useEffect(() => {
    if (agent?.latitude && agent?.longitude) {
      map.flyTo([agent.latitude, agent.longitude], 15, { duration: 1 });
    }
  }, [agent, map]);
  return null;
}

function parseCoordinates(coordinates: string, type: string) {
  try {
    const parsed = JSON.parse(coordinates);

    if (type === "CIRCLE") {
      return {
        center: [parsed.center[1], parsed.center[0]] as [number, number],
        radius: parsed.radius
      };
    }

    if (type === "POLYGON") {
      return {
        positions: parsed.map((coord: number[]) => [coord[1], coord[0]] as [number, number])
      };
    }
  } catch {
    return null;
  }
  return null;
}

interface Props {
  agents: Agent[];
  selectedAgent: Agent | null;
  onSelectAgent: (agent: Agent) => void;
  routePoints: RoutePoint[];
  geofences: Geofence[];
}

export function AgentMap({ agents, selectedAgent, onSelectAgent, routePoints, geofences }: Props) {
  const agentsWithLocation = agents.filter(a => a.latitude && a.longitude);

  const center: [number, number] = agentsWithLocation.length > 0
    ? [agentsWithLocation[0].latitude, agentsWithLocation[0].longitude]
    : [-23.5505, -46.6333];

  const routeCoords: [number, number][] = routePoints.map(p => [p.latitude, p.longitude]);

  return (
    <MapContainer center={center} zoom={12} style={{ height: "100%", width: "100%" }}>
      <TileLayer
        attribution='&copy; OpenStreetMap contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <MapFocus agent={selectedAgent} />

      {agentsWithLocation.map(agent => (
        <Marker
          key={agent.id}
          position={[agent.latitude, agent.longitude]}
          eventHandlers={{ click: () => onSelectAgent(agent) }}
        >
          <Popup>
            <div className="text-sm">
              <strong>{agent.name}</strong><br />
              {agent.role} — {agent.team}<br />
              Status: {agent.status}<br />
              Bateria: {agent.battery}%
            </div>
          </Popup>
        </Marker>
      ))}

      {routeCoords.length > 1 && (
        <Polyline positions={routeCoords} color="blue" weight={3} opacity={0.7} />
      )}

      {geofences.map(geofence => {
        if (!geofence.coordinates) {
          return null
        };
        
        const parsed = parseCoordinates(geofence.coordinates, geofence.type);
        if (!parsed) {
          return null;
        }
        if (geofence.type === "CIRCLE" && parsed.center && parsed.radius) {
          return (
            <Circle
              key={geofence.id}
              center={parsed.center}
              radius={parsed.radius}
              pathOptions={{
                color: "orange",
                fillColor: "orange",
                fillOpacity: 0.15,
                weight: 2,
              }}
            >
              <Popup>
                <div className="text-sm">
                  <strong>{geofence.name}</strong><br />
                  Tipo: Círculo<br />
                  {geofence.alertOnEnter && <span>🔔 Alerta ao entrar<br /></span>}
                  {geofence.alertOnExit && <span>🔔 Alerta ao sair</span>}
                </div>
              </Popup>
            </Circle>
          );
        }

        if (geofence.type === "POLYGON" && parsed.positions) {
          return (
            <Polygon
              key={geofence.id}
              positions={parsed.positions}
              pathOptions={{
                color: "green",
                fillColor: "green",
                fillOpacity: 0.15,
                weight: 2,
              }}
            >
              <Popup>
                <div className="text-sm">
                  <strong>{geofence.name}</strong><br />
                  Tipo: Polígono<br />
                  {geofence.alertOnEnter && <span>🔔 Alerta ao entrar<br /></span>}
                  {geofence.alertOnExit && <span>🔔 Alerta ao sair</span>}
                </div>
              </Popup>
            </Polygon>
          );
        }

        return null;
      })}
    </MapContainer>
  );
}
