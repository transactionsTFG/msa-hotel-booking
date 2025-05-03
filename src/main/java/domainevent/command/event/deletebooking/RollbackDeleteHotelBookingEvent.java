package domainevent.command.event.deletebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.RollbackDeleteHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;

@Stateless
@RollbackDeleteHotelBookingEventQualifier
@Local(CommandHandler.class)
public class RollbackDeleteHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(RollbackDeleteHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("---- ROLLBACK CANCELAR RESERVA INICIADO ----");
        LOGGER.info("JSON recibido: {}", json);

        EventData eventData = EventData.fromJson(json, Long.class);
        long bookingId = (Long) eventData.getData();

        boolean success = this.bookingService.rollbackDeleteBooking(bookingId);

        LOGGER.info("---- ROLLBACK CANCELAR RESERVA " + (success ? "EXISTOSO" : "FALLIDO") + " ----");

    }

}
