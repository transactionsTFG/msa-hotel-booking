package domainevent.command.event.createbooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.hotelbooking.commandevent.CreateHotelBookingCommand;
import msa.commons.microservices.hotelbooking.qualifier.RollbackCreateHotelBookingEventQualifier;
import msa.commons.saga.SagaPhases;

@Stateless
@RollbackCreateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class RollbackCreateHotelBookingEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(RollbackCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS ROLLBACK SAGA CREACION DE RESERVA *****");

        EventData eventData = EventData.fromJson(json, CreateHotelBookingCommand.class);
        CreateHotelBookingCommand command = (CreateHotelBookingCommand) eventData.getData();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(command.getBookingId())
                .userId(command.getUserId() + "")
                .statusSaga(SagaPhases.CANCELLED)
                .available(false)
                .build();
        this.bookingService.updateOnlyReservation(bookingDTO);
        LOGGER.info("***** ROLLBACK TERMINADO CON EXITO EN SAGA CREACION DE RESERVA *****");
    }

}
