package rubank.util;

import java.util.Calendar;

/**
 * The Date class represents a date with year, month, and day.
 * Provides methods to validate the date, check if it is a leap year,
 * check if the date is in the future, and check if the date represents an adult (18+ years).
 * Implements Comparable to compare dates.
 * @author Alison Chu, Byounguk Kim
 */
public class Date implements Comparable<Date> {
    private int year;
    private int month;
    private int day;
    public static final int QUADRENNIAL = 4;
    public static final int CENTENNIAL = 100;
    public static final int QUATERCENTENNIAL = 400;
    private static final int JANUARY = 1, MARCH = 3, MAY = 5, JULY = 7, AUGUST = 8, OCTOBER = 10, DECEMBER = 12;
    private static final int APRIL = 4, JUNE = 6, SEPTEMBER = 9, NOVEMBER = 11;
    private static final int FEBRUARY = 2;
    private static final int DAYS_31 = 31;
    private static final int DAYS_30 = 30;
    private static final int DAYS_FEBRUARY_NON_LEAP = 28;
    private static final int DAYS_FEBRUARY_LEAP = 29;

    /**
     * Constructs a Date from a string in the format "MM/DD/YYYY".
     * @param date the date string
     */
    public Date(String date) {
        String[] parts = date.split("/");
        if (parts.length < 3){
            throw new IllegalArgumentException("Invalid date");
        }
        this.month = Integer.parseInt(parts[0]);
        this.day = Integer.parseInt(parts[1]);
        this.year = Integer.parseInt(parts[2]);
    }

    public Date() {// Use today's date from java.util.Calendar
        Calendar now = Calendar.getInstance();
        this.year = now.get(Calendar.YEAR);
        this.month = now.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        this.day = now.get(Calendar.DAY_OF_MONTH);
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    /**
     * Checks if the year is a leap year.
     * @param year the year to check
     * @return true if the year is a leap year, false otherwise
     */
    private boolean isLeapYear(int year) {
        if (year % QUADRENNIAL == 0) {
            if (year % CENTENNIAL == 0) {
                return year % QUATERCENTENNIAL == 0;
            }
            return true;
        }
        return false;
    }

    /**
     * Validates the date.
     * @return true if the date is valid, false otherwise
     */
    public boolean isValid() {
        if (month<JANUARY||month>DECEMBER) return false;
        int maxDays;
        if (month == JANUARY || month == MARCH || month == MAY || month == JULY ||
                month == AUGUST || month == OCTOBER || month == DECEMBER) {
            maxDays = DAYS_31;
        } else if (month == APRIL || month == JUNE || month == SEPTEMBER || month == NOVEMBER) {
            maxDays = DAYS_30;
        } else {
            maxDays = isLeapYear(year) ? DAYS_FEBRUARY_LEAP : DAYS_FEBRUARY_NON_LEAP;
        }

        if (day < 1 || day > maxDays) return false;

        return true;
    }

    /**
     * Checks if the date represents an adult (18+ years).
     * @return true if the date represents an adult, false otherwise
     */
    public boolean isAdult() {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month - 1, day);  

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        return age >= 18;
    }

    /**
     * Checks if the date is in the future.
     * @return true if the date is in the future, false otherwise
     */ 
    public boolean isFutureDate() {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month - 1, day);
        return birthDate.after(today);
    }

    public int getAge() {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) 
       || (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)
          && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
        age--;
        }
        return age;
    }
    

    /**
     * Compares this date with another date.
     * @param other the date to compare to
     * @return a negative integer, zero, or a positive integer as this date is less than, equal to, or greater than the specified date
     */
    @Override
    public int compareTo(Date other) {
        int yearDiff = this.year - other.year;
        if (yearDiff != 0) {
            return yearDiff < 0 ? -1 : 1;
        }

        // Compare months
        int monthDiff = this.month - other.month;
        if (monthDiff != 0) {
            return monthDiff < 0 ? -1 : 1;
        }

        // Compare days
        int dayDiff = this.day - other.day;
        if (dayDiff != 0) {
            return dayDiff < 0 ? -1 : 1;
        }

        return 0; // Same exact date
    }

    /**
     * Returns a string representation of the date in the format "MM/DD/YYYY".
     * @return a string representation of the date
     */
    @Override
    public String toString(){
        return month + "/" + day + "/" + year;
    }

    /**
     * Checks if this date is equal to another object.
     * @param obj the object to compare to
     * @return true if this date is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Date other = (Date) obj;
        return this.year == other.year &&
                this.month == other.month &&
                this.day == other.day;
    }

    /**
     * The main method serves as a testbed for the Date class.
     * It runs several test cases to validate the functionality of the Date class.
     * Each test case prints the date, the test objective, the result of the isValid() method, and the expected outcome.
     */
//    public static void main(String[] args) {
//        System.out.println("Date class Testbed main() running.\n");
//
//        Date d1 = new Date("0/15/2000");
//        System.out.println("Test #1: d1 = " + d1);
//        System.out.println("Test Objective: Validate month");
//        System.out.println("isValid() => " + d1.isValid() + " (expected false)");
//        System.out.println("Reason: month=0 is out of valid range (1..12).\n");
//
//        // 2) month=13 => invalid month range
//        Date d2 = new Date("13/15/2020");
//        System.out.println("Test #2: d2 = " + d2);
//        System.out.println("Test Objective: Validate month");
//        System.out.println("isValid() => " + d2.isValid() + " (expected false)");
//        System.out.println("Reason: month=13 is out of valid range (1..12).\n");
//
//        // 3) day=31 in April => invalid day for a 30-day month
//        Date d3 = new Date("4/31/2023");
//        System.out.println("Test #3: d3 = " + d3);
//        System.out.println("Test Objective: Validate day");
//        System.out.println("isValid() => " + d3.isValid() + " (expected false)");
//        System.out.println("Reason: April has 30 days, so day=31 is invalid.\n");
//
//        // 4) day=29 in 2023 => not a leap year => invalid date
//        Date d4 = new Date("2/29/2023");
//        System.out.println("Test #4: d4 = " + d4);
//        System.out.println("Test Objective: Validate leap year + day");
//        System.out.println("isValid() => " + d4.isValid() + " (expected false)");
//        System.out.println("Reason: 2023 is not a leap year, so Feb 29 is invalid.\n");
//
//        // 5) day=29 in 2020 => leap year => valid date
//        Date d5 = new Date("2/29/2020");
//        System.out.println("Test #5: d5 = " + d5);
//        System.out.println("Test Objective: Validate leap year + day");
//        System.out.println("isValid() => " + d5.isValid() + " (expected true)");
//        System.out.println("Reason: 2020 is a leap year, so Feb 29 is valid.\n");
//
//        // 6) 7/4/1999 => normal valid date
//        Date d6 = new Date("7/4/1999");
//        System.out.println("Test #6: d6 = " + d6);
//        System.out.println("Test Objective: Validate day");
//        System.out.println("isValid() => " + d6.isValid() + " (expected true)");
//        System.out.println("Reason: month=7, day=4, year=1999 => within valid ranges.\n");
//        System.out.println("End of Date testbed main().");
//    }
}