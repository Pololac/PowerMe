package com.powerme.dto;

public record TimeSlotDto(
        String start,   // "HH:mm"
        String end,
        boolean available
) {}
