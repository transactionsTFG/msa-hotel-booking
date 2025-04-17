package domainevent.command.event.checkroomsavailability;

import java.util.Arrays;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.CreateHotelBookingDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.CreateHotelBookingCommand;
import msa.commons.microservices.hotelbooking.qualifier.CheckRoomsAvailabilityByCreateHotelBookingEventQualifier;

@Stateless
@CheckRoomsAvailabilityByCreateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CheckRoomsAvailabilityByCreateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CheckRoomsAvailabilityByCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {

        LOGGER.info("JSON recibido: {}", json);
        CreateHotelBookingDTO createHotelBookingDTO = this.gson.fromJson(json, CreateHotelBookingDTO.class);

        boolean areRoomsAvailable = this.bookingService.checkRoomsAvailability(createHotelBookingDTO);

        EventData eventData = new EventData(createHotelBookingDTO.getSagaId(), Arrays.asList(EventId.ROLLBACK_CREATE_HOTEL_BOOKING),
                CreateHotelBookingCommand.builder()
                        .bookingId(createHotelBookingDTO.getBookingId())
                        .build());

        if (areRoomsAvailable) {
            this.jmsCommandPublisher.publish(EventId.CONFIRM_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING, eventData);
        } else {
            this.jmsCommandPublisher.publish(EventId.CANCEL_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING, eventData);
        }

    }

}
