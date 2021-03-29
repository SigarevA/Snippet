package ru.vsu.cs.Crocodile.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Calendar;

@WritingConverter
public class LongToCalendarConverter implements Converter<Long, Calendar> {
    @Override
    public Calendar convert(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }
}
