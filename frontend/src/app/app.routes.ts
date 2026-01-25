import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { authGuard } from './core/guards/auth-guard';
import { ResetPasswordConfirm } from './features/auth/reset-password-confirm/reset-password-confirm';
import { ResetPasswordRequest } from './features/auth/reset-password-request/reset-password-request';
import { Vehicles } from './features/dashboard/pages/vehicles/vehicles';
import { FavoriteStations } from './features/dashboard/pages/favorite-stations/favorite-stations';
import { Charges } from './features/dashboard/pages/charges/charges';
import { Balance } from './features/dashboard/pages/balance/balance';
import { PaymentMethods } from './features/dashboard/pages/payment-methods/payment-methods';
import { Support } from './features/dashboard/pages/support/support';
import { Invoices } from './features/dashboard/pages/invoices/invoices';
import { Faq } from './features/faq/faq';
import { DashboardLayout } from './features/dashboard/layout/dashboard-layout';
import { Profile } from './features/dashboard/pages/profile/profile';
import { Bookings } from './features/dashboard/pages/bookings/bookings';
import { BookingFlow } from './features/booking/booking-flow';
import { MapPage } from './features/map/map-page/map-page';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'map', component: MapPage },
  { path: 'faq', component: Faq },

  // Auth = pages avec wrapper visuel
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  //{ path: 'activate', component: ActivateAccount },
  { path: 'reset-password', component: ResetPasswordRequest },
  { path: 'reset-password/:token', component: ResetPasswordConfirm },

  { path: 'booking', component: BookingFlow, canActivate: [authGuard] },

  {
    path: 'dashboard',
    component: DashboardLayout,
    canActivate: [authGuard],
    children: [
      // page par défaut du dashboard
      { path: '', pathMatch: 'full', redirectTo: 'profile' },

      { path: 'profile', component: Profile },
      { path: 'bookings', component: Bookings },
      { path: 'vehicles', component: Vehicles },
      { path: 'favorite-stations', component: FavoriteStations },
      { path: 'charges', component: Charges },
      { path: 'balance', component: Balance },
      { path: 'payment-methods', component: PaymentMethods },
      { path: 'invoices', component: Invoices },
      { path: 'support', component: Support },
    ],
  },
];
