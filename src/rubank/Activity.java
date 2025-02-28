package rubank;

import rubank.util.Date;

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
        String atmTag = atm ? "[ATM]" : "";
        String depositOrWithdrawal = (type == 'D') ? "deposit" : "withdrawal";
        return String.format("%s::%s%s::%s::$%,.2f",
                date.toString(),
                location.name(),
                atmTag,
                depositOrWithdrawal,
                amount);
    }

}