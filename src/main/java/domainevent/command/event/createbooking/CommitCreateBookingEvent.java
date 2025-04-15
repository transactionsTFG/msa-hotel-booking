package domainevent.command.event.createbooking;

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
import msa.commons.microservices.hotelbooking.qualifier.CommitCreateBookingEventQualifier;
import msa.commons.saga.SagaPhases;

@Stateless
@CommitCreateBookingEventQualifier
@Local(CommandHandler.class)
public class CommitCreateBookingEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CommitCreateBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS COMMIT SAGA CREACION DE RESERVA *****");

        EventData eventData = EventData.fromJson(json, CreateHotelBookingCommand.class);
        CreateHotelBookingCommand command = (CreateHotelBookingCommand) eventData.getData();

        if (!this.bookingService.validateSagaId(command.getBookingId(), eventData.getSagaId())) {
            this.jmsCommandPublisher.publish(EventId.ROLLBACK_CREATE_HOTEL_BOOKING, eventData);
        } else {

            BookingDTO bookingDTO = BookingDTO.builder()
                    .id(command.getBookingId())
                    .userId(command.getCustomerInfo().getIdCustomer() + "")
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
