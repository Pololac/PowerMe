export interface UserDto {
  id: number;
  email: string;
  roles: string[];
  firstname?: string | null;
  lastname?: string | null;
}
