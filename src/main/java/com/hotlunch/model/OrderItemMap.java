package com.hotlunch.model;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 9/6/13
 * Time: 8:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderItemMap
{
    private String date;
    private LunchOrderItem lunchOrderItem;

    public OrderItemMap()
    {

    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public LunchOrderItem getLunchOrderItem()
    {
        return lunchOrderItem;
    }

    public void setLunchOrderItem(LunchOrderItem lunchOrderItem)
    {
        this.lunchOrderItem = lunchOrderItem;
    }
}
