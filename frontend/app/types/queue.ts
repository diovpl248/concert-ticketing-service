export enum QueueStatus {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED'
}

export interface QueueTokenResponse {
  token: string;
  status: QueueStatus;
  position: number;
  estimatedWaitTime: number;
}

export interface QueueStatusResponse {
  token: string;
  status: QueueStatus;
  position: number;
  estimatedWaitTime: number;
}
