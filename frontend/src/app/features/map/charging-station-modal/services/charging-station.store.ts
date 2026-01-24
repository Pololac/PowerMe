import { Injectable, inject, signal, computed } from '@angular/core';
import { ChargingStationApi } from './charging-station.api';
import {ChargingStationDetailDto} from '../../../../core/models/dto/charging-station-detail.dto';
import {
  ChargingStationAvailabilityDto, ChargingStationTimeSlotDto
} from '../../../../core/models/dto/charging-station-availability.dto';


@Injectable({ providedIn: 'root' })
export class ChargingStationStore {
  private readonly api = inject(ChargingStationApi);

  /* ========= Station ========= */
  readonly station = signal<ChargingStationDetailDto | null>(null);
  readonly loading = signal(false);

  openStation(id: number) {
    this.loading.set(true);

    this.api.getOne(id).subscribe({
      next: (station) => this.station.set(station),
      error: () => this.station.set(null),
      complete: () => this.loading.set(false),
    });
  }

  closeStation() {
    this.station.set(null);
    this.resetSelection();
  }

  /* ========= Availability ========= */
  readonly date = signal<string | null>(null);
  readonly availability = signal<ChargingStationAvailabilityDto | null>(null);
  readonly availabilityLoading = signal(false);

  loadAvailability(date: string) {
    const station = this.station();
    if (!station) return;

    this.date.set(date);
    this.availabilityLoading.set(true);
    this.selectedSlots.set([]);

    this.api.getAvailability(station.id, date).subscribe({
      next: (data) => this.availability.set(data),
      error: () => this.availability.set(null),
      complete: () => this.availabilityLoading.set(false),
    });
  }
  readonly visibleSlots = computed(() => {
    const availability = this.availability();
    const selectedDate = this.date();

    if (!availability || !selectedDate) return [];

    const slots = availability.slots;

    const today = new Date();
    const todayStr = today.toISOString().slice(0, 10); // Convertit en string YYYY-MM-DD

    // Si pas aujourd’hui → tout afficher
    if (selectedDate !== todayStr) {
      return slots;
    }

    // Si aujourd’hui → filtrer à partir de maintenant
    const now = new Date();
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    return slots.filter((slot) => {
      const [sh, sm] = slot.start.split(':').map(Number);
      const [eh, em] = slot.end.split(':').map(Number);

      const start = sh * 60 + sm;
      const end = eh * 60 + em;

      // slot qui traverse minuit → on l'accepte si le start est après now
      if (end < start) {
        return start >= nowMinutes;
      }

      return start >= nowMinutes;
    });
  });

  /* ========= Slot selection ========= */
  readonly selectedSlots = signal<ChargingStationTimeSlotDto[]>([]);
  readonly selectedSlotsCount = computed(() => this.selectedSlots().length);

  selectSlot(slot: ChargingStationTimeSlotDto) {
    if (!slot.available) return;

    const slots = this.selectedSlots();

    // Si aucun slot sélectionné ou que le slot sélectionné n'est pas contigu au précédent, on réinitialise
    if (slots.length === 0 || slots.at(-1)!.end !== slot.start) {
      this.selectedSlots.set([slot]);
    } else {
      this.selectedSlots.set([...slots, slot]);
    }
  }

  /* ========= Derived ========= */
  // Station, date et slots choisis -> Résa peut avoir lieu
  readonly canBook = computed(
    () => !!this.station() && !!this.date() && this.selectedSlots().length > 0,
  );

  // Réinitialise toutes les valeurs qu'on vient de set
  resetSelection() {
    this.date.set(null);
    this.availability.set(null);
    this.selectedSlots.set([]);
  }
}
