package rubank;

import rubank.util.Date;
import java.util.Calendar;

public class CertificateDeposit extends Savings {
    private int term;     
    private Date openDate; // the date the CD was opened

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
        double annualRate = pickAnnualRate(term);
        return balance * (annualRate / 12.0);
    }

    private double pickAnnualRate(int t) {
        switch(t){
            case 3:  return RATE_3MO;
            case 6:  return RATE_6MO;
            case 9:  return RATE_9MO;
            case 12: return RATE_12MO;
            default: return RATE_3MO; // fallback?? not sure if need
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

