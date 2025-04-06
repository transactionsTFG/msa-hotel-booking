package business.services;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.booking.Booking;
import business.booking.BookingDTO;
import business.dto.CreateBookingDTO;
import business.mapper.BookingMapper;
import msa.commons.saga.SagaPhases;

@Stateless
public class BookingServiceImpl implements BookingService {

    private EntityManager entityManager;

    public BookingServiceImpl() {
    }

    @Inject
    public BookingServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public BookingDTO beginCreateBooking(CreateBookingDTO createBookingDTO) {
        Booking booking = BookingMapper.INSTANCE.createBookingDTOToEntity(createBookingDTO, SagaPhases.STARTED);

        this.entityManager.persist(booking);
        this.entityManager.flush();

        return BookingMapper.INSTANCE.entityToDTO(booking);
    }

    @Override
    public void confirmCreateBooking(long bookingId) {
        Booking booking = this.entityManager.find(Booking.class, bookingId, LockModeType.OPTIMISTIC);

        booking.setAvailable(true);
        booking.setStatus(SagaPhases.COMPLETED);

        this.entityManager.merge(booking);
    }

    @Override
    public void cancelCreateBooking(long bookingId) {
        Booking booking = this.entityManager.find(Booking.class, bookingId, LockModeType.OPTIMISTIC);

        this.entityManager.remove(booking);
    }

}
