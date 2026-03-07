<template>
  <div class="bg-white min-h-screen">
    <AppHeader title="공연 상세" />
    <div class="p-5" v-if="concert">

    <div class="aspect-video bg-gray-200 rounded-xl mb-6 relative overflow-hidden">
        <img :src="concert.thumbnailUrl || 'https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?q=80&w=1000&auto=format&fit=crop'" class="object-cover w-full h-full" />
    </div>

    <h2 class="text-2xl font-bold mb-2">{{ concert.title }}</h2>
    <p class="text-gray-500 mb-6">{{ concert.category }} • {{ concert.venue }}</p>

    <div class="space-y-4 mb-8">
      <h3 class="font-bold">날짜 및 회차 선택</h3>
      <div class="flex gap-3 overflow-x-auto pb-2">
        <button 
          v-for="date in concert.dates" 
          :key="date.id"
          :class="['px-4 py-2 border rounded-lg text-sm shrink-0', selectedDateId === date.id ? 'border-black bg-black text-white' : 'border-gray-200 text-gray-500']"
          @click="selectedDateId = date.id"
        >
          {{ new Date(date.datetime).toLocaleString('ko-KR', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' }) }}
        </button>
      </div>
    </div>

    <button 
      class="w-full bg-black text-white font-bold py-4 rounded-xl shadow-lg active:scale-95 transition-transform disabled:opacity-50 disabled:scale-100" 
      :disabled="!selectedDateId"
      @click="enterQueue"
    >
      예매하기 (대기열 입장)
    </button>
    </div>
    
    <div v-else class="p-5 flex justify-center items-center py-20">
      <p class="text-gray-500">불러오는 중...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from '#imports';
import { useConcertStore } from '~/stores/concert.store';
import { storeToRefs } from 'pinia';

const route = useRoute();
const router = useRouter();
const concertStore = useConcertStore();
const { currentConcert: concert } = storeToRefs(concertStore);

const selectedDateId = ref<number | null>(null);

onMounted(async () => {
    try {
        const concertId = Number(route.params.id);
        await concertStore.fetchConcertDetail(concertId);
        
		if (concert.value) {
			if (concert.value.dates.length > 0 && concert.value.dates[0]) {
				selectedDateId.value = concert.value.dates[0].id;
			}
		}
    } catch (e) {
        console.error('Failed to load concert details', e);
    }
});

const enterQueue = () => {
    if (!selectedDateId.value) return;
    navigateTo(`/queue?concertId=${route.params.id}&dateId=${selectedDateId.value}`);
}
</script>
