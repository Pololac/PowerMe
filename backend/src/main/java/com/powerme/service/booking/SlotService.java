package com.powerme.service.booking;

import com.powerme.exception.ValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SlotService {

    private static final int SLOT_MINUTES = 30;
    private static final int SLOTS_PER_DAY = 48;

    public SlotRange computeRange(LocalDate date, List<Integer> slots) {

        List<Integer> sorted = slots.stream().distinct().sorted().toList();

        if (sorted.isEmpty()) {
            throw new ValidationException("Aucun créneau sélectionné");
        }

        if (sorted.getFirst() < 0 || sorted.getLast() >= SLOTS_PER_DAY) {
            throw new ValidationException("Créneaux hors plage");
        }

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) != sorted.get(i - 1) + 1) {
                throw new ValidationException("Les créneaux doivent être consécutifs");
            }
        }

        LocalDateTime start = date.atStartOfDay() // Transforme LocalDate (2026-02-01) en LocalDateTime à 00:00
                .plusMinutes((long) sorted.getFirst() * SLOT_MINUTES); // Détermine heure début en f° de l'index du slot

        LocalDateTime end = date.atStartOfDay()
                .plusMinutes((long) (sorted.getLast() + 1) * SLOT_MINUTES);

        ZoneId zone = ZoneId.systemDefault();

        return new SlotRange(
                start.atZone(zone).toInstant(), // Convertit en UTC
                end.atZone(zone).toInstant(),
                sorted
        );
    }

    public record SlotRange(
            Instant start,
            Instant end,
            List<Integer> slots
    ) {}
}
