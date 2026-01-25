import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BookingIntentService {
  private readonly pendingLocationId = signal<number | null>(null);

  set(locationId: number) {
    this.pendingLocationId.set(locationId);
  }

  consume(): number | null {
    const id = this.pendingLocationId();
    this.pendingLocationId.set(null);
    return id;
  }
}
