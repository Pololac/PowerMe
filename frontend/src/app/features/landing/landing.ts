import { ChangeDetectionStrategy, Component } from '@angular/core';
import { LandingNavbar } from '../../shared/components/landing-navbar/landing-navbar';

@Component({
  selector: 'app-landing',
  imports: [LandingNavbar],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Landing {}
