package com.hotlunch.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OrderItem implements Serializable
{
    private String studentName;
    private String studentGrade;
    private double orderTotal;
    private String month;
    private HLOrderItemMap lunchOrderItemMap;
    private SortedMap<Date, LunchOrderItem> sortedLunchMap = new TreeMap<Date, LunchOrderItem>();
    private String orderText;
    private PaymentInfo paymentInfo;
    private Date currentDate;

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentGrade()
    {
        return studentGrade;
    }

    public void setStudentGrade(String studentGrade)
    {
        this.studentGrade = studentGrade;
    }

    public double getOrderTotal()
    {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal)
    {
        this.orderTotal = orderTotal;
    }

    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public String getOrderText()
    {
        return orderText;
    }

    public void setOrderText(String orderText)
    {
        this.orderText = orderText;
    }

    public HLOrderItemMap getLunchOrderItemMap()
    {
        return lunchOrderItemMap;
    }

    public void setLunchOrderItemMap(HLOrderItemMap lunchOrderItemMap)
    {
        if (lunchOrderItemMap == null)
        {
            this.lunchOrderItemMap = new HLOrderItemMap();
        }
        this.lunchOrderItemMap = lunchOrderItemMap;
    }

    public SortedMap<Date, LunchOrderItem> getSortedLunchMap()
    {
        return sortedLunchMap;
    }

    public void setSortedLunchMap(SortedMap<Date, LunchOrderItem> sortedLunchMap)
    {
        this.sortedLunchMap = sortedLunchMap;
    }

    public PaymentInfo getPaymentInfo()
    {
        return paymentInfo;
    }

    public Date getCurrentDate()
    {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate)
    {
        this.currentDate = currentDate;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo)
    {
        this.paymentInfo = paymentInfo;
    }
}
