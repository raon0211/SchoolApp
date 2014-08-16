package in.suhj.banpo.PresentationModels;

import org.joda.time.DateTime;
import org.robobinding.itempresentationmodel.ItemPresentationModel;
import org.robobinding.presentationmodel.PresentationModel;

import in.suhj.banpo.Infrastructure.Helpers.DateHelper;
import in.suhj.banpo.Models.Schedule;

/**
 * Created by SuhJin on 2014-08-10.
 */
@PresentationModel
public class ScheduleItemContentPresentationModel implements ItemPresentationModel<String>
{
    private String content;

    public String getSchedule()
    {
        return content;
    }

    public void updateData(int index, String content)
    {
        this.content = content;
    }
}
