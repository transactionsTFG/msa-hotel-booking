package business.services;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.dto.CreateBookingDTO;

public interface BookingService {

    boolean createBookingAsync(CreateBookingDTO createBookingDTO);

    BookingDTO createBookingSync(BookingDTO bookingDTO);

    boolean validateSagaId(long bookingId, String sagaId);

    boolean updateOnlyReservation(BookingDTO bookingDTO);

    boolean updateBooking(BookingWithLinesDTO booking);
}
