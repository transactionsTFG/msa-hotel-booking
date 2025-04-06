package business.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateBookingDTO {
    private String startDate;
    private String endDate;
    private int numberOfNights;
    private Boolean withBreakfast;
    private int peopleNumber;
    private double totalPrice;
    private String customerDNI;
    private String userId;
    private List<String> rooIds;
}
