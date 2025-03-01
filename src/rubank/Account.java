package rubank;

import rubank.util.List;
import java.text.DecimalFormat;

/**
 * The Account class represents a bank account.
 * An account has a unique account number, a holder, and a balance.
 * An account can be compared to another account based on the account number.
 * @author Alison Chu, Byounguk Kim
 */
public abstract class Account implements Comparable<Account> {
    protected AccountNumber number;
    protected Profile holder;
    protected double balance;
    protected List<Activity> activities;

    /**
     * Constructs an account with the given account number, holder, and balance.
     * @param number the account number
     * @param holder the account holder
     * @param balance the account balance
     */
    public Account(AccountNumber number, Profile holder, double balance){
        this.number = number;
        this.holder = holder;
        this.balance = balance;
        this.activities = new List<>();
    }

    public final void statement() {
        printActivities();
        double interest = interest();
        double fee = fee();
        printInterestFee(interest, fee);
        printBalance(interest, fee);
    }
    
    public void addActivity(Activity activity){
        activities.add(activity);
    }

    public abstract double interest();
    public abstract double fee();

    protected void printActivities() {
        if (activities.isEmpty()) {
            System.out.println("\t[Activity] No transactions");
        } else {
            System.out.println("\t[Activity]");
            for (Activity act : activities) {
                // "deposit" or "withdrawal"
                String wOrD = (act.getType() == 'D') ? "deposit" : "withdrawal";

                // Build something like:
                // "2/5/2025::PRINCETON[ATM]::withdrawal::$500.00"
                System.out.printf("\t\t%s::%s%s::%s::$%,.2f\n",
                        act.getDate().toString(),        // e.g. "2/5/2025"
                        act.getLocation().name(),       // e.g. "PRINCETON"
                        act.isAtm() ? "[ATM]" : "",     // only "[ATM]" if 'atm == true'
                        wOrD,                           // "deposit" / "withdrawal"
                        act.getAmount());               // e.g. 500 => "500.00"
            }
        }
    }

    protected void printInterestFee(double intr, double f){
        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.printf("\t[interest] $%s [Fee] $%s\n", 
                df.format(intr), df.format(f));
    }

    protected void printBalance(double intr, double f){
        balance = balance + intr - f; // post them
        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.println("\t[Balance] $" + df.format(balance));
    }

    /**
     * Deposits the given amount into the account.
     * @param amount the amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
    }

    /**
     * Withdraws the given amount from the account.
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     */
    public void withdraw(double amount) {
        if (balance < amount) {
            throw new IllegalArgumentException();
        }
        balance -= amount;
    }

    /**
     * Returns the account number of this account.
     * @return the account number
     */
    public AccountNumber getNumber(){
        return number;
    }

    /**
     * Returns the holder of this account.
     * @return the account holder
     */
    public Profile getHolder(){
        return holder;
    }

    /**
     * Returns the balance of this account.
     * @return the account balance
     */
    public double getBalance(){
        return balance;
    }
    
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Compares this account with the specified account for order based on account numbers.
     * @param other the account to be compared
     * @return a negative integer, zero, or a positive integer as this account is less than, equal to,
     * or greater than the specified account.
     */
    @Override
    public int compareTo(Account other){
        return this.number.compareTo(other.number);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two accounts are equal if they have the same account number and holder.
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false;

        Account other = (Account) obj;
        return this.holder.equals(other.holder) && this.number.getType() == other.number.getType();
    }

    /**
     * Returns a string representation of this account.
     * The string representation consists of the account number, holder, balance, and branch.
     * @return a string representation of this account
     */
    @Override
    public String toString() {
        return String.format("Account#[%s] Holder[%s] Balance[$%.2f] Branch [%s]",
            number.toString(), 
            holder.toString(),
            balance,
            number.getBranch().name() 
        );
    }

}

