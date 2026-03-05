import { useNuxtApp } from '#imports';
import type { BookingRequest, BookingResponse } from '~/types/booking';

export const useBookingApi = () => {
  const { $api } = useNuxtApp() as any;

  return {
    createBooking: async (request: BookingRequest, queueToken: string): Promise<BookingResponse> => {
      const res = await $api.post('/api/bookings', request, {
        headers: { 'Queue-Token': queueToken }
      });
      return res.data;
    }
  };
};
