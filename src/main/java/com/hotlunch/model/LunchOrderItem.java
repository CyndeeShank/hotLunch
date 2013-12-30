package com.hotlunch.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 8/26/13
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class LunchOrderItem implements Serializable
{
    private String dateString;
    private int itemType;
    private String itemTypeString;
    private String choice1;
    private String choice2;
    private boolean orderOne;
    private boolean orderAdditional;

    public String getDateString()
    {
        return dateString;
    }

    public void setDateString(String dateString)
    {
        this.dateString = dateString;
    }

    public String getItemTypeString()
    {
        return itemTypeString;
    }

    public void setItemTypeString(String itemTypeString)
    {
        this.itemTypeString = itemTypeString;
    }

    public int getItemType()
    {
        return itemType;
    }

    public void setItemType(int itemType)
    {
        this.itemType = itemType;
    }

    public String getChoice1()
    {
        return choice1;
    }

    public void setChoice1(String choice1)
    {
        this.choice1 = choice1;
    }

    public String getChoice2()
    {
        return choice2;
    }

    public void setChoice2(String choice2)
    {
        this.choice2 = choice2;
    }

    public boolean isOrderOne()
    {
        return orderOne;
    }

    public void setOrderOne(boolean orderOne)
    {
        this.orderOne = orderOne;
    }

    public boolean isOrderAdditional()
    {
        return orderAdditional;
    }

    public void setOrderAdditional(boolean orderAdditional)
    {
        this.orderAdditional = orderAdditional;
    }
}
