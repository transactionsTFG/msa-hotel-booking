package controller;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.booking.BookingWithLinesDTO;
import business.dto.CreateHotelBookingDTO;
import business.dto.DeleteBookingLineDTO;
import business.dto.UpdateHotelBookingDTO;
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
    @Path("/createBooking")
    public Response createBooking(CreateHotelBookingDTO booking) {
        LOGGER.info("Iniciando creacion de reserva: {}", booking);
        boolean success = this.bookingService.createBookingAsync(booking);

        if (success)
            return Response.status(Response.Status.CREATED).entity("La creacion de la reserva se ha inicado").build();

        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No se cumplen las reglas de negocio").build();
    }

    @GET
    @Path("/getBooking/{bookingId}")
    public Response readBooking(@PathParam(value = "bookingId") long bookingId) {
        LOGGER.info("Leyendo reserva con id {}", bookingId);
        BookingWithLinesDTO bookingWithLinesDTO = this.bookingService.getBookingWithLines(bookingId);

        return Response.status(Response.Status.OK)
                .entity(bookingWithLinesDTO == null ? "No existe la reserva con id " + bookingId
                        : "Reserva encontrada: " + bookingWithLinesDTO.toString())
                .build();

    }

    @POST
    @Path("/updateBooking")
    @Transactional
    public Response updateBooking(UpdateHotelBookingDTO updateBookingDTO) {
        LOGGER.info("Modificando la reserva {}", updateBookingDTO.getBookingId());
        boolean success = this.bookingService.beginModifyBooking(updateBookingDTO);

        if (success)
            return Response.status(Response.Status.CREATED).entity("La actualizacion de la reserva se ha inicado")
                    .build();

        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No se cumplen las reglas de negocio").build();
    }

    @POST
    @Transactional
    @Path("/deleteBooking/{bookingId}")
    public Response deleteBooking(@PathParam(value = "bookingId") long bookingId) {
        LOGGER.info("Cancelando reserva con id {}", bookingId);

        boolean success = this.bookingService.beginDeleteBooking(bookingId);

        if (success)
            return Response.status(Response.Status.OK).entity("La cancelacion de la reserva se ha inicado").build();

        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("La reserva no existe o ya ha sido cancelada")
                .build();

    }

    @POST
    @Transactional
    @Path("/deleteBookingLine")
    public Response deleteBookingLine(DeleteBookingLineDTO deleteBookingLineDTO) {
        LOGGER.info("Cancelando linea de reserva {}", deleteBookingLineDTO.toString());

        boolean success = this.bookingService.beginDeleteBookingLine(deleteBookingLineDTO);

        if (success)
            return Response.status(Response.Status.OK).entity("La cancelacion de la linea de reserva se ha inicado")
                    .build();

        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity("La linea de reserva no existe o ya ha sido cancelada")
                .build();

    }

}
