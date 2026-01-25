import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface SidebarMenuItem {
  label: string;
  icon: string; // nom du fichier svg dans /assets/icons
  route: string;
}

@Component({
  selector: 'app-dashboard-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './dashboard-sidebar.html',
  styleUrl: './dashboard-sidebar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardSidebar {
  readonly items: readonly SidebarMenuItem[] = [
    {
      label: 'Profil',
      icon: 'profile',
      route: 'account/me',
    },
    {
      label: 'Paramètres',
      icon: 'settings',
      route: 'account/settings',
    },
    {
      label: 'Véhicules',
      icon: 'car',
      route: 'vehicles',
    },
    {
      label: 'Stations favorites',
      icon: 'charging',
      route: 'favorite-stations',
    },
    {
      label: 'Recharges',
      icon: 'charge',
      route: 'charges',
    },
    {
      label: 'Solde',
      icon: 'wallet',
      route: 'balance',
    },
    {
      label: 'Moyens de paiement',
      icon: 'card',
      route: 'payment-methods',
    },
    {
      label: 'Factures',
      icon: 'bill',
      route: 'invoices',
    },
    {
      label: 'Assistance',
      icon: 'support',
      route: 'support',
    },
  ];
}
