package business.services;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.dto.CreateHotelBookingDTO;
import business.dto.DeleteBookingLineDTO;
import business.dto.UpdateHotelBookingDTO;

public interface BookingService {

    boolean createBookingAsync(CreateHotelBookingDTO createBookingDTO);

    BookingDTO createBookingSync(BookingDTO bookingDTO);

    boolean validateSagaId(long bookingId, String sagaId);

    boolean updateOnlyReservation(BookingDTO bookingDTO);

    boolean updateBooking(BookingWithLinesDTO booking);

    boolean checkRoomsAvailabilityByCreateHotelBooking(CreateHotelBookingDTO createHotelBookingDTO);

    BookingWithLinesDTO getBookingWithLines(long bookingId);

    boolean beginDeleteBooking(long bookingId);

    double deleteBooking(long bookingId, String sagaId);

    boolean rollbackDeleteBooking(long bookingId);

    boolean commitDeleteBooking(long bookingId);

    boolean beginDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO);

    double deleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO, String sagaId);

    boolean rollbackDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO);

    boolean commitDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO);

    boolean beginModifyBooking(UpdateHotelBookingDTO updateBookingDTO);

    boolean modifyBooking(UpdateHotelBookingDTO updateBookingDTO);

    boolean commitModifyBooking(UpdateHotelBookingDTO updateBookingDTO);

    boolean rollbackModifyBooking(UpdateHotelBookingDTO updateBookingDTO);

    boolean checkRoomsAvailabilityByUpdateHotelBooking(UpdateHotelBookingDTO updateHotelBookingDTO);
}
