package rubank.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateTest {

    @Test
    public void testInvalidMonthZero(){
        Date date = new Date("0/15/2000");
        assertFalse(date.isValid());
    }

    @Test
    public void testInvalidMonthThirteen(){
        Date date = new Date("13/15/2020");
        assertFalse(date.isValid());
    }

    @Test
    public void testInvalidDayInNonFebMonth(){
        Date date = new Date("4/31/2023");
        assertFalse(date.isValid());
    }

    @Test
    public void testDaysInFeb_NonLeap(){
        Date date = new Date("2/29/2023");
        assertFalse(date.isValid());
    }

    @Test
    public void testDaysInFeb_Leap(){
        Date date = new Date("2/29/2020");
        assertTrue(date.isValid());
    }

    @Test
    public void testValidDayInNonFebMonth(){
        Date date = new Date("7/4/1999");
        assertTrue(date.isValid());
    }
}