import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useBookingApi } from '../composables/useBookingApi';
import type { BookingRequest, BookingResponse, BookingDetailResponse } from '~/types/booking';

export const useBookingStore = defineStore('booking', () => {
  const isBooking = ref(false);
  const lastBookingResult = ref<BookingResponse | null>(null);

  const bookingApi = useBookingApi();

  const currentBookingDetail = ref<BookingDetailResponse | null>(null);

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

  const fetchBookingDetail = async (bookingId: number) => {
	const data = await bookingApi.fetchBookingDetail(bookingId);
	currentBookingDetail.value = data;
  };

  return { isBooking, lastBookingResult, currentBookingDetail, createBooking, fetchBookingDetail };
});
