package domainevent.command.event.deletebookingline;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.DeleteBookingLineDTO;
import business.saga.bookingcreation.qualifier.BeginDeleteHotelBookingLineQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.DeleteHotelBookingLineCommand;

@Stateless
@BeginDeleteHotelBookingLineQualifier
@Local(CommandHandler.class)
public class BeginDeleteHotelBookingLineEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeginDeleteHotelBookingLineEvent.class);

    @Override
    public void publishCommand(String json) {

        LOGGER.info("JSON recibido: {}", json);

        DeleteBookingLineDTO deleteBookingLineDTO = this.gson.fromJson(json, DeleteBookingLineDTO.class);
        final String sagaId = UUID.randomUUID().toString();

        double moneyReturned = this.bookingService.deleteBookingLine(deleteBookingLineDTO, sagaId);

        EventData eventData = new EventData(sagaId, Arrays.asList(), new DeleteHotelBookingLineCommand(
                deleteBookingLineDTO.getBookingId(), deleteBookingLineDTO.getRoomId()));

        LOGGER.info("***** INICIADA SAGA CANCELACION LINEA RESERVA ***** {}", sagaId);

        if (moneyReturned <= 0) {
            this.jmsCommandPublisher.publish(EventId.ROLLBACK_DELETE_HOTEL_BOOKINGLINE, eventData);
        } else {
            this.jmsCommandPublisher.publish(EventId.COMMIT_DELETE_HOTEL_BOOKINGLINE, eventData);
        }

    }

}
