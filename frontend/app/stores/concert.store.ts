import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useConcertApi } from '../composables/useConcertApi';
import type { ConcertResponse, ConcertDateResponse, SeatResponse, DisplayConcert } from '~/types/concert';

export const useConcertStore = defineStore('concert', () => {
  const trendingConcerts = ref<DisplayConcert[]>([]);
  const upcomingConcerts = ref<DisplayConcert[]>([]);
  const currentConcert = ref<ConcertResponse | null>(null);
  const currentDates = ref<ConcertDateResponse[]>([]);
  const currentSeats = ref<SeatResponse[]>([]);

  const concertApi = useConcertApi();

  const fetchConcertsList = async () => {
    const data = await concertApi.getConcerts();
    const formatted: DisplayConcert[] = data.map((c) => ({
      id: c.id,
      title: c.title,
      category: c.category,
      date: '오픈 일정 미정',
      venue: c.venue,
      image: c.thumbnailUrl || 'https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?q=80&w=1000&auto=format&fit=crop',
      badge: c.id === 1 ? 'HOT' : null
    }));
    trendingConcerts.value = formatted.slice(0, 2);
    upcomingConcerts.value = formatted.slice(2);
  };

  const fetchConcertDetail = async (id: number) => {
    const concertRes = await concertApi.getConcert(id);
    currentConcert.value = concertRes;
  };

  const fetchSeats = async (concertId: number, dateId: number, queueToken: string) => {
    const data = await concertApi.getSeats(concertId, dateId, queueToken);
    currentSeats.value = data;
  };

  return { 
    trendingConcerts, upcomingConcerts, currentConcert, currentDates, currentSeats,
    fetchConcertsList, fetchConcertDetail, fetchSeats 
  };
});
