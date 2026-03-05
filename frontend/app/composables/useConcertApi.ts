import { useNuxtApp } from '#imports';
import type { ConcertResponse, ConcertDateResponse, SeatResponse } from '~/types/concert';

export const useConcertApi = () => {
  const { $api } = useNuxtApp() as any;

  return {
    getConcerts: async (): Promise<ConcertResponse[]> => {
      const res = await $api.get('/api/concerts');
      return res.data;
    },
    getConcert: async (concertId: number): Promise<ConcertResponse> => {
      const res = await $api.get(`/api/concerts/${concertId}`);
      return res.data;
    },
    getConcertDates: async (concertId: number): Promise<ConcertDateResponse[]> => {
      const res = await $api.get(`/api/concerts/${concertId}/dates`);
      return res.data;
    },
    getSeats: async (concertId: number, dateId: number, queueToken: string): Promise<SeatResponse[]> => {
      const res = await $api.get(`/api/concerts/${concertId}/dates/${dateId}/seats`, {
        headers: { 'Queue-Token': queueToken }
      });
      return res.data;
    }
  };
};
