package domainevent.command;

import javax.ejb.Local;
import javax.ejb.Stateless;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.microservices.hotelbooking.qualifier.CommitBookingQualifier;
import msa.commons.parser.NumberParser;

@Stateless
@CommitBookingQualifier
@Local(EventHandler.class)
public class ConfirmCreateBookingEvent extends BaseHandler {
    @Override
    public void handleCommand(Object event) {
        long bookingId = NumberParser.toLong(event);
        this.bookingService.cancelCreateBooking(bookingId);
    }
}
