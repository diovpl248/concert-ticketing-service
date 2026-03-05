export enum SeatStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  SOLD_OUT = 'SOLD_OUT'
}

export interface ConcertDateResponse {
  id: number;
  datetime: string;
}

export interface ConcertResponse {
  id: number;
  title: string;
  category: string;
  thumbnailUrl: string;
  venue: string;
  dates: ConcertDateResponse[];
}

export interface SeatResponse {
  id: number;
  section: string;
  rowNo: string;
  colNo: string;
  price: number;
  status: SeatStatus;
}

export interface DisplayConcert {
  id: number;
  title: string;
  category: string;
  date: string;
  venue: string;
  image: string;
  badge: string | null;
}
