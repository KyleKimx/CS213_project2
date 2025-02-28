package rubank;

/**
 * AccountType enumeration represents the type of bank account.
 * Each account type has a unique code.
 * The account types are CHECKING, SAVINGS, and MONEY_MARKET.
 * Provides methods to get the code of the account type and to convert a string to an AccountType.
 * @author Alison Chu, Byounguk Kim
 */
public enum AccountType {
    CHECKING("01"),
    SAVINGS("02"),
    MONEY_MARKET("03"),
    COLLEGE_CHECKING("04"),
    CD("05");

    private final String code;

    /**
     * Constructs an AccountType with the specified code.
     * @param code the code of the account type
     */
    AccountType(String code) {
        this.code = code;
    }

    /**
     * Returns the code of the account type.
     * @return the code of the account type
     */
    public String getCode() {
        return code;
    }

    /**
     * Converts a string to an AccountType.
     * The string is compared case-insensitively to the names of the account types.
     * @param input the string to convert
     * @return the corresponding AccountType, or null if no match is found
     */
    public static AccountType fromString(String input) {
        if(input == null) return null;
        String normalized = input.trim().toLowerCase();
        switch(normalized){
            case "checking":        return CHECKING;
            case "savings":         return SAVINGS;
            case "moneymarket":     return MONEY_MARKET;
            case "money_market":    return MONEY_MARKET;
            case "college":
            case "collegechecking":
            case "college_checking":
                return COLLEGE_CHECKING;
            case "certificate":
            case "cd":
            case "certificate_deposit":
                return CD;
            default:
                return null;
        }
    }
}
