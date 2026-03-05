import { defineStore } from 'pinia';
import { ref } from 'vue';
import { usePaymentApi } from '../composables/usePaymentApi';
import type { PaymentRequest, PaymentResponse } from '~/types/payment';

export const usePaymentStore = defineStore('payment', () => {
  const isProcessing = ref(false);
  const lastPaymentResult = ref<PaymentResponse | null>(null);

  const processPayment = async (request: PaymentRequest, queueToken: string) => {
    const paymentApi = usePaymentApi();
    isProcessing.value = true;
    try {
      const data = await paymentApi.processPayment(request, queueToken);
      lastPaymentResult.value = data;
      return data;
    } finally {
      isProcessing.value = false;
    }
  };

  return { isProcessing, lastPaymentResult, processPayment };
});
