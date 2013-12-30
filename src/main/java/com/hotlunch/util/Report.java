package com.hotlunch.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.repackaged.org.apache.commons.logging.impl.Jdk13LumberjackLogger;
import com.hotlunch.model.*;
import com.hotlunch.service.HLService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created with IntelliJ IDEA.
 * User: cyndeeshank
 * Date: 9/5/13
 * Time: 7:50 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Report
{
    @Autowired(required = true)
    private HLService hlService;

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private static final Logger logger = Logger.getLogger(Report.class.getCanonicalName());

    public void createTicketingList(SortedMap<Date, List<TicketItem>> ticketMap, ServletOutputStream outputStream)
    {
        try
        {
            // create a new speadsheet
            HSSFWorkbook wb = new HSSFWorkbook();

            /**
             * iterate through the map, get the keys which are the dates and make each date it's own
             * sheet, then go throug the list and print out the grade, names and selections
             */
            Set dateKeys = ticketMap.keySet();
            Iterator iterator = dateKeys.iterator();
            while (iterator.hasNext())
            {
                Date dateKey = (Date) iterator.next();
                List<TicketItem> ticketItemList = ticketMap.get(dateKey);
                // create a new sheet for each date
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
                String dateString = dateFormat.format(dateKey);
                HSSFSheet sheet = wb.createSheet(dateString);

                /**
                 * create the title row and header row
                 */
                HSSFRow rowHeader = sheet.createRow((short) 1);

                // create a font and style for the header row
                HSSFFont fontHeader = wb.createFont();
                fontHeader.setFontHeightInPoints((short) 10);
                fontHeader.setFontName("Arial");
                fontHeader.setItalic(true);
                fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                HSSFCellStyle styleHeader = wb.createCellStyle();
                styleHeader.setWrapText(true);
                styleHeader.setFont(fontHeader);
                styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

                // create the header row cells, populate the cell values and set the styles
                HSSFCell cellHeader = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Grade");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(1, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("First Name");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(2, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Last Name");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(3, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Item Ordered");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(4, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Choice 1");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(5, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Choice 2");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(6, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Additional Ordered");
                cellHeader.setCellStyle(styleHeader);

                /**
                 * populate the remaining rows and cells
                 */
                int rowNum = 2;
                int columnNum = 0;

                for (TicketItem ticketItem : ticketItemList)
                {
                    HSSFRow rowData = null;
                    rowData = sheet.createRow(rowNum);

                    // grade
                    HSSFCell cellRow = rowData.createCell(0);
                    cellRow.setCellValue(ticketItem.getGrade());

                    cellRow = rowData.createCell(1);
                    cellRow.setCellValue(ticketItem.getFirstName());
                    cellRow = rowData.createCell(2);
                    cellRow.setCellValue(ticketItem.getLastName());

                    cellRow = rowData.createCell(3);
                    LunchOrderItem lunchOrderItem = ticketItem.getLunchOrderItem();
                    cellRow.setCellValue(Util.getPrettyName(lunchOrderItem.getItemTypeString()));
                    //cellRow.setCellValue(lunchOrderItem.getItemTypeString()); // item type
                    //logger.info("item type string: " + lunchOrderItem.getItemTypeString() + "/ pretty: " + Util.getPrettyName(lunchOrderItem.getItemTypeString()));

                    cellRow = rowData.createCell(4); // choice 1
                    //logger.info("regular choice: " + lunchOrderItem.getChoice1() + "/ pretty choice: " + Util.getPrettyChoiceName(lunchOrderItem.getChoice1(), lunchOrderItem.getItemType()));
                    String choice1 = lunchOrderItem.getChoice1();
                    if (choice1 != null)
                    {
                        cellRow.setCellValue(Util.getPrettyChoiceName(lunchOrderItem.getChoice1(), lunchOrderItem.getItemType()));
                    }
                    else
                    {
                        cellRow.setCellValue(choice1);
                        //cellRow.setCellValue(lunchOrderItem.getChoice1());
                    }

                    cellRow = rowData.createCell(5); // choice 2
                    String choice2 = lunchOrderItem.getChoice2();
                    if (choice2 != null)
                    {
                        cellRow.setCellValue(Util.getPrettyChoiceName(lunchOrderItem.getChoice2(), lunchOrderItem.getItemType()));
                    }
                    else
                    {
                        cellRow.setCellValue(choice2);
                        //cellRow.setCellValue(lunchOrderItem.getChoice2());
                        //logger.info("regular choice: " + lunchOrderItem.getChoice2() + "/ pretty choice: " + Util.getPrettyChoiceName(lunchOrderItem.getChoice2(), lunchOrderItem.getItemType()));
                    }

                    cellRow = rowData.createCell(6);
                    if (lunchOrderItem.isOrderAdditional())
                    {
                        //cellRow.setCellValue(lunchOrderItem.isOrderAdditional());
                        cellRow.setCellValue("yes");
                    }
                    rowNum++;
                }
            }

            /**
             * write the workbook to the outputstream
             */
            wb.write(outputStream);
            //out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void createTicketingListOld(Map<String, List<OrderItem>> mapByGrade, ServletOutputStream outputStream)
    {
        try
        {
            // create a new speadsheet
            HSSFWorkbook wb = new HSSFWorkbook();
            /**
             * iterate through the map, get the key, which is the grade string, then get the list for that grade
             * iterate through the list, get the name, then the orderItemMap, print the key, which is the date
             * then print the items ordered for that date
             */
            Set gradeMapKeySet = mapByGrade.keySet();
            Iterator gradeIter = gradeMapKeySet.iterator();
            while (gradeIter.hasNext())
            {
                // the key is the grade string
                String gradeKey = (String) gradeIter.next();
                List<OrderItem> gradeItemlist = (List<OrderItem>) mapByGrade.get(gradeKey);
                logger.log(Level.INFO, "------ number of items in gradeItemList for grade: " + gradeKey + "/" + gradeItemlist.size());
                // create a new sheet for each grade
                HSSFSheet sheet = wb.createSheet("Grade " + gradeKey);

                /**
                 * create the title row and header row
                 */
                HSSFRow rowHeader = sheet.createRow((short) 1);

                // create a font and style for the header row
                HSSFFont fontHeader = wb.createFont();
                fontHeader.setFontHeightInPoints((short) 10);
                fontHeader.setFontName("Arial");
                fontHeader.setItalic(true);
                fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                HSSFCellStyle styleHeader = wb.createCellStyle();
                styleHeader.setWrapText(true);
                styleHeader.setFont(fontHeader);
                styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

                // create the header row cells, populate the cell values and set the styles
                HSSFCell cellHeader = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Grade");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(1, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Date");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(2, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("First Name");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(3, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Last Name");
                cellHeader.setCellStyle(styleHeader);

                //cellHeader = rowHeader.createCell((short) 2);
                cellHeader = rowHeader.createCell(4, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Item Ordered");
                cellHeader.setCellStyle(styleHeader);

                //cellHeader = rowHeader.createCell((short) 3);
                cellHeader = rowHeader.createCell(5, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Choice 1");
                cellHeader.setCellStyle(styleHeader);

                //cellHeader = rowHeader.createCell((short) 4);
                cellHeader = rowHeader.createCell(6, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Choice 2");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(7, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Additional Ordered");
                cellHeader.setCellStyle(styleHeader);

                /**
                 * populate the remaining rows and cells
                 */
                int rowNum = 2;
                int columnNum = 0;

                for (OrderItem orderItem : gradeItemlist)
                {
                    HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();
                    Map lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
                    if (lunchOrderItemMap != null)
                    {
                        //logger.log(Level.INFO, "lunchOrderItemMap: " + lunchOrderItemMap);
                        Set keys = lunchOrderItemMap.keySet();
                        //logger.log(Level.INFO, "lunchOrderItemMap keys: " + keys);
                        Iterator iterator = keys.iterator();
                        HSSFRow rowData = null;
                        while (iterator.hasNext())
                        {
                            String key = (String) iterator.next();
                            LunchOrderItem lunchOrderItem = (LunchOrderItem) lunchOrderItemMap.get(key);
                            //logger.log(Level.INFO, "lunchOrderItem: " + lunchOrderItem.getItemTypeString());
                            rowData = sheet.createRow(rowNum);

                            // grade
                            HSSFCell cellRow = rowData.createCell(0);
                            cellRow.setCellValue(gradeKey);

                            // date
                            cellRow = rowData.createCell(1);
                            cellRow.setCellValue(key);

                            cellRow = rowData.createCell(2);
                            cellRow.setCellValue(orderItem.getStudentName());
                            //cellRow.setCellValue(orderItem.getStudentName()); // student name
                            // split the student name into first and last name
                            //String[] names = split(orderItem.getStudentName(), "");
                            //logger.log(Level.INFO, "Split name: " + orderItem.getStudentName() + " into first: " + names[0] + " and last: " + names[1]);
                            cellRow = rowData.createCell(3);
                            cellRow.setCellValue("");

                            cellRow = rowData.createCell(4);
                            //cellRow.setCellValue(lunchOrderItem.getItemTypeString()); // item type
                            cellRow.setCellValue(Util.getPrettyName(lunchOrderItem.getItemTypeString()));

                            cellRow = rowData.createCell(5); // choice 1
                            //cellRow.setCellValue(Util.getPrettyChoiceName(lunchOrderItem.getChoice1(), lunchOrderItem.getItemType()));
                            cellRow.setCellValue(lunchOrderItem.getChoice1());
                            cellRow = rowData.createCell(6); // choice 2
                            //cellRow.setCellValue(Util.getPrettyChoiceName(lunchOrderItem.getChoice2(), lunchOrderItem.getItemType()));
                            cellRow.setCellValue(lunchOrderItem.getChoice2());
                            cellRow = rowData.createCell(7);
                            if (lunchOrderItem.isOrderAdditional())
                            {
                                //cellRow.setCellValue(lunchOrderItem.isOrderAdditional());
                                cellRow.setCellValue("yes");
                            }
                            rowNum++;
                        }
                    }
                    else
                    {
                        logger.log(Level.INFO, "*****lunchOrderItemMap is null for : " + orderItem.getStudentName());
                    }
                }
            }

            /**
             * write the workbook to the outputstream
             */
            wb.write(outputStream);
            //out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void createPaymentList(Map<String, List<OrderItem>> mapByGrade, ServletOutputStream outputStream)
    {
        try
        {
            // create a new speadsheet
            HSSFWorkbook wb = new HSSFWorkbook();
            /**
             * iterate through the map, get the key, which is the grade string, then get the list for that grade
             * iterate through the list, get the name, then the orderItemMap, print the key, which is the date
             * then print the items ordered for that date
             */
            Set gradeMapKeySet = mapByGrade.keySet();
            Iterator gradeIter = gradeMapKeySet.iterator();
            while (gradeIter.hasNext())
            {
                // the key is the grade string
                String gradeKey = (String) gradeIter.next();
                List<OrderItem> gradeItemlist = (List<OrderItem>) mapByGrade.get(gradeKey);
                logger.log(Level.INFO, "------ number of items in gradeItemList for grade: " + gradeKey + "/" + gradeItemlist.size());
                // create a new sheet for each grade
                HSSFSheet sheet = wb.createSheet("Grade " + gradeKey);

                /**
                 * create the title row and header row
                 */
                HSSFRow rowHeader = sheet.createRow((short) 1);

                // create a font and style for the header row
                HSSFFont fontHeader = wb.createFont();
                fontHeader.setFontHeightInPoints((short) 10);
                fontHeader.setFontName("Arial");
                fontHeader.setItalic(true);
                fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                HSSFCellStyle styleHeader = wb.createCellStyle();
                styleHeader.setWrapText(true);
                styleHeader.setFont(fontHeader);
                styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

                // create the header row cells, populate the cell values and set the styles
                HSSFCell cellHeader = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Name");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(1, HSSFCell.CELL_TYPE_NUMERIC);
                cellHeader.setCellValue("Order Total");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(2, HSSFCell.CELL_TYPE_BOOLEAN);
                cellHeader.setCellValue("Paid");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(3, HSSFCell.CELL_TYPE_NUMERIC);
                cellHeader.setCellValue("Amount Paid");
                cellHeader.setCellStyle(styleHeader);

                cellHeader = rowHeader.createCell(4, HSSFCell.CELL_TYPE_STRING);
                cellHeader.setCellValue("Payment Type");
                cellHeader.setCellStyle(styleHeader);

                /**
                 * populate the remaining rows and cells
                 */
                int rowNum = 2;
                int columnNum = 0;
                DataFormat df = wb.createDataFormat();
                CellStyle cs = wb.createCellStyle();

                for (OrderItem orderItem : gradeItemlist)
                {
                    PaymentInfo paymentInfo = orderItem.getPaymentInfo();

                    HSSFRow rowData = sheet.createRow(rowNum);

                    HSSFCell cellRow = rowData.createCell(0);
                    cellRow.setCellValue(orderItem.getStudentName());

                    cellRow = rowData.createCell(1);
                    cellRow.setCellValue(orderItem.getOrderTotal());
                    cs.setDataFormat(df.getFormat("#,##0.00"));
                    cellRow.setCellStyle(cs);

                    if (paymentInfo != null)
                    {
                        cellRow = rowData.createCell(2);
                        if (paymentInfo.isPaid())
                        {
                            cellRow.setCellValue("yes");
                        }
                        else
                        {
                            cellRow.setCellValue("no");
                        }

                        // amount paid
                        cellRow = rowData.createCell(3);
                        cellRow.setCellValue(paymentInfo.getAmountPaid());
                        cellRow.setCellStyle(cs);

                        cellRow = rowData.createCell(4);
                        switch (paymentInfo.getPaymentType())
                        {
                            case Constants.PAYMENT_TYPE_CHECK:
                                cellRow.setCellValue("Check");
                                break;
                            case Constants.PAYMENT_TYPE_CASH:
                                cellRow.setCellValue("Cash");
                                break;
                            default:
                                cellRow.setCellValue("none");
                        }
                        rowNum++;
                    }
                    else
                    {
                        cellRow = rowData.createCell(2);
                        cellRow.setCellValue("no");

                        cellRow = rowData.createCell(3);
                        cellRow.setCellValue("0");

                        cellRow = rowData.createCell(4);
                        cellRow.setCellValue("none");
                    }
                }

            }

            /**
             * write the workbook to the outputstream
             */
            wb.write(outputStream);
            //out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void createPurchasingSidesList(List<OrderItem> orderItemList, ServletOutputStream outputStream)
    {
        /**
         * create the workbook, sheet, title row and header row
         */
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow rowHeader = sheet.createRow((short) 1);

        // create a font and style for the header row
        HSSFFont fontHeader = wb.createFont();
        fontHeader.setFontHeightInPoints((short) 10);
        fontHeader.setFontName("Arial");
        fontHeader.setItalic(true);
        fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle styleHeader = wb.createCellStyle();
        styleHeader.setWrapText(true);
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // create the header row cells, populate the cell values and set the styles
        HSSFCell cellHeader = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Date");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(1, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Item");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(2, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Choices");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(3, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Amount Ordered"); // total ordered plus additional
        cellHeader.setCellStyle(styleHeader);

        HSSFRow rowData = null;
        int rowNum = 2;

        Map<String, PurchasingItem> allItemsMap = new HashMap<String, PurchasingItem>();
        Map<String, List<LunchOrderItem>> dateListMap = hlService.createPurchasingMap(orderItemList);

        Set dateKeys = dateListMap.keySet();
        Iterator iterator = dateKeys.iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next(); // date -- need to parse into #1 and #2
            List<LunchOrderItem> lunchOrderItemList = dateListMap.get(key);
            int numOrdered = lunchOrderItemList.size();
            logger.log(Level.INFO, "key: " + key + " --- size of list: " + numOrdered);

            PurchasingItem purchasingItem = new PurchasingItem();
            purchasingItem.setChoice1Amount(numOrdered);
            // check the first item in the list to determine optiion, etc
            logger.log(Level.INFO, "*** item type: " + lunchOrderItemList.get(0).getItemType());

            switch (lunchOrderItemList.get(0).getItemType())
            {
                // kfc
                case 60:
                    purchasingItem.setVendorName("KFC");
                    purchasingItem.setChoice1("fruit");
                    break;

                // carls
                case 10:
                    purchasingItem.setVendorName("Carls Jr.");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("baked lays");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                // chickfila
                case 20:
                    purchasingItem.setVendorName("Chick-Fil-A");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("cookies");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                // chipotle
                case 30:
                    purchasingItem.setVendorName("Chipotle");
                    purchasingItem.setChoice1("fruit");
                    break;

                // flamebroiler
                case 40:
                    purchasingItem.setVendorName("Flame Broiler");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("frozen gogurt");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                // hotdog
                case 50:
                    purchasingItem.setVendorName("Weinerschnitzel");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("cheeze its");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                // pickupstix
                case 70:
                    purchasingItem.setVendorName("Pick Up Stix");
                    purchasingItem.setChoice1("fruit");
                    break;

                // pizza1 and pizza2
                case 80:
                case 90:
                    purchasingItem = getPizzaSidesInfo(lunchOrderItemList);
                    purchasingItem.setVendorName("Pizza Hut");
                    purchasingItem.setChoice3("rice krispie treat");
                    purchasingItem.setChoice3Amount(numOrdered);
                    break;

                // portofino1 and portofino2
                case 100:
                case 110:
                    purchasingItem.setVendorName("Portofino");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("fruit roll-up");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                case 120:
                    purchasingItem.setVendorName("Smoothie & Sandwich");
                    break;

                // subway
                case 130:
                    purchasingItem.setVendorName("Subway");
                    purchasingItem.setChoice1("fruit");
                    purchasingItem.setChoice2("sun chips");
                    purchasingItem.setChoice2Amount(numOrdered);
                    break;

                // taquitos
                case 140:
                    purchasingItem.setVendorName("Taquitos");
                    purchasingItem.setChoice1("fruit");
                    break;
            }
            allItemsMap.put(key, purchasingItem);
        }

        /**
         * iterate thru and print the rows
         */
        Set keys = allItemsMap.keySet();
        logger.log(Level.INFO, "allItemsMap.keySet: " + keys);
        Iterator iterator1 = keys.iterator();
        while (iterator1.hasNext())
        {
            String date = (String) iterator1.next();
            PurchasingItem purchasingItem = allItemsMap.get(date);
            logger.log(Level.INFO, "purchasingItem: " + purchasingItem.getVendorName());

            rowData = sheet.createRow(rowNum);
            // date
            HSSFCell cellRow = rowData.createCell(0);
            cellRow.setCellValue(date);
            // vendor
            cellRow = rowData.createCell(1);
            cellRow.setCellValue(purchasingItem.getVendorName());

            // choices
            cellRow = rowData.createCell(2);
            cellRow.setCellValue(purchasingItem.getChoice1());

            // amount ordered
            cellRow = rowData.createCell(3);
            cellRow.setCellValue(purchasingItem.getChoice1Amount());

            if (purchasingItem.getChoice2() != null)
            {
                rowNum++;
                rowData = sheet.createRow(rowNum);
                cellRow = rowData.createCell(2);
                cellRow.setCellValue(purchasingItem.getChoice2());
                cellRow = rowData.createCell(3);
                cellRow.setCellValue(purchasingItem.getChoice2Amount());
            }
            if (purchasingItem.getChoice3() != null)
            {
                rowNum++;
                rowData = sheet.createRow(rowNum);
                cellRow = rowData.createCell(2);
                cellRow.setCellValue(purchasingItem.getChoice3());
                cellRow = rowData.createCell(3);
                cellRow.setCellValue(purchasingItem.getChoice3Amount());
            }
            rowNum++;
            rowNum++;
        }

        /**
         * write the workbook to the outputstream
         */
        try
        {
            wb.write(outputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createPurchasingList(List<OrderItem> orderItemList, ServletOutputStream outputStream)
    {
        /**
         * create the workbook, sheet, title row and header row
         */
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow rowHeader = sheet.createRow((short) 1);

        // create a font and style for the header row
        HSSFFont fontHeader = wb.createFont();
        fontHeader.setFontHeightInPoints((short) 10);
        fontHeader.setFontName("Arial");
        fontHeader.setItalic(true);
        fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFCellStyle styleHeader = wb.createCellStyle();
        styleHeader.setWrapText(true);
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // create the header row cells, populate the cell values and set the styles
        HSSFCell cellHeader = rowHeader.createCell(0, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Day");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(1, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Date");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(2, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Item");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(3, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Choices");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(4, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Ordered"); // total ordered plus additional
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(5, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Extra");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(6, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Item Cost");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(7, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Check Amount");
        cellHeader.setCellStyle(styleHeader);

        cellHeader = rowHeader.createCell(8, HSSFCell.CELL_TYPE_STRING);
        cellHeader.setCellValue("Info");
        cellHeader.setCellStyle(styleHeader);

        HSSFRow rowData = null;
        int rowNum = 2;

        Map<String, PurchasingItem> allItemsMap = new HashMap<String, PurchasingItem>();
        Map<String, List<LunchOrderItem>> dateListMap = hlService.createPurchasingMap(orderItemList);

        Set dateKeys = dateListMap.keySet();
        Iterator iterator = dateKeys.iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next(); // date -- need to parse into #1 and #2
            List<LunchOrderItem> lunchOrderItemList = dateListMap.get(key);
            logger.log(Level.INFO, "key: " + key + " --- size of list: " + lunchOrderItemList.size());

            PurchasingItem purchasingItem = null;
            // check the first item in the list to determine optiion, etc
            logger.log(Level.INFO, "*** item type: " + lunchOrderItemList.get(0).getItemType());

            switch (lunchOrderItemList.get(0).getItemType())
            {
                // kfc
                case 60:
                    purchasingItem = getKFCInfo(lunchOrderItemList);
                    break;

                // carls
                case 10:
                    purchasingItem = getCarlsInfo(lunchOrderItemList);
                    break;

                // chickfila
                case 20:
                    purchasingItem = getChickFilAFInfo(lunchOrderItemList);
                    break;

                // chipotle
                case 30:
                    purchasingItem = getChipotleInfo(lunchOrderItemList);
                    break;

                // flamebroiler
                case 40:
                    purchasingItem = getFlamebroilerInfo(lunchOrderItemList);
                    break;

                // hotdog
                case 50:
                    purchasingItem = getHotDogInfo(lunchOrderItemList);
                    break;

                // pickupstix
                case 70:
                    purchasingItem = getPickupStixInfo(lunchOrderItemList);
                    break;

                // pizza1 and pizza2
                case 80:
                case 90:
                    purchasingItem = getPizzaInfo(lunchOrderItemList);
                    break;

                // portofino1
                case 100:
                    purchasingItem = getPortofino1Info(lunchOrderItemList);
                    break;

                // portofino2
                case 110:
                    purchasingItem = getPortofino2Info(lunchOrderItemList);
                    break;

                // smoothie
                case 120:
                    allItemsMap.put(key, purchasingItem);
                    purchasingItem = getSmoothieInfo(lunchOrderItemList);
                    purchasingItem = getSandwichInfo(lunchOrderItemList);
                    break;

                // subway
                case 130:
                    purchasingItem = getSubwayInfo(lunchOrderItemList);
                    break;

                // taquitos
                case 140:
                    purchasingItem = getTaquitosInfo(lunchOrderItemList);
                    break;
            }
            allItemsMap.put(key, purchasingItem);
        }

        /**
         * iterate thru and print the rows
         */
        Set keys = allItemsMap.keySet();
        logger.log(Level.INFO, "allItemsMap.keySet: " + keys);
        Iterator iterator1 = keys.iterator();
        while (iterator1.hasNext())
        {
            String date = (String) iterator1.next();
            PurchasingItem purchasingItem = allItemsMap.get(date);
            logger.log(Level.INFO, "purchasingItem: " + purchasingItem.getVendorName());

            rowData = sheet.createRow(rowNum);
            // day
            HSSFCell cellRow = rowData.createCell(0);
            cellRow.setCellValue(date);
            // date
            cellRow = rowData.createCell(1);
            cellRow.setCellValue(date);
            // vendor
            cellRow = rowData.createCell(2);
            cellRow.setCellValue(purchasingItem.getVendorName());
            // choices
            cellRow = rowData.createCell(3);
            cellRow.setCellValue(purchasingItem.getChoice1());
            // amounts
            cellRow = rowData.createCell(4);
            cellRow.setCellValue(purchasingItem.getChoice1Amount());

            // extra
            cellRow = rowData.createCell(5);
            cellRow.setCellValue(purchasingItem.getExtra());

            // item cost
            cellRow = rowData.createCell(6);
            cellRow.setCellValue(purchasingItem.getItemCost());

            // check amount
            cellRow = rowData.createCell(7);
            cellRow.setCellValue("");

            // info
            cellRow = rowData.createCell(8);
            cellRow.setCellValue(purchasingItem.getInfo());

            if (purchasingItem.getChoice2() != null)
            {
                rowNum++;
                rowData = sheet.createRow(rowNum);
                cellRow = rowData.createCell(3);
                cellRow.setCellValue(purchasingItem.getChoice2());
                cellRow = rowData.createCell(4);
                cellRow.setCellValue(purchasingItem.getChoice2Amount());
            }
            if (purchasingItem.getChoice3() != null)
            {
                rowNum++;
                rowData = sheet.createRow(rowNum);
                cellRow = rowData.createCell(3);
                cellRow.setCellValue(purchasingItem.getChoice3());
                cellRow = rowData.createCell(4);
                cellRow.setCellValue(purchasingItem.getChoice3Amount());
            }
            if (purchasingItem.getChoice4() != null)
            {
                rowNum++;
                rowData = sheet.createRow(rowNum);
                cellRow = rowData.createCell(3);
                cellRow.setCellValue(purchasingItem.getChoice4());
                cellRow = rowData.createCell(4);
                cellRow.setCellValue(purchasingItem.getChoice4Amount());
            }
            rowNum++;
            rowNum++;
        }

        /**
         * write the workbook to the outputstream
         */
        try
        {
            wb.write(outputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createPurchasingListOld(List<OrderItem> orderItemList, ServletOutputStream outputStream)
    {
        try
        {
            logger.log(Level.INFO, "------ number of items in orderItemList: " + orderItemList.size());
            //List<OrderItem> orderItemList = hlService.getAllOrders();

            // create a new file
            //FileOutputStream out = new FileOutputStream("orders.xls");
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet();

            int rowNum = 0;
            int columnNum = 0;
            for (OrderItem orderItem : orderItemList)
            {
                HLOrderItemMap hlOrderItemMap = orderItem.getLunchOrderItemMap();
                Map lunchOrderItemMap = hlOrderItemMap.getOrderItemMap();
                if (lunchOrderItemMap != null)
                {

                    // lunchOrderItemMap has the orderItemMap in it
                    //Map<String, LunchOrderItem> orderItemMap = (Map<String, LunchOrderItem>) lunchOrderItemMap.get("orderItemMap");
                    logger.log(Level.INFO, "lunchOrderItemMap: " + lunchOrderItemMap);

                    Set keys = lunchOrderItemMap.keySet();
                    logger.log(Level.INFO, "lunchOrderItemMap keys: " + keys);
                    Iterator iterator = keys.iterator();
                    HSSFRow rowData = null;
                    while (iterator.hasNext())
                    {
                        columnNum = 0;
                        String key = (String) iterator.next();
                        LunchOrderItem lunchOrderItem = (LunchOrderItem) lunchOrderItemMap.get(key);
                        logger.log(Level.INFO, "lunchOrderItem: " + lunchOrderItem.getItemTypeString());
                        rowData = sheet.createRow(rowNum);

                        HSSFCell cellRow = rowData.createCell(columnNum);
                        cellRow.setCellValue(lunchOrderItem.getItemTypeString());
                        cellRow = rowData.createCell(columnNum + 1);
                        cellRow.setCellValue(lunchOrderItem.getChoice1());
                        cellRow = rowData.createCell(columnNum + 2);
                        cellRow.setCellValue(lunchOrderItem.getChoice2());
                        cellRow = rowData.createCell(columnNum + 3);
                        cellRow.setCellValue(lunchOrderItem.isOrderOne());
                        cellRow = rowData.createCell(columnNum + 4);
                        cellRow.setCellValue(lunchOrderItem.isOrderAdditional());
                        rowNum++;
                    }
                }
                else
                {
                    logger.log(Level.INFO, "*****lunchOrderItemMap is null for : " + orderItem.getStudentName());
                }
            }

            // write the workbook to the output stream
            wb.write(outputStream);
            //out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    private void createPurchasingListOld2()
    {
        try
        {
            List<OrderItem> orderItemList = hlService.getAllOrders();

            // create a new file
            FileOutputStream out = new FileOutputStream("orders.xls");
// create a new workbook
            Workbook wb = new HSSFWorkbook();
// create a new sheet
            Sheet s = wb.createSheet();
// declare a row object reference
            Row r = null;
// declare a cell object reference
            Cell c = null;
// create 3 cell styles
            CellStyle cs = wb.createCellStyle();
            CellStyle cs2 = wb.createCellStyle();
            CellStyle cs3 = wb.createCellStyle();
            DataFormat df = wb.createDataFormat();
// create 2 fonts objects
            Font f = wb.createFont();
            Font f2 = wb.createFont();

//set font 1 to 12 point type
            f.setFontHeightInPoints((short) 12);
//make it blue
            f.setColor((short) 0xc);
// make it bold
//arial is the default font
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);

//set font 2 to 10 point type
            f2.setFontHeightInPoints((short) 10);
//make it red
            f2.setColor((short) Font.COLOR_RED);
//make it bold
            f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

            f2.setStrikeout(true);

//set cell stlye
            cs.setFont(f);
//set the cell format
            cs.setDataFormat(df.getFormat("#,##0.0"));

//set a thin border
            cs2.setBorderBottom(cs2.BORDER_THIN);
//fill w fg fill color
            cs2.setFillPattern((short) CellStyle.SOLID_FOREGROUND);
//set the cell format to text see DataFormat for a full list
            cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

// set the font
            cs2.setFont(f2);

// set the sheet name in Unicode
            wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F " +
                    "\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430");
// in case of plain ascii
// wb.setSheetName(0, "HSSF Test");
// create a sheet with 30 rows (0-29)
            int rownum;
            for (rownum = (short) 0; rownum < 30; rownum++)
            {
                // create a row
                r = s.createRow(rownum);
                // on every other row
                if ((rownum % 2) == 0)
                {
                    // make the row height bigger  (in twips - 1/20 of a point)
                    r.setHeight((short) 0x249);
                }

                //r.setRowNum(( short ) rownum);
                // create 10 cells (0-9) (the += 2 becomes apparent later
                for (short cellnum = (short) 0; cellnum < 10; cellnum += 2)
                {
                    // create a numeric cell
                    c = r.createCell(cellnum);
                    // do some goofy math to demonstrate decimals
                    c.setCellValue(rownum * 10000 + cellnum
                            + (((double) rownum / 1000)
                            + ((double) cellnum / 10000)));

                    String cellValue;

                    // create a string cell (see why += 2 in the
                    c = r.createCell((short) (cellnum + 1));

                    // on every other row
                    if ((rownum % 2) == 0)
                    {
                        // set this cell to the first cell style we defined
                        c.setCellStyle(cs);
                        // set the cell's string value to "Test"
                        c.setCellValue("Test");
                    }
                    else
                    {
                        c.setCellStyle(cs2);
                        // set the cell's string value to "\u0422\u0435\u0441\u0442"
                        c.setCellValue("\u0422\u0435\u0441\u0442");
                    }


                    // make this column a bit wider
                    s.setColumnWidth((short) (cellnum + 1), (short) ((50 * 8) / ((double) 1 / 20)));
                }
            }

//draw a thick black border on the row at the bottom using BLANKS
// advance 2 rows
            rownum++;
            rownum++;

            r = s.createRow(rownum);

// define the third style to be the default
// except with a thick black border at the bottom
            cs3.setBorderBottom(cs3.BORDER_THICK);

//create 50 cells
            for (short cellnum = (short) 0; cellnum < 50; cellnum++)
            {
                //create a blank type cell (no value)
                c = r.createCell(cellnum);
                // set it to the thick black border style
                c.setCellStyle(cs3);
            }

//end draw thick black border


// demonstrate adding/naming and deleting a sheet
// create a sheet, set its title then delete it
            s = wb.createSheet();
            wb.setSheetName(1, "DeletedSheet");
            wb.removeSheetAt(1);
//end deleted sheet

// write the workbook to the output stream
// close our file (don't blow out our file handles
            wb.write(out);
            out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private PurchasingItem getSandwichInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getSandwichInfo...size of list: " + lunchOrderItemList.size());
        int turkey = 0;
        int ham = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Albertson's");
        purchasingItem.setItemCost(Constants.SANDWICH_ITEM_COST);
        purchasingItem.setChoice1("turkey croissant");
        purchasingItem.setChoice2("ham croissant");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice2().equalsIgnoreCase("turkey"))
            {
                turkey++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    turkey++;
                }
            }
            else if (lunchOrderItem.getChoice2().equalsIgnoreCase("ham"))
            {
                ham++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    ham++;
                }
            }
        }
        logger.log(Level.INFO, "turkey: " + turkey + "/ ham: " + ham);
        purchasingItem.setChoice1Amount(turkey);
        purchasingItem.setChoice2Amount(ham);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getSmoothieInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getSmoothieInfo...size of list: " + lunchOrderItemList.size());
        int jetty = 0;
        int mango = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Tropical Smoothie");
        purchasingItem.setItemCost(Constants.SMOOTHIE_ITEM_COST);
        purchasingItem.setChoice1("jetty punch");
        purchasingItem.setChoice2("mango magic");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("jetty"))
            {
                jetty++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    jetty++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("mango"))
            {
                mango++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    mango++;
                }
            }
        }
        logger.log(Level.INFO, "jetty: " + jetty + "/ mango: " + mango);
        purchasingItem.setChoice1Amount(jetty);
        purchasingItem.setChoice2Amount(mango);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getPortofino1Info(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getPortofino1Info...size of list: " + lunchOrderItemList.size());
        int cheese = 0;
        int meat = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Portofino-1");
        purchasingItem.setItemCost(Constants.PORTOFINO_ITEM_COST);
        purchasingItem.setChoice1("cheese sauce");
        purchasingItem.setChoice2("meat sauce");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                cheese++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    cheese++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("meat"))
            {
                meat++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    meat++;
                }
            }
        }
        logger.log(Level.INFO, "cheese: " + cheese + "/ meat: " + meat);
        purchasingItem.setChoice1Amount(cheese);
        purchasingItem.setChoice2Amount(meat);

        return purchasingItem;
    }

    private PurchasingItem getPortofino2Info(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getPortofino2Info...size of list: " + lunchOrderItemList.size());
        int pasta = 0;
        int lasagna = 0;
        int salad = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Portofino-2");
        purchasingItem.setItemCost(Constants.PORTOFINO_ITEM_COST);
        purchasingItem.setChoice1("pasta with meatball");
        purchasingItem.setChoice2("lasagna");
        purchasingItem.setChoice3("chicken salad");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("pasta"))
            {
                pasta++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    pasta++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("lasagna"))
            {
                lasagna++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    lasagna++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("salad"))
            {
                salad++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    salad++;
                }
            }
        }
        logger.log(Level.INFO, "pasta: " + pasta + "/ lasagna: " + lasagna + "/ salad: " + salad);
        purchasingItem.setChoice1Amount(pasta);
        purchasingItem.setChoice2Amount(lasagna);
        purchasingItem.setChoice3Amount(salad);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getKFCInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getKFCInfo...size of list: " + lunchOrderItemList.size());
        int strips = 0;
        int grilled = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("KFC");
        purchasingItem.setItemCost(Constants.KFC_ITEM_COST);
        purchasingItem.setChoice1("chicken strips");
        purchasingItem.setChoice2("grilled drumstick & thigh");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("strips"))
            {
                strips++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    strips++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("grilled"))
            {
                grilled++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    grilled++;
                }
            }
        }
        logger.log(Level.INFO, "strips: " + strips + "/ grilled: " + grilled);
        purchasingItem.setChoice1Amount(strips);
        purchasingItem.setChoice2Amount(grilled);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getPizzaSidesInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getPizzaSidesInfo...size of list: " + lunchOrderItemList.size());
        int salad = 0;
        int veggie = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setChoice1("salad");
        purchasingItem.setChoice2("veggie");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice2().equalsIgnoreCase("salad"))
            {
                salad++;
            }
            else if (lunchOrderItem.getChoice2().equalsIgnoreCase("veggie"))
            {
                veggie++;
            }
        }
        logger.log(Level.INFO, "salad: " + salad + "/ veggie: " + veggie);
        purchasingItem.setChoice1Amount(salad);
        purchasingItem.setChoice2Amount(veggie);

        return purchasingItem;
    }

    private PurchasingItem getPizzaInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getPizzaInfo...size of list: " + lunchOrderItemList.size());
        int cheese = 0;
        int pepperoni = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setInfo("(total pizza orders: " + lunchOrderItemList.size() + ")");
        purchasingItem.setVendorName("Pizza Hut");
        purchasingItem.setItemCost(Constants.PIZZA_ITEM_COST);
        purchasingItem.setChoice1("cheese pizza");
        purchasingItem.setChoice2("pepperoni pizza");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                cheese++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    cheese++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("pepp"))
            {
                pepperoni++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    pepperoni++;
                }
            }
        }
        logger.log(Level.INFO, "cheese: " + cheese + "/ pepperoni: " + pepperoni);
        // pizza amount = total ordered / 8 (rounded up)
        int amount = (int) Math.ceil(cheese / 8.0);
        logger.log(Level.INFO, "Cheese Pizza ceil: " + amount);
        purchasingItem.setChoice1Amount(amount);
        amount = (int) Math.ceil(pepperoni / 8.0);
        logger.log(Level.INFO, "Pepperoni Pizza ceil: " + amount);
        purchasingItem.setChoice2Amount(amount);
        /**
         purchasingItem.setChoice1Amount(cheese / 8);
         purchasingItem.setChoice2Amount(pepperoni / 8);
         **/
        purchasingItem.setExtra(1); // 1 extra cheese pizza

        return purchasingItem;
    }

    private PurchasingItem getSubwayInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getSubwayInfo...size of list: " + lunchOrderItemList.size());
        int turkeyWhite = 0;
        int turkeyWheat = 0;
        int hamWhite = 0;
        int hamWheat = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Subway");
        purchasingItem.setItemCost(Constants.SUBWAY_ITEM_COST);
        purchasingItem.setChoice1("turkey sandwich on white roll");
        purchasingItem.setChoice2("turkey sandwich on wheat roll");
        purchasingItem.setChoice3("ham sandwich on white roll");
        purchasingItem.setChoice4("ham sandwich on wheat roll");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("turkey") && lunchOrderItem.getChoice2().equalsIgnoreCase("white"))
            {
                turkeyWhite++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    turkeyWhite++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("turkey") && lunchOrderItem.getChoice2().equalsIgnoreCase("wheat"))
            {
                turkeyWheat++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    turkeyWheat++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("ham") && lunchOrderItem.getChoice2().equalsIgnoreCase("white"))
            {
                hamWhite++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    hamWhite++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("ham") && lunchOrderItem.getChoice2().equalsIgnoreCase("wheat"))
            {
                hamWheat++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    hamWheat++;
                }
            }
        }
        logger.log(Level.INFO, "turkey/white: " + turkeyWhite + "/ turkeyWheat: " + turkeyWheat +
                "/ hamWhite: " + hamWhite + "/ hamWheat: " + hamWheat);
        purchasingItem.setChoice1Amount(turkeyWhite);
        purchasingItem.setChoice2Amount(turkeyWheat);
        purchasingItem.setChoice3Amount(hamWhite);
        purchasingItem.setChoice4Amount(hamWheat);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getPickupStixInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getPickupStixInfo...size of list: " + lunchOrderItemList.size());
        int chickenWhite = 0;
        int chickenBrown = 0;
        int tofuWhite = 0;
        int tofuBrown = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Pick Up Stix");
        purchasingItem.setItemCost(Constants.PICKUPSTIX_ITEM_COST);
        purchasingItem.setChoice1("chicken bowl with white rice");
        purchasingItem.setChoice2("chicken bowl with brown rice");
        purchasingItem.setChoice3("tofu bowl with white rice");
        purchasingItem.setChoice4("tofu bowl with brown rice");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken") && lunchOrderItem.getChoice2().equalsIgnoreCase("white"))
            {
                chickenWhite++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    chickenWhite++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken") && lunchOrderItem.getChoice2().equalsIgnoreCase("brown"))
            {
                chickenBrown++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    chickenBrown++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("tofu") && lunchOrderItem.getChoice2().equalsIgnoreCase("white"))
            {
                tofuWhite++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    tofuWhite++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("tofu") && lunchOrderItem.getChoice2().equalsIgnoreCase("brown"))
            {
                tofuBrown++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    tofuBrown++;
                }
            }
        }
        logger.log(Level.INFO, "chicken/white: " + chickenWhite + "/ chickenBrown: " + chickenBrown + "/ tofuWhite: " + tofuWhite + "/ tofuBrown: "
                + tofuBrown);
        purchasingItem.setChoice1Amount(chickenWhite);
        purchasingItem.setChoice2Amount(chickenBrown);
        purchasingItem.setChoice3Amount(tofuWhite);
        purchasingItem.setChoice4Amount(tofuBrown);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getCarlsInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getCarlsInfo...size of list: " + lunchOrderItemList.size());
        int cheese = 0;
        int regular = 0;
        int chicken = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Carls Jr");
        purchasingItem.setItemCost(Constants.CARLS_ITEM_COST);
        purchasingItem.setChoice1("plain cheeseburger");
        purchasingItem.setChoice2("plain hamburger");
        purchasingItem.setChoice3("chicken stars");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                cheese++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    cheese++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("regular"))
            {
                regular++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    regular++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken"))
            {
                chicken++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    chicken++;
                }
            }
        }
        logger.log(Level.INFO, "cheese: " + cheese + "/ regular: " + regular + "/ chicken: " + chicken);
        purchasingItem.setChoice1Amount(cheese);
        purchasingItem.setChoice2Amount(regular);
        purchasingItem.setChoice3Amount(chicken);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getChickFilAFInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getChickFilAInfo...size of list: " + lunchOrderItemList.size());
        int breaded = 0;
        int grilled = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Chick-Fil-A");
        purchasingItem.setItemCost(Constants.CHICKFILA_ITEM_COST);
        purchasingItem.setChoice1("breaded nuggets");
        purchasingItem.setChoice2("grilled nuggets");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.getChoice1().equalsIgnoreCase("breaded"))
            {
                breaded++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    breaded++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("grilled"))
            {
                grilled++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    grilled++;
                }
            }
        }
        logger.log(Level.INFO, "strips: " + breaded + "/ grilled: " + grilled);
        purchasingItem.setChoice1Amount(breaded);
        purchasingItem.setChoice2Amount(grilled);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }


    private PurchasingItem getFlamebroilerInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getFlamebroilerInfo...size of list: " + lunchOrderItemList.size());
        int bowl = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Flame Broiler");
        purchasingItem.setItemCost(Constants.FLAMEBROILER_ITEM_COST);
        purchasingItem.setChoice1("kids chicken bowl");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.isOrderOne())
            {
                bowl++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    bowl++;
                }
            }
        }
        logger.log(Level.INFO, "chicken bowl: " + bowl);
        purchasingItem.setChoice1Amount(bowl);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getTaquitosInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getTaquitosInfo...size of list: " + lunchOrderItemList.size());
        int taq = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Taquitos");
        purchasingItem.setItemCost(Constants.TAQUITOS_ITEM_COST);
        purchasingItem.setChoice1("3 chicken taquitos");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.isOrderOne())
            {
                taq++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    taq++;
                }
            }
        }
        logger.log(Level.INFO, "taquitos: " + taq);
        purchasingItem.setChoice1Amount(taq);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getHotDogInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        logger.log(Level.INFO, "inside getHotDogInfo...size of list: " + lunchOrderItemList.size());
        int hotdog = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Wienerschnitzel");
        purchasingItem.setItemCost(Constants.HOTDOG_ITEM_COST);
        purchasingItem.setChoice1("plain hot dog");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();
            if (lunchOrderItem.isOrderOne())
            {
                hotdog++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    hotdog++;
                }
            }
        }
        logger.log(Level.INFO, "hotdog: " + hotdog);
        purchasingItem.setChoice1Amount(hotdog);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    private PurchasingItem getChipotleInfo(List<LunchOrderItem> lunchOrderItemList)
    {
        int chicken = 0;
        int cheese = 0;
        PurchasingItem purchasingItem = new PurchasingItem();
        purchasingItem.setVendorName("Portofino");
        purchasingItem.setItemCost(Constants.CHIPOTLE_ITEM_COST);
        purchasingItem.setChoice1("cheese quesadilla");
        purchasingItem.setChoice2("chicken quesadilla");

        Iterator iterator1 = lunchOrderItemList.iterator();
        while (iterator1.hasNext())
        {
            LunchOrderItem lunchOrderItem = (LunchOrderItem) iterator1.next();

            if (lunchOrderItem.getChoice1().equalsIgnoreCase("cheese"))
            {
                cheese++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    cheese++;
                }
            }
            else if (lunchOrderItem.getChoice1().equalsIgnoreCase("chicken"))
            {
                chicken++;
                if (lunchOrderItem.isOrderAdditional())
                {
                    chicken++;
                }
            }
        }
        logger.log(Level.INFO, "cheese: " + cheese + "/ chicken: " + chicken);
        purchasingItem.setChoice1Amount(cheese);
        purchasingItem.setChoice2Amount(chicken);
        purchasingItem.setExtra(5);

        return purchasingItem;
    }

    public String[] split(String str, String delim)
    {
        String[] splitStr = null;

        if (str != null && delim != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(str, delim);
            splitStr = new String[tokenizer.countTokens()];
            for (int i = 0; i < splitStr.length; i++)
            {
                splitStr[i] = tokenizer.nextToken();
            }
        }
        return splitStr;
    }
}
