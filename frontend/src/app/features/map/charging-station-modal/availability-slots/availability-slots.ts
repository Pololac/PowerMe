import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  output,
  signal,
} from '@angular/core';
import { ChargingStationTimeSlotDto } from '../../../../core/models/dto/charging-station-availability.dto';

@Component({
  selector: 'app-availability-slots',
  imports: [],
  templateUrl: './availability-slots.html',
  styleUrl: './availability-slots.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvailabilitySlots {
  readonly slots = input<ChargingStationTimeSlotDto[]>([]);
  readonly selectedSlots = input<ChargingStationTimeSlotDto[]>([]);
  readonly slotClicked = output<ChargingStationTimeSlotDto>();

  // Nbr de slots affichés
  readonly pageSize = 8;
  // Position de départ
  readonly offset = signal(24);

  readonly visible = computed(() =>
    this.slots().slice(this.offset(), this.offset() + this.pageSize),
  );

  constructor() {
    effect(() => {
      const slots = this.slots();
      this.offset.set(0);

      // Afficher les slots à partir de midi
      const noonIndex = slots.findIndex((s) => s.start === '12:00');
      if (noonIndex !== -1) {
        this.offset.set(noonIndex);
      }
    });
  }

  canUp = computed(() => this.offset() > 0);
  canDown = computed(() => this.offset() + this.pageSize < this.slots().length);

  up() {
    if (this.canUp()) {
      this.offset.update((v) => v - 1);
    }
  }

  down() {
    if (this.canDown()) {
      this.offset.update((v) => v + 1);
    }
  }

  select(slot: ChargingStationTimeSlotDto) {
    if (!slot.available) return;
    this.slotClicked.emit(slot);
  }

  isSelected(slot: ChargingStationTimeSlotDto): boolean {
    return this.selectedSlots().some((s) => s.start === slot.start);
  }
}
