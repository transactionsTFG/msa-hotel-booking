package business.booking;

import java.util.List;

import business.bookingline.BookingLineDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingWithLinesDTO {
    private BookingDTO bookingDTO;
    private List<BookingLineDTO> bookingLines;
}
