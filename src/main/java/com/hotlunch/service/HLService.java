package com.hotlunch.service;

import com.google.gson.reflect.TypeToken;
import com.hotlunch.util.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import com.hotlunch.model.*;
import com.hotlunch.util.Util;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.String;

@Service
public class HLService
{
    @Autowired(required = true)
    private Report report;

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private static final Logger logger = Logger.getLogger(HLController.class.getCanonicalName());

    public boolean addLunchOrder(OrderItem orderItem)
    {
        boolean status = true;
        logger.log(Level.INFO, "-=[ calling addLunchOrder for name: " + orderItem.getStudentName() + " / grade: " + orderItem.getStudentGrade() + " ]=-");
        try
        {
            Key orderKey = KeyFactory.createKey("OrderItem", orderItem.getStudentName());
            Entity orderEntity = null;
            try
            {
                Query query = new Query("OrderItem");
                query.addFilter("name", Query.FilterOperator.EQUAL, orderItem.getStudentName());
                query.addFilter("grade", Query.FilterOperator.EQUAL, orderItem.getStudentGrade());
                query.addFilter("month", Query.FilterOperator.EQUAL, orderItem.getMonth());
                List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
                if (!results.isEmpty())
                {
                    logger.log(Level.INFO, "-=[ Found " + results.size() + " entities ]=-");
                    orderEntity = results.remove(0);
                    logger.log(Level.INFO, "-=[ Found Entity: " + orderEntity + " ]=-");
                }
                //orderEntity = datastoreService.get(orderKey);
            }
            catch (Exception e)
            {
                logger.log(Level.INFO, "-=[ orderEntity not found, creating new one ]=-" + orderItem.getStudentName());
                e.printStackTrace();
            }

            if (orderEntity == null)
            {
                orderEntity = new Entity("OrderItem", orderKey);
                logger.log(Level.INFO, "-=[ created new Entity OrderItem ]=-");
            }

            Gson gson = new Gson();
            String orderItemString = gson.toJson(orderItem.getLunchOrderItemMap());
            orderEntity.setProperty("name", orderItem.getStudentName());
            orderEntity.setProperty("grade", orderItem.getStudentGrade());
            orderEntity.setProperty("total", orderItem.getOrderTotal());
            orderEntity.setProperty("month", orderItem.getMonth());
            Text orderText = new Text(orderItemString);
            orderEntity.setProperty("orderText", orderText);
            String paymentInfoString = gson.toJson(orderItem.getPaymentInfo());
            orderEntity.setProperty("paymentInfo", paymentInfoString);
            orderEntity.setProperty("currentDate", orderItem.getCurrentDate());

            datastoreService.put(orderEntity);
            logger.log(Level.INFO, "-=[ saved LunchOrderItem to datastore ]=-");
        }
        catch (Exception e)
        {
            status = false;
            e.printStackTrace();
            logger.log(Level.INFO, "-=[ ERROR saving LunchOrderItem to datastore ]=-");
        }

        return status;
    }

    public boolean addStudentName(String firstName, String lastName, String grade)
    {
        boolean status = true;
        logger.log(Level.INFO, "-=[ calling addStNudentame for name: " + firstName + " " + lastName + " / grade: " + grade + " ]=-");
        try
        {
            Key studentKey = KeyFactory.createKey("StudentInfo", firstName + lastName);
            Entity studentEntity = null;
            try
            {
                Query query = new Query("StudentInfo");
                query.addFilter("firstname", Query.FilterOperator.EQUAL, firstName);
                query.addFilter("lastname", Query.FilterOperator.EQUAL, lastName);
                query.addFilter("grade", Query.FilterOperator.EQUAL, grade);
                List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
                if (!results.isEmpty())
                {
                    logger.log(Level.INFO, "-=[ Found " + results.size() + " entities ]=-");
                    studentEntity = results.remove(0);
                    logger.log(Level.INFO, "-=[ Found Entity: " + studentEntity + " ]=-");
                }
            }
            catch (Exception e)
            {
                logger.log(Level.INFO, "-=[ StudentInfo Entity not found, creating new one ]=-" + firstName + lastName);
                e.printStackTrace();
            }

            if (studentEntity == null)
            {
                studentEntity = new Entity("StudentInfo", studentKey);
                logger.log(Level.INFO, "-=[ created new Entity StudentInfo ]=-");
            }

            studentEntity.setProperty("firstname", firstName);
            studentEntity.setProperty("lastname", lastName);
            studentEntity.setProperty("grade", grade);

            datastoreService.put(studentEntity);
            logger.log(Level.INFO, "-=[ saved StudentInfo to datastore ]=-");
        }
        catch (Exception e)
        {
            status = false;
            e.printStackTrace();
            logger.log(Level.INFO, "-=[ ERROR saving StudentInfo to datastore ]=-");
        }

        return status;
    }

