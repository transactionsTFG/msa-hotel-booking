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
        CreateHotelBookingDTO createBookingDTO = this.gson.fromJson(json, CreateHotelBookingDTO.class);
        final String sagaId = UUID.randomUUID().toString();
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setAvailable(false);
        bookingDTO.setUserId("-1");
        bookingDTO.setStatusSaga(SagaPhases.STARTED);
        bookingDTO.setSagaId(sagaId);
        bookingDTO = this.bookingService.createBookingSync(bookingDTO);

        LOGGER.info("***** INICIAMOS SAGA CREACION DE RESERVA {} *****", sagaId);

        EventData eventData = new EventData(sagaId, Arrays.asList(EventId.ROLLBACK_CREATE_HOTEL_BOOKING),
                CreateHotelBookingCommand.builder()
                        .sagaId(sagaId)
                        .bookingId(bookingDTO.getId())
                        .userId(Long.parseLong(createBookingDTO.getUserId()))
                        .startDate(createBookingDTO.getStartDate())
                        .endDate(createBookingDTO.getEndDate())
                        .numberOfNights(createBookingDTO.getNumberOfNights())
                        .withBreakfast(createBookingDTO.getWithBreakfast())
                        .peopleNumber(createBookingDTO.getPeopleNumber())
                        .customerDNI(createBookingDTO.getCustomerDNI())
                        .roomsInfo(createBookingDTO.getRoomsInfo())
                        .customerInfo(BookingMapper.dtoToCustomerInfo(createBookingDTO.getCustomer()))
                        .build());

        this.jmsCommandPublisher.publish(EventId.VALIDATE_HOTEL_CUSTOMER_BY_CREATE_HOTEL_BOOKING, eventData);
    }

}
