package rubank;

/**
 * College Checking extends Checking.
 * Requirements:
 *  - same 1.5% annual interest as Checking
 *  - no monthly fee
 *  - campus code: 1=New Brunswick, 2=Newark, 3=Camden
 *  - must be 24 or younger to open
 *  
 * NOTE: The no‐fee rule overrides the standard Checking fee logic.
 * So we override fee() to return 0, but keep the same interest() from Checking.
 * 
 * We add a campus field with a Campus enum.
 */
public class CollegeChecking extends Checking {
    private Campus campus;

    private static final double COLLEGE_MONTHLY_FEE = 0.0;

    public CollegeChecking(AccountNumber number, Profile holder, double balance, Campus campus) {
        super(number, holder, balance);
        this.campus = campus;
    }

    @Override
    public double fee() {
        return COLLEGE_MONTHLY_FEE;
    }

    public Campus getCampus() {
        return campus;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Campus[%s]", campus.name());
    }
}
