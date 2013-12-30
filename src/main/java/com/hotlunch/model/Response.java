package com.hotlunch.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 9/1/13
 * Time: 8:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Response implements Serializable
{
    public static String SUCCESS = "Success";
    public static String ERROR = "Error";

    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
