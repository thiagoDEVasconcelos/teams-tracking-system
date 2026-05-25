"use client";

import { useState } from "react";
import { useAgents, useDeleteAgent } from "@/hooks/useAgents";
import { AgentForm } from "@/components/agents/AgentForm";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Pencil, Trash2, Plus } from "lucide-react";
import { Agent } from "@/types/agent";

export default function AgentsPage() {
  const { data: agents, isLoading } = useAgents();
  const deleteAgent = useDeleteAgent();
  const [open, setOpen] = useState(false);
  const [selected, setSelected] = useState<Agent | null>(null);

  function handleEdit(agent: Agent) {
    setSelected(agent);
    setOpen(true);
  }

  function handleCreate() {
    setSelected(null);
    setOpen(true);
  }

  if (isLoading) {
    return <p>Carregando agentes...</p>;
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Agentes</h1>
        <Button onClick={handleCreate}>
          <Plus className="w-4 h-4 mr-2" /> Novo Agente
        </Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Nome</TableHead>
            <TableHead>Função</TableHead>
            <TableHead>Equipe</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Bateria</TableHead>
            <TableHead>Ações</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {agents?.map(agent => (
            <TableRow key={agent.id}>
              <TableCell>{agent.name}</TableCell>
              <TableCell>{agent.role}</TableCell>
              <TableCell>{agent.team}</TableCell>
              <TableCell>
                <Badge variant={agent.status === "ONLINE" ? "default" : "secondary"}>
                  {agent.status}
                </Badge>
              </TableCell>
              <TableCell>{agent.battery}%</TableCell>
              <TableCell className="flex gap-2">
                <Button size="sm" variant="outline" onClick={() => handleEdit(agent)}>
                  <Pencil className="w-4 h-4" />
                </Button>
                <Button size="sm" variant="destructive" onClick={() => deleteAgent.mutate(agent.id)}>
                  <Trash2 className="w-4 h-4" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{selected ? "Editar Agente" : "Novo Agente"}</DialogTitle>
          </DialogHeader>
          <AgentForm agent={selected} onSuccess={() => setOpen(false)} />
        </DialogContent>
      </Dialog>
    </div>
  );
}