package domainevent.command.event.updatebooking;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.UpdateHotelBookingDTO;
import business.mapper.BookingMapper;
import business.saga.bookingmodification.qualifier.BeginUpdateHotelBookingQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.hotelbooking.UpdateHotelBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@BeginUpdateHotelBookingQualifier
@Local(CommandHandler.class)
public class BeginUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeginUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);

        EventData eventData = EventData.fromJson(json, UpdateHotelBookingCommand.class);

        LOGGER.info("***** INICIANDO SAGA MODIFICACION DE RESERVA {} *****", eventData.getSagaId());

        this.jmsCommandPublisher.publish(EventId.VALIDATE_HOTEL_CUSTOMER_BY_UPDATE_HOTEL_BOOKING, eventData);

    }

}
