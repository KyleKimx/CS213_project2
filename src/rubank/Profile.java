package rubank;

import rubank.util.Date;

/**
 * The Profile class represents a user's profile with a first name, last name, and date of birth.
 * Implements Comparable to compare profiles based on last name, first name, and date of birth.
 * Provides methods to get the first name, last name, and date of birth.
 * Also includes methods to compare profiles, check equality, and return a string representation of the profile.
 * @author Alison Chu, Byounguk Kim
 */
public class Profile implements Comparable<Profile>{
    private String fname;
    private String lname;
    private Date dob;

    /**
     * Constructs a Profile with the specified first name, last name, and date of birth.
     * @param fname the first name
     * @param lname the last name
     * @param dob the date of birth
     */
    public Profile(String fname, String lname, Date dob) {
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    /**
     * Returns the first name.
     * @return the first name
     */
    public String getFirstName() {
        return fname;
    }

    /**
     * Returns the last name.
     * @return the last name
     */
    public String getLastName() {
        return lname;
    }

    /**
     * Returns the date of birth.
     * @return the date of birth
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Compares this profile with another profile based on last name, first name, and date of birth.
     * @param other the profile to compare to
     * @return a negative integer, zero, or a positive integer as this profile is less than, equal to, or greater than the specified profile
     */
    @Override
    public int compareTo(Profile other){
        int lastNameComparison = this.lname.compareToIgnoreCase(other.lname);
        if (lastNameComparison != 0) return lastNameComparison;
        int firstNameComparison = this.fname.compareToIgnoreCase(other.fname);
        if (firstNameComparison != 0) return firstNameComparison;
        return this.dob.compareTo(other.dob);
    }

    /**
     * Checks if this profile is equal to another object.
     * @param obj the object to compare to
     * @return true if this profile is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Profile)) return false;
        Profile other = (Profile) obj;
        return this.fname.equalsIgnoreCase(other.fname) &&
                this.lname.equalsIgnoreCase(other.lname) &&
                this.dob.compareTo(other.dob) == 0;
    }

    /**
     * Returns a string representation of the profile in the format "first name last name date of birth".
     * @return a string representation of the profile
     */
    @Override
    public String toString() {
        return String.format("%s %s %s", fname, lname, dob);
    }

    /**
     * The main method serves as a testbed for the Profile class.
     * It runs several test cases to validate the functionality of the Profile class.
     * Each test case prints the profiles, the test objective, the result of the compareTo() method, and the expected outcome.
     */
    public static void main(String[] args) {
        System.out.println("Profile class Testbed main() running.");

        Profile pA = new Profile("John", "Doe", new Date("2/19/2000"));
        Profile pB = new Profile("Kate", "Lindsey", new Date("8/31/2001"));
        Profile pC = new Profile("John", "Doe", new Date("7/8/1999"));
        Profile pD = new Profile("john", "doe", new Date("2/19/2000")); // same, ignoring case
        Profile pE = new Profile("April", "Doe", new Date("2/19/2000"));

        System.out.println("Profiles defined:");
        System.out.println("pA = " + pA);
        System.out.println("pB = " + pB);
        System.out.println("pC = " + pC);
        System.out.println("pD = " + pD);
        System.out.println("pE = " + pE);
        System.out.println();

        System.out.println("Test #1: Compare pA with pB (Doe vs Lindsey)");
        int r1 = pA.compareTo(pB);
        System.out.println("pA.compareTo(pB) => " + r1 + "  (expected -1, since 'Doe' < 'Lindsey')\n");

        System.out.println("Test #2: Compare pB with pA (Lindsey vs Doe)");
        int r2 = pB.compareTo(pA);
        System.out.println("pB.compareTo(pA) => " + r2 + "  (expected 1, 'Lindsey' > 'Doe')\n");

        System.out.println("Test #3: Compare pE with pA (Both 'Doe'; 'April' vs 'John')");
        int r3 = pE.compareTo(pA);
        System.out.println("pE.compareTo(pA) => " + r3 + "  (expected -1, 'April' < 'John')\n");

        System.out.println("Test #4: Compare pA with pE (Both 'Doe'; 'John' vs 'April')");
        int r4 = pA.compareTo(pE);
        System.out.println("pA.compareTo(pE) => " + r4 + "  (expected 1, 'John' > 'April')\n");

        System.out.println("Test #5: Compare pC with pA (Both 'John Doe'; DOB=7/8/1999 vs 2/19/2000)");
        int r5 = pC.compareTo(pA);
        System.out.println("pC.compareTo(pA) => " + r5 
                + "  (expected -1, '7/8/1999' < '2/19/2000')\n");

        System.out.println("Test #6: Compare pA with pC (Both 'John Doe'; DOB=2/19/2000 vs 7/8/1999)");
        int r6 = pA.compareTo(pC);
        System.out.println("pA.compareTo(pC) => " + r6 
                + "  (expected 1, '2/19/2000' > '7/8/1999')\n");

        System.out.println("Test #7: Compare pA with pD (Both 'John Doe' ignoring case, same DOB)");
        int r7 = pA.compareTo(pD);
        System.out.println("pA.compareTo(pD) => " + r7 + "  (expected 0, everything same ignoring case + same dob)\n");

        System.out.println("End of Profile testbed main().");
    }
}