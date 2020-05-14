package com.bencarlisle.wirelesswidth;

import java.util.Calendar;

class Encounter {
    private final String idString;
    private final String timestampString;

    private static String convertDateToString(Calendar calendar) {
        return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) +"/" + calendar.get(Calendar.YEAR);
    }

    private static String convertTimeToString(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        String hourString = String.valueOf(hour);
        int minute = calendar.get(Calendar.MINUTE);
        String minuteString;
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = String.valueOf(minute);
        }

        String amString = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        return hourString + ":" + minuteString + " " + amString;
    }

    Encounter(long id, long timestamp) {
        this.idString = String.valueOf(id);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        this.timestampString = convertDateToString(calendar) + " " + convertTimeToString(calendar);
    }

    String getId() {
        return idString;
    }

    String getTimestamp() {
        return timestampString;
    }
}
