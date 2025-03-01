package rubank;

import org.junit.Before;
import org.junit.Test;
import rubank.util.Date;

import static org.junit.Assert.assertEquals;

public class ProfileTest {

    private Profile pA;
    private Profile pB;
    private Profile pC;
    private Profile pD;
    private Profile pE;

    @Before
    public void setUP(){
        pA = new Profile("John", "Doe", new Date("2/19/2000"));
        pB = new Profile("Kate", "Lindsey", new Date("8/31/2001"));
        pC = new Profile("John", "Doe", new Date("7/8/1999"));
        pD = new Profile("john", "doe", new Date("2/19/2000"));
        pE = new Profile("April", "Doe", new Date("2/19/2000"));
    }

    @Test
    public void testCompareToLastNameLess() {
        assertEquals(-1, pA.compareTo(pB));
    }

    @Test
    public void testCompareToLastNameGreater() {
        assertEquals( 1, pB.compareTo(pA));
    }

    @Test
    public void testCompareToFirstNameLess() {
        assertEquals( -1, pE.compareTo(pA));
    }

    @Test
    public void testCompareToFirstNameGreater() {
        assertEquals(1, pA.compareTo(pE));
    }

    @Test
    public void testCompareToDOBEarlier() {
        assertEquals(-1, pC.compareTo(pA));
    }

    @Test
    public void testCompareToDOBLater() {
        assertEquals( 1, pA.compareTo(pC));
    }

    @Test
    public void testCompareToEqualIgnoreCase() {
        assertEquals(0, pA.compareTo(pD));
    }

}