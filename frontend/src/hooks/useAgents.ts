import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { agentsService } from "@/services/agents";
import { toast } from "sonner";
import { AgentRequest } from "@/types/agent";

export function useAgents() {
  return useQuery({
    queryKey: ["agents"],
    queryFn: agentsService.findAll,
  });
}

export function useAgent(id: number) {
  return useQuery({
    queryKey: ["agents", id],
    queryFn: () => agentsService.findById(id),
  });
}

export function useCreateAgent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AgentRequest) => agentsService.create(data),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });

      toast.success("Agente criado com sucesso!");
    },

    onError: () => {
      toast.error("Erro ao criar agente");
    },
  });
}

export function useUpdateAgent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: AgentRequest }) =>
      agentsService.update(id, data),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });

      toast.success("Agente atualizado com sucesso!");
    },

    onError: () => {
      toast.error("Erro ao atualizar agente");
    },
  });
}

export function useDeleteAgent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => agentsService.delete(id),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });

      toast.success("Agente removido com sucesso!");
    },

    onError: () => {
      toast.error("Erro ao remover agente");
    },
  });
}
