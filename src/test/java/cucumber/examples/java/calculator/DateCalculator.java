package cucumber.examples.java.calculator;

import java.util.Date;

public class DateCalculator {

    public static ThreadLocal<Boolean> failMode = new ThreadLocal<Boolean>();

    private Date now;

    public DateCalculator(Date now) {
        this.now = now;
    }

    public String isDateInThePast(Date date) {
        if (failMode.get() != null) {
            return "error";
        }
        return (date.before(now)) ? "yes" : "no";
    }
}
