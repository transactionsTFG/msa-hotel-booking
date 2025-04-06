package domainevent.command.handler;

import javax.ejb.EJB;
import javax.inject.Inject;

import business.services.BookingService;
import domainevent.publisher.IJMSEventPublisher;

public abstract class BaseHandler implements EventHandler {
    protected BookingService bookingService;
    protected IJMSEventPublisher jmsEventDispatcher;
    @EJB
    public void setTypeBookingServices(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @Inject
    public void setJmsEventDispatcher(IJMSEventPublisher jmsEventDispatcher) {
        this.jmsEventDispatcher = jmsEventDispatcher;
    }
}