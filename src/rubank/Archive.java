package rubank;

import rubank.util.Date;

/**
 * The Archive class represents an archive of closed accounts.
 * It is implemented as a singly linked list of AccountNode objects.
 * Provides methods to add an account to the archive, print the archive, and check if the archive is empty.
 * The archive is used to store accounts that have been closed.
 * @author Alison Chu, Byounguk Kim
 */
public class Archive {
    private AccountNode first;

    /**
     * Represents a node in the singly linked list of accounts.
     * Each node contains an account and a reference to the next node.
     */
    private static class AccountNode {
        private Account data;
        private AccountNode next;
        private Date close;

        /**
         * Constructs an AccountNode with the specified account and next node.
         * @param data the account data
         * @param next the next node in the list
         */
        public AccountNode(Account data, AccountNode next, Date close) {
            this.data = data;
            this.next = next;
            this.close = close;
        }
    }

    /**
     * Adds the specified account to the archive.
     * The account is added to the beginning of the linked list.
     * @param account the account to add
     */
    public void add(Account account, Date close){
        AccountNode newNode = new AccountNode(account, first, close);
        first = newNode;
    }

    /**
     * Prints all accounts in the archive.
     * Each account is printed on a new line.
     */
    public void print() {
        AccountNode current = first;
        while (current != null) {
            System.out.println(current.data.toString() + " Closed[" + current.close.toString() + "]");
            // If the account has activities, print them (remove the [ATM] tag for archived accounts)
            if(!current.data.getActivities().isEmpty()){
                System.out.println("\t[Activity]");
                for(Activity act : current.data.getActivities()){
                    String actStr = act.toString();
                    if(actStr.contains("[ATM]")){
                        actStr = actStr.replace("[ATM]", "");
                    }
                    System.out.println("\t\t" + actStr);
                }
            }
            current = current.next;
        }
    }

    /**
     * Checks if the archive is empty.
     * @return true if the archive is empty, false otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }
}