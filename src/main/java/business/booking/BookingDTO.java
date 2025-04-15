package business.booking;


import business.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msa.commons.saga.SagaPhases;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {

    private CustomerDTO customerDTO;
    private long id;
    private boolean withBreakfast;
    private int peopleNumber;
    private String userId;
    private boolean available;
    private double totalPrice;
    private String sagaId;
    private SagaPhases statusSaga;

}