package domainevent.command.event.updatebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.UpdateHotelBookingDTO;
import business.qualifier.RollbackUpdateHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.hotelbooking.UpdateHotelBookingCommand;
import msa.commons.event.EventData;

@Stateless
@RollbackUpdateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class RollbackUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(RollbackUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS ROLLBACK SAGA MODIFICACION DE RESERVA *****");

        EventData eventData = EventData.fromJson(json, UpdateHotelBookingCommand.class);
        UpdateHotelBookingCommand command = (UpdateHotelBookingCommand) eventData.getData();

        UpdateHotelBookingDTO updateHotelBookingDTO = UpdateHotelBookingDTO.builder()
                .bookingId(command.getBookingId())
                .roomsInfo(command.getRoomsInfo())
                .build();

        this.bookingService.rollbackModifyBooking(updateHotelBookingDTO);
        LOGGER.info("***** ROLLBACK TERMINADO CON EXITO EN SAGA MODIFICACION DE RESERVA *****");
    }

}
