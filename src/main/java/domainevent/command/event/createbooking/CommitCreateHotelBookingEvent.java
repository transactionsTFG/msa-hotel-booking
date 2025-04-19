package domainevent.command.event.createbooking;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.bookingline.BookingLineDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.commandevent.CreateHotelBookingCommand;
import msa.commons.microservices.hotelbooking.qualifier.CommitCreateHotelBookingEventQualifier;
import msa.commons.saga.SagaPhases;

@Stateless
@CommitCreateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CommitCreateHotelBookingEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CommitCreateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS COMMIT SAGA CREACION DE RESERVA *****");
        LOGGER.info("JSON recibido: {}", json);
        EventData eventData = EventData.fromJson(json, CreateHotelBookingCommand.class);
        CreateHotelBookingCommand command = (CreateHotelBookingCommand) eventData.getData();

        if (!this.bookingService.validateSagaId(command.getBookingId(), eventData.getSagaId())) {
            this.jmsCommandPublisher.publish(EventId.ROLLBACK_CREATE_HOTEL_BOOKING, new EventData(eventData.getSagaId(),
                    Arrays.asList(EventId.ROLLBACK_CREATE_HOTEL_BOOKING), eventData));
        } else {

            BookingDTO bookingDTO = BookingDTO.builder()
                    .id(command.getBookingId())
                    .userId(command.getUserId() + "")
                    .statusSaga(SagaPhases.COMPLETED)
                    .available(true)
                    .build();

            List<BookingLineDTO> bookingLineDTOs = command.getRoomsInfo().stream().map(roomInfo -> {

                return BookingLineDTO.builder()
                        .roomId(roomInfo.getRoomId())
                        .bookingDTO(bookingDTO)
                        .numberOfNights(command.getNumberOfNights())
                        .roomDailyPrice(roomInfo.getDailyPrice())
                        .startDate(command.getStartDate())
                        .endDate(command.getEndDate())
                        .available(true)
                        .build();

            }).toList();

            BookingWithLinesDTO bookingWithLinesDTO = BookingWithLinesDTO.builder()
                    .bookingDTO(bookingDTO)
                    .bookingLines(bookingLineDTOs)
                    .build();

            this.bookingService.updateBooking(bookingWithLinesDTO);
        }

        LOGGER.info("***** COMMIT TERMINADO CON EXITO EN SAGA CREACION DE RESERVA *****");
    }
}
