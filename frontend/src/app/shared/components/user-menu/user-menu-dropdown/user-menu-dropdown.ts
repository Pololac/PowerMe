import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth/auth-service';

interface UserMenuItem {
  label: string;
  icon: string; // nom du fichier svg dans /assets/icons
  route?: string;
  action?: () => void;
  danger?: boolean;
}

@Component({
  selector: 'app-user-menu-dropdown',
  imports: [],
  templateUrl: './user-menu-dropdown.html',
  styleUrl: './user-menu-dropdown.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserMenuDropdown {
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  readonly closeMenu = output<void>();

  readonly items: readonly UserMenuItem[] = [
    {
      label: 'Profil',
      icon: 'profile',
      route: '/dashboard/profile',
    },
    {
      label: 'Mes bornes',
      icon: 'charging',
      route: '/station',
    },
    {
      label: 'Tableau de bord',
      icon: 'dashboard',
      route: '/dashboard',
    },
    {
      label: 'Paramètres',
      icon: 'settings',
      route: '/dashboard/settings',
    },
    {
      label: 'Assistance',
      icon: 'support',
      route: '/support',
    },
    {
      label: 'Déconnexion',
      icon: 'logout',
      danger: true,
      action: () => this.logout(),
    },
  ];

  onItemClick(item: UserMenuItem): void {
    if (item.route) {
      this.navigate(item.route);
      return;
    }

    item.action?.();
  }

  private navigate(route: string): void {
    this.router.navigateByUrl(route);
    this.closeMenu.emit();
  }

  private logout(): void {
    this.auth.logout();
    this.closeMenu.emit();
  }
}
