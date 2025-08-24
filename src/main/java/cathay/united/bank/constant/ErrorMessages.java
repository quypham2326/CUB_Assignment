package cathay.united.bank.constant;

public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String CURRENCY_NOT_FOUND_BY_CODE =
            "Currency not found with code: '%s'";

    public static final String CURRENCY_NOT_FOUND_BY_ID =
            "Currency not found with ID: %s";

    public static final String CURRENCY_ALREADY_EXISTS =
            "Currency with code '%s' already exists";

    public static final String CURRENCY_CODE_ALREADY_EXISTS =
            "Cannot update currency: code '%s' is already used by another currency";

    public static final String CANNOT_DELETE_CURRENCY_WITH_RATES =
            "Cannot delete currency '%s' because it has existing exchange rates. Please remove all exchange rates " +
            "before deleting this currency";

    public static final String CURRENCY_DELETION_NOT_ALLOWED =
            "Currency cannot be deleted due to business constraints";

    public static final String START_DATE_AFTER_END_DATE =
            "End date must be greater than or equal to start date";
}
