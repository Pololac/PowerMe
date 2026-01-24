package com.powerme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record BookingCreateRequestDto(
        @NotNull Long stationId,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @NotNull
        @Size(min = 1)
        List<@NotNull @Min(0) Integer> slots
) {}
