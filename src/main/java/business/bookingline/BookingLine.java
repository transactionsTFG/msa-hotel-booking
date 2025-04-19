package business.bookingline;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import business.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedQueries({
        @NamedQuery(name = "business.bookingLine.BookingLine.findByBookingIdAndRoomId", query = "SELECT b FROM BookingLine b WHERE b.booking.id = :bookingId AND b.roomId = :roomId"),
        @NamedQuery(name = "business.bookingLine.BookingLine.findByRoomId", query = "SELECT b FROM BookingLine b WHERE b.roomId = :roomId"),
})
public class BookingLine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String roomId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private int numberOfNights;

    private double roomDailyPrice;

    private String startDate;

    private String endDate;

    @Column(columnDefinition = "boolean default true")
    private boolean available;

    @Version
    private int version;

    public BookingLineDTO toDTO() {
        return BookingLineDTO.builder()
                .roomId(roomId)
                .numberOfNights(numberOfNights)
                .roomDailyPrice(roomDailyPrice)
                .startDate(startDate)
                .endDate(endDate)
                .available(available)
                .build();
    }
}