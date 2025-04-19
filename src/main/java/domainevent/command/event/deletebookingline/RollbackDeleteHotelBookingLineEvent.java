package domainevent.command.event.deletebookingline;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.DeleteBookingLineDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.hotelbooking.commandevent.DeleteHotelBookingLineCommand;
import msa.commons.microservices.hotelbooking.qualifier.RollbackDeleteHotelBookingLineEventQualifier;

@Stateless
@RollbackDeleteHotelBookingLineEventQualifier
@Local(CommandHandler.class)
public class RollbackDeleteHotelBookingLineEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(RollbackDeleteHotelBookingLineEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("---- ROLLBACK CANCELAR LINEA DE RESERVA INICIADO ----");
        LOGGER.info("JSON recibido: {}", json);

        EventData eventData = EventData.fromJson(json, DeleteHotelBookingLineCommand.class);
        DeleteHotelBookingLineCommand command = (DeleteHotelBookingLineCommand) eventData.getData();
        LOGGER.info("command: {}", command.toString());

        boolean success = this.bookingService.rollbackDeleteBookingLine(DeleteBookingLineDTO.builder()
                .bookingId(command.getBookingId())
                .roomId(command.getRoomId())
                .build());

        LOGGER.info(
                "---- ROLLBACK CANCELAR LINEA DE RESERVA TERMINADO " + (success ? "EXISTOSO" : "FALLIDO") + " ----");

    }

}
