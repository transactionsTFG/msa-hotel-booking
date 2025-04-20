package business.services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import business.booking.Booking;
import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.bookingline.BookingLine;
import business.bookingline.BookingLineDTO;
import business.dto.CreateHotelBookingDTO;
import business.dto.DeleteBookingLineDTO;
import business.dto.UpdateHotelBookingDTO;
import business.validators.DateValidator;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import msa.commons.microservices.hotelroom.commandevent.model.RoomInfo;
import msa.commons.saga.SagaPhases;
import validator.CustomerSyntaxValidator;

@Stateless
public class BookingServiceImpl implements BookingService {

    private EntityManager entityManager;
    private EventHandlerRegistry eventHandlerRegistry;
    private Gson gson;
    private CustomerSyntaxValidator customerSyntaxValidator;
    private static final Logger LOGGER = LogManager.getLogger(BookingServiceImpl.class);

    public BookingServiceImpl() {
    }

    @Inject
    public BookingServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Inject
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @EJB
    public void setEventHandlerRegistry(EventHandlerRegistry eventHandlerRegistry) {
        this.eventHandlerRegistry = eventHandlerRegistry;
    }

    @Inject
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Inject
    public void setCustomerSyntaxValidator(CustomerSyntaxValidator customerSyntaxValidator) {
        this.customerSyntaxValidator = customerSyntaxValidator;
    }

    @Override
    public boolean createBookingAsync(CreateHotelBookingDTO createBookingDTO) {
        LOGGER.info("Validando datos del cliente: {}", createBookingDTO.getCustomer());
        if (!this.customerSyntaxValidator.isValid(createBookingDTO.getCustomer())) {
            return false;
        }
        LOGGER.info("Publicando comando {}", EventId.BEGIN_CREATE_HOTEL_BOOKING);
        this.eventHandlerRegistry.getHandler(EventId.BEGIN_CREATE_HOTEL_BOOKING)
                .publishCommand(this.gson.toJson(createBookingDTO));

        return true;
    }

    @Override
    public BookingDTO createBookingSync(BookingDTO bookingDTO) {

        Booking booking = new Booking();

        booking.setUserId(bookingDTO.getUserId());
        booking.setWithBreakfast(bookingDTO.isWithBreakfast());
        booking.setPeopleNumber(bookingDTO.getPeopleNumber());
        booking.setAvailable(bookingDTO.isAvailable());
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setSagaId(bookingDTO.getSagaId());
        booking.setStatusSaga(bookingDTO.getStatusSaga());

        this.entityManager.persist(booking);
        this.entityManager.flush();

        return booking.toDTO();
        // return BookingMapper.INSTANCE.entityToDTO(booking);

    }

    @Override
    public boolean validateSagaId(long bookingId, String sagaId) {
        Booking booking = this.entityManager.find(Booking.class, bookingId, LockModeType.OPTIMISTIC);
        if (booking == null || !sagaId.equals(booking.getSagaId()))
            return false;

        return true;
    }

    @Override
    public boolean updateOnlyReservation(BookingDTO bookingDTO) {
        Booking booking = this.entityManager.find(Booking.class, bookingDTO.getId());

        if (booking == null)
            return false;

        booking.setAvailable(bookingDTO.isAvailable());
        booking.setUserId(bookingDTO.getUserId());
        booking.setStatusSaga(bookingDTO.getStatusSaga());

        this.entityManager.merge(booking);
        return true;
    }

    @Override
    public boolean updateBooking(BookingWithLinesDTO bookingWithLines) {
        BookingDTO bookingDto = bookingWithLines.getBookingDTO();
        List<BookingLineDTO> bookingLines = bookingWithLines.getBookingLines();

        Booking booking = this.entityManager.find(Booking.class, bookingDto.getId());

        booking.setUserId(bookingDto.getUserId());
        booking.setWithBreakfast(bookingDto.isWithBreakfast());
        booking.setPeopleNumber(bookingDto.getPeopleNumber());
        booking.setAvailable(true);
        booking.setTotalPrice(0);
        booking.setStatusSaga(bookingDto.getStatusSaga());
        bookingLines.forEach(bl -> {
            BookingLine bookingLine = new BookingLine();

            bookingLine.setRoomId(bl.getRoomId());
            bookingLine.setBooking(booking);
            bookingLine.setNumberOfNights(bl.getNumberOfNights());
            bookingLine.setRoomDailyPrice(bl.getRoomDailyPrice());
            bookingLine.setStartDate(bl.getStartDate());
            bookingLine.setEndDate(bl.getEndDate());
            bookingLine.setAvailable(true);
            bookingLine.setStatusSaga(SagaPhases.COMPLETED);
            bookingLine.setSagaId(booking.getSagaId());

            double newTotalPrice = bl.getRoomDailyPrice() * bl.getNumberOfNights();
            booking.setTotalPrice(booking.getTotalPrice() + newTotalPrice);
            this.entityManager.merge(bookingLine);
        });

        this.entityManager.merge(booking);

        return true;
    }

