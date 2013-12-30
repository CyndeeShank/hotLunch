package com.hotlunch.model;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 9/11/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalendarMap
{
    private static CalendarMap instance = null;

    private SortedMap<Date, String> dateInfoMap = null;

    private SortedMap<Integer, String> orderedDateMap = null;
    private Map octDateInfoMap = null;
    private SortedMap<Integer, String> octOrderedDateMap = null;
    private Map novDateInfoMap = null;
    private SortedMap<Integer, String> novOrderedDateMap = null;

    private static final Logger logger = Logger.getLogger(CalendarMap.class.getCanonicalName());

    protected CalendarMap()
    {
        // Exists only to defeat instantiation.
    }

    public static CalendarMap getInstance()
    {
        if (instance == null)
        {
            instance = new CalendarMap();
        }
        return instance;
    }

    public SortedMap<Date, String> getDateInfoMap()
    {
        if (dateInfoMap == null)
        {
            createNewDateInfoMap();
        }
        return dateInfoMap;
    }

    private void createNewDateInfoMap()
    {
        logger.info("creating new DateInfoMap.....");
        dateInfoMap = new SortedMap<Date, String>()
        {
            public Comparator<? super Date> comparator()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public SortedMap<Date, String> subMap(Date date, Date date2)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public SortedMap<Date, String> headMap(Date date)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public SortedMap<Date, String> tailMap(Date date)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public Date firstKey()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public Date lastKey()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public Set<Date> keySet()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public Collection<String> values()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public Set<Entry<Date, String>> entrySet()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public int size()
            {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public boolean isEmpty()
            {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public boolean containsKey(Object o)
            {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public boolean containsValue(Object o)
            {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String get(Object o)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String put(Date date, String s)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String remove(Object o)
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public void putAll(Map<? extends Date, ? extends String> map)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void clear()
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public void addDateToMap(int month, Integer position, String dateStr)
    {
        if (dateInfoMap == null)
        {
            createNewDateInfoMap();
        }

        logger.log(Level.INFO, "==== Adding dateStr: " + dateStr + " to the dateInfoMap");
        Date date = new Date(dateStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
        String dateString = dateFormat.format(date);
        logger.log(Level.INFO, "==== putting date / formatted date: " + date + " / " + dateString + " into the dateInfoMap");
        dateInfoMap.put(date, dateString);
    }

    public Map<String, Integer> getNovDateInfoMap()
    {
        if (novDateInfoMap == null)
        {
            novDateInfoMap = new HashMap<Integer, String>();
            novDateInfoMap.put("Friday November 1, 2013", 1);

            novDateInfoMap.put("Monday November 4, 2013", 2);
            novDateInfoMap.put("Tuesday November 5, 2013", 3);
            novDateInfoMap.put("Wednesday November 6, 2013", 4);
            novDateInfoMap.put("Thursday November 7, 2013", 5);
            novDateInfoMap.put("Friday November 8, 2013", 6);

            novDateInfoMap.put("Monday November 11, 2013", 7);
            novDateInfoMap.put("Tuesday November 12, 2013", 8);
            novDateInfoMap.put("Wednesday November 13, 2013", 9);
            novDateInfoMap.put("Thursday November 14, 2013", 10);
            novDateInfoMap.put("Friday November 15, 2013", 11);

            novDateInfoMap.put("Monday November 18, 2013", 12);
            novDateInfoMap.put("Tuesday November 19, 2013", 13);
            novDateInfoMap.put("Wednesday November 20, 2013", 14);
            novDateInfoMap.put("Thursday November 21, 2013", 15);
            novDateInfoMap.put("Friday November 22, 2013", 16);

        }
        return novDateInfoMap;
    }

    public SortedMap<Integer, String> getNovOrderedDateMap()
    {
        if (novOrderedDateMap == null)
        {
            novOrderedDateMap = new TreeMap<Integer, String>();
            novOrderedDateMap.put(1, "Friday November 1, 2013");

            novOrderedDateMap.put(2, "Monday November 4, 2013");
            novOrderedDateMap.put(3, "Tuesday November 5, 2013");
            novOrderedDateMap.put(4, "Wednesday November 6, 2013");
            novOrderedDateMap.put(5, "Thursday November 7, 2013");
            novOrderedDateMap.put(6, "Friday November 8, 2013");

            novOrderedDateMap.put(7, "Monday November 11, 2013");
            novOrderedDateMap.put(8, "Tuesday November 12, 2013");
            novOrderedDateMap.put(9, "Wednesday November 13, 2013");
            novOrderedDateMap.put(10, "Thursday November 14, 2013");
            novOrderedDateMap.put(11, "Friday November 15, 2013");

            novOrderedDateMap.put(12, "Monday November 18, 2013");
            novOrderedDateMap.put(13, "Tuesday November 19, 2013");
            novOrderedDateMap.put(14, "Wednesday November 20, 2013");
            novOrderedDateMap.put(15, "Thursday November 21, 2013");
            novOrderedDateMap.put(16, "Friday November 22, 2013");
        }

        return novOrderedDateMap;
    }

    public Map<String, Integer> getOctDateInfoMap()
    {
        if (octDateInfoMap == null)
        {
            octDateInfoMap = new HashMap<Integer, String>();
            octDateInfoMap.put("Tuesday October 1, 2013", 1);
            octDateInfoMap.put("Wednesday October 2, 2013", 2);
            octDateInfoMap.put("Thursday October 3, 2013", 3);
            octDateInfoMap.put("Friday October 4, 2013", 4);

            octDateInfoMap.put("Monday October 7, 2013", 5);
            octDateInfoMap.put("Tuesday October 8, 2013", 6);
            octDateInfoMap.put("Wednesday October 9, 2013", 7);
            octDateInfoMap.put("Thursday October 10, 2013", 8);
            octDateInfoMap.put("Friday October 11, 2013", 9);

            octDateInfoMap.put("Monday October 14, 2013", 10);
            octDateInfoMap.put("Tuesday October 15, 2013", 11);
            octDateInfoMap.put("Wednesday October 16, 2013", 12);
            octDateInfoMap.put("Thursday October 17, 2013", 13);
            octDateInfoMap.put("Friday October 18, 2013", 14);

            octDateInfoMap.put("Monday October 21, 2013", 15);
            octDateInfoMap.put("Tuesday October 22, 2013", 16);
            octDateInfoMap.put("Wednesday October 23, 2013", 17);
            octDateInfoMap.put("Thursday October 24, 2013", 18);
            octDateInfoMap.put("Friday October 25, 2013", 19);

            octDateInfoMap.put("Monday October 28, 2013", 20);
            octDateInfoMap.put("Tuesday October 29, 2013", 21);
            octDateInfoMap.put("Wednesday October 30, 2013", 22);
        }
        return octDateInfoMap;
    }

    public SortedMap<Integer, String> getOctOrderedDateMap()
    {
        if (octOrderedDateMap == null)
        {
            octOrderedDateMap = new TreeMap<Integer, String>();
            octOrderedDateMap.put(1, "Tuesday October 1, 2013");
            octOrderedDateMap.put(2, "Wednesday October 2, 2013");
            octOrderedDateMap.put(3, "Thursday October 3, 2013");
            octOrderedDateMap.put(4, "Friday October 4, 2013");

            octOrderedDateMap.put(5, "Monday October 7, 2013");
            octOrderedDateMap.put(6, "Tuesday October 8, 2013");
            octOrderedDateMap.put(7, "Wednesday October 9, 2013");
            octOrderedDateMap.put(8, "Thursday October 10, 2013");
            octOrderedDateMap.put(9, "Friday October 11, 2013");

            octOrderedDateMap.put(10, "Monday October 14, 2013");
            octOrderedDateMap.put(11, "Tuesday October 15, 2013");
            octOrderedDateMap.put(12, "Wednesday October 16, 2013");
            octOrderedDateMap.put(13, "Thursday October 17, 2013");
            octOrderedDateMap.put(14, "Friday October 18, 2013");

            octOrderedDateMap.put(15, "Monday October 21, 2013");
            octOrderedDateMap.put(16, "Tuesday October 22, 2013");
            octOrderedDateMap.put(17, "Wednesday October 23, 2013");
            octOrderedDateMap.put(18, "Thursday October 24, 2013");
            octOrderedDateMap.put(19, "Friday October 25, 2013");

            octOrderedDateMap.put(20, "Monday October 28, 2013");
            octOrderedDateMap.put(21, "Tuesday October 29, 2013");
            octOrderedDateMap.put(22, "Wednesday October 30, 2013");
        }

        return octOrderedDateMap;
    }
/**
    public Map<String, Integer> getDateInfoMap()
    {
        if (dateInfoMap == null)
        {
            dateInfoMap = new HashMap<Integer, String>();
            dateInfoMap.put("Monday September 9, 2013", 1);
            dateInfoMap.put("Tuesday September 10, 2013", 2);
            dateInfoMap.put("Wednesday September 11, 2013", 3);
            dateInfoMap.put("Thursday September 12, 2013", 4);
            dateInfoMap.put("Friday September 13, 2013", 5);
            dateInfoMap.put("Monday September 16, 2013", 6);
            dateInfoMap.put("Tuesday September 17, 2013", 7);
            dateInfoMap.put("Wednesday September 18, 2013", 8);
            dateInfoMap.put("Thursday September 19, 2013", 9);
            dateInfoMap.put("Friday September 20, 2013", 10);
            dateInfoMap.put("Monday September 23, 2013", 11);
            dateInfoMap.put("Tuesday September 24, 2013", 12);
            dateInfoMap.put("Wednesday September 25 2013", 13);
            dateInfoMap.put("Thursday September 26, 2013", 14);

        }
        return dateInfoMap;
    }

    public SortedMap<Integer, String> getOrderedDateMap()
    {
        if (orderedDateMap == null)
        {
            orderedDateMap = new TreeMap<Integer, String>();
            orderedDateMap.put(1, "Monday September 9, 2013");
            orderedDateMap.put(2, "Tuesday September 10, 2013");
            orderedDateMap.put(3, "Wednesday September 11, 2013");
            orderedDateMap.put(4, "Thursday September 12, 2013");
            orderedDateMap.put(5, "Friday September 13, 2013");
            orderedDateMap.put(6, "Monday September 16, 2013");
            orderedDateMap.put(7, "Tuesday September 17, 2013");
            orderedDateMap.put(8, "Wednesday September 18, 2013");
            orderedDateMap.put(9, "Thursday September 19, 2013");
            orderedDateMap.put(10, "Friday September 20, 2013");
            orderedDateMap.put(11, "Monday September 23, 2013");
            orderedDateMap.put(12, "Tuesday September 24, 2013");
            orderedDateMap.put(13, "Wednesday September 25 2013");
            orderedDateMap.put(14, "Thursday September 26, 2013");

        }
        return orderedDateMap;
    }
 **/


}
