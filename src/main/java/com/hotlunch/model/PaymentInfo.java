package com.hotlunch.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 8/30/13
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentInfo implements Serializable
{

    private boolean paid;
    private double amountPaid;
    private int paymentType;

    public boolean isPaid()
    {
        return paid;
    }

    public void setPaid(boolean paid)
    {
        this.paid = paid;
    }

    public double getAmountPaid()
    {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid)
    {
        this.amountPaid = amountPaid;
    }

    public int getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(int paymentType)
    {
        this.paymentType = paymentType;
    }
}
