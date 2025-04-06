package business.services;

import business.booking.BookingDTO;
import business.dto.CreateBookingDTO;

public interface BookingService {
    BookingDTO beginCreateBooking(CreateBookingDTO createBookingDTO);
    void confirmCreateBooking(long bookingId);
    void cancelCreateBooking(long bookingId);
}
