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

import business.dto.CreateHotelBookingDTO;
import business.services.BookingService;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingController {

    private static final Logger LOGGER = LogManager.getLogger(BookingController.class);
    private BookingService bookingService;

    @EJB
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @POST
    @Transactional
    public Response createBooking(CreateHotelBookingDTO booking) {
        LOGGER.info("Iniciando creacion de reserva: {}", booking);
        boolean success = this.bookingService.createBookingAsync(booking);

        if (success)
            return Response.status(Response.Status.CREATED).entity("La creacion de la reserva se ha inicado").build();

        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No se cumplen las reglas de negocio").build();
    }

}
