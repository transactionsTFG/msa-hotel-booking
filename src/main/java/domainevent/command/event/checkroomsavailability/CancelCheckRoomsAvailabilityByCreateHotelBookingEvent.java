package domainevent.command.event.checkroomsavailability;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.CancelCheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;

@Stateless
@CancelCheckRoomsAvailabilityByCreateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CancelCheckRoomsAvailabilityByCreateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager
            .getLogger(CancelCheckRoomsAvailabilityByCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {

        LOGGER.info("JSON recibido: {}", json);
        LOGGER.info("Unimplemented method 'publishCommand'");

    }

}
