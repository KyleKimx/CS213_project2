package rubank;

import rubank.util.Date;
import java.util.Calendar;

public class CertificateDeposit extends Savings {
    private int term;     
    private Date openDate; // the date the CD was opened
    private double penalty;

    private static final double RATE_3MO = 0.03;  // 3.0%
    private static final double RATE_6MO = 0.0325;// 3.25%
    private static final double RATE_9MO = 0.035; // 3.50%
    private static final double RATE_12MO = 0.04; // 4.00%

    public CertificateDeposit(AccountNumber number, Profile holder, double balance, int term, Date openDate) {
        super(number, holder, balance);
        this.term = term;
        this.openDate = openDate;
        this.isLoyal = false;
    }

    @Override
    public double fee() {
        return 0.0;
    }

    @Override
    public double interest() {
        double annualRate = getAnnualRate();
        return balance * (annualRate / 12.0);
    }

    public double getAnnualRate() {
        switch (term) {
            case 3:  return 0.03;   // 3.0%
            case 6:  return 0.0325; // 3.25%
            case 9:  return 0.035;  // 3.50%
            case 12: return 0.04;   // 4.00%
            default: return 0.0;
        }
    }

    public Date maturityDate(){
        Calendar c = Calendar.getInstance();
        c.set(openDate.getYear(), openDate.getMonth() - 1, openDate.getDay(), 0, 0, 0);
        c.add(Calendar.MONTH, term);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return new Date(m + "/" + d + "/" + y);
    }

    public double computeClosingInterest(Date closeDate) {
        int daysBetween = daysBetween(openDate, closeDate)+1;
        int maturityDays = term * 30; // Approximate maturity period

        double dailyRate;
        double interest = 0.0;
        double penalty = 0.0;

        if (daysBetween >= maturityDays) {
            // Fully matured -> Apply full interest rate
            dailyRate = getAnnualRate() / 365.0;
        } else {
            // Closed early -> Apply lower interest rate and penalty
            double months = (double) daysBetween / 30.0;
            if (months <= 6) {
                dailyRate = 0.03 / 365.0;  // 3% annual rate
            } else if (months <= 9) {
                dailyRate = 0.0325 / 365.0;  // 3.25% annual rate
            } else {
                dailyRate = 0.035 / 365.0;  // 3.5% annual rate
            }
            penalty = 0.10 * (balance * dailyRate * daysBetween); // 10% penalty on interest earned
        }

        // Compute final interest
        interest = balance * dailyRate * daysBetween;

        // Round values to 2 decimal places
        interest = Math.round(interest * 100.0) / 100.0;
        penalty = Math.round(penalty * 100.0) / 100.0;

        this.penalty = penalty; // Store penalty in object

        return interest;
    }



    public double getPenalty() {
        return this.penalty;
    }

    private int daysBetween(Date start, Date end) {
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        c1.set(start.getYear(), start.getMonth()-1, start.getDay());
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c2.set(end.getYear(), end.getMonth()-1, end.getDay());

        long diffMillis = c2.getTimeInMillis() - c1.getTimeInMillis();
        long days = diffMillis / (1000L * 60L * 60L * 24L);
        return (int) days;
    }



    public int getTerm() {
        return term;
    }

    public Date getOpenDate() {
        return openDate;
    }

    @Override
    public String toString() {
        this.isLoyal = false;
        return super.toString()
                + String.format("Term[%d] Date opened[%s] Maturity date[%s]",
                term, openDate.toString(), maturityDate().toString());
    }
}

