package business.bookingline;

import business.booking.BookingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingLineDTO {
    private String roomId;
    private BookingDTO bookingDTO;
    private int numberOfNights;
    private double roomDailyPrice;
    private String startDate;
    private String endDate;
    private boolean available;
}