    @Override
    public boolean checkRoomsAvailabilityByCreateHotelBooking(CreateHotelBookingDTO createHotelBookingDTO) {

        List<RoomInfo> rooms = createHotelBookingDTO.getRoomsInfo();
        if (rooms == null)
            return false;
        for (RoomInfo roomInfo : rooms) {
            TypedQuery<BookingLine> query = this.entityManager
                    .createNamedQuery("business.bookingLine.BookingLine.findByRoomId", BookingLine.class);
            query.setParameter("roomId", roomInfo.getRoomId());

            for (BookingLine bookingLine : query.getResultList()) {

                if (bookingLine.isAvailable() && !DateValidator.validateDates(createHotelBookingDTO.getStartDate(),
                        createHotelBookingDTO.getEndDate(), bookingLine.getStartDate(), bookingLine.getEndDate())) {
                    return false;
                }
            }

        }

        return true;
    }

    @Override
    public BookingWithLinesDTO getBookingWithLines(long bookingId) {

        Booking booking = this.entityManager.find(Booking.class, bookingId, LockModeType.OPTIMISTIC);

        if (booking == null)
            return null;

        return BookingWithLinesDTO.builder()
                .bookingDTO(booking.toDTO())
                .bookingLines(booking.getBookingLines().stream().map(bookingLine -> {
                    this.entityManager.lock(bookingLine, LockModeType.OPTIMISTIC);
                    return bookingLine.toDTO();
                }).toList())
                .build();

    }

    @Override
    public boolean beginDeleteBooking(long bookingId) {

        Booking booking = this.entityManager.find(Booking.class, bookingId);

        if (booking == null || !booking.isAvailable()) {
            return false;
        }

        LOGGER.info("Publicando comando: {}", EventId.BEGIN_DELETE_HOTEL_BOOKING);

        booking.setAvailable(false);
        this.eventHandlerRegistry.getHandler(EventId.BEGIN_DELETE_HOTEL_BOOKING)
                .publishCommand(this.gson.toJson(bookingId));

        return true;
    }

    @Override
    public double deleteBooking(long bookingId, String sagaId) {

        Booking booking = this.entityManager.find(Booking.class, bookingId);

        if (booking == null) {
            return -1;
        }

        booking.getBookingLines().forEach(bookingLine -> {
            bookingLine.setStatusSaga(SagaPhases.STARTED);
            bookingLine.setSagaId(sagaId);
            bookingLine.setAvailable(false);
        });
        booking.setStatusSaga(SagaPhases.STARTED);
        booking.setSagaId(sagaId);
        booking.setAvailable(false);

        return booking.getTotalPrice();
    }

    @Override
    public boolean rollbackDeleteBooking(long bookingId) {
        Booking booking = this.entityManager.find(Booking.class, bookingId);

        if (booking == null)
            return false;

        booking.getBookingLines().forEach(bookingLine -> {
            bookingLine.setStatusSaga(SagaPhases.CANCELLED);
            bookingLine.setAvailable(true);
        });

        booking.setAvailable(true);
        booking.setStatusSaga(SagaPhases.CANCELLED);

        return true;
    }

    @Override
    public boolean commitDeleteBooking(long bookingId) {
        Booking booking = this.entityManager.find(Booking.class, bookingId);

        if (booking == null)
            return false;

        booking.getBookingLines().forEach(bookingLine -> {
            bookingLine.setStatusSaga(SagaPhases.COMPLETED);
            bookingLine.setAvailable(false);
        });

        booking.setAvailable(false);
        booking.setStatusSaga(SagaPhases.COMPLETED);
        booking.setTotalPrice(0);

        return true;
    }

