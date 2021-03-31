package com.smoothsys.qonsume_pos.Utilities;

import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Restaurant;
import com.smoothsys.qonsume_pos.Models.ShopTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {

    public static boolean shopIsOpen(Restaurant shop) {

        if(Config.DEBUG_SHOPS_ALWAYS_OPEN) {
            return true;
        }

        if(shop == null) {
            return false;
        }

        ShopTime time = getTodaysOpenTimes(shop.shoptimes);

        if(time.getId().equals("No Time")) {
            return true;
        }

        Date openTime = null;
        Date closeTime = null;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
            openTime = simpleDateFormat.parse(time.getOpen());
            closeTime = simpleDateFormat.parse(time.getClose());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentTime = new Date();
        Boolean overOpenTime = isOverOpenTime(openTime, currentTime);
        Boolean underCloseTime = isUnderCloseTime(closeTime, currentTime);
        return overOpenTime && underCloseTime;
    }

    private static boolean isUnderCloseTime(Date closeTime, Date currentTime) {

        int closeHour = closeTime.getHours();
        int closeMinute = closeTime.getMinutes();

        int currentHour = currentTime.getHours();
        int currentMinute = currentTime.getMinutes();

        if(currentHour < closeHour) {
            if(currentHour == closeHour) {

                if(currentMinute <= closeMinute) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean isOverOpenTime(Date openTime, Date currentTime) {

        int openHour = openTime.getHours();
        int openMinute = openTime.getMinutes();

        int currentHour = currentTime.getHours();
        int currentMinute = currentTime.getMinutes();

        if(currentHour >= openHour) {

            if(currentHour == openHour) {

                return currentMinute >= openMinute;
            } else {
                return true;
            }
        }
        return false;
    }

    public static ShopTime getTodaysOpenTimes(List<ShopTime> times) {

        // == First check if any times are included ==
        if(times == null) {
            return  new ShopTime("No Time","No Time","No Time","No Time");
        }

        if(times.size() == 0) {
            return  new ShopTime("No Time","No Time","No Time","No Time");
        }

        for (ShopTime time: times) {
            if(Integer.parseInt(time.getWeekday()) == getCurrentWeekDayAsInt()) {
                return time;
            }
        }

        return new ShopTime("No Time","No Time","No Time","No Time");
    }

    private static int getCurrentWeekDayAsInt() {

        Calendar calendar = Calendar.getInstance();

        // Week days in Java's calendar class works like this:
        // 1 = Sunday, 2 = Monday, 3 = Tuesday, 4 = Wednesday, 5 = Thursday, 6 = Friday, 7 = Saturday

        // However.. our ShopTime class is set to 1,2,3 .. = Monday,Tuesday,Wednesday ..

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {

            case Calendar.MONDAY:
                return 1;

            case Calendar.TUESDAY:
                return 2;

            case Calendar.WEDNESDAY:
                return 3;

            case Calendar.THURSDAY:
                return 4;

            case Calendar.FRIDAY:
                return 5;

            case Calendar.SATURDAY:
                return 6;

            case Calendar.SUNDAY:
                return 7;

            default:
                return 2;
        }
    }
}