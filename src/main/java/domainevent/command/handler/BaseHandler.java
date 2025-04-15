package domainevent.command.handler;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.google.gson.Gson;

import business.services.BookingService;
import domainevent.publisher.IJMSEventPublisher;

public abstract class BaseHandler implements CommandHandler {
    protected BookingService bookingService;
    protected IJMSEventPublisher jmsCommandPublisher;
    protected Gson gson;

    @EJB
    public void setTypeBookingServices(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @EJB
    public void setJmsCommandPublisher(IJMSEventPublisher jmsCommandPublisher) {
        this.jmsCommandPublisher = jmsCommandPublisher;
    }

    @Inject
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}