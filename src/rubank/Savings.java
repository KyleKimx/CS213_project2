package rubank;

/**
 * Regular Savings extends Account.
 * Requirements:
 *  - 2.5% annual interest normally
 *  - If loyal, +0.25% => 2.75% annual
 *  - $25 monthly fee if balance < $500, else 0
 *  - loyal is set if the holder also has a Checking, etc. (logic in TransactionManager or after load)
 */
public class Savings extends Account {
    protected boolean isLoyal; // used for the +0.25% if loyal

    private static final double BASE_ANNUAL = 0.025;     
    private static final double LOYAL_ANNUAL = 0.0275;   

    private static final double MONTHLY_FEE = 25.0;
    private static final double WAIVE_THRESHOLD = 500.0;

    public Savings(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
        this.isLoyal = false;
    }

    @Override
    public double interest() {
        double rate = isLoyal ? LOYAL_ANNUAL : BASE_ANNUAL;
        return balance * (rate / 12.0);
    }

    @Override
    public double fee() {
        if (balance >= WAIVE_THRESHOLD) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }

    @Override
    public String toString() {
        // add "[LOYAL]" if isLoyal == true
        String loyalTag = isLoyal ? "[LOYAL]" : "";
        return super.toString() + " " + loyalTag;
    }
}
