package in.suhj.banpo.Models;

import org.joda.time.DateTime;

import in.suhj.banpo.Infrastructure.Helpers.DateHelper;

/**
 * Created by SuhJin on 2014-08-17.
 */
public class Notice
{
    private String title;
    private String buttonContent;
    private String url;
    private boolean show;

    public Notice(String title, String buttonContent, String url, boolean show)
    {
        this.title = title;
        this.buttonContent = buttonContent;
        this.url = url;
        this.show = show;
    }

    public String getTitle()
    {
        return title;
    }

    public String getButtonContent()
    {
        return buttonContent;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean getShow()
    {
        return show;
    }
}
