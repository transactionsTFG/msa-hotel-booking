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
    private String userId;
    private boolean withBreakfast;
    private int peopleNumber;
    private boolean available;
    private double totalPrice;
    private SagaPhases statusSaga;
    private String sagaId;

}