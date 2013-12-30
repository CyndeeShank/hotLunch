package com.hotlunch.model;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 9/24/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class PurchasingItem
{
    private String vendorName;
    private String day;
    private String date;
    private String choice1;
    private int choice1Amount;
    private String choice2;
    private int choice2Amount;
    private String choice3;
    private int choice3Amount;
    private String choice4;
    private int choice4Amount;
    private int extra;
    private double itemCost;
    private String info;

    public String getVendorName()
    {
        return vendorName;
    }

    public void setVendorName(String vendorName)
    {
        this.vendorName = vendorName;
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getChoice1()
    {
        return choice1;
    }

    public void setChoice1(String choice1)
    {
        this.choice1 = choice1;
    }

    public int getChoice1Amount()
    {
        return choice1Amount;
    }

    public void setChoice1Amount(int choice1Amount)
    {
        this.choice1Amount = choice1Amount;
    }

    public String getChoice2()
    {
        return choice2;
    }

    public void setChoice2(String choice2)
    {
        this.choice2 = choice2;
    }

    public int getChoice2Amount()
    {
        return choice2Amount;
    }

    public void setChoice2Amount(int choice2Amount)
    {
        this.choice2Amount = choice2Amount;
    }

    public String getChoice3()
    {
        return choice3;
    }

    public void setChoice3(String choice3)
    {
        this.choice3 = choice3;
    }

    public int getChoice3Amount()
    {
        return choice3Amount;
    }

    public void setChoice3Amount(int choice3Amount)
    {
        this.choice3Amount = choice3Amount;
    }

    public String getChoice4()
    {
        return choice4;
    }

    public void setChoice4(String choice4)
    {
        this.choice4 = choice4;
    }

    public int getChoice4Amount()
    {
        return choice4Amount;
    }

    public void setChoice4Amount(int choice4Amount)
    {
        this.choice4Amount = choice4Amount;
    }

    public int getExtra()
    {
        return extra;
    }

    public void setExtra(int extra)
    {
        this.extra = extra;
    }

    public double getItemCost()
    {
        return itemCost;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public void setItemCost(double itemCost)
    {
        this.itemCost = itemCost;
    }
}
