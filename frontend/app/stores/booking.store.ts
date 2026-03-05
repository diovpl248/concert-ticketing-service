import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useBookingApi } from '../composables/useBookingApi';
import type { BookingRequest, BookingResponse } from '~/types/booking';

export const useBookingStore = defineStore('booking', () => {
  const isBooking = ref(false);
  const lastBookingResult = ref<BookingResponse | null>(null);

  const bookingApi = useBookingApi();

  const createBooking = async (request: BookingRequest, queueToken: string) => {
    isBooking.value = true;
    try {
      const data = await bookingApi.createBooking(request, queueToken);
      lastBookingResult.value = data;
      return data;
    } finally {
      isBooking.value = false;
    }
  };

  return { isBooking, lastBookingResult, createBooking };
});
