package business.services;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.dto.CreateHotelBookingDTO;

public interface BookingService {

    boolean createBookingAsync(CreateHotelBookingDTO createBookingDTO);

    BookingDTO createBookingSync(BookingDTO bookingDTO);

    boolean validateSagaId(long bookingId, String sagaId);

    boolean updateOnlyReservation(BookingDTO bookingDTO);

    boolean updateBooking(BookingWithLinesDTO booking);

    boolean checkRoomsAvailability(CreateHotelBookingDTO createHotelBookingDTO);

    BookingWithLinesDTO getBookingWithLines(long bookingId);
}
