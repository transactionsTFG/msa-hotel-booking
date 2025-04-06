package domainevent.command;

import javax.ejb.Local;
import javax.ejb.Stateless;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.microservices.hotelbooking.qualifier.RollbackBookingQualifier;
import msa.commons.parser.NumberParser;

@Stateless
@RollbackBookingQualifier
@Local(EventHandler.class)
public class CancelCreateBookingEvent extends BaseHandler {

    @Override
    public void handleCommand(Object event) {
        long bookingId = NumberParser.toLong(event);
        this.bookingService.cancelCreateBooking(bookingId);
    }

}
