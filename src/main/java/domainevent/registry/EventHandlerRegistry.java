package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.qualifier.CommitBookingQualifier;
import msa.commons.microservices.hotelbooking.qualifier.RollbackBookingQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);
    private EventHandler confirmCreateBookingHandler;
    private EventHandler cancelCreateBookingHandler;

    @PostConstruct
    public void init(){
        this.handlers.put(EventId.CREATE_BOOKING, confirmCreateBookingHandler);
        this.handlers.put(EventId.FAILED_BOOKING, cancelCreateBookingHandler);
    }

    public EventHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setConfirmCreateUsHandler(@CommitBookingQualifier EventHandler confirmCreateUseHandler) {
        this.confirmCreateBookingHandler = confirmCreateUseHandler;
    }
    @Inject
    public void setCancelCreateUsHandler(@RollbackBookingQualifier EventHandler cancelCreateUseHandler) {
        this.cancelCreateBookingHandler = cancelCreateUseHandler;
    }

}