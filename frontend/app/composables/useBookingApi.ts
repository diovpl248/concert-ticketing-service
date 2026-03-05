import type { BookingRequest, BookingResponse } from '~/types/booking';

export const useBookingApi = () => {
  const { $api } = useNuxtApp();

  return {
    createBooking: async (request: BookingRequest, queueToken: string): Promise<BookingResponse> => {
      const res = await $api.post('/api/bookings', request, {
        headers: { 'Queue-Token': queueToken }
      });
      return res.data;
    }
  };
};
