import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { AuthLayout } from './layout/auth-layout/auth-layout';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { MainLayout } from './layout/main-layout/main-layout';
import { Dashboard } from './features/dashboard/dashboard';
import { Booking } from './features/booking/booking';

export const routes: Routes = [
  { path: '', component: Landing },
  {
    path: 'auth',
    component: AuthLayout,
    children: [
      { path: 'login', component: Login },
      { path: 'register', component: Register },
    ],
  },
  {
    path: '',
    component: MainLayout,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'booking', component: Booking },
      { path: 'map', component: Map },
    ],
  },
];
