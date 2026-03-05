import type { QueueTokenResponse, QueueStatusResponse } from '~/types/queue';

export const useQueueApi = () => {
  const { $api } = useNuxtApp();

  return {
    issueToken: async (concertId: number): Promise<QueueTokenResponse> => {
      const res = await $api.post('/queue/tokens', { concertId });
      return res.data;
    },
    getStatus: async (concertId: number, token: string): Promise<QueueStatusResponse> => {
      const res = await $api.get(`/queue/status?concertId=${concertId}`, {
        headers: { 'Queue-Token': token }
      });
      return res.data;
    }
  };
};
