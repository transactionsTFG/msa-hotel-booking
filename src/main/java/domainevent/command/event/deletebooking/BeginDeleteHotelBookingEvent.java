package domainevent.command.event.deletebooking;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.saga.bookingcreation.qualifier.BeginDeleteHotelBookingQualifier;
import domainevent.command.event.createbooking.BeginCreateHotelBookingEvent;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@BeginDeleteHotelBookingQualifier
@Local(CommandHandler.class)
public class BeginDeleteHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeginCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);

        long bookingId = this.gson.fromJson(json, Long.class);
        final String sagaId = UUID.randomUUID().toString();

        double moneyReturned = this.bookingService.deleteBooking(bookingId, sagaId);


        EventData eventData = new EventData(sagaId, Arrays.asList(), bookingId);

        LOGGER.info("***** INICIADA SAGA CANCELACION RESERVA ***** {}", sagaId);

        if (moneyReturned <= 0) {
            this.jmsCommandPublisher.publish(EventId.ROLLBACK_DELETE_HOTEL_BOOKING, eventData);
        } else {
            this.jmsCommandPublisher.publish(EventId.COMMIT_DELETE_HOTEL_BOOKING, eventData);
        }

    }

}
