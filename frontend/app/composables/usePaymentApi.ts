import type { PaymentRequest, PaymentResponse } from '~/types/payment';

export const usePaymentApi = () => {
  const { $api } = useNuxtApp();

  return {
    processPayment: async (request: PaymentRequest, queueToken: string): Promise<PaymentResponse> => {
      const res = await $api.post('/api/payments', request, {
        headers: { 'Queue-Token': queueToken }
      });
      return res.data;
    }
  };
};
