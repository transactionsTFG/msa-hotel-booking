package business.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBookingDTO {
    private CreateBookingDTO bookingDTO;
    private String id;
}
