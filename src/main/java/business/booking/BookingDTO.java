package business.booking;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {

    private long id;
    private boolean withBreakfast;
    private int peopleNumber;
    private String userId;
    private boolean available;
    private double totalPrice;

}