export enum BookingStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface BookingRequest {
  concertId: number;
  dateId: number;
  seatId: number;
}

export interface BookingResponse {
  bookingId: number;
  seatId: number;
  status: BookingStatus;
  expiresAt: string;
}

export interface ConcertInfo {
  id: number;
  title: string;
  venue: string;
  thumbnailUrl: string;
  date: string;
  time: string;
}

export interface SeatInfo {
  id: number;
  section: string;
  row: string;
  col: string;
  price: number;
}

export interface PaymentInfo {
  paymentId: number;
  amount: number;
  paymentMethod: string;
  status: string;
  issuedAt: string;
}

export interface BookingDetailResponse {
  bookingId: number;
  concert: ConcertInfo;
  seat: SeatInfo;
  payment: PaymentInfo;
}
