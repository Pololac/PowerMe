package com.powerme.mapper;

import com.powerme.dto.BookingDto;
import com.powerme.entity.Booking;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingMapper {
    @Mapping(target = "stationName", source = "stationNameSnapshot")
    @Mapping(target = "stationAddress", source = "stationAddressSnapshot")
    @Mapping(target = "hourlyRate", source = "hourlyRateSnapshot")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);
}
