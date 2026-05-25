"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useCreateAgent, useUpdateAgent } from "@/hooks/useAgents";
import { Agent } from "@/types/agent";

const schema = z.object({
  name: z.string().min(1, "Nome é obrigatório"),
  role: z.string().min(1, "Função é obrigatória"),
  team: z.string().min(1, "Equipe é obrigatória"),
  phone: z.string().min(1, "Telefone é obrigatório"),
  email: z.string().email("Email inválido"),
});

type FormData = z.infer<typeof schema>;

interface Props {
  agent?: Agent | null;
  onSuccess: () => void;
}

export function AgentForm({ agent, onSuccess }: Props) {
  const createAgent = useCreateAgent();
  const updateAgent = useUpdateAgent();

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: agent ?? {},
  });

  function onSubmit(data: FormData) {
    if (agent) {
      updateAgent.mutate({ id: agent.id, data }, { onSuccess });
    } else {
      createAgent.mutate(data, { onSuccess });
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
      <div>
        <Label>Nome</Label>
        <Input {...register("name")} />
        {errors.name && <p className="text-red-500 text-sm">{errors.name.message}</p>}
      </div>
      <div>
        <Label>Função</Label>
        <Input {...register("role")} />
        {errors.role && <p className="text-red-500 text-sm">{errors.role.message}</p>}
      </div>
      <div>
        <Label>Equipe</Label>
        <Input {...register("team")} />
        {errors.team && <p className="text-red-500 text-sm">{errors.team.message}</p>}
      </div>
      <div>
        <Label>Telefone</Label>
        <Input {...register("phone")} />
        {errors.phone && <p className="text-red-500 text-sm">{errors.phone.message}</p>}
      </div>
      <div>
        <Label>Email</Label>
        <Input {...register("email")} />
        {errors.email && <p className="text-red-500 text-sm">{errors.email.message}</p>}
      </div>
      <Button type="submit" disabled={createAgent.isPending || updateAgent.isPending}>
        {agent ? "Salvar alterações" : "Criar agente"}
      </Button>
    </form>
  );
}