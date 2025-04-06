package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.booking.Booking;
import business.booking.BookingDTO;
import business.dto.CreateBookingDTO;
import msa.commons.saga.SagaPhases;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingDTO entityToDTO(Booking booking);

    Booking createBookingDTOToEntity(CreateBookingDTO createBookingDTO, SagaPhases status);
}
