package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import business.qualifier.CancelCheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import business.qualifier.CancelCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier;
import business.qualifier.CheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import business.qualifier.CheckRoomsAvailabilityByUpdateHotelBookingEventQualifier;
import business.qualifier.CommitCreateHotelBookingEventQualifier;
import business.qualifier.CommitDeleteHotelBookingEventQualifier;
import business.qualifier.CommitDeleteHotelBookingLineEventQualifier;
import business.qualifier.CommitUpdateHotelBookingEventQualifier;
import business.qualifier.ConfirmCheckRoomsAvailabilityByCreateHotelBookingEventQualifier;
import business.qualifier.ConfirmCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier;
import business.qualifier.GetHotelBookingEventQualifier;
import business.qualifier.RollbackCreateHotelBookingEventQualifier;
import business.qualifier.RollbackDeleteHotelBookingEventQualifier;
import business.qualifier.RollbackDeleteHotelBookingLineEventQualifier;
import business.qualifier.RollbackUpdateHotelBookingEventQualifier;
import business.saga.bookingcreation.qualifier.BeginCreateHotelBookingQualifier;
import business.saga.bookingdeletion.qualifier.BeginDeleteHotelBookingLineQualifier;
import business.saga.bookingdeletion.qualifier.BeginDeleteHotelBookingQualifier;
import business.saga.bookingmodification.qualifier.BeginUpdateHotelBookingQualifier;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventId;

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
    private CommandHandler beginUpdateHotelBookingEvent;
    private CommandHandler checkRoomsAvailabilityByUpdateHotelBookingEvent;
    private CommandHandler confirmCheckRoomsAvailabilityByUpdateHotelBookingEvent;
    private CommandHandler cancelCheckRoomsAvailabilityByUpdateHotelBookingEvent;
    private CommandHandler commitUpdateHotelBookingEvent;
    private CommandHandler rollbackUpdateHotelBookingEvent;

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
        this.handlers.put(EventId.BEGIN_UPDATE_HOTEL_BOOKING, beginUpdateHotelBookingEvent);

        this.handlers.put(EventId.CHECK_ROOMS_AVAILABILITY_BY_UPDATE_HOTEL_BOOKING,
                checkRoomsAvailabilityByUpdateHotelBookingEvent);
        this.handlers.put(EventId.CONFIRM_CHECK_ROOMS_AVAILABILITY_BY_UPDATE_HOTEL_BOOKING,
                confirmCheckRoomsAvailabilityByUpdateHotelBookingEvent);
        this.handlers.put(EventId.CANCEL_CHECK_ROOMS_AVAILABILITY_BY_UPDATE_HOTEL_BOOKING,
                cancelCheckRoomsAvailabilityByUpdateHotelBookingEvent);
        this.handlers.put(EventId.COMMIT_UPDATE_HOTEL_BOOKING, commitUpdateHotelBookingEvent);
        this.handlers.put(EventId.ROLLBACK_UPDATE_HOTEL_BOOKING, rollbackUpdateHotelBookingEvent);
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

    @Inject
    public void setGetHotelBookingEvent(@GetHotelBookingEventQualifier CommandHandler getHotelBookingEvent) {
        this.getHotelBookingEvent = getHotelBookingEvent;
    }

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

    @Inject
    public void setBeginDeleteHotelBookingLineEvent(
            @BeginDeleteHotelBookingLineQualifier CommandHandler beginDeleteHotelBookingLineEvent) {
        this.beginDeleteHotelBookingLineEvent = beginDeleteHotelBookingLineEvent;
    }

    @Inject
    public void setCommitDeleteHotelBookingLineEvent(
            @CommitDeleteHotelBookingLineEventQualifier CommandHandler commitDeleteHotelBookingLineEvent) {
        this.commitDeleteHotelBookingLineEvent = commitDeleteHotelBookingLineEvent;
    }

    @Inject
    public void setRollbackDeleteHotelBookingLineEvent(
            @RollbackDeleteHotelBookingLineEventQualifier CommandHandler rollbackDeleteHotelBookingLineEvent) {
        this.rollbackDeleteHotelBookingLineEvent = rollbackDeleteHotelBookingLineEvent;
    }

    @Inject
    public void setBeginUpdateHotelBookingEvent(
            @BeginUpdateHotelBookingQualifier CommandHandler beginUpdateHotelBookingEvent) {
        this.beginUpdateHotelBookingEvent = beginUpdateHotelBookingEvent;
    }

    @Inject
    public void setCheckRoomsAvailabilityByUpdateHotelBookingEvent(
            @CheckRoomsAvailabilityByUpdateHotelBookingEventQualifier CommandHandler checkRoomsAvailabilityByUpdateHotelBookingEvent) {
        this.checkRoomsAvailabilityByUpdateHotelBookingEvent = checkRoomsAvailabilityByUpdateHotelBookingEvent;
    }

    @Inject
    public void setConfirmCheckRoomsAvailabilityByUpdateHotelBookingEvent(
            @ConfirmCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier CommandHandler confirmCheckRoomsAvailabilityByUpdateHotelBookingEvent) {
        this.confirmCheckRoomsAvailabilityByUpdateHotelBookingEvent = confirmCheckRoomsAvailabilityByUpdateHotelBookingEvent;
    }

    @Inject
    public void setCancelCheckRoomsAvailabilityByUpdateHotelBookingEvent(
            @CancelCheckRoomsAvailabilityByUpdateHotelBookingEventQualifier CommandHandler cancelCheckRoomsAvailabilityByUpdateHotelBookingEvent) {
        this.cancelCheckRoomsAvailabilityByUpdateHotelBookingEvent = cancelCheckRoomsAvailabilityByUpdateHotelBookingEvent;
    }

    @Inject
    public void setCommitUpdateHotelBookingEvent(
            @CommitUpdateHotelBookingEventQualifier CommandHandler commitUpdateHotelBookingEvent) {
        this.commitUpdateHotelBookingEvent = commitUpdateHotelBookingEvent;
    }

    @Inject
    public void setRollbackUpdateHotelBookingEvent(
            @RollbackUpdateHotelBookingEventQualifier CommandHandler rollbackUpdateHotelBookingEvent) {
        this.rollbackUpdateHotelBookingEvent = rollbackUpdateHotelBookingEvent;
    }

}