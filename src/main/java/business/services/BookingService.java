package business.services;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.dto.CreateHotelBookingDTO;
import business.dto.DeleteBookingLineDTO;
import business.dto.UpdateBookingDTO;

public interface BookingService {

    boolean createBookingAsync(CreateHotelBookingDTO createBookingDTO);

    BookingDTO createBookingSync(BookingDTO bookingDTO);

    boolean validateSagaId(long bookingId, String sagaId);

    boolean updateOnlyReservation(BookingDTO bookingDTO);

    boolean updateBooking(BookingWithLinesDTO booking);

    boolean checkRoomsAvailability(CreateHotelBookingDTO createHotelBookingDTO);

    BookingWithLinesDTO getBookingWithLines(long bookingId);

    boolean beginDeleteBooking(long bookingId);

    double deleteBooking(long bookingId, String sagaId);

    boolean rollbackDeleteBooking(long bookingId);

    boolean commitDeleteBooking(long bookingId);

    double deleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO);

    boolean beginModifyBooking(UpdateBookingDTO updateBookingDTO);
}
