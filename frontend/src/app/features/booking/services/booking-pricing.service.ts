import { Injectable } from '@angular/core';
import {
  BookingPricingInput,
  BookingPricingResult,
} from '../../../core/models/dto/booking-pricing.dto';

@Injectable({ providedIn: 'root' })
export class BookingPricingService {
  computePricing(input: BookingPricingInput): BookingPricingResult {
    const durationHours = input.slots * 0.5;
    const serviceFee = 0.5;

    const total = serviceFee + input.hourlyRate * durationHours;

    return {
      durationHours,
      serviceFee,
      total: Math.round(total * 100) / 100, // Arrondit à deux décimales
    };
  }
}
