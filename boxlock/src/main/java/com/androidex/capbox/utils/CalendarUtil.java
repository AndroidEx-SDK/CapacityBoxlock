package com.androidex.capbox.utils;

import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日历工具
 *
 * @author Administrator
 */
public class CalendarUtil {

    private int weeks = 0;// 用来全局控制周数变化  
    private int months = 0; // 用来全局控制月数的变化
    private int year = 0;
    public static final String YH_ID_FORMAT = "yyyyMMdd";
    public static final String YH_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";
    public static CalendarUtil mInstance = null;
    public static String boxName = "AndroidEx";

    public static CalendarUtil getInstance() {
        if (mInstance == null) {
            mInstance = new CalendarUtil();
        }
        return mInstance;

    }

    /**
     * 组合名字Box+address后两位
     */
    public static String getName(String name, String address) {
        if (address == null) {
            if (name != null){
                return name;
            }else return "";
        }
        if (name == null || name.equals("")) {
            return "Box" + address.substring(address.length() - 2);
        } else {
            if (name.contains(boxName)) {
                if (name.trim().equals("AndroidExBox")) {
                    return "Box" + address.substring(address.length() - 2);
                } else {
                    return name.replace(boxName, "");
                }
            } else if (name.trim().equals("Box")) {
                return name + address.substring(address.length() - 2);
            } else {
                return "Box";
            }
        }
    }

    /**
     * @return 默认年月日 格式("yyyy-MM-dd")
     */
    public String getDefaultDay() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号  
        lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号  
        lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天  

        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * @return 得到当前月份的第一天 格式("yyyy-MM-dd")
     */
    public String getFirstDayOfMonth(int year, int month) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.clear();
        lastDate.set(year, month, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 得到当前是哪一天
     *
     * @param id 通过一年中的第几天
     * @return
     */
    public int[] getTimeatOneDay(int id) {
        int[] times = new int[3];
        Time t = new Time();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, t.year);
        c.set(Calendar.DAY_OF_YEAR, id);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(c);
        String[] arr = preMonday.split("-");
        for (int i = 0; i < 3; i++) {
            times[i] = Integer.parseInt(arr[i]);
        }
        return times;
    }

    /**
     * @return 得到当前日期的是在一年的第几天
     */
    public int getDaysForYear() {
        int i = -1;
        i = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return i;
    }

    /**
     * @return 得到当前的总天数
     */
    public int getAllDaysForYear() {
        int i = -1;
        i = Calendar.getInstance().getMaximum(Calendar.DAY_OF_YEAR);
        return i;
    }


    public String getMondayOFWeek() {
        weeks = 0;
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();

//        DateFormat df = DateFormat.getDateInstance(); 
//        String preMonday = df.format(monday); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }


    public String getCurrentYearFirst() {
        int yearPlus = this.getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus);
        Date yearDay = currentDate.getTime();

//        DateFormat df = DateFormat.getDateInstance(); 
//        String preYearDay = df.format(yearDay); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式  
        String preYearDay = dateFormat.format(yearDay);
        return preYearDay;
    }