    public Response addOrderPayment(String name, String grade, String month, boolean paid, double amount, int type)
    {
        Response response = new Response();
        Entity orderEntity = null;
        try
        {
            logger.log(Level.INFO, "-=[ Querying for name: " + name + " / grade: " + grade + " / month: " + month + " ]=-");
            Query query = new Query("OrderItem");
            query.addFilter("name", Query.FilterOperator.EQUAL, name);
            query.addFilter("grade", Query.FilterOperator.EQUAL, grade);
            query.addFilter("month", Query.FilterOperator.EQUAL, month);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            if (!results.isEmpty())
            {
                logger.log(Level.INFO, "-=[ Found " + results.size() + " entities ]=-");
                orderEntity = results.remove(0);
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setPaid(paid);
                paymentInfo.setAmountPaid(amount);
                paymentInfo.setPaymentType(type);
                Gson gson = new Gson();
                String paymentInfoString = gson.toJson(paymentInfo);
                orderEntity.setProperty("paymentInfo", paymentInfoString);

                datastoreService.put(orderEntity);
                logger.log(Level.INFO, "-=[ Updated Entity: " + orderEntity + " ]=-");
                response.setMessage(Response.SUCCESS);
            }
            else
            {
                logger.log(Level.INFO, "-=[ orderEntity not found for: " + name + "/" + grade + " ]=-");
                response.setMessage(Response.ERROR);
            }
        }
        catch (Exception e)
        {
            //logger.log(Level.INFO, "-=[ orderEntity not found for: " + name + "/" + grade + " ]=-");
            logger.log(Level.INFO, "-=[ ERROR -- exception... ]=-");
            response.setMessage(Response.ERROR);
            e.printStackTrace();
        }

        if (orderEntity == null)
        {
            // return error
            logger.log(Level.INFO, "-=[ ERROR -- need to figure out why it wasn't found for update ]=-");
            response.setMessage(Response.ERROR);
        }
        return response;
    }

    public LunchItem addOrUpdateLunchItem(LunchItem lunchItem)
    {
        logger.log(Level.INFO, "-=[ calling addOrdUpdateLunchItem ]=-");
        Key itemKey = KeyFactory.createKey("LunchItem", lunchItem.getName());
        Entity lunchEntity = null;
        try
        {
            lunchEntity = datastoreService.get(itemKey);
        }
        catch (EntityNotFoundException e)
        {
            logger.log(Level.INFO, "-=[ lunchEntity not found, creating new one ]=-" + lunchItem.getName());
        }

        if (lunchEntity == null)
        {
            lunchEntity = new Entity("LunchItem", itemKey);
            logger.log(Level.INFO, "-=[ created new Entity LunchItem ]=-");
        }

        lunchEntity.setProperty("name", lunchItem.getName());
        lunchEntity.setProperty("desc", lunchItem.getDesc());
        lunchEntity.setProperty("choice1", lunchItem.getChoice1());
        lunchEntity.setProperty("choice2", lunchItem.getChoice2());
        lunchEntity.setProperty("price", lunchItem.getPrice());
        lunchEntity.setProperty("priceAdd", lunchItem.getPriceAdd());
        Gson gson = new Gson();
        String sides = gson.toJson(lunchItem.getSides());
        lunchEntity.setProperty("sides", sides);
        String condiments = gson.toJson(lunchItem.getCondiments());
        lunchEntity.setProperty("condiments", condiments);
        String papergoods = gson.toJson(lunchItem.getPaperGoods());
        lunchEntity.setProperty("papergoods", papergoods);

        datastoreService.put(lunchEntity);
        logger.log(Level.INFO, "-=[ saved Entity LunchItem ]=-");

        return lunchItem;
    }

