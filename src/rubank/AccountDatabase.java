package rubank;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import rubank.util.List;
import rubank.util.Sort;
import rubank.util.Date;

/**
 * The AccountDatabase class represents a database of accounts. It is responsible for storing and managing accounts.
 * It also provides methods for adding, removing, and retrieving accounts from the database.
 * The database is implemented as an array of accounts.
 * @author Alison Chu, Byounguk Kim
 */
public class AccountDatabase extends List<Account> {
    private Archive archive;

    public AccountDatabase(){
        super();
        this.archive = new Archive();
    }

    public void printArchive(){
        if(archive.isEmpty()){
            System.out.println("Archive is empty.");
        } else {
            System.out.println("\n*List of closed accounts in the archive.");
            archive.print();
            System.out.println("*end of list.\n");
        }
    }

    public void printStatements(){
        Sort.sortByHolder(this);
        System.out.println("\n*Account statements by account holder.");
        Profile current = null;
        int countHolder = 0;
        for(int i=0; i<size(); i++){
            Account acct = get(i);
            Profile p = acct.getHolder();
            if(current == null || !current.equals(p)){
                current = p;
                countHolder++;
                System.out.printf("%d.%s\n", countHolder, current.toString());
            }
            System.out.println("\t[Account#] " + acct.getNumber());
            acct.statement();
            System.out.println();
        }
        System.out.println("*end of statements.\n");
    }

    public void printByBranch(){
        if(isEmpty()){
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by branch location (county, city).");
        Sort.sortByBranch(this);
        String currentCounty = null;
        for(int i=0; i<size(); i++){
            Account a = get(i);
            Branch b = a.getNumber().getBranch();
            String cty = b.getCounty();
            if(currentCounty == null || !cty.equalsIgnoreCase(currentCounty)){
                currentCounty = cty;
                System.out.println("County: " + cty);
            }
            System.out.println(a);
        }
        System.out.println("*end of list.\n");
    }

    public void printByHolder(){
        if(isEmpty()){
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by account holder and number.");
        Sort.sortByHolder(this);
        for(int i=0; i<size(); i++){
            Account a = get(i);
            System.out.println(a);
        }
        System.out.println("*end of list.\n");
    }

    public void printByType(){
        if(isEmpty()){
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by account type and number.");
        Sort.sortByType(this);
        AccountType currentType = null;
        for(int i=0; i<size(); i++){
            Account a = get(i);
            AccountType t = a.getNumber().getType();
            if(currentType == null || t != currentType){
                currentType = t;
                System.out.println("Account Type: " + t.name());
            }
            System.out.println(a);
        }
        System.out.println("*end of list.\n");
    }

    private Branch parseBranch(String input){
        for(Branch b : Branch.values()){
            if(b.name().equalsIgnoreCase(input)){
                return b;
            }
        }
        return null;
    }

    public int loadAccounts(File file) throws IOException {
        Scanner sc = new Scanner(file);
        int count = 0;

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split(",");

            AccountType type = AccountType.fromString(tokens[0]);
            Branch branch = parseBranch(tokens[1]);
            String fname = tokens[2];
            String lname = tokens[3];
            Date dob = new Date(tokens[4]);
            double initBal = Double.parseDouble(tokens[5]);

            Profile holder = new Profile(fname, lname, dob);
            AccountNumber acctNum = new AccountNumber(branch, type);

            Account newAcct = null;
            switch (type) {
                case CHECKING:
                    newAcct = new Checking(acctNum, holder, initBal);
                    break;
                case SAVINGS:
                    newAcct = new Savings(acctNum, holder, initBal);
                    break;
                case MONEY_MARKET:
                    newAcct = new MoneyMarket(acctNum, holder, initBal);
                    break;
                case COLLEGE_CHECKING:
                    int campusCode = Integer.parseInt(tokens[6]);
                    Campus c = Campus.fromInt(campusCode);
                    newAcct = new CollegeChecking(acctNum, holder, initBal, c);
                    break;
                case CD:
                    int term = Integer.parseInt(tokens[6]);
                    Date cdOpen = new Date(tokens[7]);
                    newAcct = new CertificateDeposit(acctNum, holder, initBal, term, cdOpen);
                    break;
                default:
                    continue;
            }
            add(newAcct);
            count++;
        }

        sc.close();
        // Return how many accounts were loaded (caller can print if desired).
        return count;
    }
    public int processActivities(File file) throws IOException {
        Scanner sc = new Scanner(file);
        int processedCount = 0;

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split(",");
            if (tokens.length < 5) continue;

            char dw = tokens[0].charAt(0); 
            String acctNumString = tokens[1];
            Date actDate = new Date(tokens[2]);
            Branch loc = parseBranch(tokens[3]);
            double amt = Double.parseDouble(tokens[4]);

            Account target = findByNumber(acctNumString);
            if (target == null) {
                continue;
            }

            if (dw == 'D') {
                target.deposit(amt);
            } else {
                target.withdraw(amt);
            }
            Activity activity = new Activity(actDate, loc, dw, amt, true);
            target.addActivity(activity);

            processedCount++;
        }

        sc.close();
        return processedCount;
    }

    public Account findByNumber(String acctNumString){
        for(int i=0; i<size(); i++){
            Account a = get(i);
            if(a.getNumber().toString().equals(acctNumString)){
                return a;
            }
        }
        return null;
    }

    /**
     * Returns the archive of closed accounts.
     * @return the archive
     */
    public Archive getArchive() {
        return archive;
    }

    public boolean isArchiveEmpty() {
        return archive.isEmpty();
    }

    public void closeAccount(Account a, Date closeDate){
        remove(a);
        archive.add(a, closeDate);
    }

    public boolean closeAllAccountsForHolder(Profile holder, Date closeDate) {
        boolean closedAny = false;
        for (int i = size() - 1; i >= 0; i--) {
            Account a = get(i);
            if (a.getHolder().equals(holder)) {
                remove(a);
                archive.add(a, closeDate);
                closedAny = true;
            }
        }
        return closedAny;
    }

    public boolean hasDuplicateType(Profile holder, AccountType type) {
        for (int i = 0; i < size(); i++) {
            Account a = get(i);
            if (a.getHolder().equals(holder) && a.getNumber().getType() == type) {
                return true;
            }
        }
        return false;
    }

}


