package cathay.united.bank.constant;

public final class ErrorMessageKeys {

    private ErrorMessageKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String CURRENCY_NOT_FOUND_BY_CODE = "error.currency.not.found.by.code";
    public static final String CURRENCY_NOT_FOUND_BY_ID = "error.currency.not.found.by.id";
    public static final String CURRENCY_ALREADY_EXISTS = "error.currency.already.exists";
    public static final String CURRENCY_CODE_ALREADY_EXISTS = "error.currency.code.already.exists";
    public static final String CANNOT_DELETE_CURRENCY_WITH_RATES = "error.currency.cannot.delete.with.rates";
    public static final String CURRENCY_DELETION_NOT_ALLOWED = "error.currency.deletion.not.allowed";
    public static final String START_DATE_AFTER_END_DATE = "error.start.date.after.end.date";
    public static final String VALIDATION_ERROR = "error.validation";
    public static final String CURRENCY_CODE_REQUIRE = "error.currency.code.required";
    public static final String CURRENCY_CODE_PATTEN_UNMATCH = "error.currency.code.pattern";
    public static final String CURRENCY_NAME_REQUIRE = "error.currency.name.required";
    public static final String CURRENCY_NAME_SIZE = "error.currency.name.size";
    public static final String SYMBOL_SIZE = "error.symbol.size";

}
