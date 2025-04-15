package domainevent.command.event.createbooking;

import java.util.Arrays;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingDTO;
import business.dto.CreateBookingDTO;
import business.saga.bookingcreation.mapper.BookingCreationMapper;
import business.saga.bookingcreation.qualifier.BeginCreateBookingQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.CreateHotelBookingCommand;
import msa.commons.saga.SagaPhases;

@Stateless
@BeginCreateBookingQualifier
@Local(CommandHandler.class)
public class BeginCreateBookingEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(BeginCreateBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        CreateBookingDTO createBookingDTO = this.gson.fromJson(json, CreateBookingDTO.class);
        final String sagaId = UUID.randomUUID().toString();
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setAvailable(false);
        bookingDTO.setUserId("-1");
        bookingDTO.setStatusSaga(SagaPhases.STARTED);
        bookingDTO.setSagaId(sagaId);
        bookingDTO = this.bookingService.createBookingSync(bookingDTO);

        LOGGER.info("***** INICIAMOS SAGA CREACION DE RESERVA {} *****", sagaId);

        EventData eventData = new EventData(sagaId, Arrays.asList(EventId.ROLLBACK_CANCEL_HOTEL_BOOKING),
                CreateHotelBookingCommand.builder()
                        .customerInfo(BookingCreationMapper.INSTANCE.dtoToCustomerInfo(createBookingDTO.getCustomer()))
                        .roomsInfo(createBookingDTO.getRoomsInfo())
                        .build());

        this.jmsCommandPublisher.publish(EventId.VALIDATE_HOTEL_CUSTOMER, eventData);
    }

}
