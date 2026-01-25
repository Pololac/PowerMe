package com.powerme.dto;

public record TimeSlotDto(
        int index,  // clé technique
        String start,   // "HH:mm"
        String end,
        boolean available
) {}
