"use client";

import { useCheckIns, useCreateCheckIn } from "@/hooks/useCheckIns";
import { useAgents } from "@/hooks/useAgents";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

const schema = z.object({
  agentId: z.coerce.number().min(1, "Selecione um agente"),
  latitude: z.coerce.number(),
  longitude: z.coerce.number(),
  notes: z.string().optional(),
});

type FormData = z.infer<typeof schema>;

export default function CheckInsPage() {
  const { data: checkIns, isLoading } = useCheckIns();
  const { data: agents } = useAgents();
  const createCheckIn = useCreateCheckIn();

const {
  register,
  handleSubmit,
  reset,
  formState: { errors },
} = useForm<FormData>({
  resolver: zodResolver(schema),
});

  function onSubmit(data: FormData) {
    createCheckIn.mutate(data, { onSuccess: () => reset() });
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-bold">Check-ins</h1>

      <Card>
        <CardHeader>
          <CardTitle>Registrar Check-in Manual</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-2 gap-4">
            <div>
              <Label>Agente</Label>
              <select {...register("agentId")} className="w-full border rounded px-3 py-2 text-sm">
                <option value="">Selecione...</option>
                {agents?.map(a => (
                  <option key={a.id} value={a.id}>{a.name}</option>
                ))}
              </select>
              {errors.agentId && <p className="text-red-500 text-sm">{errors.agentId.message}</p>}
            </div>
            <div>
              <Label>Latitude</Label>
              <Input {...register("latitude")} placeholder="-23.5505" />
            </div>
            <div>
              <Label>Longitude</Label>
              <Input {...register("longitude")} placeholder="-46.6333" />
            </div>
            <div>
              <Label>Observações</Label>
              <Input {...register("notes")} placeholder="Opcional" />
            </div>
            <div className="col-span-2">
              <Button type="submit" disabled={createCheckIn.isPending}>
                Registrar Check-in
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {isLoading ? <p>Carregando...</p> : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Agente</TableHead>
              <TableHead>Tipo</TableHead>
              <TableHead>Origem</TableHead>
              <TableHead>Endereço</TableHead>
              <TableHead>Data</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {checkIns?.map(c => (
              <TableRow key={c.id}>
                <TableCell>{c.agent?.name}</TableCell>
                <TableCell><Badge>{c.type}</Badge></TableCell>
                <TableCell>{c.source}</TableCell>
                <TableCell>{c.address ?? "—"}</TableCell>
                <TableCell>{new Date(c.occurredAt).toLocaleString("pt-BR")}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
}