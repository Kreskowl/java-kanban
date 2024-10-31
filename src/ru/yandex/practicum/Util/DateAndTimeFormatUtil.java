package ru.yandex.practicum.Util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateAndTimeFormatUtil {
    private static final DateTimeFormatter DATE_AND_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_AND_TIME_FORMAT) : "";
    }

    public static LocalDateTime parseDateTime(String date) {
        return date.isEmpty() ? null : LocalDateTime.parse(date, DATE_AND_TIME_FORMAT);
    }

    public static String formatDurationTime(Duration duration) {
        if (duration == null || duration.isZero()) {
            return "";
        }

        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

        StringBuilder formattedDuration = new StringBuilder();
        if (days > 0) formattedDuration.append(days).append(" days ");
        if (hours > 0) formattedDuration.append(hours).append(" hours ");
        if (minutes > 0) formattedDuration.append(minutes).append(" minutes");

        return formattedDuration.toString().trim();
    }

    public static Duration parseFormattedDuration(String durationString) {
        if (durationString == null || durationString.isEmpty()) {
            return Duration.ZERO;
        }

        long days = 0;
        long hours = 0;
        long minutes = 0;

        String[] parts = durationString.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("days")) {
                days = Long.parseLong(parts[i - 1]);
            } else if (parts[i].equals("hours")) {
                hours = Long.parseLong(parts[i - 1]);
            } else if (parts[i].equals("minutes")) {
                minutes = Long.parseLong(parts[i - 1]);
            }
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }
}

