package rubank;

public enum Campus {
    NEW_BRUNSWICK("1"),
    NEWARK("2"),
    CAMDEN("3");

    private final String code;

    Campus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Campus fromInt(int code) {
        switch(code){
            case 1: return NEW_BRUNSWICK;
            case 2: return NEWARK;
            case 3: return CAMDEN;
            default: return null;
        }
    }
}
