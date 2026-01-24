import {ChangeDetectionStrategy, Component, computed, input, output, signal} from '@angular/core';
import {DatepickerValue, NgxsmkDatepickerComponent} from 'ngxsmk-datepicker';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-availability-calendar',
  imports: [NgxsmkDatepickerComponent, FormsModule],
  templateUrl: './availability-calendar.html',
  styleUrl: './availability-calendar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvailabilityCalendar {
  readonly selectedDate = input<string | null>(null);
  // Signal interne UNIQUEMENT pour le datepicker
  readonly date = signal<Date | null>(null);
  readonly dateSelected = output<string>();

  // 👉 minDate simple et stable
  readonly today = new Date();

  onDateChange(value: DatepickerValue) {
    if (!(value instanceof Date)) return;

    this.date.set(value);
    this.dateSelected.emit(this.toDateString(value));
  }

  private toDateString(date: Date): string {
    return `${date.getFullYear()}-${String(
      date.getMonth() + 1
    ).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}
