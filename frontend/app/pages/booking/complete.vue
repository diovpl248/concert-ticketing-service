<template>
  <div class="h-[100dvh] overflow-hidden bg-gray-50 flex flex-col">
    <!-- Header -->
    <AppHeader title="예매 완료" :showBack="false"/>

    <main class="flex-1 overflow-y-auto px-5 py-4">
      <template v-if="bookingDetail">
        <!-- Success Message -->
        <div class="flex flex-col items-center justify-center mb-8 text-center animate-fade-in-up">
          <div class="w-20 h-20 bg-indigo-600 rounded-full flex items-center justify-center mb-6 shadow-lg shadow-indigo-200">
            <component :is="Check" class="w-10 h-10 text-white stroke-[3]" />
          </div>
          <h2 class="text-2xl font-bold text-gray-900 mb-2">예매가 완료되었습니다!</h2>
          <p class="text-gray-500 text-sm">예매번호 <span class="font-bold text-gray-900">{{ bookingDetail.bookingId }}</span></p>
        </div>

        <!-- Ticket Card -->
        <div class="bg-white overflow-hidden shadow-xl shadow-gray-200/60 border border-gray-100 relative mx-2 rounded-3xl animate-fade-in-up" style="animation-delay: 0.1s;">
            <!-- Top Section: Concert Info -->
            <div class="p-8 border-b border-gray-100 border-dashed relative">
                <div class="flex flex-col items-center text-center mb-8">
                    <div class="w-32 h-44 bg-gray-200 rounded-2xl overflow-hidden shadow-md mb-5">
                        <img class="w-full h-full object-cover" :src="bookingDetail.concert.thumbnailUrl" :alt="bookingDetail.concert.title"/>
                    </div>
                    <div>
                        <h3 class="text-xl font-bold text-gray-900 leading-tight mb-2">{{ bookingDetail.concert.title }}</h3>
                        <div class="flex items-center justify-center gap-1.5 text-sm text-gray-500 font-medium">
                            <component :is="MapPin" class="w-4 h-4 text-gray-400" />
                            <span>{{ bookingDetail.concert.venue }}</span>
                        </div>
                    </div>
                </div>

                <div class="space-y-4 pt-2">
                   <div class="flex justify-between items-center">
                        <span class="text-sm text-gray-400 font-medium">일시</span>
                        <span class="text-base font-bold text-gray-900">{{ bookingDetail.concert.date }} {{ bookingDetail.concert.time }}</span>
                   </div>
                   <div class="flex justify-between items-center">
                        <span class="text-sm text-gray-400 font-medium">매수</span>
                        <span class="text-base font-bold text-gray-900">1매</span>
                   </div>
                </div>
            </div>

            <!-- Bottom Section: Seat & Payment -->
            <div class="p-8 bg-gray-50/50">
                 <h4 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-5 px-1">좌석 정보</h4>
                 <div class="space-y-3 mb-8">
                     <div class="flex justify-between items-center p-4 bg-white rounded-2xl border border-gray-200">
                         <span class="text-sm font-bold text-gray-900">{{ bookingDetail.seat.section }}구역 {{ bookingDetail.seat.row }}열 {{ bookingDetail.seat.col }}번</span>
                         <span class="text-xs font-medium text-gray-500">{{ bookingDetail.seat.price.toLocaleString() }}원</span>
                     </div>
                 </div>

                 <div class="border-t border-gray-200 my-6"></div>

                 <div class="flex justify-between items-end">
                    <div class="flex flex-col gap-1 mt-4">
                        <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">총 결제 금액</span>
                        <span class="text-xs text-gray-500 font-medium" v-if="bookingDetail.payment">결제 완료 ({{ bookingDetail.payment.paymentMethod }})</span>
                    </div>
                    <span class="text-2xl font-bold text-indigo-600 tracking-tight" v-if="bookingDetail.payment">
                        {{ bookingDetail.payment.amount.toLocaleString() }}원
                    </span>
                 </div>
            </div>
        </div>
      </template>

      <!-- Loading Placeholder -->
      <div v-else class="flex items-center justify-center h-full">
        <div class="animate-spin w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full"></div>
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
import { useRoute } from '#imports';
import { useBookingStore } from '~/stores/booking.store';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';

const route = useRoute();
const bookingStore = useBookingStore();

const bookingId = Number(route.query.bookingId);
const { currentBookingDetail: bookingDetail } = storeToRefs(bookingStore);

onMounted(async () => {
    try {
        await bookingStore.fetchBookingDetail(bookingId);
    } catch (e) {
        console.error('Failed to load booking detail', e);
        alert('예매 정보를 불러오지 못했습니다.');
    }
});

const goHome = () => {
    navigateTo('/');
}
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.4s ease-out forwards;
  opacity: 0;
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
