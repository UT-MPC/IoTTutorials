package com.example.temperatureconverter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Created by christinejulien on 6/16/17.
 */

public class DisplayConversionTest {

    @Test
    public void shouldContainTheCorrectExtras()  throws Exception {
        Context context = mock(Context.class);
        Intent intent = MainActivity.createConversion(context, "conversionTextTest");
        assertNotNull(intent);
        Bundle extras = intent.getExtras();
        assertNotNull(extras);
        assertEquals("conversionTextTest", extras.getString("TEXT"));
    }
}
