export enum PaymentStatus {
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

export interface PaymentRequest {
  bookingId: number;
  paymentMethod: string;
  amount: number;
}

export interface PaymentResponse {
  paymentId: number;
  status: PaymentStatus;
  ticketCode: string;
}
