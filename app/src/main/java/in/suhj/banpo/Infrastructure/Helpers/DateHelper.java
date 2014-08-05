package in.suhj.banpo.Infrastructure.Helpers;

import org.joda.time.DateTime;

/**
 * Created by SuhJin on 2014-08-05.
 */
public class DateHelper
{
    public static int GetDateId(DateTime date)
    {
        return Integer.parseInt("" + date.getYear() + date.getMonthOfYear() + date.getDayOfMonth());
    }
}
