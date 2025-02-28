package rubank;

import rubank.util.List;

public class Checking extends Account {

    private static final double ANNUAL_INTEREST = 0.015; // 1.5%
    private static final double MONTHLY_FEE = 15.0;
    private static final double WAIVE_THRESHOLD = 1000.0;

    public Checking(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
    }

    @Override
    public double interest() {
        return balance * (ANNUAL_INTEREST / 12.0);
    }

    @Override
    public double fee() {
        if (balance >= WAIVE_THRESHOLD) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }
}

