import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { UserMenu } from '../user-menu/user-menu';
import { AuthService } from '../../../core/services/auth-service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, UserMenu],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Navbar {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly isAuthenticated = this.auth.isAuthenticated;

  readonly currentUrl = signal(this.router.url);

  constructor() {
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)).subscribe(() => {
      this.currentUrl.set(this.router.url);
    });
  }
}
