import { SocketType } from '../../../core/models/enums/socket-type.enum';

export const SOCKET_TYPE_LABEL: Record<SocketType, string> = {
  [SocketType.TYPE_2]: 'Type 2',
  [SocketType.TYPE_2S]: 'Type 2s',
  [SocketType.CCS]: 'CCS',
  [SocketType.CHADEMO]: 'Chademo',
};
