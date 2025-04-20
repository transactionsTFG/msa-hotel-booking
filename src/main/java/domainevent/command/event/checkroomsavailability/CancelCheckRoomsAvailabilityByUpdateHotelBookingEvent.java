package domainevent.command.event.checkroomsavailability;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.microservices.hotelbooking.qualifier.CancelCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier;

@Stateless
@CancelCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CancelCheckRoomsAvailabilityByUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager
            .getLogger(CancelCheckRoomsAvailabilityByUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {

        LOGGER.info("JSON recibido: {}", json);
        LOGGER.info("Unimplemented method 'publishCommand'");

    }

}
