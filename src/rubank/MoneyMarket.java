package rubank;

/**
 * MoneyMarket extends Savings.
 * Requirements:
 *  - 3.5% annual, +0.25% if loyal => 3.75%
 *  - $25 monthly fee if balance < $2000
 *  - up to 3 free withdrawals per statement cycle; if >3, add $10 fee
 *  - 'withdrawal' field tracks how many times we've withdrawn in the cycle
 *  - no downgrading to regular savings in Project 2
 */
public class MoneyMarket extends Savings {
    private int withdrawal; // how many times we've withdrawn so far

    private static final double BASE_ANNUAL = 0.035;     // 3.5%
    private static final double LOYAL_ANNUAL = 0.0375;   // 3.75%

    private static final double BALANCE_THRESHOLD = 2000.0;
    private static final double MONTHLY_FEE = 25.0;
    private static final int FREE_WITHDRAWALS = 3;
    private static final double EXTRA_WITHDRAW_FEE = 10.0;

    public MoneyMarket(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
        this.withdrawal = 0; // start with 0
    }

    @Override
    public boolean withdraw(double amount) {
        if (balance < amount) {
            return false; // insufficient
        }
        // otherwise proceed
        balance -= amount;
        withdrawal++;
        return true;
    }

    @Override
    public double interest() {
        double rate = isLoyal ? LOYAL_ANNUAL : BASE_ANNUAL;
        return balance * (rate / 12.0);
    }

    @Override
    public double fee() {
        double f = 0.0;
        // base monthly fee if below $2,000
        if (balance < BALANCE_THRESHOLD) {
            f += MONTHLY_FEE;
        }
        // extra $10 if withdrew more than 3 times
        if (withdrawal > FREE_WITHDRAWALS) {
            f += EXTRA_WITHDRAW_FEE;
        }
        return f;
    }

    @Override
    public String toString() {
        String loyalTag = isLoyal ? "[LOYAL]" : "";
        return super.toString() + String.format(" Withdrawal[%d] %s", withdrawal, loyalTag);
    }
}
