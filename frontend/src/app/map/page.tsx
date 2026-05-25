"use client";

import dynamic from "next/dynamic";
import { useState } from "react";
import { useAgents } from "@/hooks/useAgents";
import { useAgentRoute } from "@/hooks/useLocations";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Battery, MapPin, Wifi } from "lucide-react";
import { useGeofences } from "@/hooks/useGeofences";

const AgentMap = dynamic(
  () => import("@/components/map/AgentMap").then(m => m.AgentMap),
  { ssr: false, loading: () => <p>Carregando mapa...</p> }
);

export default function MapPage() {
  const { data: agents, isLoading } = useAgents();
  const [selectedAgentId, setSelectedAgentId] = useState<number | null>(null);
  const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
  const { data: geofences } = useGeofences();
  const selectedAgent = agents?.find(agent => agent.id === selectedAgentId) ?? null;

  const { data: route } = useAgentRoute(
    selectedAgent?.id ?? 0,
    date
  );

  if (isLoading) return <p>Carregando agentes...</p>;

  return (
    <div className="flex gap-4 h-[calc(100vh-120px)]">

      <div className="w-80 flex flex-col gap-4 overflow-y-auto">

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-base">Agentes</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-2 p-3">
            {agents?.map(agent => (
              <button
                key={agent.id}
                onClick={() => setSelectedAgentId(agent.id)}
                className={`w-full text-left p-3 rounded-lg border transition-colors hover:bg-muted ${
                  selectedAgent?.id === agent.id ? "border-primary bg-muted" : "border-border"
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-medium text-sm">{agent.name}</span>
                  <Badge variant={agent.status === "ONLINE" ? "default" : "secondary"} className="text-xs">
                    {agent.status}
                  </Badge>
                </div>
                <div className="flex items-center gap-3 mt-1">
                  <span className="text-xs text-muted-foreground">{agent.role}</span>
                  <span className="text-xs text-muted-foreground">🔋 {agent.battery}%</span>
                </div>
              </button>
            ))}
          </CardContent>
        </Card>

        {selectedAgent && (
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-base">{selectedAgent.name}</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-3 text-sm">
              <div className="flex items-center gap-2">
                <Wifi className="w-4 h-4 text-muted-foreground" />
                <span>Status: </span>
                <Badge variant={selectedAgent.status === "ONLINE" ? "default" : "secondary"}>
                  {selectedAgent.status}
                </Badge>
              </div>
              <div className="flex items-center gap-2">
                <Battery className="w-4 h-4 text-muted-foreground" />
                <span>Bateria: {selectedAgent.battery}%</span>
              </div>
              {selectedAgent.currentAddress && (
                <div className="flex items-start gap-2">
                  <MapPin className="w-4 h-4 text-muted-foreground mt-0.5" />
                  <span className="text-xs">{selectedAgent.currentAddress}</span>
                </div>
              )}
              {selectedAgent.latitude && (
                <div className="text-xs text-muted-foreground">
                  <p>Lat: {selectedAgent.latitude}</p>
                  <p>Lng: {selectedAgent.longitude}</p>
                </div>
              )}
              {selectedAgent.lastSeen && (
                <p className="text-xs text-muted-foreground">
                  Última vez visto: {new Date(selectedAgent.lastSeen).toLocaleString("pt-BR")}
                </p>
              )}

              <div className="mt-2">
                <Label className="text-xs">Rota do dia</Label>
                <Input
                  type="date"
                  value={date}
                  onChange={e => setDate(e.target.value)}
                  className="mt-1 text-sm"
                />
              </div>

              {route && route.length > 0 && (
                <div className="mt-2">
                  <p className="text-xs font-medium mb-2">{route.length} pontos registrados</p>
                  <div className="flex flex-col gap-1 max-h-48 overflow-y-auto">
                    {route.map((point, i) => (
                      <div key={i} className="text-xs border rounded p-2">
                        <p className="font-medium">
                          {new Date(point.recordedAt).toLocaleTimeString("pt-BR")}
                        </p>
                        <p className="text-muted-foreground">
                          {point.latitude.toFixed(5)}, {point.longitude.toFixed(5)}
                        </p>
                        {point.speed && (
                          <p className="text-muted-foreground">{point.speed} km/h</p>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {route && route.length === 0 && (
                <p className="text-xs text-muted-foreground">
                  Nenhum ponto registrado nessa data.
                </p>
              )}
            </CardContent>
          </Card>
        )}
      </div>

      <div className="flex-1 rounded-lg overflow-hidden border">
        <AgentMap
          agents={agents ?? []}
          selectedAgent={selectedAgent}
          onSelectAgent={agent => setSelectedAgentId(agent.id)}
          routePoints={route ?? []}
          geofences={geofences ?? []}
        />
      </div>
    </div>
  );
}
