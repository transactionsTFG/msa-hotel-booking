package domainevent.command.event.checkroomsavailability;

import java.util.Arrays;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.CreateHotelBookingDTO;
import business.qualifier.CheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.hotelbooking.CreateHotelBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@CheckRoomsAvailabilityByCreateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CheckRoomsAvailabilityByCreateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CheckRoomsAvailabilityByCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {

        LOGGER.info("JSON recibido: {}", json);
        EventData data = EventData.fromJson(json, CreateHotelBookingCommand.class);
        CreateHotelBookingCommand command = (CreateHotelBookingCommand) data.getData();

        CreateHotelBookingDTO createHotelBookingDTO = CreateHotelBookingDTO.builder()
                .roomsInfo(command.getRoomsInfo())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .build();

        boolean areRoomsAvailable = this.bookingService.checkRoomsAvailabilityByCreateHotelBooking(createHotelBookingDTO);

        EventData eventData = new EventData(data.getSagaId(),
                Arrays.asList(EventId.ROLLBACK_CREATE_HOTEL_BOOKING),
                command);

        if (areRoomsAvailable) {
            this.jmsCommandPublisher.publish(EventId.CONFIRM_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING,
                    eventData);
        } else {
            this.jmsCommandPublisher.publish(EventId.CANCEL_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING,
                    eventData);
        }

    }

}
