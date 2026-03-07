<template>
  <div class="h-[100dvh] overflow-hidden bg-gray-50 flex flex-col">
    <!-- Header -->
    <AppHeader title="결제하기" />

    <main class="flex-1 overflow-y-auto px-5 py-6">
      <!-- Concert Info -->
      <div class="bg-white rounded-3xl p-5 shadow-sm mb-5 border border-gray-100">
        <div class="flex gap-4">
          <div class="w-20 h-28 bg-gray-200 rounded-xl overflow-hidden shrink-0 relative shadow-sm">
            <img class="w-full h-full object-cover" src="https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?q=80&w=1000&auto=format&fit=crop" />
          </div>
          <div class="flex flex-col justify-between py-0.5">
            <div>
              <span class="inline-block px-2.5 py-1 rounded-md text-[10px] font-bold bg-indigo-50 text-indigo-600 mb-2 uppercase tracking-wide">티켓 오픈</span>
              <h2 class="text-[17px] font-bold text-gray-900 leading-tight mb-1.5">네온 나이츠 월드 투어 2024</h2>
            </div>
            <div class="space-y-1.5">
              <div class="flex items-center gap-1.5 text-sm font-medium text-gray-600">
                <component :is="MapPin" class="w-4 h-4 text-gray-400" />
                <span class="truncate">서울 잠실 올림픽 주경기장</span>
              </div>
              <div class="flex items-center gap-1.5 text-sm font-medium text-gray-600">
                <component :is="Calendar" class="w-4 h-4 text-gray-400" />
                <span>10월 24일</span>
                <span class="w-1 h-1 rounded-full bg-gray-300 mx-1"></span>
                <span>19:00</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Seat Info -->
      <div class="mb-5">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 px-1">선택 좌석</h3>
        <div class="bg-white rounded-3xl shadow-sm overflow-hidden divide-y divide-gray-100 border border-gray-100">
          <div class="p-5 flex items-center justify-around">
            <div class="flex flex-col">
              <span class="text-xs font-medium text-gray-400 mb-1">구역</span>
              <span class="text-[17px] font-bold text-gray-900">VIP A</span>
            </div>
             <div class="h-8 w-[1px] bg-gray-100"></div>
            <div class="flex flex-col items-start">
              <span class="text-xs font-medium text-gray-400 mb-1">열</span>
              <span class="text-[17px] font-bold text-gray-900">12</span>
            </div>
             <div class="h-8 w-[1px] bg-gray-100"></div>
            <div class="flex flex-col items-start">
              <span class="text-xs font-medium text-gray-400 mb-1">번호</span>
              <span class="text-[17px] font-bold text-gray-900">04</span>
            </div>
          </div>
          <div class="px-5 py-4 bg-indigo-50/50 flex items-start gap-3">
             <component :is="Info" class="w-5 h-5 text-indigo-500 mt-0.5" />
             <p class="text-xs font-medium text-indigo-900 leading-relaxed pt-0.5">
               이 좌석은 <span class="font-bold tabular-nums">04:59</span> 동안 임시 확보됩니다.<br>시간 내에 결제를 완료해주세요.
             </p>
          </div>
        </div>
      </div>

      <!-- Payment Summary -->
      <div class="mb-5">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 px-1">결제 정보</h3>
        <div class="bg-white rounded-3xl p-5 shadow-sm space-y-3 border border-gray-100">
            <div class="flex justify-between items-center text-sm">
                <span class="text-gray-500 font-medium">티켓 금액 (1매)</span>
                <span class="font-semibold text-gray-900">{{ amount.toLocaleString() }}원</span>
            </div>
            <div class="flex justify-between items-center text-sm">
                <span class="text-gray-500 font-medium">예매 수수료</span>
                <span class="font-semibold text-gray-900">0원</span>
            </div>
            <div class="pt-4 border-t border-dashed border-gray-200 mt-2">
                <div class="flex justify-between items-end">
                    <span class="text-base font-bold text-gray-900">총 결제 금액</span>
                    <span class="text-2xl font-bold text-indigo-600 tracking-tight">{{ amount.toLocaleString() }}원</span>
                </div>
            </div>
        </div>
      </div>
      
       <!-- Payment Method -->
       <div class="mb-4">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 px-1">결제 수단</h3>
        <div class="bg-white rounded-3xl p-5 shadow-sm flex items-center justify-between border border-gray-100">
            <div class="flex items-center gap-3">
                <div class="w-10 h-7 bg-slate-800 rounded flex items-center justify-center shadow-sm">
                    <span class="text-[10px] font-bold text-white tracking-widest font-sans">VISA</span>
                </div>
                <div class="flex flex-col">
                    <span class="text-sm font-bold text-gray-900 leading-none mb-1">현대카드</span>
                    <span class="text-xs font-medium text-gray-400 leading-none">•••• 4242</span>
                </div>
            </div>
             <button class="text-xs font-bold text-indigo-600 hover:text-indigo-700 transition-colors py-1 px-2 hover:bg-indigo-50 rounded-lg">변경</button>
        </div>
      </div>

    </main>

    <!-- Bottom Action -->
    <div class="bg-white border-t border-gray-100 p-5 pb-8 shadow-[0_-10px_40px_rgba(0,0,0,0.03)] rounded-t-[32px] z-20">
        <div class="space-y-3">
            <button 
                class="w-full h-14 bg-indigo-600 hover:bg-indigo-700 active:bg-indigo-800 text-white rounded-2xl font-bold text-base shadow-lg shadow-indigo-200/50 transition-all flex items-center justify-center gap-2 group disabled:opacity-50" 
                @click="processPayment(true)"
                :disabled="isProcessing"
            >
                <span>{{ isProcessing ? '결제 처리중...' : amount.toLocaleString() + '원 결제하기' }}</span>
                <component :is="ArrowRight" class="w-5 h-5 group-hover:translate-x-1 transition-transform" v-if="!isProcessing" />
            </button>
             <button 
                 class="w-full h-14 bg-white border border-red-200 text-red-500 hover:bg-red-50 active:bg-red-100 rounded-2xl font-bold text-sm transition-colors flex items-center justify-center gap-2 disabled:opacity-50" 
                 @click="processPayment(false)"
                 :disabled="isProcessing"
             >
                 <component :is="AlertCircle" class="w-5 h-5" />
                 결제 실패 취소
             </button>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronLeft, MapPin, Calendar, Info, ArrowRight, AlertCircle } from 'lucide-vue-next';
import { useRoute, useRouter } from '#imports';
import { usePaymentStore } from '~/stores/payment.store';
import { storeToRefs } from 'pinia';
import { PaymentMethod } from '~/types/payment';

definePageMeta({
  layout: 'process'
});

const route = useRoute();
const router = useRouter();

const paymentStore = usePaymentStore();
const { isProcessing } = storeToRefs(paymentStore);

const bookingId = Number(route.query.bookingId);
const queueToken = route.query.queueToken as string;
const concertId = Number(route.query.concertId);
const amount = Number(route.query.amount) || 0;

const processPayment = async (success: boolean) => {
    if (!success) {
        alert('결제를 취소했습니다.');
        navigateTo('/');
        return;
    }
    
    try {
        await paymentStore.processPayment({
			concertId,
            bookingId,
            paymentMethod: PaymentMethod.CARD,
            amount
        }, queueToken);
        navigateTo({ path: '/booking/complete', query: { bookingId } });
    } catch(e) {
        alert('결제 처리 중 에러가 발생했습니다.');
        console.error('Payment failed', e);
    }
}
</script>
