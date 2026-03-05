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
