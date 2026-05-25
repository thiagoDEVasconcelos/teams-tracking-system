"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "sonner";
import { useState } from "react";
import { useRealtimeEvents } from "@/hooks/useRealtimeEvents";

function RealtimeEvents() {
  useRealtimeEvents();
  return null;
}

export function Providers({
  children,
}: {
  children: React.ReactNode;
}) {
  const [queryClient] = useState(() => new QueryClient());

  return (
    <QueryClientProvider client={queryClient}>
      <RealtimeEvents />
      {children}
      <Toaster richColors position="top-right" />
    </QueryClientProvider>
  );
}
