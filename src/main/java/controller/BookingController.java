package controller;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.CreateBookingDTO;
import business.servicesevent.BookingServiceEventAdapter;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingController {
    private static final Logger LOGGER = LogManager.getLogger(BookingController.class);
    private BookingServiceEventAdapter bookingServiceEventAdapter;
    
    @EJB
    public void setBookingService(BookingServiceEventAdapter bookingServiceEventAdapter) {
        this.bookingServiceEventAdapter = bookingServiceEventAdapter;
    }
    

    @POST
    @Transactional
    public Response createBooking(CreateBookingDTO createBookingDTO) {
        LOGGER.info("Creando reserva de hotel: {}", createBookingDTO);
        boolean isCreated = bookingServiceEventAdapter.beginCreateBooking(createBookingDTO);
        if (!isCreated)
            return Response.status(Response.Status.CONFLICT).entity("Cliente o habitaciones no disponibles").build();
        return Response.status(Response.Status.CREATED).entity("Reserva de hotel creada correctamente").build();
    }
    
}
