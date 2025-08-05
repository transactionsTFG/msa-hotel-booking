package controller;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingDTO;
import business.booking.BookingWithLinesDTO;
import business.services.BookingService;

@Path("/booking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingController {

    private static final Logger LOGGER = LogManager.getLogger(BookingController.class);
    private BookingService bookingService;

    @GET
    @Path("/{id}")
    public BookingWithLinesDTO getBookingById(@PathParam("id") long id) {
        LOGGER.info("Fetching booking with ID: {}", id);
        return this.bookingService.getBookingWithLines(id);
    }

    @EJB
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }
}
