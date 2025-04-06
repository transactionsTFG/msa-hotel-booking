package business.servicesevent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import business.booking.BookingDTO;
import business.dto.CreateBookingDTO;
import business.services.BookingService;
import domainevent.publisher.IJMSEventPublisher;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.CreateHotelBookingCommand;
import msa.commons.parser.NumberParser;

@Stateless
public class BookingServiceEventAdapterImpl implements BookingServiceEventAdapter {

    private BookingService bookingService;
    private IJMSEventPublisher jmsEventPublisher;

    @EJB
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;    
    }

    @Inject
    public void setJmsEventDispatcher(IJMSEventPublisher jmsEventPublisher) {
        this.jmsEventPublisher = jmsEventPublisher;
    }

    @Override
    public boolean beginCreateBooking(CreateBookingDTO createBookingDTO) {
        BookingDTO bookingDTO = this.bookingService.beginCreateBooking(createBookingDTO);

        if (bookingDTO == null) 
            return false;

        this.jmsEventPublisher.publish(EventId.VALIDATE_CUSTOMER_AND_ROOMS, new CreateHotelBookingCommand(
            NumberParser.toLong(createBookingDTO.getUserId()),
            createBookingDTO.getStartDate(),
            createBookingDTO.getEndDate(),
            createBookingDTO.getNumberOfNights(),
            createBookingDTO.getWithBreakfast(),
            createBookingDTO.getPeopleNumber(),
            createBookingDTO.getCustomerDNI(),
            createBookingDTO.getRooIds()));
        return true;
    }

}
