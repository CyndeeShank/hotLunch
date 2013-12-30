package com.hotlunch.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 8/29/13
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class HLOrderItemMap implements Serializable
{
    private Map<String, LunchOrderItem> orderItemMap;

    public Map<String, LunchOrderItem> getOrderItemMap()
    {
        return orderItemMap;
    }

    public void setOrderItemMap(Map<String, LunchOrderItem> orderItemMap)
    {
        this.orderItemMap = orderItemMap;
    }
}
