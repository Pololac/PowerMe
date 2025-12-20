import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { UserMenu } from '../user-menu/user-menu';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, UserMenu],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Navbar {
  readonly auth = inject(AuthService);

  readonly isLogged = this.auth.isLogged;
}
