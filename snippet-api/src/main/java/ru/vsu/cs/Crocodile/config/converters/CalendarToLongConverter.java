package ru.vsu.cs.Crocodile.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Calendar;

@WritingConverter
public class CalendarToLongConverter implements Converter<Calendar, Long> {

    @Override
    public Long convert(Calendar source) {
        return source.getTimeInMillis();
    }
}