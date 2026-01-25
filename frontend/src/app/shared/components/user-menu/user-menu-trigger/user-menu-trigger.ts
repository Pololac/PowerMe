import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { User } from '../../../../core/models/domain/user.model';

@Component({
  selector: 'app-user-menu-trigger',
  imports: [],
  templateUrl: './user-menu-trigger.html',
  styleUrl: './user-menu-trigger.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserMenuTrigger {
  readonly user = input.required<User>();
  readonly open = input.required<boolean>();
  readonly toggleMenu = output<void>();
}
