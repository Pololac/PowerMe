package com.powerme.service.pricing;

import java.math.BigDecimal;
import java.util.List;

public interface PricingService {
    BigDecimal computePrice(
            BigDecimal hourlyRate,
            List<Integer> slots
    );
}
