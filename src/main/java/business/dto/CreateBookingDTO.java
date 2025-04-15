package business.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import msa.commons.microservices.hotelroom.commandevent.model.RoomInfo;

@Data
@Builder
public class CreateBookingDTO {
    private CustomerDTO customer;
    private String startDate;
    private String endDate;
    private int numberOfNights;
    private Boolean withBreakfast;
    private int peopleNumber;
    private double totalPrice;
    private String customerDNI;
    private String userId;
    private List<RoomInfo> roomsInfo;
}
