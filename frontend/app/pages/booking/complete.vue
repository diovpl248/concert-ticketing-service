<template>
  <div class="h-[100dvh] overflow-hidden bg-gray-50 flex flex-col">
    <!-- Header -->
    <AppHeader title="예매 완료" :showBack="false"/>

    <main class="flex-1 overflow-y-auto px-5 py-4">
      <!-- Success Message -->
      <div class="flex flex-col items-center justify-center mb-8 text-center animate-fade-in-up">
        <div class="w-20 h-20 bg-indigo-600 rounded-full flex items-center justify-center mb-6 shadow-lg shadow-indigo-200">
          <component :is="Check" class="w-10 h-10 text-white stroke-[3]" />
        </div>
        <h2 class="text-2xl font-bold text-gray-900 mb-2">예매가 완료되었습니다!</h2>
        <p class="text-gray-500 text-sm">예매번호 <span class="font-bold text-gray-900">{{ payment?.ticketCode }}</span></p>
      </div>

      <!-- Ticket Card -->
      <div class="bg-white overflow-hidden shadow-xl shadow-gray-200/60 border border-gray-100 relative mx-2 rounded-3xl" v-if="concert && booking">
          <!-- Top Section: Concert Info -->
          <div class="p-8 border-b border-gray-100 border-dashed relative">
              <div class="flex flex-col items-center text-center mb-8">
                  <div class="w-32 h-44 bg-gray-200 rounded-2xl overflow-hidden shadow-md mb-5">
                      <img class="w-full h-full object-cover" :src="concert.thumbnailUrl" :alt="concert.title"/>
                  </div>
                  <div>
                      <h3 class="text-xl font-bold text-gray-900 leading-tight mb-2">{{ concert.title }}</h3>
                      <div class="flex items-center justify-center gap-1.5 text-sm text-gray-500 font-medium">
                          <component :is="MapPin" class="w-4 h-4 text-gray-400" />
                          <span>{{ concert.venue }}</span>
                      </div>
                  </div>
              </div>

              <div class="space-y-4 pt-2">
                 <div class="flex justify-between items-center">
                      <span class="text-sm text-gray-400 font-medium">일시</span>
                      <span class="text-base font-bold text-gray-900">{{ concertStore.currentDates[0]?.datetime || '-' }}</span>
                 </div>
                 <div class="flex justify-between items-center">
                      <span class="text-sm text-gray-400 font-medium">매수</span>
                      <span class="text-base font-bold text-gray-900">1매</span> <!-- Assuming 1 ticket per flow for now -->
                 </div>
              </div>
          </div>

          <!-- Bottom Section: Seat & Payment -->
          <div class="p-8 bg-gray-50/50">
               <h4 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-5 px-1">좌석 정보</h4>
               <div class="space-y-3 mb-8">
                   <div v-for="seat in selectedSeats" :key="seat.id" class="flex justify-between items-center p-4 bg-white rounded-2xl border border-gray-200">
                       <span class="text-sm font-bold text-gray-900">{{ seat.section }}구역 {{ seat.rowNo }}열 {{ seat.colNo }}번</span>
                       <span class="text-xs font-medium text-gray-500">{{ seat.price.toLocaleString() }}원</span>
                   </div>
               </div>

               <div class="border-t border-gray-200 my-6"></div>

               <div class="flex justify-between items-end">
                  <div class="flex flex-col gap-1 mt-4">
                      <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">총 결제 금액</span>
                      <!-- For demo, we just say "가상 통장 결제" since frontend flow hardcoded simple payment method -->
                      <span class="text-xs text-gray-500 font-medium" v-if="payment">결제 완료 ({{ payment.status }})</span>
                  </div>
                  <span class="text-2xl font-bold text-indigo-600 tracking-tight" v-if="payment">
                      <!-- Simple representation, assuming one seat booked -->
                      {{ selectedSeats[0]?.price.toLocaleString() }}원
                  </span>
               </div>
          </div>
      </div>
      <div v-else class="text-center py-20 text-gray-400 h-full flex items-center justify-center">
        예매 정보가 없습니다. 경로가 올바르지 않습니다.
      </div>

    </main>

    <!-- Bottom Action -->
    <div class="p-5 pb-8 bg-white border-t border-gray-100 shadow-[0_-10px_40px_rgba(0,0,0,0.03)] z-20">
        <button class="w-full h-14 bg-gray-900 text-white rounded-2xl font-bold text-base shadow-lg active:scale-[0.98] transition-all" @click="goHome">
            홈으로 돌아가기
        </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Check, MapPin } from 'lucide-vue-next';
import { useRouter } from '#imports';
import { useConcertStore } from '~/stores/concert.store';
import { useBookingStore } from '~/stores/booking.store';
import { usePaymentStore } from '~/stores/payment.store';
import { storeToRefs } from 'pinia';

const router = useRouter();
const concertStore = useConcertStore();
const bookingStore = useBookingStore();
const paymentStore = usePaymentStore();

const { currentConcert: concert } = storeToRefs(concertStore);
const { lastBookingResult: booking } = storeToRefs(bookingStore);
const { lastPaymentResult: payment } = storeToRefs(paymentStore);

// TODO: Optionally get seat information if needed, but we can rely on what user booked 
// For now, simple format just uses store basic info
const selectedSeats = computed(() => {
    // In a real scenario, you'd match booking.seatId with concertStore.currentSeats
    // and map it to a readable string. Adding simple fallback for now:
    if(!booking.value) return [];
    const seat = concertStore.currentSeats.find(s => s.id === booking.value?.seatId);
    return seat ? [seat] : [];
});

const goHome = () => {
    navigateTo('/');
}
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.5s ease-out forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
