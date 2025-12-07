import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-navbar',
  imports: [],
  templateUrl: './landing-navbar.html',
  styleUrl: './landing-navbar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingNavbar {
  private router = inject(Router);

  scrolled = signal(false);

  constructor() {
    // Detecte le scroll
    effect(() => {
      const handler = () => {
        this.scrolled.set(window.scrollY > 20);
      };
      window.addEventListener('scroll', handler);
      return () => window.removeEventListener('scroll', handler);
    });
  }

  goToMap() {
    this.router.navigate(['/map']);
  }

  addStationClicked() {
    // Plus tard : ouvrir modale login
    console.log('TODO : Open Login Modal');
  }
}
