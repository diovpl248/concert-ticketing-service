import type { ConcertResponse, SeatResponse } from '~/types/concert';

export const useConcertApi = () => {
  const { $api } = useNuxtApp();

  return {
    getConcerts: async (): Promise<ConcertResponse[]> => {
      const res = await $api.get('/api/concerts');
      return res.data;
    },
    getConcert: async (concertId: number): Promise<ConcertResponse> => {
      const res = await $api.get(`/api/concerts/${concertId}`);
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
