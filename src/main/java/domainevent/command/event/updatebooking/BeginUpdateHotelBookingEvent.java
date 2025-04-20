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
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.UpdateHotelBookingCommand;

@Stateless
@BeginUpdateHotelBookingQualifier
@Local(CommandHandler.class)
public class BeginUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeginUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("JSON recibido: {}", json);

        UpdateHotelBookingDTO updateBookingDTO = this.gson.fromJson(json, UpdateHotelBookingDTO.class);
        final String sagaId = UUID.randomUUID().toString();

        LOGGER.info("***** INICIANDO SAGA MODIFICACION DE RESERVA {} *****", sagaId);

        EventData eventData = new EventData(sagaId, Arrays.asList(EventId.ROLLBACK_UPDATE_HOTEL_BOOKING),
                UpdateHotelBookingCommand.builder()
                        .sagaId(sagaId)
                        .bookingId(updateBookingDTO.getBookingId())
                        .userId(Long.parseLong(updateBookingDTO.getUserId()))
                        .startDate(updateBookingDTO.getStartDate())
                        .endDate(updateBookingDTO.getEndDate())
                        .numberOfNights(updateBookingDTO.getNumberOfNights())
                        .withBreakfast(updateBookingDTO.getWithBreakfast())
                        .peopleNumber(updateBookingDTO.getPeopleNumber())
                        .customerDNI(updateBookingDTO.getCustomerDNI())
                        .roomsInfo(updateBookingDTO.getRoomsInfo())
                        .customerInfo(
                                BookingMapper.dtoToCustomerInfo(updateBookingDTO.getCustomer()))
                        .build());

        this.jmsCommandPublisher.publish(EventId.VALIDATE_HOTEL_CUSTOMER_BY_UPDATE_HOTEL_BOOKING, eventData);

    }

}