    public Student getStudentFirstLastName(String name, String grade)
    {
        //logger.log(Level.INFO, "-=[ calling getStudentFirstLastName for Name" + name + " / Grade: " + grade + " ]=-");
        Student student = new Student();
        try
        {
            Iterable<Entity> entities = Util.listEntities("StudentInfo", "grade", grade);

            for (Entity studentEntity : entities)
            {
                String firstname = (String) studentEntity.getProperty("firstname");
                String lastname = (String) studentEntity.getProperty("lastname");
                if (name.startsWith(firstname) && name.endsWith(lastname))
                {
                    //logger.info("found " + firstname + " -- " + lastname);
                    student.setFirstName((String) studentEntity.getProperty("firstname"));
                    student.setLastName((String) studentEntity.getProperty("lastname"));
                    student.setGrade(grade);
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.INFO, "-=[ Entity Not Found: " + e.getMessage() + " ]=-");
            e.printStackTrace();
        }
        return student;
    }

    public List<Student> getNamesForGrade(String grade)
    {
        List<Student> studentList = new ArrayList<Student>();
        try
        {
            Iterable<Entity> entities = Util.listEntities("StudentInfo", "grade", grade);

            for (Entity studentEntity : entities)
            {
                Student student = new Student();
                student.setFirstName((String) studentEntity.getProperty("firstname"));
                student.setLastName((String) studentEntity.getProperty("lastname"));
                student.setGrade(grade);
                studentList.add(student);
            }
        }
        catch (Exception e)
        {
            logger.log(Level.INFO, "-=[ Entity Not Found: " + e.getMessage() + " ]=-");
            e.printStackTrace();
        }
        return studentList;
    }

    public OrderItem getLunchOrder(String month, String name, String grade)
    {
        OrderItem orderItem = new OrderItem();
        try
        {
            Query query = new Query("OrderItem");
            query.addFilter("month", Query.FilterOperator.EQUAL, month);
            query.addFilter("name", Query.FilterOperator.EQUAL, name);
            query.addFilter("grade", Query.FilterOperator.EQUAL, grade);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            if (!results.isEmpty())
            {
                logger.log(Level.INFO, "-=[ Found " + results.size() + " entities ]=-");
                //TODO -- if results > 1, return a message to email cyndee to fix the issue
                Entity orderItemEntity = results.remove(0);
                logger.log(Level.INFO, "-=[ Found Entity: " + orderItemEntity + " ]=-");
                Text orderText = (Text) orderItemEntity.getProperty("orderText");
                String orderItemString = orderText.getValue();
                Gson gson = new Gson();
                HLOrderItemMap hlOrderItemMap = gson.fromJson(orderItemString, HLOrderItemMap.class);
                logger.log(Level.INFO, "hlOrderItemMap after converting back to object: " + hlOrderItemMap);
                orderItem.setLunchOrderItemMap(hlOrderItemMap);
                orderItem.setOrderText(orderItemString);
                orderItem.setMonth((String) orderItemEntity.getProperty("month"));
                orderItem.setStudentName((String) orderItemEntity.getProperty("name"));
                orderItem.setStudentGrade((String) orderItemEntity.getProperty("grade"));
                Double total = (Double) orderItemEntity.getProperty("total");
                orderItem.setOrderTotal(total);

                String paymentInfoString = (String) orderItemEntity.getProperty("paymentInfo");
                orderItem.setPaymentInfo(gson.fromJson(paymentInfoString, PaymentInfo.class));
            }
        }
        catch (Exception e)
        {
            logger.log(Level.INFO, "-=[ Entity Not Found: " + e.getMessage() + " ]=-");
            e.printStackTrace();

            orderItem = null;
        }
        return orderItem;
    }


    public LunchItem getLunchItem(String name)
    {
        LunchItem lunchItem = null;
        try
        {
            Query query = new Query("LunchIem");
            query.addFilter("name", Query.FilterOperator.EQUAL, name);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            if (!results.isEmpty())
            {
                Entity lunchEntity = results.remove(0);
                logger.log(Level.INFO, "-=[ Found Entity: " + lunchEntity + " ]=-");
                String lunchItemString = (String) lunchEntity.getProperty("contents");
                Gson gson = new Gson();
                lunchItem = gson.fromJson(lunchItemString, LunchItem.class);
            }

            /**
             Key itemKey = KeyFactory.createKey("LunchItem", name);
             Entity lunchEntity = datastoreService.get(itemKey);
             String lunchItemString = (String) lunchEntity.getProperty("contents");
             Gson gson = new Gson();
             lunchItem = gson.fromJson(lunchItemString, LunchItem.class);
             **/
        }
        catch (Exception e)
        {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            logger.log(Level.INFO, "-=[ Entity Not Found: " + e.getMessage() + " ]=-");
            lunchItem = null;
        }

        return lunchItem;
    }

    public HLCalendar addCalendar(String month, Map<Date, LunchItem> calendarMap)
    {
        logger.log(Level.INFO, "-=[ calling addCalendar ]=-");
        Key calendarKey = KeyFactory.createKey("calendar", month);
        HLCalendar hlCalendar = new HLCalendar();
        hlCalendar.setMonth(month);
        HLCalendarItem hlCalendarItem = new HLCalendarItem();
        hlCalendarItem.setDateLunchItemMap(calendarMap);
        hlCalendar.setHlCalendarItem(hlCalendarItem);

        Gson gson = new Gson();
        String calendarMapString = gson.toJson(calendarMap);

        Entity monthEntity = new Entity(month, calendarKey);
        Text calendarText = new Text(calendarMapString);
        monthEntity.setProperty("calendarMapText", calendarText);

        datastoreService.put(monthEntity);

        return hlCalendar;
    }

    public HLCalendar getCalendar(String month)
    {
        logger.log(Level.INFO, "-=[ calling getCalendar ]=-");

        Key calendarKey = KeyFactory.createKey("calendar", month);
        Entity monthEntity = Util.findEntity(calendarKey);
        Text calendarText = (Text) monthEntity.getProperty("calendarMapText");

        String calendarMapString = calendarText.getValue();
        Gson gson = new Gson();
        HLCalendarItem hlCalendarItem = gson.fromJson(calendarMapString, HLCalendarItem.class);

        HLCalendar hlCalendar = new HLCalendar();
        hlCalendar.setMonth(month);
        hlCalendar.setHlCalendarItem(hlCalendarItem);

        return hlCalendar;
    }


    public HLCalendar addCalendarItem(String month, Date date, LunchItem lunchItem)
    {
        logger.log(Level.INFO, "-=[ calling addCalendarItem ]=-");
        Key itemKey = KeyFactory.createKey("HotDogLunchItem", lunchItem.getName());
        HLCalendar hlCalendar = new HLCalendar();

        Gson gson = new Gson();
        String lunchItemString = gson.toJson(lunchItem);

        Entity lunchEntity = new Entity("HotDog", itemKey);
        lunchEntity.setProperty("lunchItem", lunchItemString);

        datastoreService.put(lunchEntity);

        return hlCalendar;
    }

    public List<OrderItem> getAllOrdersByMonth(String month)
    {
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = new ArrayList();
            Iterable<Entity> entities = Util.listEntities("OrderItem", "month", month);
            for (Entity orderItemEntity : entities)
            {
                logger.log(Level.ALL, "Entity: " + orderItemEntity);
                try
                {
                    OrderItem orderItem = new OrderItem();
                    Text orderText = (Text) orderItemEntity.getProperty("orderText");
                    String orderItemString = orderText.getValue();
                    Gson gson = new Gson();
                    HLOrderItemMap hlOrderItemMap = gson.fromJson(orderItemString, HLOrderItemMap.class);
                    // check for empty orderItemMap
                    if (hlOrderItemMap.getOrderItemMap() == null)
                    {
                        try
                        {
                            logger.log(Level.INFO, "*****hlOrderItemMap.getOrderItemMap == null......");
                            // convert orderText to List of orderItemMap
                            Map<String, LunchOrderItem> orderItemMap = new HashMap<String, LunchOrderItem>();
                            orderItemMap = gson.fromJson(orderItemString, orderItemMap.getClass());
                            logger.log(Level.INFO, "*****orderItemMap after converting orderText to object: " + orderItemMap);
                        }
                        catch (Exception e)
                        {
                            logger.log(Level.SEVERE, "Unable to convert OrderItemString for: " + orderItemEntity.getProperty("name"));
                            e.printStackTrace();
                        }
                    }
                    orderItem.setLunchOrderItemMap(hlOrderItemMap);
                    orderItem.setOrderText(orderItemString);
                    orderItem.setMonth((String) orderItemEntity.getProperty("month"));
                    orderItem.setStudentName((String) orderItemEntity.getProperty("name"));
                    orderItem.setStudentGrade((String) orderItemEntity.getProperty("grade"));
                    Double total = (Double) orderItemEntity.getProperty("total");
                    orderItem.setOrderTotal(total);

                    String paymentInfoString = (String) orderItemEntity.getProperty("paymentInfo");
                    orderItem.setPaymentInfo(gson.fromJson(paymentInfoString, PaymentInfo.class));
                    orderItemList.add(orderItem);
                }
                catch (Exception e)
                {
                    logger.log(Level.SEVERE, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return orderItemList;
    }

    public List<OrderItem> getAllOrders()
    {
        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = new ArrayList();
            Iterable<Entity> entities = Util.listEntities("OrderItem", null, null);
            for (Entity orderItemEntity : entities)
            {
                logger.log(Level.ALL, "Entity: " + orderItemEntity);
                try
                {
                    OrderItem orderItem = new OrderItem();
                    Text orderText = (Text) orderItemEntity.getProperty("orderText");
                    String orderItemString = orderText.getValue();
                    Gson gson = new Gson();
                    HLOrderItemMap hlOrderItemMap = gson.fromJson(orderItemString, HLOrderItemMap.class);
                    logger.log(Level.INFO, "hlOrderItemMap after converting back to object: " + hlOrderItemMap);
                    logger.log(Level.INFO, "name/orderItemString: " + orderItemEntity.getProperty("name") + "/" + orderItemString);
                    // check for empty orderItemMap
                    if (hlOrderItemMap.getOrderItemMap() == null)
                    {
                        try
                        {
                            logger.log(Level.INFO, "*****hlOrderItemMap.getOrderItemMap == null......");
                            // convert orderText to List of orderItemMap
                            Map<String, LunchOrderItem> orderItemMap = new HashMap<String, LunchOrderItem>();
                            orderItemMap = gson.fromJson(orderItemString, orderItemMap.getClass());

                            logger.log(Level.INFO, "*****orderItemMap after converting orderText to object: " + orderItemMap);

                            //HLOrderItemMap hlOrderItemMap1 = new HLOrderItemMap();
                            //hlOrderItemMap1.setOrderItemMap(orderItemMap);
                            //hlOrderItemMap.setOrderItemMap(hlOrderItemMap1.getOrderItemMap());
                        }
                        catch (Exception e)
                        {
                            logger.log(Level.SEVERE, "Unable to convert OrderItemString for: " + orderItemEntity.getProperty("name"));
                            e.printStackTrace();
                        }

                    }
                    orderItem.setLunchOrderItemMap(hlOrderItemMap);
                    orderItem.setOrderText(orderItemString);
                    orderItem.setMonth((String) orderItemEntity.getProperty("month"));
                    orderItem.setStudentName((String) orderItemEntity.getProperty("name"));
                    orderItem.setStudentGrade((String) orderItemEntity.getProperty("grade"));
                    Double total = (Double) orderItemEntity.getProperty("total");
                    orderItem.setOrderTotal(total);

                    String paymentInfoString = (String) orderItemEntity.getProperty("paymentInfo");
                    orderItem.setPaymentInfo(gson.fromJson(paymentInfoString, PaymentInfo.class));
                    orderItemList.add(orderItem);
                }
                catch (Exception e)
                {
                    logger.log(Level.SEVERE, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return orderItemList;
    }

    public Map<String, List<OrderItem>> getOrdersForAllGrades(String month)
    {
        logger.log(Level.INFO, "-=[ calling getOrdersForAllGrades for month: " + month + " ]=-");
        Map<String, List<OrderItem>> gradeMap = new HashMap<String, List<OrderItem>>();
        try
        {
            List<OrderItem> orderItems = getOrdersByGrade("K", month);
            gradeMap.put("K", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade K: " + orderItems.size());
            orderItems = getOrdersByGrade("1", month);
            gradeMap.put("1", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 1: " + orderItems.size());
            orderItems = getOrdersByGrade("2", month);
            gradeMap.put("2", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 2: " + orderItems.size());
            orderItems = getOrdersByGrade("3", month);
            gradeMap.put("3", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 3: " + orderItems.size());
            orderItems = getOrdersByGrade("4", month);
            gradeMap.put("4", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 4: " + orderItems.size());
            orderItems = getOrdersByGrade("5", month);
            gradeMap.put("5", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 5: " + orderItems.size());
            orderItems = getOrdersByGrade("6", month);
            gradeMap.put("6", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 6: " + orderItems.size());
            orderItems = getOrdersByGrade("7", month);
            gradeMap.put("7", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 7: " + orderItems.size());
            orderItems = getOrdersByGrade("8", month);
            gradeMap.put("8", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade 8: " + orderItems.size());
            orderItems = getOrdersByGrade("F", month);
            gradeMap.put("F", orderItems);
            logger.log(Level.INFO, "number of orders returned for grade F: " + orderItems.size());
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return gradeMap;
    }

    public List<OrderItem> getOrdersByGrade(String grade, String month)
    {
        logger.log(Level.INFO, "calling getOrdersByGrade for Grade: " + grade);

        List<OrderItem> orderItemList = null;
        try
        {
            orderItemList = new ArrayList();

            // get the orders for the given grade and month
            //Iterable<Entity> entities = Util.listEntities("OrderItem", "grade", grade);
            Query query = new Query("OrderItem");
            query.addFilter("grade", Query.FilterOperator.EQUAL, grade);
            query.addFilter("month", Query.FilterOperator.EQUAL, month);
            Iterable<Entity> entities = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity orderItemEntity : entities)
            {
                try
                {
                    OrderItem orderItem = new OrderItem();
                    Text orderText = (Text) orderItemEntity.getProperty("orderText");
                    String orderItemString = orderText.getValue();
                    Gson gson = new Gson();
                    HLOrderItemMap hlOrderItemMap = gson.fromJson(orderItemString, HLOrderItemMap.class);
                    //logger.log(Level.INFO, "name/orderItemString: " + orderItemEntity.getProperty("name") + "/" + orderItemString);
                    // check for empty orderItemMap
                    if (hlOrderItemMap.getOrderItemMap() == null)
                    {
                        try
                        {
                            logger.log(Level.INFO, "orderItemMap is null for name: " + orderItemEntity.getProperty("name"));
                            // convert orderText to List of orderItemMap
                            Map<String, LunchOrderItem> orderItemMap = new HashMap<String, LunchOrderItem>();
                            orderItemMap = gson.fromJson(orderItemString, orderItemMap.getClass());
                        }
                        catch (Exception e)
                        {
                            logger.log(Level.SEVERE, "Unable to convert OrderItemString for: " + orderItemEntity.getProperty("name"));
                            e.printStackTrace();
                        }

                    }
                    orderItem.setLunchOrderItemMap(hlOrderItemMap);
                    //orderItem.setOrderText(orderItemString); // don't think we need this anymore....
                    orderItem.setMonth((String) orderItemEntity.getProperty("month"));
                    orderItem.setStudentName((String) orderItemEntity.getProperty("name"));
                    orderItem.setStudentGrade((String) orderItemEntity.getProperty("grade"));
                    Double total = (Double) orderItemEntity.getProperty("total");
                    orderItem.setOrderTotal(total);

                    String paymentInfoString = (String) orderItemEntity.getProperty("paymentInfo");
                    orderItem.setPaymentInfo(gson.fromJson(paymentInfoString, PaymentInfo.class));
                    orderItemList.add(orderItem);
                }
                catch (Exception e)
                {
                    logger.log(Level.SEVERE, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return orderItemList;

    }

    public SortedMap<Date, List<LunchOrderItem>> createPurchasingMap(List<OrderItem> orderItemList)
    {
        logger.log(Level.INFO, "in createPurchasingMap");
        Map<String, List<LunchOrderItem>> dateListMap = new HashMap<String, List<LunchOrderItem>>();

        for (OrderItem orderItem : orderItemList)
        {
            // get the HLOrderItem map from the orderItem
            HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();

            // get the lunchOrderItemMap from the HLOrderItemMap
            Map<String, LunchOrderItem> lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
            if (lunchOrderItemMap != null)
            {
                // get the keys, which is the date string
                Set keys = lunchOrderItemMap.keySet();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext())
                {
                    String key = (String) iterator.next(); // date string
                    Date date = new Date(key);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
                    String dateString = dateFormat.format(date);
                    //logger.log(Level.INFO, "==== key/date/string: " + key + "/" + date + "/" + dateString);
                    LunchOrderItem lunchOrderItem = lunchOrderItemMap.get(key);
                    List<LunchOrderItem> lunchOrderItemList = null;
                    if (dateListMap.containsKey(key))
                    {
                        // get list from map because it already exists
                        lunchOrderItemList = dateListMap.get(key);
                    }
                    else
                    {
                        // create new list
                        lunchOrderItemList = new ArrayList<LunchOrderItem>();
                    }
                    lunchOrderItemList.add(lunchOrderItem);
                    // save the list to the map (with the date as the key)
                    dateListMap.put(key, lunchOrderItemList);
                }
            }
        }


        SortedMap<Date, List<LunchOrderItem>> updatedMap = new TreeMap<Date, List<LunchOrderItem>>();
        // convert the string/list to a date/list
        Set keys = dateListMap.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext())
        {
            String key = (String)iterator.next(); // date string
            List<LunchOrderItem> lunchOrderItems = dateListMap.get(key);
            Date date = new Date(key);
            updatedMap.put(date, lunchOrderItems);
        }

        logger.log(Level.INFO, "******  Size of updatedMap: " + updatedMap.size());
        Set keyset = updatedMap.keySet();
        Iterator iter = keyset.iterator();
        while (iter.hasNext())
        {
            Date key = (Date) iter.next(); // date
            List<LunchOrderItem> lunchOrderItemList = updatedMap.get(key);
            logger.log(Level.INFO, "key: " + key + " --- size of list: " + lunchOrderItemList.size());
        }

        return updatedMap;
        //return dateListMap;
    }

    public Map<String, List<LunchOrderItem>> createPurchasingMapCurrent(List<OrderItem> orderItemList)
    {
        logger.log(Level.INFO, "in createPurchasingMap");
        Map<String, List<LunchOrderItem>> dateListMap = new HashMap<String, List<LunchOrderItem>>();

        for (OrderItem orderItem : orderItemList)
        {
            // get the HLOrderItem map from the orderItem
            HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();

            // get the lunchOrderItemMap from the HLOrderItemMap
            Map<String, LunchOrderItem> lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
            if (lunchOrderItemMap != null)
            {
                // get the keys, which is the date string
                Set keys = lunchOrderItemMap.keySet();
                Iterator iterator = keys.iterator();
                while (iterator.hasNext())
                {
                    String key = (String) iterator.next(); // date string
                    Date date = new Date(key);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
                    String dateString = dateFormat.format(date);
                    //logger.log(Level.INFO, "==== key/date/string: " + key + "/" + date + "/" + dateString);
                    LunchOrderItem lunchOrderItem = lunchOrderItemMap.get(key);
                    List<LunchOrderItem> lunchOrderItemList = null;
                    if (dateListMap.containsKey(key))
                    {
                        // get list from map because it already exists
                        lunchOrderItemList = dateListMap.get(key);
                    }
                    else
                    {
                        // create new list
                        lunchOrderItemList = new ArrayList<LunchOrderItem>();
                    }
                    lunchOrderItemList.add(lunchOrderItem);
                    // save the list to the map (with the date as the key)
                    dateListMap.put(key, lunchOrderItemList);
                }
            }
        }

        logger.log(Level.INFO, "******  Size of dateListMap: " + dateListMap.size());
        Set keyset = dateListMap.keySet();
        Iterator iter = keyset.iterator();
        while (iter.hasNext())
        {
            String key = (String) iter.next(); // date
            List<LunchOrderItem> lunchOrderItemList = dateListMap.get(key);
            //logger.log(Level.INFO, "key: " + key + " --- size of list: " + lunchOrderItemList.size());
        }
        return dateListMap;
    }

    public SortedMap<Date, List<TicketItem>> createTicketingMap(String month)
    {
        logger.log(Level.INFO, "------------------------------ in createTicketingMap ------------------------------ ");
        SortedMap<String, List<TicketItem>> ticketMap = new TreeMap<String, List<TicketItem>>();
        try
        {
            List<OrderItem> orderItemList = getAllOrdersByMonth(month);
            logger.info(" -- size of orderItemList: " + orderItemList.size());


            List<TicketItem> ticketItemList = null;

            for (OrderItem orderItem : orderItemList)
            {
                // get the HLOrderItem map from the orderItem
                HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();

                // get the lunchOrderItemMap from the HLOrderItemMap
                Map<String, LunchOrderItem> lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();

                logger.info("size of lunchOrderItemMap: " + lunchOrderItemMap.size());

                if (lunchOrderItemMap != null)
                {
                    // get the keys, for a single order which is the date string
                    Set keys = lunchOrderItemMap.keySet();
                    //logger.info("size of keySet.........." + keys.size());
                    Iterator iterator = keys.iterator();
                    while (iterator.hasNext())
                    {
                        String key = (String) iterator.next(); // date string
                        logger.info("key.........." + key);
                        Date date = new Date(key);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
                        LunchOrderItem lunchOrderItem = lunchOrderItemMap.get(key);

                        //logger.info("creating a new ticketItem..........");
                        TicketItem ticketItem = new TicketItem();
                        Student student = getStudentFirstLastName(orderItem.getStudentName(), orderItem.getStudentGrade());
                        //logger.info("-- setting firstname: " + student.getFirstName() + " / lastname: " + student.getLastName() + "---");
                        ticketItem.setFirstName(student.getFirstName());
                        ticketItem.setLastName(student.getLastName());
                        ticketItem.setGrade(orderItem.getStudentGrade());
                        ticketItem.setLunchOrderItem(lunchOrderItem);

                        if (ticketMap.containsKey(key))
                        {
                            // get list from map because it already exists
                            //logger.info("- found the list for key: " + key);
                            ticketItemList = ticketMap.get(key);
                        }
                        else
                        {
                            // create new list
                            //logger.info("- creating new list for key: " + key);
                            ticketItemList = new ArrayList<TicketItem>();
                        }
                        //logger.info("adding ticketItem to the ticketItemList");
                        ticketItemList.add(ticketItem);

                        // save the list to the map (with the date as the key)
                        //logger.info("adding ticketItemList to the ticketItemMap for date: " + key);
                        ticketMap.put(key, ticketItemList);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SortedMap<Date, List<TicketItem>> updatedTicketMap = new TreeMap<Date, List<TicketItem>>();
        // convert the string/list to a date/list map
        Set keySet = ticketMap.keySet();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext())
        {
            String key = (String)iterator.next();
            List<TicketItem> ticketItemList = ticketMap.get(key);
            Date date = new Date(key);
            updatedTicketMap.put(date, ticketItemList);
        }

        return updatedTicketMap;
    }
}
