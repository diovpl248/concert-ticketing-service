<template>
  <div class="bg-white min-h-screen">
    <AppHeader title="공연 상세" />
    <div class="p-5">

    <div class="aspect-video bg-gray-200 rounded-xl mb-6 relative overflow-hidden">
        <img src="https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?q=80&w=1000&auto=format&fit=crop" class="object-cover w-full h-full" />
    </div>

    <h2 class="text-2xl font-bold mb-2">네온 나이츠 월드 투어 2024</h2>
    <p class="text-gray-500 mb-6">10월 24일 • 서울 잠실 올림픽 주경기장</p>

    <div class="space-y-4 mb-8">
      <h3 class="font-bold">날짜 및 회차 선택</h3>
      <div class="flex gap-3">
        <button class="px-4 py-2 border border-black bg-black text-white rounded-lg text-sm">10월 24일 19:00</button>
        <button class="px-4 py-2 border border-gray-200 text-gray-500 rounded-lg text-sm">10월 25일 19:00</button>
      </div>
    </div>

    <button class="w-full bg-black text-white font-bold py-4 rounded-xl shadow-lg active:scale-95 transition-transform" @click="enterQueue">
      예매하기 (대기열 입장)
    </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronLeft } from 'lucide-vue-next';
const router = useRouter();

const enterQueue = () => {
    // Generate a mock token or ID
    navigateTo('/queue');
}
</script>
