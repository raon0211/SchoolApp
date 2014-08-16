package in.suhj.banpo.Infrastructure.Controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.suhj.banpo.App;
import in.suhj.banpo.R;

/**
 * Created by SuhJin on 2014-08-15.
 */
public class ScheduleItemContent extends LinearLayout
{
    public ScheduleItemContent(Context context) {
        super(context);
    }

    public ScheduleItemContent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleItemContent(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public void setContent(String schedule)
    {
        LayoutInflater inflater = (LayoutInflater)App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.template_schedule_content_card, this);

        TextView textView = (TextView)findViewById(R.id.schedule_day_item_text);
        textView.setText(schedule);
    }

    public void setBackground(Drawable resource)
    {
        LinearLayout layout = (LinearLayout)findViewById(R.id.schedule_day_item_layout);
        layout.setBackgroundDrawable(resource);
    }
}
