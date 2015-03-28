package com.alliander.osgp.shared.mappers;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLGregorianCalendarToDateTimeConverter extends BidirectionalConverter<XMLGregorianCalendar, DateTime> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLGregorianCalendarToDateTimeConverter.class);

    @Override
    public XMLGregorianCalendar convertFrom(final DateTime source, final Type<XMLGregorianCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(source.toGregorianCalendar());
        } catch (final DatatypeConfigurationException e) {
            LOGGER.debug("newXMLGregorianCalendar failed", e);
            // Sonar will complain about not rethrowing the exception,
            // but we don't want that in this case!
            return null;
        }
    }

    @Override
    public DateTime convertTo(final XMLGregorianCalendar source, final Type<DateTime> destinationType) {
        if (source == null) {
            return null;
        }

        return new DateTime(source.toGregorianCalendar().getTime());
    }
}