    // 获得本年最后一天的日期 *  
    public String getCurrentYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);
        return years + "-12-31";
    }

    // 获得上年第一天的日期 *
    public String getPreviousYearFirst() {
        year--;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value = years_value + year;
        return years_value + "-1-1";
    }

    // 获得上年最后一天的日期  
    public String getPreviousYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value = years_value + year;
        return years_value + "-12-31";
    }

    // 获得下年第一天的日期 *  
    public String getNextYearFirst() {
        year++;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value = years_value + year;
        return years_value + "-1-1";
    }

    // 获得下年最后一天的日期  
    public String getNextYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式  
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value = years_value + year;
        return years_value + "-12-31";
    }

    /**
     * @return
     */
    private int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......  
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1  
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    private int getYearPlus() {
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// 获得当天是一年中的第几天  
        cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天  
        cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。  
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        if (yearOfNumber == 1) {
            return -MaxYear;
        } else {
            return 1 - yearOfNumber;
        }
    }

    //上周、周日
    public String getPreviousWeekSunday() {
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
//        DateFormat df = DateFormat.getDateInstance(); 
//        String preMonday = df.format(monday); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }

    //上周、周一
    public String getPreviousWeekday() {
        weeks--;
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        Date monday = currentDate.getTime();
//        DateFormat df = DateFormat.getDateInstance(); 
//        String preMonday = df.format(monday); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }

    //下一周、周一
    public String getNextMonday() {
        weeks++;
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        Date monday = currentDate.getTime();
//        DateFormat df = DateFormat.getDateInstance(); 
//        String preMonday = df.format(monday); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }

    //下周周日
    public String getNextSunday() {

        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
//        DateFormat df = DateFormat.getDateInstance(); 
//        String preMonday = df.format(monday); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = dateFormat.format(monday);
        return preMonday;
    }

    //上月一 号
    public String getPreviousMonthFirst() {
        months--;
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号  
        lastDate.add(Calendar.MONTH, months);// 减一个月，变为下月的1号  
        // lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天  

        str = sdf.format(lastDate.getTime());
        return str;
    }

    //上月末号
    public String getPreviousMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, months);// 减一个月  
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天  
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天  
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //下个月一 号
    public String getNextMonthFirst() {
        months++;
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, months);// 减一个月  
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天  
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //下个月末号
    public String getNextMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, months);// 加一个月  
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天  
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天  
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //得到两个日期之间的所有日期
    public static GregorianCalendar[] getBetweenDate(String d1, String d2) throws ParseException {
        Vector<GregorianCalendar> v = new Vector<GregorianCalendar>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar gc1 = new GregorianCalendar(), gc2 = new GregorianCalendar();
        gc1.setTime(sdf.parse(d1));
        gc2.setTime(sdf.parse(d2));
        do {
            GregorianCalendar gc3 = (GregorianCalendar) gc1.clone();
            v.add(gc3);
            gc1.add(Calendar.DAY_OF_MONTH, 1);
        } while (!gc1.after(gc2));
        return v.toArray(new GregorianCalendar[v.size()]);
    }

    //两日期的ArrayList<String>
    public static ArrayList<String> getBetweenDataString(String data1, String data2) {
        ArrayList<String> str = new ArrayList<String>();
        GregorianCalendar[] ga = null;
        try {
            ga = getBetweenDate(data1, data2);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (GregorianCalendar e : ga) {
            System.out.println(e.get(Calendar.YEAR) + "年 " +
                    +(e.get(Calendar.MONTH) + 1) + "月 " +
                    e.get(Calendar.DAY_OF_MONTH) + "号");
            str.add(e.get(Calendar.YEAR) + "-" + (e.get(Calendar.MONTH) + 1) + "-" + e.get(Calendar.DAY_OF_MONTH));
        }
        return str;
    }


    /**
     * 得到系统当前日期的前或者后几天
     *
     * @param iDate 如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @return Date 返回系统当前日期的前或者后几天
     * @see Calendar#add(int, int)
     */
    public static String getDateBeforeOrAfter(int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, iDate * (-1));
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return dayBefore;
    }

    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     * @return String 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd
     * HH:mm:ss
     * @see SimpleDateFormat#format(Date)
     */
    public static String getFormatDateTime(Date currDate, String format) {
        if (currDate == null) {
            return "";
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.format(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(YH_ID_FORMAT);
            try {
                return dtFormatdB.format(currDate);
            } catch (Exception ex) {
            }
        }
        return "";
    }

    /**
     * 时间戳转换成字符窜
     *
     * @param time
     * @param format 时间格式，如yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getDateToString(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(d);
    }

    /**
     * 将字符串转为时间戳
     *
     * @param time
     * @param format 时间格式，如yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getStringToDate(String time, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public static ArrayList<Integer> getTheDayBefore(int year, int month, int day) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        String str = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
        str = getSpecifiedDayBefore(str);
        String[] resultStr = str.split("-");
        result.add(Integer.valueOf(resultStr[0]));
        result.add(Integer.valueOf(resultStr[1]));
        result.add(Integer.valueOf(resultStr[2]));
        return result;
    }

    //获得两日期相差的天数
    public static int getTwoDay(String sj1, String sj2) {
        if (sj2 == null) {
            return 29;
        }
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        int day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (int) ((date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000));
        } catch (Exception e) {
            return 29;
        }
        return day;
    }

    //设置保存的ID和日期
    public static void setSaveDateAndId() {
//    	YHApplication.mPedoMeter.id = getFormatDateTime(getDateBeforeOrAfter(YHApplication.saveDataTimes),YH_ID_FORMAT);
//    	String[] dateString = getFormatDateTime(getDateBeforeOrAfter(YHApplication.saveDataTimes),YH_DATE_FORMAT).split("-");
//    	YHApplication.getInstance().mPedoMeter.year = Integer.valueOf(dateString[0]);
//    	YHApplication.getInstance().mPedoMeter.month = Integer.valueOf(dateString[1]);
//    	YHApplication.getInstance().mPedoMeter.day = Integer.valueOf(dateString[2]);
    }

    //获取当天的日期
    public static String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式  
        String hehe = dateFormat.format(now);
        return hehe;
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    public static String getNowTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 日期格式  
        String hehe = dateFormat.format(now);
        return hehe;
    }

    //获取系统当前时间：时分秒
    public static int[] getTime() {
        Time t = new Time();
        t.setToNow();
        int[] nowTime = new int[6];
        nowTime[0] = t.year - 2000;
        nowTime[1] = t.month + 1;
        nowTime[2] = t.monthDay;
        nowTime[3] = t.hour;
        nowTime[4] = t.minute;
        nowTime[5] = t.second;

        return nowTime;
    }

    /**
     * 获取系统时间数组
     *
     * @return
     */
    public static int[] getNowTimeForDay() {
        Time t = new Time();
        t.setToNow();
        int[] nowTime = new int[4];
        nowTime[0] = t.year;
        nowTime[1] = t.month;
        nowTime[2] = t.monthDay;
        nowTime[3] = getWeekForYear(nowTime[0], nowTime[1], nowTime[3]);
        return nowTime;
    }


    //拆分当天日期
    public static int[] splitNowTime(String hehe) {
        int[] data = new int[3];
        String[] str = hehe.split("-");
        for (int i = 0; i < str.length; i++) {
            data[i] = Integer.valueOf(str[i]);
        }
        return data;
    }


    /**
     * 获取当前的时间
     *
     * @return
     */
    public static String getNewTime() {
        String time = "";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制   
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = sdf.format(dt);

        return time;

    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public static CharSequence getNowRecordTime() {
        // TODO Auto-generated method stub
        String str = "";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        str = format.format(date);
        return str;
    }

    /**
     * 获取星期几
     */
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    /**
     * 得到开始和结束的时间间隔
     *
     * @param start
     * @param end
     * @return
     */
    public static String getRecordTimeForStartAndEnd(String start, String end) {
        String str = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date st = format.parse(start);
            Date et = format.parse(end);
            long time = et.getTime() - st.getTime();
            int min = (int) (time / 1000 / 60);
            str = String.valueOf(min);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取第几周
     *
     * @param time
     * @return
     */
    public static String getWeekForDay(String time) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date data = null;
        try {
            data = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(data);
        String week = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
        return week;
    }


    /**
     * 获取指定月的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysForMonth(int year, int month) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM");
        Calendar rightNow = Calendar.getInstance();
        try {
            rightNow.setTime(simpleDate.parse(year + "-" + month));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);//根据年月 获取月份天数
    }

    /**
     * 根据输入的年月周数来取该周首天
     *
     * @param year 年份(>0)
     * @param week 当年周数
     * @return 获取该周的7天的日期
     */
    public static List<Integer> getFirstDayByMonthWeek(int year, int week) {
        List<Integer> list = new ArrayList<Integer>();
        //该周第一天日期
        Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(Calendar.YEAR, year);
        c1.set(Calendar.WEEK_OF_YEAR, week);
        c1.setFirstDayOfWeek(Calendar.MONDAY);
        for (int i = 1; i <= 7; i++) {//从星期天开始算
            c1.set(Calendar.DAY_OF_WEEK, i);
            list.add(c1.get(Calendar.DAY_OF_MONTH));
        }
        return list;
    }


    /**
     * 获取指定年月的月份有多少天
     *
     * @param year  哪一年
     * @param month 那一个月
     * @return
     */
    public static int getNumberForMonth(int year, int month) {
        int day = 0;
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, 2014);
        time.set(Calendar.MONTH, month - 1);//Calendar对象默认一月为0
        day = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }

    /**
     * 获取指定年月的月份有几周
     *
     * @param year  哪一年
     * @param month 那一个月
     * @return
     */
    public static int getWeeksForMonth(int year, int month) {
        int week = 0;
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, 2014);
        time.set(Calendar.MONTH, month - 1);//Calendar对象默认一月为0
        time.setFirstDayOfWeek(Calendar.MONDAY);
        week = time.getActualMaximum(Calendar.WEEK_OF_MONTH);//本月份的天数
        return week;
    }

    /**
     * 获取上一天
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int[] getTimesForDayByPro(int year, int month, int day) {
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(year, month, day);
        int i = time.get(Calendar.DAY_OF_YEAR);
        time.set(Calendar.DAY_OF_YEAR, i - 1);

        times[0] = time.get(Calendar.YEAR);
        times[1] = time.get(Calendar.MONTH);
        times[2] = time.get(Calendar.WEEK_OF_YEAR);
        times[3] = time.get(Calendar.DAY_OF_MONTH);

        return times;

    }

    /**
     * 获取上一周
     *
     * @param year
     * @param week
     * @return
     */
    public static int[] getTimesForWeekByPro(int year, int week) {
        // TODO Auto-generated method stub
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.setFirstDayOfWeek(Calendar.MONDAY);
        if ((week - 1) == 0) {
            year = year - 1;
            times[0] = time.get(Calendar.YEAR);
            times[1] = 11;
            times[2] = time.getActualMaximum(Calendar.WEEK_OF_YEAR);
            times[3] = 1;
        } else {
            time.set(Calendar.WEEK_OF_YEAR, week - 1);
            times[0] = time.get(Calendar.YEAR);
            times[1] = time.get(Calendar.MONTH);
            times[2] = time.get(Calendar.WEEK_OF_YEAR);
            times[3] = time.get(Calendar.DAY_OF_MONTH);
        }
        return times;
    }

    /**
     * 获取上一月
     *
     * @param year
     * @param month
     * @return
     */
    public static int[] getTimesForMonthByPro(int year, int month) {
        // TODO Auto-generated method stub
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);
        time.set(Calendar.DAY_OF_MONTH, 1);
        times[0] = time.get(Calendar.YEAR);
        times[1] = time.get(Calendar.MONTH);
        times[2] = time.get(Calendar.WEEK_OF_YEAR);
        times[3] = time.get(Calendar.DAY_OF_MONTH);

        return times;
    }

    /**
     * 获取下一天
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int[] getTimesForDayByNext(int year, int month, int day) {
        // TODO Auto-generated method stub
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(year, month, day);
        int i = time.get(Calendar.DAY_OF_YEAR);
        time.set(Calendar.DAY_OF_YEAR, i + 1);

        times[0] = time.get(Calendar.YEAR);
        times[1] = time.get(Calendar.MONTH);
        times[2] = time.get(Calendar.WEEK_OF_YEAR);
        times[3] = time.get(Calendar.DAY_OF_MONTH);
        return times;
    }

    /**
     * 获取下一周
     *
     * @param year
     * @param week
     * @return
     */
    public static int[] getTimesForWeekByNext(int year, int week) {
        // TODO Auto-generated method stub
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.setFirstDayOfWeek(Calendar.MONDAY);
        int length = time.getActualMaximum(Calendar.WEEK_OF_YEAR);
        if ((week + 1) > length) {
            times[0] = year + 1;
            times[1] = 0;
            times[2] = 1;
            times[3] = 1;
        } else {

            time.set(Calendar.WEEK_OF_YEAR, week + 1);
            times[0] = time.get(Calendar.YEAR);
            times[1] = time.get(Calendar.MONTH);
            times[2] = time.get(Calendar.WEEK_OF_YEAR);
            times[3] = time.get(Calendar.DAY_OF_MONTH);
        }
        return times;
    }

    /**
     * 获取下一月
     *
     * @param year
     * @param month
     * @return
     */
    public static int[] getTimesForMonthByNext(int year, int month) {
        // TODO Auto-generated method stub
        int[] times = new int[4];
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.DAY_OF_MONTH, 1);
        time.set(Calendar.MONTH, month + 1);
        times[0] = time.get(Calendar.YEAR);
        times[1] = time.get(Calendar.MONTH);
        times[2] = time.get(Calendar.WEEK_OF_YEAR);
        times[3] = time.get(Calendar.DAY_OF_MONTH);
        return times;
    }


    public static int getWeekForYear(int year, int month, int day) {
        int i = -1;
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.DAY_OF_MONTH, day);
        time.set(Calendar.MONTH, month - 1);
        i = time.get(Calendar.WEEK_OF_YEAR);
        return i;
    }

    /**
     * 18位身份证校验,粗略的校验
     *
     * @param idCard
     * @return
     * @author lyl
     */
    public static boolean is18ByteIdCard(String idCard) {
        Pattern pattern1 = Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$"); //粗略的校验
        Matcher matcher = pattern1.matcher(idCard);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 18位身份证校验,比较严格校验
     *
     * @param idCard
     * @return
     * @author lyl
     */
    public static boolean is18ByteIdCardComplex(String idCard) {
        Pattern pattern1 = Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$");
        Matcher matcher = pattern1.matcher(idCard);
        int[] prefix = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int[] suffix = new int[]{1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        if (matcher.matches()) {
            Map<String, String> cityMap = initCityMap();
            if (cityMap.get(idCard.substring(0, 2)) == null) {
                return false;
            }
            int idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和
            for (int i = 0; i < 17; i++) {
                idCardWiSum += Integer.valueOf(idCard.substring(i, i + 1)) * prefix[i];
            }

            int idCardMod = idCardWiSum % 11;//计算出校验码所在数组的位置
            String idCardLast = idCard.substring(17);//得到最后一位身份证号码

            //如果等于2，则说明校验码是10，身份证号码最后一位应该是X
            if (idCardMod == 2) {
                if (idCardLast.equalsIgnoreCase("x")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                if (idCardLast.equals(suffix[idCardMod] + "")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private static Map<String, String> initCityMap() {
        Map<String, String> cityMap = new HashMap<String, String>();
        cityMap.put("11", "北京");
        cityMap.put("12", "天津");
        cityMap.put("13", "河北");
        cityMap.put("14", "山西");
        cityMap.put("15", "内蒙古");

        cityMap.put("21", "辽宁");
        cityMap.put("22", "吉林");
        cityMap.put("23", "黑龙江");

        cityMap.put("31", "上海");
        cityMap.put("32", "江苏");
        cityMap.put("33", "浙江");
        cityMap.put("34", "安徽");
        cityMap.put("35", "福建");
        cityMap.put("36", "江西");
        cityMap.put("37", "山东");

        cityMap.put("41", "河南");
        cityMap.put("42", "湖北");
        cityMap.put("43", "湖南");
        cityMap.put("44", "广东");
        cityMap.put("45", "广西");
        cityMap.put("46", "海南");

        cityMap.put("50", "重庆");
        cityMap.put("51", "四川");
        cityMap.put("52", "贵州");
        cityMap.put("53", "云南");
        cityMap.put("54", "西藏");

        cityMap.put("61", "陕西");
        cityMap.put("62", "甘肃");
        cityMap.put("63", "青海");
        cityMap.put("64", "宁夏");
        cityMap.put("65", "新疆");

//          cityMap.put("71", "台湾");
//          cityMap.put("81", "香港");
//          cityMap.put("82", "澳门");
//          cityMap.put("91", "国外");
//          System.out.println(cityMap.keySet().size());
        return cityMap;
    }

}
