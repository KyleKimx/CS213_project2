package rubank;

import rubank.util.Date;
import rubank.util.List;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * The TransactionManager class manages transactions for accounts in the AccountDatabase.
 * It provides methods to open, close, deposit, and withdraw from accounts.
 * It also provides methods to print accounts in various orders.
 */
public class TransactionManager {
    private final AccountDatabase accountDatabase;

    // ---------------------------------------------
    // NEW: We fix a single date for console deposits/withdrawals:
//    private static final Date CONSOLE_TODAY = new Date("2/14/2025");
    // ---------------------------------------------

    /**
     * Constructs a TransactionManager with an empty AccountDatabase.
     */
    public TransactionManager() {
        accountDatabase = new AccountDatabase();
    }

    private void updateLoyalStatusForSavings(Profile holder) {
        boolean hasChecking = hasChecking(holder);
        for (int i = 0; i < accountDatabase.size(); i++) {
            Account a = accountDatabase.get(i);
            if (a.getHolder().equals(holder) &&
                    a.getNumber().getType() == AccountType.SAVINGS &&
                        !(a instanceof CertificateDeposit)) {
                ((Savings) a).isLoyal = hasChecking;
            }
        }
    }

    private boolean hasChecking(Profile holder) {
        for (int i = 0; i < accountDatabase.size(); i++) {
            Account a = accountDatabase.get(i);
            if (a.getHolder().equals(holder) && a.getNumber().getType() == AccountType.CHECKING) {
                return true;
            }
        }
        return false;
    }

    private Account createBasicAccount(AccountType acctType, Branch branch, Profile holder, double initDeposit) {
        AccountNumber num = new AccountNumber(branch, acctType);
        switch (acctType) {
            case CHECKING:
                return new Checking(num, holder, initDeposit);
            case SAVINGS:
                Savings s = new Savings(num, holder, initDeposit);
                if (hasChecking(holder)) {
                    s.isLoyal = true;
                }
                return s;
            case MONEY_MARKET:
                MoneyMarket mm = new MoneyMarket(num, holder, initDeposit);
                mm.isLoyal = (initDeposit >= 5000);
                return mm;
            default:
                return null;
        }
    }

    private void openAccount(String[] tokens) {
        if (tokens.length < 6) {
            System.out.println("Missing data for opening an account.");
            return;
        }

        String typeInput = tokens[1].toLowerCase();
        String branchInput = tokens[2];
        String fname = tokens[3];
        String lname = tokens[4];
        String dobString = tokens[5];

        AccountType acctType = parseAccountType(typeInput);
        if (acctType == null) return;

        Branch branch = parseBranch(branchInput);
        if (branch == null) return;

        Date dob = parseDate(dobString);
        if (dob == null) return;

        Profile holder = new Profile(fname, lname, dob);

        double initDep = 0.0;
        int term = 0;
        Date cdOpenDate = null;
        int campusCode = 0;

        switch (acctType) {
            case CHECKING:
            case SAVINGS:
            case MONEY_MARKET:
                if (tokens.length < 7) {
                    System.out.println("Missing data tokens for opening an account.");
                    return;
                }
                initDep = parseDeposit(tokens[6], acctType);
                if (initDep < 0) {
                    return;
                }
                break;

            case COLLEGE_CHECKING:
                if (tokens.length < 8) {
                    System.out.println("Missing data tokens for opening an account.");
                    return;
                }
                if (dob.getAge() > 23 || dob.getAge() < 18) {
                    System.out.println("Not eligible to open: " + dob + " over 24.");
                    return;
                }
                initDep = parseDeposit(tokens[6], AccountType.CHECKING);
                if (initDep < 0) {
                    return;
                }
                try {
                    campusCode = Integer.parseInt(tokens[7]);
                } catch (NumberFormatException e) {
                    System.out.println(tokens[7] + " - invalid campus code.");
                    return;
                }
                Campus campus = Campus.fromInt(campusCode);
                if (campus == null) {
                    System.out.println(campusCode + " is not a valid campus code (1,2,3).");
                    return;
                }
                break;

            case CD:
                if (tokens.length < 9) {
                    System.out.println("Missing deposit, term, or open date for certificate deposit.");
                    return;
                }
                initDep = parseDeposit(tokens[6], AccountType.CD);
                if (initDep < 0) {
                    return;
                }
                try {
                    term = Integer.parseInt(tokens[7]);
                } catch (NumberFormatException e) {
                    System.out.println(tokens[7] + " - invalid term (3,6,9,12).");
                    return;
                }
                cdOpenDate = parseDateNoAgeCheck(tokens[8]);
                if (cdOpenDate == null) {
                    return;
                }
                if (term != 3 && term != 6 && term != 9 && term != 12) {
                    System.out.println(term + " is not a valid term.");
                    return;
                }
                break;

            default:
                System.out.println(typeInput + " - invalid account type.");
                return;
        }

        if (acctType != AccountType.CD && accountDatabase.hasDuplicateType(holder, acctType)) {
            System.out.println(fname + " " + lname + " already has a " + acctType.name() + " account.");
            return;
        }

        Account newAcct;
        switch (acctType) {
            case CHECKING:
            case SAVINGS:
            case MONEY_MARKET:
                newAcct = createBasicAccount(acctType, branch, holder, initDep);
                break;

            case COLLEGE_CHECKING:
                AccountNumber ccNum = new AccountNumber(branch, AccountType.COLLEGE_CHECKING);
                Campus c = Campus.fromInt(campusCode);
                newAcct = new CollegeChecking(ccNum, holder, initDep, c);
                break;

            case CD:
                AccountNumber cdNum = new AccountNumber(branch, AccountType.CD);
                newAcct = new CertificateDeposit(cdNum, holder, initDep, term, cdOpenDate);
                break;

            default:
                return;
        }

        if (newAcct instanceof Checking) {
            updateLoyalStatusForSavings(holder);
        }

        accountDatabase.add(newAcct);
        System.out.printf("%s account %s has been opened.\n", acctType.name(), newAcct.getNumber().toString());
    }

