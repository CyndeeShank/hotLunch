package com.hotlunch.model;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 8/22/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class HLCalendarItem
{
    private Map<Date, LunchItem> dateLunchItemMap;

    public Map<Date, LunchItem> getDateLunchItemMap()
    {
        return dateLunchItemMap;
    }

    public void setDateLunchItemMap(Map<Date, LunchItem> dateLunchItemMap)
    {
        this.dateLunchItemMap = dateLunchItemMap;
    }
}
