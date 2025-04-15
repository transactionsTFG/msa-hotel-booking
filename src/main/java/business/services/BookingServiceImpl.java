package business.services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.google.gson.Gson;

import business.booking.Booking;
import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.bookingline.BookingLine;
import business.bookingline.BookingLineDTO;
import business.dto.CreateBookingDTO;
import business.mapper.BookingMapper;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import validator.CustomerSyntaxValidator;

@Stateless
public class BookingServiceImpl implements BookingService {

    private EntityManager entityManager;
    private EventHandlerRegistry eventHandlerRegistry;
    private Gson gson;
    private CustomerSyntaxValidator customerSyntaxValidator;

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
    public void setCommandHandlerRegistry(EventHandlerRegistry eventHandlerRegistry) {
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
    public boolean createBookingAsync(CreateBookingDTO createBookingDTO) {

        if (!this.customerSyntaxValidator.isValid(createBookingDTO.getCustomer())) {
            return false;
        }

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

        this.entityManager.persist(booking);
        this.entityManager.flush();

        return BookingMapper.INSTANCE.entityToDTO(booking);

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
        bookingLines.forEach(bl -> {
            BookingLine bookingLine = new BookingLine();

            bookingLine.setRoomId(bl.getRoomId());
            bookingLine.setBooking(booking);
            bookingLine.setNumberOfNights(bl.getNumberOfNights());
            bookingLine.setRoomDailyPrice(bl.getRoomDailyPrice());
            bookingLine.setStartDate(bl.getStartDate());
            bookingLine.setEndDate(bl.getEndDate());
            bookingLine.setAvailable(true);

            double newTotalPrice = bl.getRoomDailyPrice() * bl.getRoomDailyPrice();
            booking.setTotalPrice(booking.getTotalPrice() + newTotalPrice);
            this.entityManager.merge(bookingLine);
        });

        this.entityManager.merge(booking);

        return true;
    }

}
