package rubank.util;

import rubank.Account;
import rubank.Branch;
import rubank.Profile;
import rubank.AccountType;

/**
 * Utility class for sorting lists of Accounts in different ways:
 * by branch, by holder, by type, etc.
 * Methods are static and can modify a List<Account> in-place.
 * @author ...
 */
public class Sort {

    /**
     * Sorts the accounts by branch (county, city), in-place.
     * @param list the list of accounts
     */
    public static void sortByBranch(List<Account> list) {
        // Simple selection sort (in-place)
        int n = list.size();
        for(int i = 0; i < n-1; i++){
            int minIndex = i;
            for(int j = i+1; j < n; j++){
                Branch b1 = list.get(j).getNumber().getBranch();
                Branch b2 = list.get(minIndex).getNumber().getBranch();
                // compare counties ignoring case
                int countyComparison = b1.getCounty().compareToIgnoreCase(b2.getCounty());
                if(countyComparison < 0){
                    minIndex = j;
                } else if(countyComparison == 0){
                    // compare city name ignoring case
                    int cityComparison = b1.name().compareToIgnoreCase(b2.name());
                    if(cityComparison < 0){
                        minIndex = j;
                    }
                }
            }
            if(minIndex != i){
                Account temp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, temp);
            }
        }
    }

    /**
     * Sort by holder (last name, first name, DOB), then by account number if needed.
     * @param list the list
     */
    public static void sortByHolder(List<Account> list) {
        int n = list.size();
        for(int i=0; i<n-1; i++){
            int minIndex = i;
            for(int j=i+1; j<n; j++){
                Profile p1 = list.get(j).getHolder();
                Profile p2 = list.get(minIndex).getHolder();
                int cmp = p1.compareTo(p2);  // by last, first, dob
                if(cmp < 0){
                    minIndex = j;
                } else if(cmp == 0){
                    // tie-break by accountNumber
                    Account a1 = list.get(j);
                    Account a2 = list.get(minIndex);
                    if(a1.getNumber().compareTo(a2.getNumber()) < 0){
                        minIndex = j;
                    }
                }
            }
            if(minIndex != i){
                Account tmp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, tmp);
            }
        }
    }

    /**
     * Sort by account type, then by account number if needed.
     * The order of AccountTypes is presumably CHECKING < SAVINGS < MONEY_MARKET < COLLEGE_CHECKING < CD,
     * or you can define the order by comparing the enum's ordinal or code.
     * @param list the list
     */
    public static void sortByType(List<Account> list) {
        int n = list.size();
        for(int i=0; i<n-1; i++){
            int minIndex = i;
            for(int j=i+1; j<n; j++){
                AccountType t1 = list.get(j).getNumber().getType();
                AccountType t2 = list.get(minIndex).getNumber().getType();
                // compare by ordinal or define your own order
                int cmp = t1.compareTo(t2);
                if(cmp < 0){
                    minIndex = j;
                } else if(cmp == 0){
                    // tie-break by accountNumber
                    Account a1 = list.get(j);
                    Account a2 = list.get(minIndex);
                    if(a1.getNumber().compareTo(a2.getNumber()) < 0){
                        minIndex = j;
                    }
                }
            }
            if(minIndex != i){
                Account tmp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, tmp);
            }
        }
    }
}

