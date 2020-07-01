package com.example.voiceassistent;

import com.example.voiceassistent.parse.ParsingHtmlService;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMethod(){
        System.out.println(ParsingHtmlService.getHoliday("19 марта 2020"));
        System.out.println(ParsingHtmlService.getHoliday("20 марта 2020"));
    }
}