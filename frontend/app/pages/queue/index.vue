<template>
  <div class="flex flex-col min-h-screen">
    <AppHeader title="대기 상태" :showBack="true" backPath="/" />

    <main class="flex-1 px-6 pt-6 pb-10 flex flex-col items-center max-w-md mx-auto w-full">
      <div class="w-full mb-6 text-center">
        <h2 class="text-2xl font-bold text-gray-900 mb-2 tracking-tight">네온 나이츠 월드 투어 2024</h2>
        <p class="text-gray-500 text-sm font-medium">10월 24일 • 서울 잠실 올림픽 주경기장</p>
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
        
        <div class="w-full bg-gray-100 rounded-full h-1.5 mb-5 overflow-hidden">
          <div class="bg-indigo-500 h-1.5 rounded-full transition-all duration-1000 ease-linear" :style="{ width: progress + '%' }"></div>
        </div>
        
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
		<p class="text-center text-[11px] font-mono text-gray-300 mt-4 tracking-wide uppercase">Queue ID: Q-8829-XJ29</p>
      </div>
    </main>

    <!-- <div class="sticky bottom-0 left-0 right-0 p-6 bg-white border-t border-gray-100 pb-safe shadow-[0_-4px_20px_rgba(0,0,0,0.02)]">
      <div class="max-w-md mx-auto">
        <button class="w-full bg-gray-50 text-gray-400 font-semibold py-4 rounded-xl text-[15px] cursor-not-allowed flex items-center justify-center gap-2.5 transition-all" disabled>
          <component :is="Lock" class="w-[18px] h-[18px]" />
          좌석 선택 페이지 진입 중...
        </button>
        
      </div>
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { ChevronLeft, Hourglass, Info, Bell, Lock } from 'lucide-vue-next';
import { useRoute, useRouter } from '#imports';
import { useQueueStore } from '~/stores/queue.store';
import { storeToRefs } from 'pinia';
import { QueueStatus } from '~/types/queue';

definePageMeta({
  layout: 'process'
});

const route = useRoute();
const router = useRouter();
const queueStore = useQueueStore();
const { position, estimatedMinutes, status, token } = storeToRefs(queueStore);

const progress = ref(20);
const formattedPosition = computed(() => position.value.toLocaleString());

const goBack = () => {
  navigateTo('/');
}

onMounted(async () => {
  const concertId = Number(route.query.concertId);
  const dateId = Number(route.query.dateId);
  if (!concertId) return navigateTo('/');

  try {
    await queueStore.enterQueue(concertId);
    
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
      progress.value = Math.min(100, progress.value + 5);

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
