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
        int daysBetween = daysBetween(openDate, closeDate);
        int maturityDays = term * 30; // approximate
        if (daysBetween >= maturityDays) {
            // closed AFTER maturity => interest = bal * annualRate/365 * daysBetween
            double dailyRate = getAnnualRate() / 365.0;
            penalty = 0.0;
            return balance * dailyRate * daysBetween;
        } else {
            // closed BEFORE maturity => special stepped rate
            double months = (double) daysBetween / 30.0;
            double earlyAnnual;
            if (months <= 6) {
                earlyAnnual = 0.03;
            } else if (months <= 9) {
                earlyAnnual = 0.0325;
            } else {
                earlyAnnual = 0.035;
            }
            double dailyRate = earlyAnnual / 365.0;
            double interest = balance * dailyRate * daysBetween;
            penalty = 0.10 * interest;
            return interest;
        }
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
        return super.toString() 
             + String.format(" Term[%d] Date opened[%s] Maturity date[%s]",
                             term, openDate, maturityDate());
    }
}

