package business.servicesevent;

import business.dto.CreateBookingDTO;

public interface BookingServiceEventAdapter {
    boolean beginCreateBooking(CreateBookingDTO createBookingDTO);
}
