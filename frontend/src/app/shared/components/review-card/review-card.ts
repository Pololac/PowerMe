import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-review-card',
  imports: [CommonModule],
  templateUrl: './review-card.html',
  styleUrl: './review-card.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewCard {
  @Input() author!: string;
  @Input() city!: string;
  @Input() rating!: number;
  @Input() text!: string;
  @Input() avatarUrl!: string;

  starsArray = Array.from({ length: 5 });
}
