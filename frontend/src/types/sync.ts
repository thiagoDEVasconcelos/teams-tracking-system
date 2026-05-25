export interface SyncLog {
  id: number;
  schedulerName: string;
  status: string;
  recordsProcessed: number;
  errorMessage: string;
  syncToken: string;
  executedAt: string;
}