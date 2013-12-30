package com.hotlunch.model;

import java.io.Serializable;
import java.lang.Float;
import java.lang.String;
import java.util.List;

public class LunchItem implements Serializable
{
    private int itemType;
    private String name;
    private String desc;
    private boolean choice;
    private String choice1;
    private String choice2;
    private String choice3;
    private Float price;
    private Float priceAdd;
    private List<String> sides;
    private List<String> condiments;
    private List<String> paperGoods;

    public int getItemType()
    {
        return itemType;
    }

    public void setItemType(int itemType)
    {
        this.itemType = itemType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public boolean isChoice()
    {
        return choice;
    }

    public void setChoice(boolean choice)
    {
        this.choice = choice;
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

    public String getChoice3()
    {
        return choice3;
    }

    public void setChoice3(String choice3)
    {
        this.choice3 = choice3;
    }

    public Float getPrice()
    {
        return price;
    }

    public void setPrice(Float price)
    {
        this.price = price;
    }

    public void setChoice2(String choice2)
    {
        this.choice2 = choice2;
    }

    public Float getPriceAdd()
    {
        return priceAdd;
    }

    public void setPriceAdd(Float priceAdd)
    {
        this.priceAdd = priceAdd;
    }

    public List<String> getSides()
    {
        return sides;
    }

    public void setSides(List<String> sides)
    {
        this.sides = sides;
    }

    public List<String> getCondiments()
    {
        return condiments;
    }

    public void setCondiments(List<String> condiments)
    {
        this.condiments = condiments;
    }

    public List<String> getPaperGoods()
    {
        return paperGoods;
    }

    public void setPaperGoods(List<String> paperGoods)
    {
        this.paperGoods = paperGoods;
    }
}

