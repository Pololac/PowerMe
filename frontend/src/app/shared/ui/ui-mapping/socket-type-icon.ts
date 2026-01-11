import { SocketType } from '../../../core/models/enums/socket-type.enum';

export const SOCKET_TYPE_ICON: Record<SocketType, string> = {
  [SocketType.TYPE_2]: 'plug-type2',
  [SocketType.TYPE_2S]: 'plug-type2',
  [SocketType.CCS]: 'plug-ccs',
  [SocketType.CHADEMO]: 'plug-chademo',
};
