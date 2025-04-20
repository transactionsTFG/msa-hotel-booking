package domainevent.command.event.updatebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.UpdateHotelBookingDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.hotelbooking.commandevent.UpdateHotelBookingCommand;
import msa.commons.microservices.hotelbooking.qualifier.CommitUpdateHotelBookingEventQualifier;

@Stateless
@CommitUpdateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CommitUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CommitUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS COMMIT SAGA MODIFICACION DE RESERVA *****");
        LOGGER.info("JSON recibido: {}", json);
        EventData eventData = EventData.fromJson(json, UpdateHotelBookingCommand.class);
        UpdateHotelBookingCommand command = (UpdateHotelBookingCommand) eventData.getData();

        UpdateHotelBookingDTO updateHotelBookingDTO = UpdateHotelBookingDTO.builder()
                .bookingId(command.getBookingId())
                .sagaId(eventData.getSagaId())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .numberOfNights(command.getNumberOfNights())
                .withBreakfast(command.isWithBreakfast())
                .peopleNumber(command.getPeopleNumber())
                .roomsInfo(command.getRoomsInfo())
                .build();

        this.bookingService.commitModifyBooking(updateHotelBookingDTO);
        LOGGER.info("***** COMMIT TERMINADO CON EXITO EN SAGA MODIFICACION DE RESERVA *****");

    }

}
