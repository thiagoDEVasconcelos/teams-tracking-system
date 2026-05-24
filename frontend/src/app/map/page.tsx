"use client";

import dynamic from "next/dynamic";
import { useAgents } from "@/hooks/useAgents";

const AgentMap = dynamic(
  () => import("@/components/map/AgentMap").then(m => m.AgentMap),
  { ssr: false, loading: () => <p>Carregando mapa...</p> }
);

export default function MapPage() {
  const { data: agents, isLoading } = useAgents();

  if (isLoading) return <p>Carregando agentes...</p>;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">Mapa em Tempo Real</h1>
      <AgentMap agents={agents ?? []} />
    </div>
  );
}