    @Override
    public boolean beginDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO) {
        Booking booking = this.entityManager.find(Booking.class, deleteBookingLineDTO.getBookingId(),
                LockModeType.OPTIMISTIC);

        if (booking == null || !booking.isAvailable()) {
            return false;
        }

        LOGGER.info("Publicando comando: {}", EventId.BEGIN_DELETE_HOTEL_BOOKINGLINE);

        this.eventHandlerRegistry.getHandler(EventId.BEGIN_DELETE_HOTEL_BOOKINGLINE)
                .publishCommand(this.gson.toJson(deleteBookingLineDTO));

        return true;
    }

    @Override
    public double deleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO, String sagaId) {
        double moneyReturned = 0;

        Booking booking = this.entityManager.find(Booking.class, deleteBookingLineDTO.getBookingId());

        if (booking == null) {
            return -1;
        }

        TypedQuery<BookingLine> query = this.entityManager
                .createNamedQuery("business.bookingLine.BookingLine.findByBookingIdAndRoomId", BookingLine.class);
        query.setParameter("bookingId", deleteBookingLineDTO.getBookingId());
        query.setParameter("roomId", deleteBookingLineDTO.getRoomId() + "");
        BookingLine bookingLine = query.getResultList().isEmpty() ? null : query.getResultList().get(0);

        if (bookingLine == null) {
            return -3;
        }

        bookingLine.setAvailable(false);
        bookingLine.setSagaId(sagaId);
        bookingLine.setStatusSaga(SagaPhases.STARTED);
        moneyReturned = (bookingLine.getRoomDailyPrice() * bookingLine.getNumberOfNights());

        return moneyReturned;

    }

    @Override
    public boolean rollbackDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO) {
        Booking booking = this.entityManager.find(Booking.class, deleteBookingLineDTO.getBookingId());

        if (booking == null)
            return false;

        TypedQuery<BookingLine> query = this.entityManager
                .createNamedQuery("business.bookingLine.BookingLine.findByBookingIdAndRoomId", BookingLine.class);
        query.setParameter("bookingId", deleteBookingLineDTO.getBookingId());
        query.setParameter("roomId", deleteBookingLineDTO.getRoomId() + "");
        BookingLine bookingLine = query.getResultList().isEmpty() ? null : query.getResultList().get(0);

        bookingLine.setAvailable(true);
        bookingLine.setStatusSaga(SagaPhases.CANCELLED);

        booking.setAvailable(true);
        booking.setStatusSaga(SagaPhases.CANCELLED);

        return true;
    }

    @Override
    public boolean commitDeleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO) {
        Booking booking = this.entityManager.find(Booking.class, deleteBookingLineDTO.getBookingId());

        if (booking == null)
            return false;

        TypedQuery<BookingLine> query = this.entityManager
                .createNamedQuery("business.bookingLine.BookingLine.findByBookingIdAndRoomId", BookingLine.class);
        query.setParameter("bookingId", deleteBookingLineDTO.getBookingId());
        query.setParameter("roomId", deleteBookingLineDTO.getRoomId() + "");
        BookingLine bookingLine = query.getResultList().isEmpty() ? null : query.getResultList().get(0);

        bookingLine.setAvailable(false);
        bookingLine.setStatusSaga(SagaPhases.COMPLETED);

        booking.setStatusSaga(SagaPhases.COMPLETED);
        booking.setTotalPrice(
                booking.getTotalPrice() - (bookingLine.getNumberOfNights() * bookingLine.getRoomDailyPrice()));

        if (booking.getTotalPrice() <= 0) {
            booking.setAvailable(false);
            booking.setTotalPrice(0);
        }

        return true;
    }

    @Override
    public boolean beginModifyBooking(UpdateHotelBookingDTO updateBookingDTO) {
        Booking booking = this.entityManager.find(Booking.class, updateBookingDTO.getBookingId(),
                LockModeType.OPTIMISTIC);

        LOGGER.info("Validando datos del cliente: {}", updateBookingDTO.getCustomer());
        if (!this.customerSyntaxValidator.isValid(updateBookingDTO.getCustomer())) {
            return false;
        }

        if (booking == null || !booking.isAvailable()) {
            return false;
        }

        LOGGER.info("Publicando comando: {}", EventId.BEGIN_UPDATE_HOTEL_BOOKING);

        this.eventHandlerRegistry.getHandler(EventId.BEGIN_UPDATE_HOTEL_BOOKING)
                .publishCommand(this.gson.toJson(updateBookingDTO));

        return true;
    }

    @Override
    public boolean modifyBooking(UpdateHotelBookingDTO updateBookingDTO) {
        Booking booking = this.entityManager.find(Booking.class, updateBookingDTO.getBookingId());

        if (booking == null) {
            return false;
        }

        updateBookingDTO.getRoomsInfo().forEach(roomInfo -> {
            TypedQuery<BookingLine> query = this.entityManager
                    .createNamedQuery("business.bookingLine.BookingLine.findByBookingIdAndRoomId", BookingLine.class);
            query.setParameter("bookingId", updateBookingDTO.getBookingId());
            query.setParameter("roomId", roomInfo.getRoomId());
            BookingLine bookingLine = query.getResultList().isEmpty() ? null : query.getResultList().get(0);

            if (bookingLine == null) {
                bookingLine = new BookingLine();
                bookingLine.setRoomId(roomInfo.getRoomId());
                bookingLine.setRoomDailyPrice(roomInfo.getDailyPrice());
            }
            bookingLine.setNumberOfNights(updateBookingDTO.getNumberOfNights());
            bookingLine.setStartDate(updateBookingDTO.getStartDate());
            bookingLine.setEndDate(updateBookingDTO.getEndDate());
            bookingLine.setAvailable(true);
            bookingLine.setSagaId(updateBookingDTO.getSagaId());
            bookingLine.setStatusSaga(SagaPhases.STARTED);

            this.entityManager.merge(bookingLine);

        });
        booking.setSagaId(updateBookingDTO.getSagaId());
        booking.setStatusSaga(SagaPhases.STARTED);

        return true;
    }

    @Override
    public boolean commitModifyBooking(UpdateHotelBookingDTO updateBookingDTO) {
        Booking booking = this.entityManager.find(Booking.class, updateBookingDTO.getBookingId());

        if (booking == null) {
            return false;
        }

        booking.setTotalPrice(0);
        booking.getBookingLines().forEach(bookingLine -> {
            booking.setTotalPrice(
                    booking.getTotalPrice() + (bookingLine.getRoomDailyPrice() * bookingLine.getNumberOfNights()));
            bookingLine.setStatusSaga(SagaPhases.COMPLETED);
        });

        booking.setStatusSaga(SagaPhases.COMPLETED);

        return true;
    }

    @Override
    public boolean rollbackModifyBooking(UpdateHotelBookingDTO updateBookingDTO) {
        Booking booking = this.entityManager.find(Booking.class, updateBookingDTO.getBookingId());

        if (booking == null) {
            return false;
        }

        booking.getBookingLines().forEach(bookingLine -> {
            bookingLine.setStatusSaga(SagaPhases.CANCELLED);
        });

        booking.setStatusSaga(SagaPhases.CANCELLED);

        return true;
    }

    @Override
    public boolean checkRoomsAvailabilityByUpdateHotelBooking(UpdateHotelBookingDTO updateHotelBookingDTO) {

        List<RoomInfo> rooms = updateHotelBookingDTO.getRoomsInfo();
        if (rooms == null)
            return false;

        for (RoomInfo roomInfo : rooms) {
            TypedQuery<BookingLine> existingBookingLinesQuery = this.entityManager
                    .createNamedQuery("business.bookingLine.BookingLine.findByBookingIdAndRoomId", BookingLine.class);
            existingBookingLinesQuery.setParameter("bookingId", updateHotelBookingDTO.getBookingId());
            existingBookingLinesQuery.setParameter("roomId", roomInfo.getRoomId());

            List<BookingLine> existingBookingLines = existingBookingLinesQuery.getResultList();

            for (BookingLine bookingLine : existingBookingLines) {
                if ((bookingLine.getBooking().getId() != updateHotelBookingDTO.getBookingId()
                        || !bookingLine.getRoomId().equals(roomInfo.getRoomId())) &&
                        !DateValidator.validateDates(updateHotelBookingDTO.getStartDate(),
                                updateHotelBookingDTO.getEndDate(), bookingLine.getStartDate(),
                                bookingLine.getEndDate())) {
                    return false;
                }
            }

        }

        return true;
    }

}
