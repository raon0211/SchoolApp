package in.suhj.banpo.Infrastructure.Helpers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * Created by SuhJin on 2014-08-05.
 */
public class DateHelper
{
    public static int GetDateId(DateTime date)
    {
        int month = date.getMonthOfYear();
        int day = date.getDayOfMonth();

        String strMonth = String.valueOf(month);
        if (month < 10)
        {
            strMonth = "0" + strMonth;
        }

        String strDay = String.valueOf(day);
        if (day < 10)
        {
            strDay = "0" + strDay;
        }

        return Integer.parseInt("" + date.getYear() + strMonth + strDay);
    }

    public static String GetDayName(int dayOfWeek)
    {
        String dayName = "";

        switch (dayOfWeek)
        {
            case DateTimeConstants.MONDAY:
                dayName = "월";
                break;
            case DateTimeConstants.TUESDAY:
                dayName = "화";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayName = "수";
                break;
            case DateTimeConstants.THURSDAY:
                dayName = "목";
                break;
            case DateTimeConstants.FRIDAY:
                dayName = "금";
                break;
            case DateTimeConstants.SATURDAY:
                dayName = "토";
                break;
            case DateTimeConstants.SUNDAY:
                dayName = "일";
                break;
            default:
                break;
        }

        return dayName;
    }
}
