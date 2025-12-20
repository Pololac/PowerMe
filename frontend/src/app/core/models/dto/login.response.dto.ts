import { UserDto } from './user.dto';

export interface LoginResponseDto {
  accessToken: string;
  user: UserDto;
}
