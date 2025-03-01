package rubank;

import rubank.util.Date;

import java.text.DecimalFormat;

public class Activity implements Comparable<Activity> {
    private Date date;
    private Branch location;
    private char type;
    private double amount;
    private boolean atm; //true if from text file

    public Activity(Date date, Branch location, char type, double amount, boolean atm) {
        this.date = date;
        this.location = location;
        this.type = type;
        this.amount = amount;
        this.atm = atm;
    }

    public Date getDate(){
        return date;
    }

    public Branch getLocation() {
        return location;
    }

    public char getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isAtm() {
        return atm;
    }

    @Override
    public int compareTo(Activity other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Build something like:  2/1/2025::WARREN[ATM]::withdrawal::$100.00
        StringBuilder sb = new StringBuilder();

        // date => e.g. "2/1/2025"
        sb.append(date.toString());
        sb.append("::");

        // branch => e.g. "WARREN"
        sb.append(location.name());

        // If this activity came from the .txt (atm == true), append [ATM]
        if (atm) {
            sb.append("[ATM]");
        }
        sb.append("::");

        // Either "deposit" or "withdrawal"
        if (type == 'D') {
            sb.append("deposit::$");
        } else {
            sb.append("withdrawal::$");
        }

        // Format the amount with commas
        sb.append(df.format(amount));

        return sb.toString();
    }

}