package business.booking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.List;

import business.bookingline.BookingLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import msa.commons.saga.SagaPhases;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Booking implements Serializable {

    private static final long serialVersionUID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Version
    private int version;

    private String userId;

    @OneToMany(mappedBy = "booking")
    private List<BookingLine> bookingLines;

    @Column(columnDefinition = "boolean default false")
    private boolean withBreakfast;

    private int peopleNumber;

    @Column(columnDefinition = "boolean default true")
    private boolean available;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaPhases statusSaga;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    public BookingDTO toDTO() {
        return BookingDTO.builder()
                .id(id)
                .withBreakfast(withBreakfast)
                .peopleNumber(peopleNumber)
                .userId(userId)
                .available(available)
                .totalPrice(totalPrice)
                .sagaId(sagaId)
                .statusSaga(statusSaga)
                .build();
    }

}