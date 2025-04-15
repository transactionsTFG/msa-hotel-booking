package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.CommandHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.qualifier.CommitCreateBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.RollbackCreateBookingEventQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, CommandHandler> handlers = new EnumMap<>(EventId.class);
    private CommandHandler confirmCreateBookingHandler;
    private CommandHandler cancelCreateBookingHandler;

    @PostConstruct
    public void init(){
        this.handlers.put(EventId.BEGIN_CREATE_HOTEL_BOOKING, confirmCreateBookingHandler);
        this.handlers.put(EventId.FAILED_BOOKING, cancelCreateBookingHandler);
    }

    public CommandHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setConfirmCreateUsHandler(@CommitCreateBookingEventQualifier CommandHandler confirmCreateUseHandler) {
        this.confirmCreateBookingHandler = confirmCreateUseHandler;
    }
    @Inject
    public void setCancelCreateUsHandler(@RollbackCreateBookingEventQualifier CommandHandler cancelCreateUseHandler) {
        this.cancelCreateBookingHandler = cancelCreateUseHandler;
    }

}