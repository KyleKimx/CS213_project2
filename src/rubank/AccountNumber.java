package rubank;

import rubank.Random; 

/**
 * The AccountNumber class represents an account number for a bank account.
 * The account number consists of a branch code, account type code, and a 4-digit serial number.
 * The account number can be compared to another account number.
 * The account type can be downgraded from MONEY_MARKET to SAVINGS.
 * The account number is generated using a fixed-seed random number generator.
 * @author Alison Chu, Byounguk Kim
 */
public class AccountNumber implements Comparable<AccountNumber>{
    private static final int SEED = 9999;
    private static final Random RANDOM = new Random(SEED);
    private Branch branch;
    private AccountType type;
    private String number;

    /**
     * Constructs an AccountNumber with the specified branch and account type.
     * @param branch the branch of the account
     * @param type the type of the account
     */
    public AccountNumber(Branch branch, AccountType type){
        this.branch = branch;
        this.type = type;
        this.number = generateAccountNumber();
    }

    /**
     * Generates a unique account number consisting of the branch code, account type code, and a 4-digit serial number.
     * @return the generated account number
     */
    private String generateAccountNumber() {
        int serialNumber = RANDOM.nextInt(SEED); 
        return String.format("%s%s%04d", branch.getBranchCode(), type.getCode(), serialNumber);
    }

    /**
     * Returns the branch of the account.
     * @return the branch of the account
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Returns the type of the account.
     * @return the type of the account
     */
    public AccountType getType() {
        return type;
    }

    /**
     * Compares this account number with the specified account number for order.
     * @param other the other account number to compare
     * @return a negative integer, zero, or a positive integer as this account number is less than, equal to,
     * or greater than the specified account number.
     */
    @Override
    public int compareTo(AccountNumber other){
        return this.number.compareTo(other.number);
    }

    /**
     * Returns a string representation of the account number.
     * @return a string representation of the account number
     */
    @Override
    public String toString() {
        return number; 
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two account numbers are considered equal if they have the same number.
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 

        AccountNumber other = (AccountNumber) obj;
        return this.number.equals(other.number); 
    }

}
