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
        <div class="grid gap-4" v-if="seats.length > 0">
            <div v-for="row in rows" :key="row" class="flex gap-2 justify-center">
                <button 
                    v-for="seat in getSeatsByRow(row)" 
                    :key="seat.id"
                    :class="['w-8 h-8 rounded-full flex items-center justify-center text-[10px] font-bold transition-all shadow-sm', 
                             getSeatClass(seat)]"
                    :disabled="seat.status !== 'AVAILABLE'"
                    @click="toggleSeat(seat)"
                >
                    <component :is="X" v-if="seat.status !== 'AVAILABLE'" class="w-4 h-4 text-white" />
                    <span v-else>{{ seat.rowNo }}{{ seat.colNo }}</span>
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
                    <div v-if="selectedSeat" class="bg-blue-50/80 border border-blue-100 px-3 py-1.5 rounded-full flex items-center gap-3 shadow-sm">
                        <span class="text-xs font-bold text-blue-600">{{ selectedSeat.rowNo }}{{ selectedSeat.colNo }}</span>
                        <span class="text-xs font-medium text-blue-800">{{ selectedSeat.price.toLocaleString() }}원</span>
                        <button class="ml-1 text-blue-400 hover:text-blue-600" @click="selectedSeat = null">
                            <component :is="X" class="w-3 h-3" />
                        </button>
                    </div>
                    <span v-else class="text-sm text-gray-400">좌석을 선택해주세요</span>
                </div>
            </div>
            <div class="flex items-center justify-between border-t border-gray-100 pt-4">
                <div class="text-left">
                    <p class="text-xs text-gray-500 mb-0.5">총 결제 금액</p>
                    <p class="text-2xl font-bold text-gray-900 tracking-tight">{{ selectedSeat ? selectedSeat.price.toLocaleString() : '0' }}원</p>
                </div>
                <button 
                    class="bg-gray-900 text-white font-semibold py-3 px-8 rounded-full shadow-lg active:scale-[0.99] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    :disabled="!selectedSeat || isBooking"
                    @click="goToCheckout"
                >
                    {{ isBooking ? '예매 중...' : '결제하기' }}
                </button>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronLeft, MoreHorizontal, X } from 'lucide-vue-next';
import { useRoute, useRouter } from '#imports';
import { useConcertStore } from '~/stores/concert.store';
import { useBookingStore } from '~/stores/booking.store';
import { storeToRefs } from 'pinia';
import type { SeatResponse } from '~/types/concert';

definePageMeta({
  layout: 'process'
});

const route = useRoute();
const router = useRouter();

const concertStore = useConcertStore();
const bookingStore = useBookingStore();
const { currentSeats: seats } = storeToRefs(concertStore);
const { isBooking } = storeToRefs(bookingStore);

const selectedSeat = ref<any | null>(null);

const concertId = Number(route.query.concertId) || Number(route.params.id);
const dateId = Number(route.query.dateId);
const queueToken = route.query.queueToken as string;

// Computed for rows
const rows = computed(() => {
    const rowSet = new Set(seats.value.map((s: SeatResponse) => s.rowNo));
    return Array.from(rowSet).sort();
});

const getSeatsByRow = (rowNo: string) => {
    return seats.value.filter((s: SeatResponse) => s.rowNo === rowNo).sort((a: SeatResponse, b: SeatResponse) => Number(a.colNo) - Number(b.colNo));
};

onMounted(async () => {
    try {
        await concertStore.fetchSeats(concertId, dateId, queueToken);
    } catch (e) {
        console.error('Failed to load seats', e);
        alert('좌석 정보를 불러오지 못했습니다.');
    }
});

const isSelected = (seat: any) => {
    return selectedSeat.value?.id === seat.id;
}

const getSeatClass = (seat: any) => {
    if (seat.status !== 'AVAILABLE') return 'bg-gray-300 cursor-not-allowed';
    if (isSelected(seat)) return 'bg-blue-400 text-white ring-2 ring-blue-100';
    return 'bg-gray-200 hover:bg-gray-300 text-transparent hover:text-gray-500';
}

const toggleSeat = (seat: any) => {
    if (seat.status !== 'AVAILABLE') return;
    
    if (isSelected(seat)) {
        selectedSeat.value = null;
    } else {
        selectedSeat.value = seat; // allow only 1 seat
    }
}

const goToCheckout = async () => {
    if (!selectedSeat.value) return;
    try {
        const res = await bookingStore.createBooking({
            concertId,
            dateId,
            seatId: selectedSeat.value.id
        }, queueToken);
        const bookingId = res.bookingId;
        navigateTo(`/payment?bookingId=${bookingId}&queueToken=${queueToken}&amount=${selectedSeat.value.price}&concertId=${concertId}`);
    } catch (e: any) {
        console.error('Booking failed', e);
        alert('예약 처리에 실패했습니다. (이미 선점된 좌석이거나 대기열 토큰 오류)');
        // refresh seats
        await concertStore.fetchSeats(concertId, dateId, queueToken);
        selectedSeat.value = null;
    }
}
</script>
