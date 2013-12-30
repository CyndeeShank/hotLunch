package com.hotlunch.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 10/28/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketItem
{
    private Date date;
    private String grade;
    private String firstName;
    private String lastName;
    private LunchOrderItem lunchOrderItem;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getGrade()
    {
        return grade;
    }

    public void setGrade(String grade)
    {
        this.grade = grade;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
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
