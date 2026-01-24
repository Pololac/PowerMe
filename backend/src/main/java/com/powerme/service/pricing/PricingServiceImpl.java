package com.powerme.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PricingServiceImpl implements PricingService {

    private static final BigDecimal HALF_HOUR_FACTOR =
            BigDecimal.valueOf(0.5);

    private static final BigDecimal SERVICE_FEE =
            BigDecimal.valueOf(0.50);

    @Override
    public BigDecimal computePrice(
            BigDecimal hourlyRate,
            List<Integer> slots
    ) {
        if (slots == null || slots.isEmpty()) {
            throw new IllegalArgumentException("Slots cannot be empty");
        }

        BigDecimal durationHours = HALF_HOUR_FACTOR
                .multiply(BigDecimal.valueOf(slots.size()));

        BigDecimal basePrice = hourlyRate
                .multiply(durationHours);

        BigDecimal totalPrice = basePrice
                .add(SERVICE_FEE)
                .setScale(2, RoundingMode.HALF_UP);

        return totalPrice;
    }
}
