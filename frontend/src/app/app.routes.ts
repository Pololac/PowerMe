import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Dashboard } from './features/dashboard/dashboard';
import { Booking } from './features/booking/booking';
import { authGuard } from './core/guards/auth-guard';
import { Map } from './features/map/map';
import { ResetPasswordConfirm } from './features/auth/reset-password-confirm/reset-password-confirm';
import { ResetPasswordRequest } from './features/auth/reset-password-request/reset-password-request';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'map', component: Map },

  // Auth = pages avec wrapper visuel
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  //{ path: 'activate', component: ActivateAccount },
  { path: 'reset-password', component: ResetPasswordRequest },
  { path: 'reset-password/:token', component: ResetPasswordConfirm },

  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'booking', component: Booking, canActivate: [authGuard] },
];