    private void closeAccount(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Missing data for closing an account.");
            return;
        }
        Date closeDate;
        try {
            closeDate = new Date(tokens[1]);
            if (!closeDate.isValid()) {
                System.out.println("Close date invalid: " + tokens[1]);
                return;
            }
        } catch (Exception e) {
            System.out.println("Close date invalid: " + tokens[1]);
            return;
        }
        if (tokens.length == 3) {
            String acctNumString = tokens[2];
            Account a = accountDatabase.findByNumber(acctNumString);
            if (a == null) {
                System.out.println(acctNumString + " account does not exist.");
                return;
            }
            closeSingleAccount(a, closeDate);
        } else if (tokens.length == 5) {
            String fname = tokens[2];
            String lname = tokens[3];
            String strDob = tokens[4];
            Date d;
            try {
                d = new Date(strDob);
                if (!d.isValid()) {
                    System.out.println(fname + " " + lname + " " + strDob + " invalid DOB!");
                    return;
                }
            } catch (Exception e) {
                System.out.println("DOB invalid: " + strDob);
                return;
            }
            Profile p = new Profile(fname, lname, d);
            closeAccountsForHolder(p, closeDate);
        } else {
            System.out.println("Missing data for closing an account.");
        }
    }

    private void closeAccountsForHolder(Profile holder, Date closeDate) {
        boolean closedAny = false;
        for (int i = 0; i <accountDatabase.size() ; i++) {
            Account candidate = accountDatabase.get(i);
            if (candidate.getHolder().equals(holder)) {
                if (!closedAny) {
                    System.out.println("Closing accounts for " + holder);
                }
                closedAny = true;
                double interestEarned = 0.0;
                double penalty = 0.0;
                if (candidate instanceof CertificateDeposit) {
                    CertificateDeposit cd = (CertificateDeposit) candidate;
                    interestEarned = cd.computeClosingInterest(closeDate);
                    penalty = cd.getPenalty();
//                    if (interestEarned > 0) {
//                        candidate.deposit(interestEarned);
//                    }
//                    candidate.balance -= penalty;
                } else {
                    interestEarned = computePartialMonthInterest(candidate, closeDate);
//                    if (interestEarned > 0) {
//                        candidate.deposit(interestEarned);
//                    }
                }
                System.out.printf("--%s interest earned: $%.2f\n",
                        candidate.getNumber(), interestEarned);
                if (penalty > 0) {
                    System.out.printf("  [penalty] $%.2f\n", penalty);
                }
                accountDatabase.closeAccount(candidate, closeDate);
                if (candidate instanceof Checking) {
                    updateLoyalStatusForSavings(candidate.getHolder());
                }
                i--;
            }
        }
        if (closedAny) {
            System.out.println("All accounts for " + holder + " are closed and moved to archive.");
        } else {
            System.out.println(holder + " does not have any accounts in the database.");
        }
    }

    private void closeSingleAccount(Account acct, Date closeDate) {
        System.out.println("Closing account " + acct.getNumber());

        double interestEarned = 0.0;
        double penalty = 0.0;

        if (acct instanceof CertificateDeposit) {
            CertificateDeposit cd = (CertificateDeposit) acct;
            interestEarned = cd.computeClosingInterest(closeDate);
            penalty = cd.getPenalty();

//            if (interestEarned > 0) {
//                acct.deposit(interestEarned);
//            }
//            acct.balance -= penalty;
        } else {
            interestEarned = computePartialMonthInterest(acct, closeDate);
//            if (interestEarned > 0) {
//                acct.deposit(interestEarned);
//            }
        }

        System.out.printf("--interest earned: $%.2f\n", interestEarned);
        if (penalty > 0) {
            System.out.printf("  penalty $%.2f\n", penalty);
        }
        accountDatabase.closeAccount(acct, closeDate);
        if (acct instanceof Checking) {
            updateLoyalStatusForSavings(acct.getHolder());
        }

    }

    private double computePartialMonthInterest(Account acct, Date closeDate) {
        double annualRate;
        if (acct instanceof Checking) {
            annualRate = 0.015;
        } else if (acct instanceof CollegeChecking) {
            annualRate = 0.015;
        } else if (acct instanceof MoneyMarket) {
            MoneyMarket mm = (MoneyMarket) acct;
            annualRate = mm.isLoyal ? 0.0375 : 0.035;
        } else if (acct instanceof Savings) {
            Savings s = (Savings) acct;
            annualRate = s.isLoyal ? 0.0275 : 0.025;
        } else {
            annualRate = 0.0;
        }
        int dayOfMonth = closeDate.getDay();
        double dailyRate = annualRate / 365.0;
        return acct.getBalance() * dailyRate * dayOfMonth;
    }

    private void processActivities() {
        if (accountDatabase.isEmpty()) {
            System.out.println("ERROR: Account database is empty! Ensure accounts are loaded before processing activities.");
            return;
        }

        File f = new File("activities.txt");
        try {
            System.out.println("Processing \"" + f.getName() + "\"...");
            accountDatabase.processActivities(f);
            Scanner sc = new Scanner(f);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split(",");

            if (tokens.length < 5) {
                continue; 
            }

            String acctNumString = tokens[1];

            Account account = accountDatabase.findByNumber(acctNumString);
            if (account == null) {
                continue;
            }

            List<Activity> activities = account.getActivities();
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                if (activity.isAtm()) {
                    System.out.println(acctNumString + "::" + activity.toString());

                    break;
                }
            }
        }

        sc.close();
           
            System.out.println("Account activities in \"" + f.getName() + "\" processed.");
        } catch (IOException e) {
            System.out.println("Error processing activities file: " + e.getMessage());
        }
    }

    private void depositAccount(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Missing data tokens for the deposit.");
            return;
        }
        String accountNumberString = tokens[1];
        double amount;
        try {
            amount = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + tokens[2] + "\" - not a valid amount.");
            return;
        }
        if (amount <= 0) {
            System.out.println(amount + " - deposit amount cannot be 0 or negative.");
            return;
        }
        Account a = accountDatabase.findByNumber(accountNumberString);
        if (a == null) {
            System.out.println(accountNumberString + " does not exist.");
            return;
        }

        // ---------------------------------------------
        // CHANGED: Use CONSOLE_TODAY for deposit logs:
        accountDatabase.deposit(a.getNumber(), amount);
        a.addActivity(new Activity(new Date(), a.getNumber().getBranch(), 'D', amount, false));
        // ---------------------------------------------

        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.println("$" + df.format(amount) + " deposited to " + accountNumberString);

        if (a instanceof MoneyMarket) {
            ((MoneyMarket)a).isLoyal = (a.getBalance() >= 5000);
        }
    }

    private void withdrawAccount(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Missing data tokens for the withdrawal.");
            return;
        }
        String accountNumberString = tokens[1];
        double amount;
        try {
            amount = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + tokens[2] + "\" - not a valid amount.");
            return;
        }
        if (amount <= 0) {
            System.out.println(amount + " withdrawal amount cannot be 0 or negative.");
            return;
        }
        Account a = accountDatabase.findByNumber(accountNumberString);
        if (a == null) {
            System.out.println(accountNumberString + " does not exist.");
            return;
        }
        boolean isMoneyMarket = (a instanceof MoneyMarket);
        boolean success = accountDatabase.withdraw(a.getNumber(), amount);
        if (!success) {
            if (isMoneyMarket && a.getBalance() < 2000) {
                System.out.printf("%s balance below $2,000 - withdrawing $%,.2f - insufficient funds.\n",
                        accountNumberString, amount);
            } else {
                System.out.println(accountNumberString + " - insufficient funds.");
            }
        } else {
            // ---------------------------------------------
            // CHANGED: Use CONSOLE_TODAY for withdrawal logs:
            a.addActivity(new Activity(new Date(), a.getNumber().getBranch(), 'W', amount, false));
            // ---------------------------------------------
            if (isMoneyMarket && a.getBalance() < 2000) {
                System.out.printf("%s balance below $2,000 - $%,.2f withdrawn from %s\n",
                        accountNumberString, amount, accountNumberString);
            } else {
                System.out.printf("$%,.2f withdrawn from %s\n", amount, accountNumberString);
            }
        }
        if (a instanceof MoneyMarket) {
            ((MoneyMarket)a).isLoyal = (a.getBalance() >= 5000);
        }
    }

    private Profile parseProfile(String fname, String lname, String dobString) {
        Date dob = parseDate(dobString);
        if (dob == null) {
            return null;
        }
        return new Profile(fname, lname, dob);
    }

    private AccountType parseAccountType(String input) {
        String normalized = input.trim().toLowerCase();
        switch (normalized) {
            case "moneymarket":
                normalized = "money_market";
                break;
            case "college":
                normalized = "college_checking";
                break;
            case "certificate":
                normalized = "cd";
                break;
            default:
        }
        try {
            return AccountType.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(input + " - invalid account type.");
            return null;
        }
    }

    private Branch parseBranch(String input) {
        for (Branch b : Branch.values()) {
            if (b.name().equalsIgnoreCase(input)) {
                return b;
            }
        }
        System.out.println(input + " - invalid branch.");
        return null;
    }

    private Date parseDate(String input) {
        try {
            Date dob = new Date(input);

            if (!dob.isValid()) {
                System.out.println("DOB invalid: " + input + " not a valid calendar date!");
                return null;
            }
            if (dob.isFutureDate()) {
                System.out.println("DOB invalid: " + input + " cannot be today or a future day.");
                return null;
            }
            if (!dob.isAdult()) {
                System.out.println("Not eligible to open: " + input + " under 18.");
                return null;
            }
            return dob;
        } catch (IllegalArgumentException e) {
            System.out.println("DOB invalid: " + input + " not a valid calendar date!");
            return null;
        }
    }

    private Date parseDateNoAgeCheck(String input) {
        try {
            Date d = new Date(input);
            if (!d.isValid()) {
                System.out.println("Date invalid: " + input + " not a valid calendar date!");
                return null;
            }
            if (d.isFutureDate()) {
                System.out.println("Date invalid: " + input + " cannot be today or a future day.");
            }
            return d;
        } catch (IllegalArgumentException e) {
            System.out.println("Date invalid: " + input + " not a valid calendar date!");
            return null;
        }
    }

    private double parseDeposit(String input, AccountType accountType) {
        double deposit;
        try {
            deposit = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + input + "\" - not a valid amount.");
            return -1;
        }
        if (deposit <= 0) {
            System.out.println("Initial deposit cannot be 0 or negative.");
            return -1;
        }
        if (accountType == AccountType.MONEY_MARKET && deposit < 2000) {
            System.out.println("Minimum of $2,000 to open a Money Market account.");
            return -1;
        }
        if (accountType == AccountType.CD && deposit < 1000) {
            System.out.println("Minimum of $1,000 to open a Certificate Deposit account.");
            return -1;
        }
        return deposit;
    }

    public void run() {
        try {
            accountDatabase.loadAccounts(new File("accounts.txt"));
            System.out.println("Accounts in \"accounts.txt\" loaded to the database.");
        } catch (IOException e) {
            System.out.println("Could not load accounts from \"accounts.txt\": " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Transaction Manager is running.");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("\\s+");
            String command = tokens[0];

            if (command.equals("Q") && tokens.length == 1) {
                System.out.println("Transaction Manager is terminated.");
                break;
            }

            switch (command) {
                case "O":
                    openAccount(tokens);
                    break;
                case "C":
                    closeAccount(tokens);
                    break;
                case "D":
                    depositAccount(tokens);
                    break;
                case "W":
                    withdrawAccount(tokens);
                    break;
                case "A":
                    if (tokens.length == 1) {
                        processActivities();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                case "P":
                    System.out.println("P command is deprecated!");
                    break;
                case "PA":
                    if (tokens.length == 1) {
                        printArchive();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                case "PB":
                    if (tokens.length == 1) {
                        printByBranch();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                case "PH":
                    if (tokens.length == 1) {
                        printByHolder();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                case "PT":
                    if (tokens.length == 1) {
                        printByType();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                case "PS":
                    if (tokens.length == 1) {
                        printStatements();
                    } else {
                        System.out.println("Invalid command!");
                    }
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
        scanner.close();
    }

    private void printArchive() {
        if (accountDatabase.isArchiveEmpty()) {
            System.out.println("Archive is empty.");
        } else {
            accountDatabase.printArchive();
        }
    }

    private void printByBranch() {
        if (accountDatabase.isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        accountDatabase.printByBranch();
    }

    private void printByHolder() {
        if (accountDatabase.isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        accountDatabase.printByHolder();
    }

    private void printByType() {
        if (accountDatabase.isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        accountDatabase.printByType();
    }

    private void printStatements() {
        if (accountDatabase.isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        accountDatabase.printStatements();
    }
}
