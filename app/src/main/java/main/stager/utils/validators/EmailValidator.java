package main.stager.utils.validators;

import android.content.Context;

import main.stager.R;
import main.stager.utils.Utilits;

public class EmailValidator extends AValidator<String> {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 60;
    private static final String EMAIL_REGEX = ".+@.+";

    public EmailValidator(Context context) {
        super(context);
    }

    @Override
    public boolean isValid(String email) {
        if (Utilits.isNullOrEmpty(email)) {
            message = context.getString(R.string.Validator_ErrorMessage_NoEmail);
            return false;
        }

        if (email.length() < MIN_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MinLengthEmail);
            return false;
        }

        if (email.length() > MAX_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MaxLengthEmail);
            return false;
        }

        if (!email.matches(EMAIL_REGEX)) {
            message = context.getString(R.string.Validator_ErrorMessage_IncorrectEmail);
            return false;
        }

        return true;
    }
}
