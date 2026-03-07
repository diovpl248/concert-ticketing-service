import type { BookingRequest, BookingResponse, BookingDetailResponse } from '~/types/booking';

export const useBookingApi = () => {
  const { $api } = useNuxtApp();

  return {
    createBooking: async (request: BookingRequest, queueToken: string): Promise<BookingResponse> => {
      const res = await $api.post('/api/bookings', request, {
        headers: { 'Queue-Token': queueToken }
      });
      return res.data;
    },
    fetchBookingDetail: async (bookingId: number): Promise<BookingDetailResponse> => {
      const res = await $api.get(`/api/bookings/${bookingId}`);
      return res.data;
    }
  };
};
