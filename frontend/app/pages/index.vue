<template>
  <div>
    <!-- Header -->
    <header class="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-gray-100/50">
      <div class="px-5 pt-5 pb-4">
        <div class="flex justify-between items-center mb-5 h-10">
          <div class="flex items-center gap-1">
            <h1 class="text-xl font-bold text-gray-900 ml-1">공연 예매</h1>
          </div>
        </div>
        
        <!-- Search -->
        <div class="relative mb-5 group">
          <component :is="Search" class="absolute left-4 top-3.5 text-gray-400 group-focus-within:text-black transition-colors w-5 h-5 stroke-1" />
          <input class="w-full bg-gray-50 border border-gray-100 rounded-[14px] py-3 pl-11 pr-4 text-sm text-gray-900 placeholder-gray-400 focus:ring-1 focus:ring-black focus:border-black transition-all shadow-sm outline-none" placeholder="아티스트, 공연장 검색..." type="text"/>
        </div>

        <!-- Categories -->
        <div class="flex gap-2.5 overflow-x-auto hide-scrollbar pb-1 -mx-5 px-5">
          <button v-for="cat in categories" :key="cat" :class="['flex-none px-4 py-2 rounded-[12px] font-medium text-[13px] border transition-colors', cat === activeCategory ? 'bg-black text-white border-black shadow-sm' : 'bg-gray-50 text-gray-600 border-gray-100 hover:bg-gray-100']" @click="activeCategory = cat">
            {{ cat }}
          </button>
        </div>
      </div>
    </header>

    <main class="px-5 pt-4 space-y-8">
      <!-- Trending Section -->
      <section>
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-bold text-gray-900">실시간 인기 공연</h2>
          <NuxtLink class="text-xs font-medium text-gray-400 hover:text-black transition-colors" to="/">전체보기</NuxtLink>
        </div>
        <div class="flex overflow-x-auto hide-scrollbar gap-4 -mx-5 px-5 pb-4 snap-x snap-mandatory">
          <div v-for="concert in trendingConcerts" :key="concert.id" class="snap-center shrink-0 w-[80vw] relative rounded-[16px] overflow-hidden aspect-[16/10] shadow-sm border border-gray-100 group" @click="goToConcert(concert.id)">
            <img :alt="concert.title" class="absolute inset-0 w-full h-full object-cover" :src="concert.image"/>
            <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent"></div>
            <div class="absolute bottom-0 left-0 p-5 w-full">
              <span class="inline-block px-2.5 py-1 bg-white/20 backdrop-blur-md rounded-lg text-[10px] font-semibold text-white mb-2 border border-white/10 uppercase tracking-wide">{{ concert.category }}</span>
              <h3 class="text-xl font-bold text-white mb-1 leading-tight text-balance">{{ concert.title }}</h3>
              <p class="text-white/80 text-xs mb-3 flex items-center gap-1.5 font-medium">
                <component :is="Calendar" class="w-3.5 h-3.5" /> {{ concert.date }} • {{ concert.venue }}
              </p>
              <button class="w-full bg-white text-black font-semibold py-2.5 rounded-[12px] text-sm hover:bg-gray-50 transition-colors shadow-sm active:scale-[0.98]">예매하기</button>
            </div>
          </div>
        </div>
      </section>

      <!-- Upcoming Section -->
      <section>
        <h2 class="text-lg font-bold text-gray-900 mb-4">티켓 오픈 예정</h2>
        <div class="grid grid-cols-1 gap-4 pb-10">
          <article v-for="concert in upcomingConcerts" :key="concert.id" class="bg-gray-50 rounded-[16px] p-4 flex gap-4 border border-gray-100" @click="goToConcert(concert.id)">
            <div class="w-28 h-32 shrink-0 relative rounded-[12px] overflow-hidden bg-gray-200">
              <img :alt="concert.title" class="absolute inset-0 w-full h-full object-cover" :src="concert.image"/>
              <div v-if="concert.badge" :class="['absolute top-2 left-2 text-white text-[9px] font-bold px-1.5 py-0.5 rounded-[6px]', concert.badge === 'HOT' ? 'bg-red-500' : 'bg-black/70 backdrop-blur-[2px]']">{{ concert.badge }}</div>
            </div>
            <div class="flex flex-col flex-1 justify-between py-0.5">
              <div>
                <div class="flex justify-between items-start mb-1.5">
                  <span :class="['px-2.5 py-1 rounded-[8px] text-[10px] font-semibold', getTagColor(concert.category)]">{{ concert.category }}</span>
                  <button class="text-gray-300 hover:text-red-500 transition-colors">
                    <component :is="Heart" class="w-5 h-5 stroke-1" />
                  </button>
                </div>
                <h3 class="font-bold text-[15px] text-gray-900 leading-snug line-clamp-2">{{ concert.title }}</h3>
                <div class="mt-2 space-y-1">
                  <p class="text-xs text-gray-500 font-medium flex items-center gap-1.5">
                    <component :is="Calendar" class="w-3.5 h-3.5 text-gray-400" /> {{ concert.date }}
                  </p>
                  <p class="text-xs text-gray-500 font-medium flex items-center gap-1.5 truncate">
                    <component :is="MapPin" class="w-3.5 h-3.5 text-gray-400" /> {{ concert.venue }}
                  </p>
                </div>
              </div>
              <button class="mt-3 w-full bg-black text-white font-medium py-2.5 rounded-[12px] text-[13px] active:bg-gray-800 transition-colors shadow-sm">예매하기</button>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { Bell, Search, Calendar, MapPin, Heart } from 'lucide-vue-next';
import { useConcertStore } from '~/stores/concert.store';
import { storeToRefs } from 'pinia';

const activeCategory = ref('전체');
const categories = ['전체', 'K-POP', '뮤지컬', '클래식', '인디', '전시'];

const concertStore = useConcertStore();
const { trendingConcerts, upcomingConcerts } = storeToRefs(concertStore);

onMounted(async () => {
  try {
    await concertStore.fetchConcertsList();
  } catch (error) {
    console.error('Failed to fetch concerts', error);
  }
});

const getTagColor = (category: string) => {
  switch (category) {
    case '클래식': return 'bg-blue-100 text-blue-700';
    case 'K-POP': return 'bg-orange-100 text-orange-700';
    case '인디': return 'bg-purple-100 text-purple-700';
    case '전시': return 'bg-green-100 text-green-700';
    default: return 'bg-gray-100 text-gray-700';
  }
};

const goToConcert = (id: number) => {
  navigateTo(`/concerts/${id}`);
};
</script>

<style scoped>
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
