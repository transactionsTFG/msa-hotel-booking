package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import business.saga.bookingcreation.qualifier.BeginCreateHotelBookingQualifier;
import business.saga.bookingcreation.qualifier.BeginDeleteHotelBookingLineQualifier;
import business.saga.bookingcreation.qualifier.BeginDeleteHotelBookingQualifier;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelbooking.qualifier.BeginDeleteHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.BeginDeleteHotelBookingLineEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.CancelCheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.CheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.CommitCreateHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.CommitDeleteHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.CommitDeleteHotelBookingLineEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.ConfirmCheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.GetHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.RollbackCreateHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.RollbackDeleteHotelBookingEventQualifier;
import msa.commons.microservices.hotelbooking.qualifier.RollbackDeleteHotelBookingLineEventQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, CommandHandler> handlers = new EnumMap<>(EventId.class);
    private CommandHandler beginCreateHotelBookingEvent;
    private CommandHandler commitCreateHotelBookingEvent;
    private CommandHandler rollbackCreateHotelBookingEvent;
    private CommandHandler checkRoomsAvailabilityByCreateHotelBookingEvent;
    private CommandHandler confirmCheckRoomsAvailabilityByCreateHotelBookingEvent;
    private CommandHandler cancelCheckRoomsAvailabilityByCreateHotelBookingEvent;
    private CommandHandler getHotelBookingEvent;
    private CommandHandler beginDeleteHotelBookingEvent;
    private CommandHandler commitDeleteHotelBookingEvent;
    private CommandHandler rollbackDeleteHotelBookingEvent;
    private CommandHandler beginDeleteHotelBookingLineEvent;
    private CommandHandler commitDeleteHotelBookingLineEvent;
    private CommandHandler rollbackDeleteHotelBookingLineEvent;

    @PostConstruct
    public void init() {
        this.handlers.put(EventId.BEGIN_CREATE_HOTEL_BOOKING, beginCreateHotelBookingEvent);
        this.handlers.put(EventId.COMMIT_CREATE_HOTEL_BOOKING, commitCreateHotelBookingEvent);
        this.handlers.put(EventId.ROLLBACK_CREATE_HOTEL_BOOKING, rollbackCreateHotelBookingEvent);
        this.handlers.put(EventId.CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING,
                checkRoomsAvailabilityByCreateHotelBookingEvent);
        this.handlers.put(EventId.CONFIRM_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING,
                confirmCheckRoomsAvailabilityByCreateHotelBookingEvent);
        this.handlers.put(EventId.CANCEL_CHECK_ROOMS_AVAILABILITY_BY_CREATE_HOTEL_BOOKING,
                cancelCheckRoomsAvailabilityByCreateHotelBookingEvent);
        this.handlers.put(EventId.GET_HOTEL_BOOKING, getHotelBookingEvent);
        this.handlers.put(EventId.BEGIN_DELETE_HOTEL_BOOKING, beginDeleteHotelBookingEvent);
        this.handlers.put(EventId.COMMIT_DELETE_HOTEL_BOOKING, commitDeleteHotelBookingEvent);
        this.handlers.put(EventId.ROLLBACK_DELETE_HOTEL_BOOKING, rollbackDeleteHotelBookingEvent);
        this.handlers.put(EventId.BEGIN_DELETE_HOTEL_BOOKINGLINE, beginDeleteHotelBookingLineEvent);
        this.handlers.put(EventId.COMMIT_DELETE_HOTEL_BOOKINGLINE, commitDeleteHotelBookingLineEvent);
        this.handlers.put(EventId.ROLLBACK_DELETE_HOTEL_BOOKINGLINE, rollbackDeleteHotelBookingLineEvent);
    }

    public CommandHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setBeginCreateHotelBookingEvent(
            @BeginCreateHotelBookingQualifier CommandHandler beginCreateHotelBookingEvent) {
        this.beginCreateHotelBookingEvent = beginCreateHotelBookingEvent;
    }

    @Inject
    public void setCommitCreateHotelBookingEvent(
            @CommitCreateHotelBookingEventQualifier CommandHandler commitCreateHotelBookingEvent) {
        this.commitCreateHotelBookingEvent = commitCreateHotelBookingEvent;
    }

    @Inject
    public void setRollbackCreateHotelBookingEvent(
            @RollbackCreateHotelBookingEventQualifier CommandHandler rollbackCreateHotelBookingEvent) {
        this.rollbackCreateHotelBookingEvent = rollbackCreateHotelBookingEvent;
    }

    @Inject
    public void setCheckRoomsAvailabilityByCreateHotelBookingEvent(
            @CheckRoomsAvailabilityByCreateHotelBookingEventQualifier CommandHandler checkRoomsAvailabilityByCreateHotelBookingEvent) {
        this.checkRoomsAvailabilityByCreateHotelBookingEvent = checkRoomsAvailabilityByCreateHotelBookingEvent;
    }

    @Inject
    public void setConfirmCheckRoomsAvailabilityByCreateHotelBookingEvent(
            @ConfirmCheckRoomsAvailabilityByCreateHotelBookingEventQualifier CommandHandler confirmCheckRoomsAvailabilityByCreateHotelBookingEvent) {
        this.confirmCheckRoomsAvailabilityByCreateHotelBookingEvent = confirmCheckRoomsAvailabilityByCreateHotelBookingEvent;
    }

    @Inject
    public void setCancelCheckRoomsAvailabilityByCreateHotelBookingEvent(
            @CancelCheckRoomsAvailabilityByCreateHotelBookingEventQualifier CommandHandler cancelCheckRoomsAvailabilityByCreateHotelBookingEvent) {
        this.cancelCheckRoomsAvailabilityByCreateHotelBookingEvent = cancelCheckRoomsAvailabilityByCreateHotelBookingEvent;
    }

    // @Inject
    // public void setGetHotelBookingEvent(@GetHotelBookingEventQualifier CommandHandler getHotelBookingEvent) {
    //     this.getHotelBookingEvent = getHotelBookingEvent;
    // }

    @Inject
    public void setBeginDeleteHotelBookingEvent(
            @BeginDeleteHotelBookingQualifier CommandHandler beginDeleteHotelBookingEvent) {
        this.beginDeleteHotelBookingEvent = beginDeleteHotelBookingEvent;
    }

    @Inject
    public void setCommitDeleteHotelBookingEvent(
            @CommitDeleteHotelBookingEventQualifier CommandHandler commitDeleteHotelBookingEvent) {
        this.commitDeleteHotelBookingEvent = commitDeleteHotelBookingEvent;
    }

    @Inject
    public void setRollbackDeleteHotelBookingEvent(
            @RollbackDeleteHotelBookingEventQualifier CommandHandler rollbackDeleteHotelBookingEvent) {
        this.rollbackDeleteHotelBookingEvent = rollbackDeleteHotelBookingEvent;
    }

    // @Inject
    // public void setBeginDeleteHotelBookingLineEvent(
    //         @BeginDeleteHotelBookingLineQualifier CommandHandler beginDeleteHotelBookingLineEvent) {
    //     this.beginDeleteHotelBookingLineEvent = beginDeleteHotelBookingLineEvent;
    // }

    // @Inject
    // public void setCommitDeleteHotelBookingLineEvent(
    //         @CommitDeleteHotelBookingLineEventQualifier CommandHandler commitDeleteHotelBookingLineEvent) {
    //     this.commitDeleteHotelBookingLineEvent = commitDeleteHotelBookingLineEvent;
    // }

    // @Inject
    // public void setRollbackDeleteHotelBookingLineEvent(
    //         @RollbackDeleteHotelBookingLineEventQualifier CommandHandler rollbackDeleteHotelBookingLineEvent) {
    //     this.rollbackDeleteHotelBookingLineEvent = rollbackDeleteHotelBookingLineEvent;
    // }

}