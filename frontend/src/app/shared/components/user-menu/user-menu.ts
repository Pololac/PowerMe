import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { UserMenuTrigger } from './user-menu-trigger/user-menu-trigger';
import { UserMenuDropdown } from './user-menu-dropdown/user-menu-dropdown';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-user-menu',
  imports: [UserMenuTrigger, UserMenuDropdown],
  templateUrl: './user-menu.html',
  styleUrl: './user-menu.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserMenu {
  readonly auth = inject(AuthService);

  readonly open = signal(false);

  toggle() {
    this.open.update((v) => !v);
  }

  close() {
    this.open.set(false);
  }
}
