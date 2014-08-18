package in.suhj.banpo.Infrastructure.Controls;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-15.
 */
public class ScheduleCardItem extends LinearLayout
{
    private Context context;

    public ScheduleCardItem(Context context) {
        this(context, null);
    }

    public ScheduleCardItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.template_schedule_card, this);
    }

    public void setDateString(String dateString)
    {
        TextView dateStringTB = (TextView)findViewById(R.id.schedule_day_date_string);
        dateStringTB.setText(dateString);
    }

    public void setSchedules(ArrayList<String> schedules)
    {
        LinearLayout listLayout = (LinearLayout)findViewById(R.id.schedule_day_list);

        for (String schedule : schedules)
        {
            ScheduleItemContent content = new ScheduleItemContent(context);
            content.setContent(schedule);
            listLayout.addView(content);
        }

        ScheduleItemContent lastContent = (ScheduleItemContent)listLayout.getChildAt(listLayout.getChildCount() - 1);
        lastContent.removeBackground();
    }
}
