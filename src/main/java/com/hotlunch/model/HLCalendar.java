package com.hotlunch.model;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 8/21/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class HLCalendar
{
    private String month;
    private HLCalendarItem hlCalendarItem;

    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public HLCalendarItem getHlCalendarItem()
    {
        return hlCalendarItem;
    }

    public void setHlCalendarItem(HLCalendarItem hlCalendarItem)
    {
        this.hlCalendarItem = hlCalendarItem;
    }
}
