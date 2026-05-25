import { useQuery } from "@tanstack/react-query";
import { geofencesService } from "@/services/geofences";

export function useGeofences() {
  return useQuery({
    queryKey: ["geofences"],
    queryFn: geofencesService.findAll,
  });
}
