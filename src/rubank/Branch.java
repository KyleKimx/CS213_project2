package rubank;

/**
 * Branch enumeration presents a branch of the bank.
 * Each branch has a unique branch code, county, and zip code.
 * Provides methods to get the branch code, county, and zip code.
 * @author Alison Chu, Byounguk Kim
 */
public enum Branch {
    EDISON("100", "Middlesex", "08817"),
    BRIDGEWATER("200", "Somerset", "08807"),
    PRINCETON("300", "Mercer", "08542"),
    PISCATAWAY("400", "Middlesex", "08854"),
    WARREN("500", "Somerset", "07057");

    private final String branchCode;
    private final String county;
    private final String zip;

    /**
     * Constructs a Branch with the specified branch code, county, and zip code.
     * @param branchCode the unique code of the branch
     * @param county the county where the branch is located
     * @param zip the zip code of the branch
     */
    Branch(String branchCode, String county, String zip) {
        this.branchCode = branchCode;
        this.county = county;
        this.zip = zip;
    }

    /**
     * Returns the branch code.
     * @return the branch code
     */
    public String getBranchCode(){
        return branchCode;
    }

    /**
     * Returns the county where the branch is located.
     * @return the county
     */
    public String getCounty() {
        return county;
    }

    /**
     * Returns the zip code of the branch.
     * @return the zip code
     */
    public String getZip(){
        return zip; 
    }

}