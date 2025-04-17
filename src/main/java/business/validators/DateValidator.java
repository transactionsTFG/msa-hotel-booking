package business.validators;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator {
    public static boolean validateDates(String newStartDateString, String newEndDateString, String bookingLineStartDateString,
            String bookingLineEndDateString) {
        final String pattern = "yyyy-MM-dd";
        try {
            Date newStartDate = new SimpleDateFormat(pattern).parse(newStartDateString);
            Date newEndDate = new SimpleDateFormat(pattern).parse(newEndDateString);
            Date bookingLineStartDate = new SimpleDateFormat(pattern).parse(bookingLineStartDateString);
            Date bookingLineEndDate = new SimpleDateFormat(pattern).parse(bookingLineEndDateString);
            if ((newStartDate.before(bookingLineStartDate) && newEndDate.before(bookingLineStartDate)) ||
                    (newStartDate.after(bookingLineEndDate) && newEndDate.after(bookingLineEndDate))) {

            } else {
                throw new Exception("Rooms already booked");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}