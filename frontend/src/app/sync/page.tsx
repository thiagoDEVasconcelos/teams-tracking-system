"use client";

import { useState } from "react";
import { useSyncLogs, useSyncAgents, useSyncLocations, useSyncCheckIns, useSyncGeofences } from "@/hooks/useSyncLogs";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { CheckCircle, XCircle, RefreshCw } from "lucide-react";

const SCHEDULERS = ["AGENT_SYNC", "LOCATION_SYNC", "CHECKIN_SYNC", "GEOFENCE_SYNC"];

const SCHEDULERS_LABELS: Record<string, string> = {
  AGENT_SYNC: "Agentes",
  LOCATION_SYNC: "Localizações",
  CHECKIN_SYNC: "Check-ins",
  GEOFENCE_SYNC: "Geofences",
};

const PAGE_SIZE = 10;

export default function SyncPage() {
  const { data: logs, isLoading } = useSyncLogs();
  const syncAgents = useSyncAgents();
  const syncLocations = useSyncLocations();
  const syncCheckIns = useSyncCheckIns();
  const syncGeofences = useSyncGeofences();

  const [page, setPage] = useState(1);

  function getLatestLog(schedulerName: string) {
    return logs
      ?.filter(l => l.schedulerName === schedulerName)
      .sort((a, b) => new Date(b.executedAt).getTime() - new Date(a.executedAt).getTime())[0];
  }

  const sortedLogs = logs
    ?.slice()
    .sort((a, b) => new Date(b.executedAt).getTime() - new Date(a.executedAt).getTime()) ?? [];

  const totalPages = Math.ceil(sortedLogs.length / PAGE_SIZE);
  const paginated = sortedLogs.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-bold">Monitoramento de Sincronização</h1>
        <p className="text-sm text-muted-foreground">
          Status em tempo real dos schedulers automáticos
        </p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {SCHEDULERS.map(name => {
          const latest = getLatestLog(name);
          const isSuccess = latest?.status === "SUCCESS";
          return (
            <Card key={name}>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium">
                  {SCHEDULERS_LABELS[name]}
                </CardTitle>
                {latest ? (
                  isSuccess
                    ? <CheckCircle className="w-4 h-4 text-green-500" />
                    : <XCircle className="w-4 h-4 text-red-500" />
                ) : (
                  <RefreshCw className="w-4 h-4 text-muted-foreground" />
                )}
              </CardHeader>
              <CardContent>
                {latest ? (
                  <>
                    <Badge variant={isSuccess ? "default" : "destructive"} className="mb-2">
                      {latest.status}
                    </Badge>
                    <p className="text-xs text-muted-foreground">
                      {latest.recordsProcessed} registros
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {new Date(latest.executedAt).toLocaleString("pt-BR")}
                    </p>
                    {latest.errorMessage && (
                      <p className="text-xs text-red-500 mt-1 truncate">
                        {latest.errorMessage}
                      </p>
                    )}
                  </>
                ) : (
                  <p className="text-xs text-muted-foreground">Nenhuma execução ainda</p>
                )}
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="flex gap-3 flex-wrap">
      <Button onClick={() => syncAgents.mutate()} disabled={syncAgents.isPending}>
        {syncAgents.isPending ? "Sincronizando..." : "Sincronizar Agentes"}
      </Button>
      <Button variant="outline" onClick={() => syncLocations.mutate()} disabled={syncLocations.isPending}>
        {syncLocations.isPending ? "Sincronizando..." : "Sincronizar Localizações"}
      </Button>
      <Button variant="outline" onClick={() => syncCheckIns.mutate()} disabled={syncCheckIns.isPending}>
        {syncCheckIns.isPending ? "Sincronizando..." : "Sincronizar Check-ins"}
      </Button>
      <Button variant="outline" onClick={() => syncGeofences.mutate()} disabled={syncGeofences.isPending}>
        {syncGeofences.isPending ? "Sincronizando..." : "Sincronizar Geofences"}
      </Button>
    </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">
            Histórico de Execuções
            {sortedLogs.length > 0 && (
              <span className="text-sm font-normal text-muted-foreground ml-2">
                ({sortedLogs.length} registros)
              </span>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Carregando...</p>
          ) : (
            <>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Scheduler</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Registros</TableHead>
                    <TableHead>Executado em</TableHead>
                    <TableHead>Erro</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {paginated.map(log => (
                    <TableRow key={log.id}>
                      <TableCell className="font-medium">{log.schedulerName}</TableCell>
                      <TableCell>
                        <Badge variant={log.status === "SUCCESS" ? "default" : "destructive"}>
                          {log.status}
                        </Badge>
                      </TableCell>
                      <TableCell>{log.recordsProcessed}</TableCell>
                      <TableCell>
                        {new Date(log.executedAt).toLocaleString("pt-BR")}
                      </TableCell>
                      <TableCell className="text-red-500 text-sm max-w-xs truncate">
                        {log.errorMessage ?? "—"}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              {totalPages > 1 && (
                <div className="flex items-center justify-between mt-4">
                  <p className="text-sm text-muted-foreground">
                    Página {page} de {totalPages}
                  </p>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => Math.max(1, p - 1))}
                      disabled={page === 1}
                    >
                      Anterior
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => Math.min(totalPages, p + 1))}
                      disabled={page === totalPages}
                    >
                      Próxima
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}