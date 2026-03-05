import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useQueueApi } from '../composables/useQueueApi';

export const useQueueStore = defineStore('queue', () => {
  const token = ref<string | null>(null);
  const position = ref(0);
  const status = ref('WAITING');
  const estimatedMinutes = ref(0);

  const enterQueue = async (concertId: number) => {
    const queueApi = useQueueApi();
    const data = await queueApi.issueToken(concertId);
    token.value = data.token;
    position.value = data.waitingCount || 0;
    status.value = data.status || 'WAITING';
  };

  const pollStatus = async (concertId: number) => {
    if (!token.value) return;
    const queueApi = useQueueApi();
    const data = await queueApi.getStatus(concertId, token.value);
    position.value = data.waitingCount || 0;
    status.value = data.status || 'WAITING';
    estimatedMinutes.value = Math.max(0, Math.ceil(position.value / 100));
  };

  const clearQueue = () => {
    token.value = null;
    position.value = 0;
    status.value = 'WAITING';
    estimatedMinutes.value = 0;
  };

  return { token, position, status, estimatedMinutes, enterQueue, pollStatus, clearQueue };
});
