import { UserDto } from '../dto/user.dto';
import { User } from '../domain/user.model';

export function userFromDto(dto: UserDto): User {
  return {
    id: dto.id,
    email: dto.email,
    roles: dto.roles,
  };
}
