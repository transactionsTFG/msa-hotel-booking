package domainevent.command.event.updatebooking;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingWithLinesDTO;
import business.dto.UpdateHotelBookingDTO;
import business.qualifier.CommitUpdateHotelBookingEventQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.hotelbooking.UpdateHotelBookingCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.UpdateReservation;

@Stateless
@CommitUpdateHotelBookingEventQualifier
@Local(CommandHandler.class)
public class CommitUpdateHotelBookingEvent extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(CommitUpdateHotelBookingEvent.class);

    @Override
    public void publishCommand(String json) {
        LOGGER.info("***** INICIAMOS COMMIT SAGA MODIFICACION DE RESERVA *****");
        LOGGER.info("JSON recibido: {}", json);
        EventData eventData = EventData.fromJson(json, UpdateHotelBookingCommand.class);
        UpdateHotelBookingCommand command = (UpdateHotelBookingCommand) eventData.getData();
        UpdateHotelBookingDTO updateHotelBookingDTO = UpdateHotelBookingDTO.builder()
                .bookingId(command.getBookingId())
                .sagaId(eventData.getSagaId())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .numberOfNights(command.getNumberOfNights())
                .withBreakfast(command.isWithBreakfast())
                .peopleNumber(command.getPeopleNumber())
                .roomsInfo(command.getRoomsInfo())
                .totalPrice(command.getTotalPrice())
                .build();
        this.bookingService.commitModifyBooking(updateHotelBookingDTO);
        BookingWithLinesDTO bL = this.bookingService.getBookingWithLines(command.getBookingId());
        double totalPrice = bL.getBookingLines().stream()
                .mapToDouble(line -> line.getNumberOfNights() * line.getRoomDailyPrice())
                .sum();
        command.setTotalPrice(totalPrice);
        this.bookingService.updatePriceBooking(command.getBookingId(), totalPrice, command.isWithBreakfast(), command.getPeopleNumber());
        
        eventData.setOperation(UpdateReservation.UPDATE_RESERVATION_ONLY_HOTEL_COMMIT);
        this.jmsCommandPublisher.publish(EventId.UPDATE_RESERVATION_TRAVEL, eventData);
        LOGGER.info("***** COMMIT TERMINADO CON EXITO EN SAGA MODIFICACION DE RESERVA *****");

    }

}
