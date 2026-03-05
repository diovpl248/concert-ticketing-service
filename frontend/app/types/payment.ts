export enum PaymentStatus {
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

export enum PaymentMethod {
  CARD = 'CARD',
  VIRTUAL_ACCOUNT = 'VIRTUAL_ACCOUNT'
}

export interface PaymentRequest {
  concertId: number;
  bookingId: number;
  paymentMethod: PaymentMethod;
  amount: number;
}

export interface PaymentResponse {
  paymentId: number;
  status: PaymentStatus;
  ticketCode: string;
}
