import { ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { ReviewCard } from '../review-card/review-card';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-review-slider',
  imports: [CommonModule, ReviewCard],
  templateUrl: './review-slider.html',
  styleUrl: './review-slider.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSlider {
  @ViewChild('container', { static: true })
  container!: ElementRef<HTMLDivElement>;

  reviews = [
    {
      author: 'Julien M.',
      city: 'Paris',
      rating: 5,
      avatarUrl: 'assets/images/landing/user1.webp',
      text: 'J’ai réservé une borne à 3 minutes de chez moi. Ultra simple et moins cher que les bornes publiques. Je l’utilise maintenant toutes les semaines.',
    },
    {
      author: 'Sophie R.',
      city: 'Lyon',
      rating: 5,
      avatarUrl: 'assets/images/landing/user2.webp',
      text: 'Ma borne restait inutilisée la plupart du temps. Avec PowerMe, je rentabilise mon installation sans effort et les échanges avec les conducteurs sont fluides.',
    },
    {
      author: 'Camille T.',
      city: 'Toulouse',
      rating: 4,
      avatarUrl: 'assets/images/landing/user3.webp',
      text: 'J’ai pu recharger pendant mes déplacements pro sans tourner pendant 20 minutes. Le système est clair, sécurisé et pratique.',
    },
    {
      author: 'Hugo L.',
      city: 'Nantes',
      rating: 5,
      avatarUrl: 'assets/images/landing/user4.webp',
      text: 'J’ai gagné plus que prévu le premier mois. L’application gère tout : horaires, paiements, contact. Très bonne expérience.',
    },
    {
      author: 'Emilie D.',
      city: 'Lille',
      rating: 4,
      avatarUrl: 'assets/images/landing/user5.webp',
      text: 'J’ai trouvé une borne disponible même tard le soir, ce qui est impossible avec les bornes publiques. Service au top.',
    },
    {
      author: 'Élodie V.',
      city: 'Bordeaux',
      rating: 5,
      avatarUrl: 'assets/images/landing/user6.webp',
      text: 'L’interface est super intuitive. J’ai réservé en deux minutes et l’hôte était très accueillant. Je recommande vraiment PowerMe.',
    },
  ];

  scrollLeft() {
    this.container.nativeElement.scrollBy({ left: -320, behavior: 'smooth' });
  }

  scrollRight() {
    this.container.nativeElement.scrollBy({ left: 320, behavior: 'smooth' });
  }
}
