<template>
  <div class="flex flex-col h-[100dvh] overflow-hidden bg-gray-50">
    <AppHeader title="좌석 선택">
      <template #right>
        <button class="w-10 h-10 -mr-2 flex items-center justify-center rounded-full hover:bg-gray-50 transition-colors">
          <component :is="MoreHorizontal" class="w-6 h-6 stroke-1" />
        </button>
      </template>
    </AppHeader>
    <div class="bg-white px-6 pb-4 shadow-sm border-b border-gray-100/50 z-10">
      <p class="text-center text-xs text-gray-500 mb-3 mt-3">네온 나이츠 월드 투어 • 10월 24일</p>
      <div class="flex justify-center gap-8 text-xs font-medium text-gray-500">
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded-full bg-gray-200 border border-gray-300"></div>
          <span>예매가능</span>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded-full bg-blue-400 ring-2 ring-blue-100"></div>
          <span>선택</span>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded-full bg-gray-300 relative flex items-center justify-center">
            <component :is="X" class="w-3 h-3 text-white" />
          </div>
          <span>예매완료</span>
        </div>
      </div>
    </div>

    <main class="flex-1 overflow-auto relative w-full touch-pan-x touch-pan-y bg-gray-50 p-8">
      <div class="min-h-full min-w-full flex flex-col items-center">
        <!-- Stage -->
        <div class="w-full max-w-[300px] mb-12 relative">
          <div class="h-12 w-full bg-white shadow-sm border border-gray-200 rounded-t-[50%] flex items-center justify-center mb-8 relative overflow-hidden">
             <span class="text-[10px] font-bold tracking-[0.2em] text-gray-400 uppercase relative z-10">Stage</span>
          </div>
        </div>

        <!-- Seats Grid -->
        <div class="grid gap-4">
            <div v-for="row in 4" :key="row" class="flex gap-2 justify-center">
                <button 
                    v-for="col in 6" 
                    :key="`${row}-${col}`"
                    :class="['w-8 h-8 rounded-full flex items-center justify-center text-[10px] font-bold transition-all shadow-sm', 
                             getSeatClass(row, col)]"
                    :disabled="isOccupied(row, col)"
                    @click="toggleSeat(row, col)"
                >
                    <component :is="X" v-if="isOccupied(row, col)" class="w-4 h-4 text-white" />
                    <span v-else>{{ String.fromCharCode(64+row) }}{{ col }}</span>
                </button>
            </div>
        </div>
      </div>
    </main>

    <!-- Bottom Sheet -->
    <div class="fixed bottom-0 left-0 right-0 z-30 bg-white rounded-t-[32px] shadow-[0_-8px_40px_rgba(0,0,0,0.06)] pb-8 pt-2 px-6 border-t border-gray-50">
        <div class="w-12 h-1 bg-gray-200 rounded-full mx-auto my-3"></div>
        <div class="flex flex-col gap-6 mb-6 mt-2">
            <div>
                <h3 class="text-sm text-gray-500 font-medium mb-3">선택 좌석</h3>
                <div class="flex flex-wrap gap-2">
                    <div v-for="seat in selectedSeats" :key="seat.id" class="bg-blue-50/80 border border-blue-100 px-3 py-1.5 rounded-full flex items-center gap-3 shadow-sm">
                        <span class="text-xs font-bold text-blue-600">{{ seat.label }}</span>
                        <span class="text-xs font-medium text-blue-800">150,000원</span>
                        <button class="ml-1 text-blue-400 hover:text-blue-600" @click="toggleSeat(seat.r, seat.c)">
                            <component :is="X" class="w-3 h-3" />
                        </button>
                    </div>
                    <span v-if="selectedSeats.length === 0" class="text-sm text-gray-400">좌석을 선택해주세요</span>
                </div>
            </div>
            <div class="flex items-center justify-between border-t border-gray-100 pt-4">
                <div class="text-left">
                    <p class="text-xs text-gray-500 mb-0.5">총 결제 금액</p>
                    <p class="text-2xl font-bold text-gray-900 tracking-tight">{{ (selectedSeats.length * 150000).toLocaleString() }}원</p>
                </div>
                <button 
                    class="bg-gray-900 text-white font-semibold py-3 px-8 rounded-full shadow-lg active:scale-[0.99] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    :disabled="selectedSeats.length === 0"
                    @click="goToCheckout"
                >
                    결제하기
                </button>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronLeft, MoreHorizontal, X } from 'lucide-vue-next';

definePageMeta({
  layout: 'process'
});

const router = useRouter();
const selectedSeats = ref<{r: number, c: number, id: string, label: string}[]>([]);

const isOccupied = (r: number, c: number) => {
    // Mock logic: some random seats are occupied
    return (r + c) % 5 === 0;
}

const isSelected = (r: number, c: number) => {
    return selectedSeats.value.some(s => s.r === r && s.c === c);
}

const getSeatClass = (r: number, c: number) => {
    if (isOccupied(r, c)) return 'bg-gray-300 cursor-not-allowed';
    if (isSelected(r, c)) return 'bg-blue-400 text-white ring-2 ring-blue-100';
    return 'bg-gray-200 hover:bg-gray-300 text-transparent hover:text-gray-500';
}

const toggleSeat = (r: number, c: number) => {
    if (isOccupied(r, c)) return;
    
    if (isSelected(r, c)) {
        selectedSeats.value = selectedSeats.value.filter(s => !(s.r === r && s.c === c));
    } else {
        if (selectedSeats.value.length >= 2) return; // Limit 2
        selectedSeats.value.push({
            r, c,
            id: `${r}-${c}`,
            label: `${String.fromCharCode(64+r)}${c}`
        });
    }
}

const goToCheckout = () => {
    navigateTo('/payment');
}
</script>
