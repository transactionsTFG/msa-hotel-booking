package domainevent.command.event.deletebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.CommitDeleteHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;

@Stateless
@CommitDeleteHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CommitDeleteHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CommitDeleteHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("---- COMMIT CANCELAR RESERVA INICIADO ----");
        LOGGER.info("JSON recibido: {}", json);

        EventData eventData = EventData.fromJson(json, Long.class);
        long bookingId = (Long) eventData.getData();

        boolean success = this.bookingService.commitDeleteBooking(bookingId);

        LOGGER.info("---- COMMIT CANCELAR TERMINADO " + (success ? "EXISTOSO" : "FALLIDO") + " ----");
    }

}
