package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.booking.Booking;
import business.booking.BookingDTO;
import msa.commons.saga.SagaPhases;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "customerDTO", ignore = true)
    BookingDTO entityToDTO(Booking booking);
    
    @Mapping(target = "bookingLines", ignore = true)
    @Mapping(target = "version", ignore = true)
    Booking createBookingDTOToEntity(BookingDTO createBookingDTO, SagaPhases status);
}
