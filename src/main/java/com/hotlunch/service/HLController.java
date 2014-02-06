package com.hotlunch.service;

import com.hotlunch.model.*;
import com.hotlunch.util.Constants;
import com.hotlunch.util.Report;
import com.hotlunch.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class HLController extends Constants
{
    @Autowired(required = true)
    private HLService hlService;

    @Autowired(required = true)
    private Report report;

    private static final Logger logger = Logger.getLogger(HLController.class.getCanonicalName());

    public HLController()
    {
        logger.log(Level.INFO, "-=[ HLController Constructor ]=-");
    }

    @RequestMapping(value = "/testHL", method = RequestMethod.GET)
    public
    @ResponseBody
    Object test()
    {
        logger.log(Level.INFO, "-=[ HLController calling testHL ]=-");
        return new Date();
    }

    @RequestMapping(value = "/getLunchItem/{name}", method = RequestMethod.GET)
    public
    @ResponseBody
    LunchItem getLunchItem(@PathVariable("name") String name)
    {
        logger.log(Level.INFO, "-=[ calling getLunchItem/" + name + " ]=-");
        LunchItem lunchItem = hlService.getLunchItem(name);
        return lunchItem;
    }


    @RequestMapping(value = "/addLunchItem", method = RequestMethod.POST)
    public
    @ResponseBody
    LunchItem addLunchItem(@RequestParam String name, @RequestParam String desc, @RequestParam boolean choice,
                           @RequestParam String choice1, @RequestParam String choice2, @RequestParam String choice3,
                           @RequestParam Float price, @RequestParam Float priceAdd, @RequestParam List<String> sides,
                           @RequestParam List<String> condiments, @RequestParam List<String> paperGoods)
    {
        logger.log(Level.INFO, "-=[ calling addLunchItem for " + name + " ]=-");
        LunchItem lunchItem = new LunchItem();
        lunchItem.setName(name);
        lunchItem.setDesc(desc);
        lunchItem.setChoice(choice);
        lunchItem.setPrice(price);
        if (lunchItem.isChoice())
        {
            lunchItem.setChoice1(choice1);
            lunchItem.setChoice2(choice2);
            lunchItem.setChoice3(choice2);
        }
        lunchItem.setPriceAdd(priceAdd);
        lunchItem.setSides(sides);
        lunchItem.setCondiments(condiments);
        lunchItem.setPaperGoods(paperGoods);

        logger.info("sides: " + sides);
        logger.info("condiments: " + condiments);
        logger.info("paperGoods: " + paperGoods);

        LunchItem newLunchItem = hlService.addOrUpdateLunchItem(lunchItem);
        logger.log(Level.INFO, "-=[ returning newLunchItem " + newLunchItem + " ]=-");
        return newLunchItem;
    }

    @RequestMapping(value = "/addLunchOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Object addLunchOrder(HttpServletRequest request, HttpSession session)
    {
        double orderTotal = 0.0;

        String name = (String) session.getAttribute("name");
        String grade = (String) session.getAttribute("grade");
        /**
         String name = request.getParameter("name").trim();
         String grade = request.getParameter("grade");
         **/

        logger.log(Level.INFO, "-=[ calling addLunchOrder for: " + name + " / " + grade + " ]=-");
        HLOrderItemMap hlOrderItemMap = new HLOrderItemMap();
        Map<String, LunchOrderItem> orderItemMap = new HashMap<String, LunchOrderItem>();
        /**
         * get the name and grade
         *  name=cyndee+shank
         *  &grade=2*
         */
        OrderItem orderItem = new OrderItem();
        orderItem.setStudentName(name);
        orderItem.setStudentGrade(grade);

        String month = request.getParameter("month");
        logger.info("===== month: " + month + "=====");

        /**
         * get info for Carls Jr.
         *
         * &carls-date=9%2F26%2F2013*
         * &carls-choice-1=cheese*
         * &carls-order=false*
         * &carls-add=false
         */
        int itemType = CARLS_ITEM;
        String item = "carls-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += CARLS_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CARLS_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CARLS_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);

            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = CARLS_ITEM;
        item = "carls1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += CARLS_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CARLS_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CARLS_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);

            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Chick-Fil-A
         *
         * &chickfila-date=9%2F11%2F2013*
         * &chickfila-choice-1=breaded*
         * &chickfila-order=false*
         * &chickfila-add=false*
         */
        itemType = CHICKFILA_ITEM;
        item = "chickfila-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += CHICKFILA_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CHICKFILA_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CHICKFILA_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = CHICKFILA_ITEM;
        item = "chickfila1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += CHICKFILA_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CHICKFILA_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CHICKFILA_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Chipotle
         *
         * &chipotle-date=9%2F10%2F2013*
         * &chipotle-choice-1=one/two*
         * &chipotle-choice-2=cheese/chicken
         * &chipotle-order=false*
         * &chipotle-add=false*
         */
        itemType = CHIPOTLE_ITEM;
        item = "chipotle-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            //orderTotal += CHIPOTLE_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CHIPOTLE_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            //lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));

            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                orderTotal += CHIPOTLE_CHEESE_PRICE;
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken"))
            {
                orderTotal += CHIPOTLE_CHICKEN_PRICE;
            }

            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CHIPOTLE_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }
        itemType = CHIPOTLE_ITEM;
        item = "chipotle1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            //orderTotal += CHIPOTLE_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(CHIPOTLE_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            //lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                orderTotal += CHIPOTLE_CHEESE_PRICE;
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken"))
            {
                orderTotal += CHIPOTLE_CHICKEN_PRICE;
            }

            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += CHIPOTLE_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }
        /**
         * get info for Flame Broiler
         *
         * &flame-date=9%2F18%2F2013*
         * &flame-order=false*
         * &flame-add=false*
         */
        itemType = FLAMEBROILER_ITEM;
        item = "flame-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += FLAMEBROILER_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(FLAMEBROILER_STRING);
            lunchOrderItem.setOrderOne(true);
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += FLAMEBROILER_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = FLAMEBROILER_ITEM;
        item = "flame1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += FLAMEBROILER_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(FLAMEBROILER_STRING);
            lunchOrderItem.setOrderOne(true);
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += FLAMEBROILER_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Hot Dog
         *
         * &hotdog-date=9%2F9%2F2013*
         * &hotdog-order=false*
         * &hotdog-add=false*
         */
        itemType = HOTDOG_ITEM;
        item = "hotdog-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += HOTDOG_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(HOTDOG_STRING);
            lunchOrderItem.setOrderOne(true);
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += HOTDOG_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for KFC
         *
         * &kfc-date=9%2F17%2F2013*
         * &kfc-choice-1=strips*
         * &kfc-order=false*
         * &kfc-add=false*
         */
        itemType = KFC_ITEM;
        item = "kfc-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += KFC_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(KFC_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += KFC_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = KFC_ITEM;
        item = "kfc1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += KFC_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(KFC_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += KFC_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Pick Up Stix
         *
         * &pickup-date=9%2F25%2F2013*
         * &pickup-choice-1=strips*
         * &pickup-choice-2=strips*
         * &pickup-order=false*
         * &pickup-add=false*
         */
        itemType = PICKUPSTIX_ITEM;
        item = "pickup-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PICKUPSTIX_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PICKUPSTIX_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PICKUPSTIX_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = PICKUPSTIX_ITEM;
        item = "pickup1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PICKUPSTIX_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PICKUPSTIX_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PICKUPSTIX_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Pizza 1
         *
         * &pizza1-date=9%2F13%2F2013*
         * &pizza1-choice-1=cheese*
         * &pizza1-choice-2=salad*
         * &pizza1-order=false*
         * &pizza1-add=false*
         */
        itemType = PIZZA1_ITEM;
        item = "pizza1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PIZZA1_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PIZZA1_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PIZZA1_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = PIZZA1_ITEM;
        item = "pizza3-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PIZZA1_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PIZZA1_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PIZZA1_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Pizza 2
         *
         * &pizza2-date=9%2F13%2F2013*
         * &pizza2-choice-1=cheese*
         * &pizza2-choice-2=salad*
         * &pizza2-order=false*
         * &pizza2-add=false*
         */
        itemType = PIZZA2_ITEM;
        item = "pizza2-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PIZZA2_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PIZZA2_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PIZZA2_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Portofino 1
         *
         * &port1-date=9%2F12%2F2013*
         * &port1-choice-1=cheese*
         * &port1-order=false*
         * &port1-add=false*
         */
        itemType = PORTOFINO1_ITEM;
        item = "port1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PORTOFINO1_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PORTOFINO1_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PORTOFINO1_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = PORTOFINO1_ITEM;
        item = "port1-1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PORTOFINO1_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PORTOFINO1_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PORTOFINO1_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Portofino 2
         *
         * &port2-date=9%2F12%2F2013*
         * &port2-choice-1=cheese*
         * &port2-order=false*
         * &port2-add=false*
         */
        itemType = PORTOFINO2_ITEM;
        item = "port2-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += PORTOFINO2_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(PORTOFINO2_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += PORTOFINO2_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Smoothie & Sandwich
         *
         * &smoothie-date=9%2F24%2F2013*
         * &smoothie-choice-1=jetty*
         * &smoothie-choice-2=turkey*
         * &smoothie-order=false*
         * &smoothie-add=false*
         */
        itemType = SMOOTHIE_ITEM;
        item = "smoothie-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += SMOOTHIE_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(SMOOTHIE_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += SMOOTHIE_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = SMOOTHIE_ITEM;
        item = "smoothie1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += SMOOTHIE_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(SMOOTHIE_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += SMOOTHIE_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Subway
         *
         * &subway-date=9%2F16%2F2013*
         * &subway-choice-1=turkey*
         * &subway-choice-2=white*
         * &subway-order=false*
         * &subway-add=false*
         */
        itemType = SUBWAY_ITEM;
        item = "subway-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += SUBWAY_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(SUBWAY_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += SUBWAY_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = SUBWAY_ITEM;
        item = "subway1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += SUBWAY_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(SUBWAY_STRING);
            lunchOrderItem.setOrderOne(true);
            lunchOrderItem.setChoice1(request.getParameter(item + "choice-1"));
            lunchOrderItem.setChoice2(request.getParameter(item + "choice-2"));
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += SUBWAY_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        /**
         * get info for Taquitos
         *
         * &taq-date=9%2F23%2F2013*
         * &taq-order=false*
         * &taq-add=false*
         */
        itemType = TAQUITOS_ITEM;
        item = "taq-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += TAQUITOS_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(TAQUITOS_STRING);
            lunchOrderItem.setOrderOne(true);
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += TAQUITOS_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        itemType = TAQUITOS_ITEM;
        item = "taq1-";
        if (request.getParameter(item + "order") != null && request.getParameter(item + "order").equalsIgnoreCase("true"))
        {
            orderTotal += TAQUITOS_SINGLE_PRICE;
            LunchOrderItem lunchOrderItem = new LunchOrderItem();
            lunchOrderItem.setItemType(itemType);
            lunchOrderItem.setItemTypeString(TAQUITOS_STRING);
            lunchOrderItem.setOrderOne(true);
            if (request.getParameter(item + "add").equalsIgnoreCase("true"))
            {
                lunchOrderItem.setOrderAdditional(true);
                orderTotal += TAQUITOS_ADD_PRICE;
            }
            lunchOrderItem.setDateString(request.getParameter(item + "date"));
            orderItemMap.put(formatDate(request.getParameter(item + "date")), lunchOrderItem);
            Integer dateOrder = new Integer(request.getParameter(item + "date-order"));
            CalendarMap.getInstance().addDateToMap(10, dateOrder, request.getParameter(item + "date"));
        }

        orderItem.setOrderTotal(orderTotal);
        orderItem.setMonth("February");
        orderItem.setCurrentDate(Calendar.getInstance().getTime());
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaid(false);
        orderItem.setPaymentInfo(paymentInfo);
        hlOrderItemMap.setOrderItemMap(orderItemMap);
        orderItem.setLunchOrderItemMap(hlOrderItemMap);

        //logger.log(Level.INFO, "----- OrderItemMap: " + orderItemMap + " -------");


        /**
         * call service to save order
         */
        if (hlService.addLunchOrder(orderItem))
        {
            session.setAttribute("orderItem", orderItem);
            return orderItem;
        }
        else
        {
            return null;
        }
    }

    /**
     * called from the lunch-order.html page
     *
     * @param name
     * @param grade
     * @return
     */
    @RequestMapping(value = "/getLunchOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    OrderItem getLunchOrder(@RequestParam String month, @RequestParam String name, @RequestParam String grade)
    {
        logger.log(Level.INFO, "-=[ calling getLunchOrder for month/name/grade: " + month + "/" + name + "/" + grade + " ]=-");
        OrderItem orderItem = hlService.getLunchOrder(month, name.trim(), grade);

        if (orderItem != null)
        {
            SortedMap<Date, LunchOrderItem> orderedLunchMap = orderItem.getSortedLunchMap();

            HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();
            Map<String, LunchOrderItem> lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
            if (lunchOrderItemMap != null)
            {
                Set keys = lunchOrderItemMap.keySet();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext())
                {
                    String key = (String) iterator.next(); // date string
                    Date date = new Date(key);
                    LunchOrderItem lunchOrderItem = lunchOrderItemMap.get(key);
                    orderedLunchMap.put(date, lunchOrderItem);
                }
            }

            orderItem.setSortedLunchMap(orderedLunchMap);
            return orderItem;
        }
        else
        {
            orderItem = new OrderItem();
            //TODO - set something to check to display error message
            return null;
        }
    }

    @RequestMapping(value = "/getLunchOrder", method = RequestMethod.GET)
    public
    @ResponseBody
    OrderItem getLunchOrder(HttpSession session)
    {
        OrderItem orderItem = (OrderItem) session.getAttribute("orderItem");
        if (orderItem != null)
        {
            SortedMap<Date, LunchOrderItem> orderedLunchMap = orderItem.getSortedLunchMap();

            HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();
            Map<String, LunchOrderItem> lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
            if (lunchOrderItemMap != null)
            {
                Set keys = lunchOrderItemMap.keySet();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext())
                {
                    String key = (String) iterator.next(); // date string
                    Date date = new Date(key);
                    LunchOrderItem lunchOrderItem = lunchOrderItemMap.get(key);
                    orderedLunchMap.put(date, lunchOrderItem);
                }
            }

            orderItem.setSortedLunchMap(orderedLunchMap);
            return orderItem;
        }
        else
        {
            orderItem = new OrderItem();
            //TODO - set something to check to display error message
            return null;
        }
    }

    @RequestMapping(value = "/getCalendarMap/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Integer> getCalendarMapForMonth(@PathVariable("month") String month)
    {
        logger.log(Level.INFO, "-=[ calling getCalendarMapForMonth/" + month + " ]=-");
        Map<String, Integer> calendarMap = null;
        try
        {
            if (month.equalsIgnoreCase("oct"))
            {
                calendarMap = CalendarMap.getInstance().getOctDateInfoMap();
            }
            else if (month.equalsIgnoreCase("nov"))
            {
                calendarMap = CalendarMap.getInstance().getNovDateInfoMap();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return calendarMap;
    }

    @RequestMapping(value = "/getOrderedDateMap/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    SortedMap<Integer, String> getOrderedDateMapForMonth(@PathVariable("month") String month)
    {
        logger.log(Level.INFO, "-=[ calling getOrderedDateMapForMonth/" + month + " ]=-");
        SortedMap<Integer, String> calendarMap = new TreeMap<Integer, String>();
        try
        {
            if (month.equalsIgnoreCase("oct"))
            {
                calendarMap = CalendarMap.getInstance().getOctOrderedDateMap();
                logger.log(Level.INFO, "-=[ calling getOctOrderedDateMap ]=-");
            }
            else if (month.equalsIgnoreCase("nov"))
            {
                calendarMap = CalendarMap.getInstance().getNovOrderedDateMap();
                logger.log(Level.INFO, "-=[ calling getNovOrderedDateMap ]=-");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return calendarMap;
    }

    /**
     * @RequestMapping(value = "/getCalendarMap", method = RequestMethod.GET)
     * public
     * @ResponseBody Map<String, Integer> getCalendarMap()
     * {
     * Map<String, Integer> calendarMap = null;
     * try
     * {
     * calendarMap = CalendarMap.getInstance().getDateInfoMap();
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * }
     * return calendarMap;
     * }
     * @RequestMapping(value = "/getOrderedDateMap", method = RequestMethod.GET)
     * public
     * @ResponseBody SortedMap<Integer, String> getOrderedDateMap()
     * {
     * SortedMap<Integer, String> calendarMap = new TreeMap<Integer, String>();
     * try
     * {
     * calendarMap = CalendarMap.getInstance().getOrderedDateMap();
     * }
     * catch (Exception e)
     * {
     * e.printStackTrace();
     * }
     * return calendarMap;
     * }
     * *
     */

    @RequestMapping(value = "/addStudentName", method = RequestMethod.POST)
    public
    @ResponseBody
    boolean addName(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String grade)
    {
        logger.log(Level.INFO, "-=[ calling addName for first name: {} ]=-", firstName);
        logger.log(Level.INFO, "-=[ calling addName for last name: {} ]=-", lastName);
        logger.log(Level.INFO, "-=[ calling addName for grade : {} ]=-", grade);
        return hlService.addStudentName(firstName, lastName, grade);
    }

    @RequestMapping(value = "/saveNameAndGrade", method = RequestMethod.POST)
    public
    @ResponseBody
    boolean saveNameAndGrade(@RequestParam String name, @RequestParam String grade, HttpSession session)
    {
        boolean status = true;
        try
        {
            //TODO -- add check for correctness of name and grade and return false if not found or not correct....
            logger.log(Level.INFO, "-=[ calling saveNameAndGrade for name: " + name + " ]=-", name);
            logger.log(Level.INFO, "-=[ calling saveNameAndGrade tor grade : " + grade + " ]=-", grade);

            List<String> studentList = getNamesForGrade(grade);
            if (studentList.contains(name))
            {
                session.setAttribute("name", name.trim());
                session.setAttribute("grade", grade);
            }
            else
            {
                status = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @RequestMapping(value = "/addLunchCalendar", method = RequestMethod.POST)
    public
    @ResponseBody
    HLCalendar addLunchCalendar(@RequestParam String month, @RequestParam Map<Date, LunchItem> calendarMap)
    {
        logger.log(Level.INFO, "-=[ calling addLunchCalendar for {} ]=-", month);
        HLCalendar hlCalendar = hlService.addCalendar(month, calendarMap);
        return hlCalendar;
    }

    @RequestMapping(value = "/getNames", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getNamesForEachGrade(@RequestParam String grade)
    {
        logger.log(Level.INFO, "-=[ calling getNames for grade: " + grade + " ]=-");
        return getNamesForGrade(grade);

        /**
         // convert the list of Student objects to a list of Student Names
         List<Student> studentList = hlService.getNamesForGrade(grade);
         for (Student student : studentList)
         {
         String name = student.getFirstName() + " " + student.getLastName();
         nameList.add(name);
         }
         return nameList;
         **/
    }


    @RequestMapping(value = "/getNames/{grade}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Student> getNames(@PathVariable("grade") String grade)
    {
        logger.log(Level.INFO, "-=[ calling getNames for grade: {} ]=-", grade);
        return hlService.getNamesForGrade(grade);
    }

    @RequestMapping(value = "/getLunchCalendar/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    HLCalendar getLunchCalendar(@PathVariable("month") String month)
    {
        logger.log(Level.INFO, "-=[ calling getLunchCalendar/{} ]=-", month);
        HLCalendar hlCalendar = hlService.getCalendar(month);
        return hlCalendar;
    }

    @RequestMapping(value = "/addLunchCalendarItem", method = RequestMethod.POST)
    public
    @ResponseBody
    HLCalendar addLunchCalendarItem(@RequestParam String month, @RequestParam Date date, @RequestParam LunchItem
            lunchItem)
    {
        logger.log(Level.INFO, "-=[ calling addLunchCalendarItem/{} ]=-", month);
        HLCalendar hlCalendar = hlService.addCalendarItem(month, date, lunchItem);
        return hlCalendar;
    }

    @RequestMapping(value = "/addOrderPayment", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addOrderPayment(String name, String grade, String month, boolean paid, double amount, int type)
    {
        /*
        type: 'POST',
                url: '/dispatcher/addOrderPayment',
            data: {name: name, grade: grade, paid: paid, amount: amount, type: type},
         */
        return hlService.addOrderPayment(name, grade, month, paid, amount, type);
    }

    @RequestMapping(value = "/getOrdersForGradeAndMonth", method = RequestMethod.POST)
    public
    @ResponseBody
    List<OrderItem> getOrdersForGradeAndMonth(String grade, String month)
    {
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = hlService.getOrdersByGrade(grade, month);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
        return orderItemList;
    }

    @RequestMapping(value = "/getAllOrders", method = RequestMethod.GET)
    public
    @ResponseBody
    List<OrderItem> getAllOrders()
    {
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = hlService.getAllOrders();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
        return orderItemList;
    }

    @RequestMapping(value = "/getAllOrdersForMonth/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<OrderItem> getAllOrdersForMonth(@PathVariable("month") String month)
    {
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = hlService.getAllOrdersByMonth(month);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
        return orderItemList;
    }

    @RequestMapping(value = "/getAllOrdersForMonth2/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<Date, List<LunchOrderItem>> getAllOrdersForMonth2(@PathVariable("month") String month)
    {
        Map<Date, List<LunchOrderItem>> orderListMap = null;
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = hlService.getAllOrdersByMonth(month);
            orderListMap = hlService.createPurchasingMap(orderItemList);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
        return orderListMap;
    }

    @RequestMapping(value = "/getAllTicketsFile/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getAllTicketsFile(@PathVariable("month") String month, HttpServletResponse response)
    {
        String status = "success";
        ServletOutputStream servletOutputStream = null;
        try
        {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet");
            response.setHeader("Content-disposition", "attachment; filename=ticketing-list.xls");

            //List<OrderItem> orderItemList = hlService.getAllOrdersByMonth(month);
            //report.createTicketingList(hlService.getOrdersForAllGrades(month), servletOutputStream);
            report.createTicketingList(hlService.createTicketingMap(month), servletOutputStream);

            servletOutputStream.flush();
            servletOutputStream.close();
        }
        catch (IOException e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return status;
    }

    @RequestMapping(value = "/getPurchasingSidesList/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getPurchasingSidesList(@PathVariable("month") String month, HttpServletResponse response)
    {
        String status = "success";
        ServletOutputStream servletOutputStream = null;
        try
        {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet");
            response.setHeader("Content-disposition", "attachment; filename=purchasing-sides-list.xls");

            List<OrderItem> orderItemList = hlService.getAllOrdersByMonth(month);
            report.createPurchasingSidesList(orderItemList, servletOutputStream);

            servletOutputStream.flush();
            servletOutputStream.close();
        }
        catch (IOException e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return status;
    }

    @RequestMapping(value = "/getPurchasingList/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getPurchasingList(@PathVariable("month") String month, HttpServletResponse response)
    {
        String status = "success";
        ServletOutputStream servletOutputStream = null;
        try
        {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet");
            response.setHeader("Content-disposition", "attachment; filename=purchasing-list.xls");

            List<OrderItem> orderItemList = hlService.getAllOrdersByMonth(month);
            report.createPurchasingList(orderItemList, servletOutputStream);

            servletOutputStream.flush();
            servletOutputStream.close();
        }
        catch (IOException e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return status;
    }

    @RequestMapping(value = "/getAllPaymentsFile/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getAllPaymentsFile(@PathVariable("month") String month, HttpServletResponse response)
    {
        String status = "success";
        ServletOutputStream servletOutputStream = null;
        try
        {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet");
            response.setHeader("Content-disposition", "attachment; filename=payment-list.xls");

            List<OrderItem> orderItemList = hlService.getAllOrdersByMonth(month);
            //report.createPurchasingList(orderItemList, servletOutputStream);
            report.createPaymentList(hlService.getOrdersForAllGrades(month), servletOutputStream);

            servletOutputStream.flush();
            servletOutputStream.close();
        }
        catch (IOException e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return status;
    }

    @RequestMapping(value = "/getAllOrdersFile/{month}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getAllOrdersFile(@PathVariable("month") String month, HttpServletResponse response)
    {
        String status = "success";
        ServletOutputStream servletOutputStream = null;
        try
        {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet");
            response.setHeader("Content-disposition", "attachment; filename=purchasing-list.xls");

            List<OrderItem> orderItemList = hlService.getAllOrdersByMonth(month);
            report.createPurchasingList(orderItemList, servletOutputStream);

            servletOutputStream.flush();
            servletOutputStream.close();
        }
        catch (IOException e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            status = "false";
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return status;
    }

    private List<String> getNamesForGrade(String grade)
    {
        logger.log(Level.INFO, "-=[ calling getNames for grade: " + grade + " ]=-");
        List<String> nameList = new ArrayList<String>();

        // convert the list of Student objects to a list of Student Names
        List<Student> studentList = hlService.getNamesForGrade(grade);

        for (Student student : studentList)
        {
            String name = student.getFirstName() + " " + student.getLastName();
            nameList.add(name);
        }
        return nameList;
    }

    private String formatDate(String itemDate)
    {
        /**
         Date test = new Date(itemDate);

         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MM d, yyyy");
         Date formattedDate = null;
         try
         {
         formattedDate = simpleDateFormat.parse(test.toString());
         return formattedDate.toString();
         }
         catch (ParseException e)
         {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         return itemDate;
         }
         **/
        return itemDate;
    }
}