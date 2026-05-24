import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { syncService } from "@/services/sync";
import { toast } from "sonner";

export function useSyncLogs() {
  return useQuery({
    queryKey: ["sync-logs"],
    queryFn: syncService.getLogs,
    refetchInterval: 15000,
  });
}

export function useSyncAgents() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: syncService.syncAgents,

    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
      queryClient.invalidateQueries({ queryKey: ["sync-logs"] });

      toast.success(`Sincronização concluída: ${data.synced} agentes`);
    },

    onError: () => {
      toast.error("Erro ao sincronizar agentes");
    },
  });
}

export function useSyncLocations() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: syncService.syncLocations,

    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });

      toast.success(`Localizações sincronizadas: ${data.synced}`);
    },

    onError: () => {
      toast.error("Erro ao sincronizar localizações");
    },
  });
}