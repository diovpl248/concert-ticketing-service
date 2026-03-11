<template>
  <div class="flex flex-col min-h-screen">
    <AppHeader title="대기 상태" :showBack="true" backPath="/" />

    <main class="flex-1 px-6 pt-6 pb-10 flex flex-col items-center max-w-md mx-auto w-full">
      <div class="w-full mb-6 text-center" v-if="concert">
        <h2 class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">{{ concert.title }}</h2>
        <p class="text-gray-500 text-sm font-medium">{{ concertDateLabel }} • {{ concert.venue }}</p>
      </div>
      <div class="w-full mb-6 text-center" v-else>
        <div class="h-7 bg-gray-100 rounded w-2/3 mx-auto mb-2 animate-pulse"></div>
        <div class="h-4 bg-gray-100 rounded w-1/2 mx-auto animate-pulse"></div>
      </div>

      <div class="bg-white rounded-2xl shadow-[0_2px_12px_rgba(0,0,0,0.04)] border border-gray-100 p-8 w-full mb-6 text-center relative overflow-hidden">
        <div class="absolute top-0 left-0 w-full h-[3px] bg-gray-50">
          <div class="h-full bg-indigo-500 w-1/3 animate-[loading_2s_ease-in-out_infinite]"></div>
        </div>
        <div class="mb-2 flex justify-center">
          <div class="inline-flex items-center justify-center w-14 h-14 rounded-full bg-indigo-50 text-indigo-500 mb-5">
            <component :is="Hourglass" class="w-7 h-7" />
          </div>
        </div>
        <p class="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-4">현재 대기 인원</p>
        <div class="text-[64px] font-bold text-gray-900 tracking-tighter leading-none mb-2 tabular-nums">{{ formattedPosition }}</div>
        <p class="text-gray-500 text-sm mb-8 font-medium">명 남았습니다</p>
        
        <div class="flex justify-between items-center text-sm">
          <span class="text-gray-500 font-medium">예상 대기 시간</span>
          <span class="font-bold text-gray-900">약 {{ estimatedMinutes }}분</span>
        </div>
      </div>

      <div class="space-y-3 w-full">
        <div class="flex gap-4 p-5 bg-indigo-50/50 rounded-2xl border border-gray-100 shadow-sm items-start">
          <component :is="Info" class="w-5 h-5 text-indigo-500 shrink-0 mt-0.5" />
          <div class="text-[13px] text-indigo-900 leading-relaxed">
            <p class="font-semibold text-indigo-800 mb-0.5 text-sm">새로고침 금지</p>
            페이지를 새로고침하거나 브라우저를 닫으면 대기 순서가 초기화될 수 있습니다.
          </div>
        </div>
		<p v-if="token" class="text-center text-[11px] font-mono text-gray-300 mt-4 tracking-wide uppercase">Queue Token: {{ token }}</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { Hourglass, Info } from 'lucide-vue-next';
import { useRoute, useRouter } from '#imports';
import { useQueueStore } from '~/stores/queue.store';
import { useConcertStore } from '~/stores/concert.store';
import { storeToRefs } from 'pinia';
import { QueueStatus } from '~/types/queue';

definePageMeta({
  layout: 'process'
});

const route = useRoute();
const router = useRouter();
const queueStore = useQueueStore();
const concertStore = useConcertStore();

const { position, estimatedMinutes, status, token } = storeToRefs(queueStore);
const { currentConcert: concert } = storeToRefs(concertStore);

const formattedPosition = computed(() => position.value.toLocaleString());

const concertDateLabel = computed(() => {
  if (!concert.value?.dates?.length) return '';
  const dateId = Number(route.query.dateId);
  const matchedDate = concert.value.dates.find(d => d.id === dateId);
  const target = matchedDate || concert.value.dates[0];
  return new Date(target.datetime).toLocaleString('ko-KR', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
});

onMounted(async () => {
  const concertId = Number(route.query.concertId);
  const dateId = Number(route.query.dateId);
  if (!concertId) return navigateTo('/');

  // 공연 정보 로드
  concertStore.fetchConcertDetail(concertId);

  try {
    await queueStore.enterQueue(concertId);
    
    // 토큰이 이미 활성 상태면 대기열 건너뛰기
    if (status.value === QueueStatus.ACTIVE) {
      return navigateTo(`/concerts/${concertId}/seats?dateId=${dateId}&queueToken=${token.value}`);
    }
  } catch (e) {
    console.error('Queue join failed', e);
    return;
  }

  const interval = setInterval(async () => {
	let isActive = status.value === QueueStatus.ACTIVE;
    if (!token.value || isActive) {
      clearInterval(interval);
      return;
    }
    
    try {
      await queueStore.pollStatus(concertId);

      isActive = status.value === QueueStatus.ACTIVE;
      if (isActive) {
        clearInterval(interval);
        navigateTo(`/concerts/${concertId}/seats?dateId=${dateId}&queueToken=${token.value}`);
      }
    } catch(e) {
      clearInterval(interval);
    }
  }, 3000); // 3 seconds polling
});
</script>

<style>
@keyframes loading {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(300%); }
}
</style>
