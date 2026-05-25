import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { syncService } from "@/services/sync";
import { toast } from "sonner";

export function useSyncLogs() {
  return useQuery({
    queryKey: ["sync-logs"],
    queryFn: syncService.getLogs,
  });
}

export function useSyncAgents() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: syncService.syncAgents,

    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
      queryClient.invalidateQueries({ queryKey: ["sync-logs"] });

      toast.success(`${data.synced} agentes atualizados`);
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

      toast.success(`${data.synced} localizações atualizadas`);
    },

    onError: () => {
      toast.error("Erro ao sincronizar localizações");
    },
  });
}

export function useSyncCheckIns() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: syncService.syncCheckIns,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["check-ins"] });
      queryClient.invalidateQueries({ queryKey: ["sync-logs"] });
      data.synced > 0 
        ? toast.success(`${data.synced} novos check-ins sincronizados`)
        : toast.info(`Nenhum check-in novo desde a última sincronização`);
    },
    onError: () => toast.error("Erro ao sincronizar check-ins"),
  });
}

export function useSyncGeofences() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: syncService.syncGeofences,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["sync-logs"] });
      toast.success(`${data.synced} geofences atualizadas`);
    },
    onError: () => toast.error("Erro ao sincronizar geofences"),
  });
}
