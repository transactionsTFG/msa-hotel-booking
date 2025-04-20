package domainevent.command.event.checkroomsavailability;

import java.util.Arrays;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.UpdateHotelBookingDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.UpdateHotelBookingCommand;
import msa.commons.microservices.hotelbooking.qualifier.CheckRoomsAvailabilityByUpdateHotelBookingEventQualifier;

@Stateless
@CheckRoomsAvailabilityByUpdateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CheckRoomsAvailabilityByUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CheckRoomsAvailabilityByUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);
        EventData data = EventData.fromJson(json, UpdateHotelBookingCommand.class);
        UpdateHotelBookingCommand command = (UpdateHotelBookingCommand) data.getData();

        UpdateHotelBookingDTO updateHotelBookingDTO = UpdateHotelBookingDTO.builder()
                .roomsInfo(command.getRoomsInfo())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .build();

        boolean areRoomsAvailable = this.bookingService
                .checkRoomsAvailabilityByUpdateHotelBooking(updateHotelBookingDTO);

        EventData eventData = new EventData(data.getSagaId(),
                Arrays.asList(EventId.ROLLBACK_UPDATE_HOTEL_BOOKING),
                command);

        if (areRoomsAvailable) {
            this.jmsCommandPublisher.publish(EventId.CONFIRM_CHECK_ROOMS_AVAILABILITY_BY_UPDATE_HOTEL_BOOKING,
                    eventData);
        } else {
            this.jmsCommandPublisher.publish(EventId.CANCEL_CHECK_ROOMS_AVAILABILITY_BY_UPDATE_HOTEL_BOOKING,
                    eventData);
        }
    }

}
