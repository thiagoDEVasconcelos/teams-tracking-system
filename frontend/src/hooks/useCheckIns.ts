import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { checkInsService, CheckInRequest } from "@/services/checkIns";
import { toast } from "sonner";

export function useCheckIns() {
  return useQuery({
    queryKey: ["check-ins"],
    queryFn: checkInsService.findAll,
  });
}

export function useCreateCheckIn() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CheckInRequest) => checkInsService.create(data),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["check-ins"] });

      toast.success("Check-in registrado com sucesso!");
    },

    onError: () => {
      toast.error("Erro ao registrar check-in");
    },
  });
}