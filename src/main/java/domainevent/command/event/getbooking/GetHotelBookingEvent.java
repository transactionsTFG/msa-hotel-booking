package domainevent.command.event.getbooking;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingWithLinesDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.qualifier.GetHotelBookingEventQualifier;

@Stateless
@GetHotelBookingEventQualifier
@Local(CommandHandler.class)
public class GetHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(GetHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);
        String bookingId = this.gson.fromJson(json, String.class);
        BookingWithLinesDTO bookingWithLinesDTO = this.bookingService.getBookingWithLines(Long.parseLong(bookingId));
        final String sagaId = UUID.randomUUID().toString();
        EventData eventData = new EventData(sagaId, Arrays.asList(), bookingId);
        if (bookingWithLinesDTO == null) {
            this.jmsCommandPublisher.publish(EventId.CANCEL_GET_HOTEL_BOOKING, eventData);
        } else {
            eventData.setData(bookingWithLinesDTO);
            this.jmsCommandPublisher.publish(EventId.CONFIRM_GET_HOTEL_BOOKING, eventData);
        }
    }

}
