package com.example.temperatureconverter;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by christinejulien on 6/16/17.
 */

public class ConverterUtilTest {

    @Test
    public void testConvertFahrenheitToCelsius() {
        float boilingPointFActual = ConverterUtil.convertCelsiusToFahrenheit(100);
        float boilingPointFExpected = 212;
        assertEquals("Conversion from celsius to fahrenheit failed",
                boilingPointFExpected, boilingPointFActual);
    }

    @Test
    public void testConvertCelsiusToFahrenheit() {
        float boilingPointCActual = ConverterUtil.convertFahrenheitToCelsius(212);
        float boilingPointCExpected = 100;
        assertEquals("Conversion from celsius to fahrenheit failed",
                boilingPointCExpected, boilingPointCExpected);
    }
}
