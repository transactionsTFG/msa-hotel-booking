package domainevent.command.event.createbooking;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingDTO;
import business.dto.CreateHotelBookingDTO;
import business.mapper.BookingMapper;
import business.saga.bookingcreation.qualifier.BeginCreateHotelBookingQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.hotelbooking.CreateHotelBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.saga.SagaPhases;

@Stateless
@BeginCreateHotelBookingQualifier
@Local(CommandHandler.class)
public class BeginCreateHotelBookingEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(BeginCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);
        EventData e = EventData.fromJson(json, CreateHotelBookingCommand.class);
        CreateHotelBookingCommand command = (CreateHotelBookingCommand) e.getData();
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setAvailable(false);
        bookingDTO.setUserId("-1");
        bookingDTO.setStatusSaga(SagaPhases.STARTED);
        bookingDTO.setSagaId(e.getSagaId());
        bookingDTO = this.bookingService.createBookingSync(bookingDTO);

        LOGGER.info("***** INICIAMOS SAGA CREACION DE RESERVA {} *****", e.getSagaId());
        command.setBookingId(bookingDTO.getId());
        EventData eventData = new EventData(    e.getSagaId(),
                                                e.getOperation(), 
                                                Arrays.asList(EventId.ROLLBACK_CREATE_HOTEL_BOOKING),
                                                command,
                                                e.getTransactionActive()
                                            );

        this.jmsCommandPublisher.publish(EventId.VALIDATE_HOTEL_CUSTOMER_BY_CREATE_HOTEL_BOOKING, eventData);
    }

}
