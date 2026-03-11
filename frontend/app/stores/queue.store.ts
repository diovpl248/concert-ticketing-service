import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useQueueApi } from '../composables/useQueueApi';
import { QueueStatus } from '~/types/queue';

const POLLING_INTERVAL_SEC = 3;
const EMA_ALPHA = 0.3;

export const useQueueStore = defineStore('queue', () => {
  const token = ref<string | null>(null);
  const position = ref(0);
  const status = ref<QueueStatus>(QueueStatus.WAITING);
  const estimatedMinutes = ref(0);

  // EMA 내부 상태
  let prevPosition: number | null = null;
  let smoothedRate: number | null = null;

  const queueApi = useQueueApi();

  const enterQueue = async (concertId: number) => {
    const data = await queueApi.issueToken(concertId);
    token.value = data.token;
    position.value = data.position || 0;
    status.value = data.status || QueueStatus.WAITING;

    // 초기값 설정
    prevPosition = position.value;
    smoothedRate = null;
  };

  const pollStatus = async (concertId: number) => {
    if (!token.value) return;
    const data = await queueApi.getStatus(concertId, token.value);

    const newPosition = data.position || 0;
    status.value = data.status || QueueStatus.WAITING;

    // 처리 속도(rate) 계산: 폴링 간격 동안 줄어든 인원 수 (명/초)
    if (prevPosition !== null && prevPosition > newPosition) {
      const currentRate = (prevPosition - newPosition) / POLLING_INTERVAL_SEC;

      if (smoothedRate === null) {
        smoothedRate = currentRate;
      } else {
        // EMA: 이전 값에 가중치를 두어 완만하게 변화
        smoothedRate = EMA_ALPHA * currentRate + (1 - EMA_ALPHA) * smoothedRate;
      }

      if (smoothedRate > 0) {
        const estimatedSec = newPosition / smoothedRate;
        estimatedMinutes.value = Math.max(1, Math.ceil(estimatedSec / 60));
      }
    } else if (newPosition > 0 && smoothedRate === null) {
      // 아직 rate 데이터가 없을 때 기본 추정치
      estimatedMinutes.value = Math.max(1, Math.ceil(newPosition / 100));
    }

    prevPosition = newPosition;
    position.value = newPosition;
  };

  const clearQueue = () => {
    token.value = null;
    position.value = 0;
    status.value = QueueStatus.WAITING;
    estimatedMinutes.value = 0;
    prevPosition = null;
    smoothedRate = null;
  };

  return { token, position, status, estimatedMinutes, enterQueue, pollStatus, clearQueue };
});
