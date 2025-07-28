package domainevent.command.event.deletebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.CommitDeleteHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.removereservation.RemoveBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.DeleteReservation;

@Stateless
@CommitDeleteHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CommitDeleteHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CommitDeleteHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("---- COMMIT CANCELAR RESERVA INICIADO ----");
        LOGGER.info("JSON recibido: {}", json);

        EventData eventData = EventData.fromJson(json, RemoveBookingCommand.class);
        RemoveBookingCommand c = (RemoveBookingCommand) eventData.getData();

        boolean success = this.bookingService.commitDeleteBooking(c.getIdBooking());

        LOGGER.info("---- COMMIT CANCELAR TERMINADO " + (success ? "EXISTOSO" : "FALLIDO") + " ----");
        eventData.setOperation(DeleteReservation.DELETE_RESERVATION_ONLY_AIRLINE_COMMIT);
        this.jmsCommandPublisher.publish(EventId.REMOVE_RESERVATION_TRAVEL, eventData);
    }

}
