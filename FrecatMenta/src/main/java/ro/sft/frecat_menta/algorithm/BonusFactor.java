package ro.sft.frecat_menta.algorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by 286868 on 3/15/2016.
 */
public class BonusFactor {


    private int defaultFactor = 5000;

    public BonusFactor() {
        if (isNineToFive()) {
            defaultFactor /= 1.5;
        }
    }



    public boolean isNineToFive() {
        GregorianCalendar cal = new GregorianCalendar();
        boolean mondayToFriday = cal.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        boolean nineToFive = 9 <= hour && hour <= 17;

        if (mondayToFriday && nineToFive) {
            return true;
        } else {
            return false;
        }
    }

    public float get() {
        return defaultFactor;
    }
}
