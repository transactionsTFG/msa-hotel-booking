package domainevent.command.event.deletebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.RollbackDeleteHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.removereservation.RemoveBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.DeleteReservation;

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
        RemoveBookingCommand c = (RemoveBookingCommand) eventData.getData();

        boolean success = this.bookingService.rollbackDeleteBooking(c.getIdBooking());
        eventData.setOperation(DeleteReservation.DELETE_RESERVATION_ONLY_AIRLINE_ROLLBACK);
        this.jmsCommandPublisher.publish(EventId.REMOVE_RESERVATION_TRAVEL, eventData);
        LOGGER.info("---- ROLLBACK CANCELAR RESERVA " + (success ? "EXISTOSO" : "FALLIDO") + " ----");

    }

}
