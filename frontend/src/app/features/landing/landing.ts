import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
} from '@angular/core';
import { LandingNavbar } from '../../shared/components/landing-navbar/landing-navbar';
import { Router } from '@angular/router';
import { Footer } from '../../shared/components/footer/footer';
import { ReviewSlider } from '../../shared/components/review-slider/review-slider';

@Component({
  selector: 'app-landing',
  imports: [LandingNavbar, ReviewSlider, Footer],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Landing implements AfterViewInit {
  private router = inject(Router);
  private host = inject(ElementRef);

  ngAfterViewInit() {
    const elements = document.querySelectorAll('.reveal') as NodeListOf<HTMLElement>;
    const counters = document.querySelectorAll('.pm-kpi-number') as NodeListOf<HTMLElement>;

    const animateCounter = (el: HTMLElement) => {
      const target = +el.dataset['target']!;
      const duration = 1200;
      let startTime: number | null = null;

      const step = (timestamp: number) => {
        if (!startTime) startTime = timestamp;
        const progress = Math.min((timestamp - startTime) / duration, 1);
        const current = Math.floor(progress * target);

        el.textContent = current.toLocaleString('fr-FR');

        if (progress < 1) requestAnimationFrame(step);
      };

      requestAnimationFrame(step);
    };

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('show');

            // 👉 Si c’est un KPI, on déclenche l’animation
            if (entry.target.classList.contains('pm-kpi-number')) {
              animateCounter(entry.target as HTMLElement);
            }
          }
        });
      },
      { threshold: 0.2 },
    );

    // Observer pour les reveals et les KPI
    Array.from(elements).forEach((el) => observer.observe(el));
    Array.from(counters).forEach((el) => observer.observe(el));
  }

  onFindStation() {
    this.router.navigate(['/map']);
  }

  onRentStation() {
    // Plus tard : ouvrir la modale de connexion
    console.log('TODO: ouvrir modale login pour louer une borne');
  }
}
