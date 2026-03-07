import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useQueueApi } from '../composables/useQueueApi';
import { QueueStatus } from '~/types/queue';

export const useQueueStore = defineStore('queue', () => {
  const token = ref<string | null>(null);
  const position = ref(0);
  const status = ref<QueueStatus>(QueueStatus.WAITING);
  const estimatedMinutes = ref(0);

  const queueApi = useQueueApi();

  const enterQueue = async (concertId: number) => {
    const data = await queueApi.issueToken(concertId);
    token.value = data.token;
    position.value = data.position || 0;
    status.value = data.status || QueueStatus.WAITING;
  };

  const pollStatus = async (concertId: number) => {
    if (!token.value) return;
    const data = await queueApi.getStatus(concertId, token.value);
    position.value = data.position || 0;
    status.value = data.status || QueueStatus.WAITING;
    estimatedMinutes.value = Math.max(0, Math.ceil(position.value / 100));
  };

  const clearQueue = () => {
    token.value = null;
    position.value = 0;
    status.value = QueueStatus.WAITING;
    estimatedMinutes.value = 0;
  };

  return { token, position, status, estimatedMinutes, enterQueue, pollStatus, clearQueue };
});
