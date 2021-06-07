package main.stager.utils.validators;

import android.content.Context;

import main.stager.R;
import main.stager.utils.Utilits;

public class PasswordValidator extends AValidator<String> {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 100;

    public PasswordValidator(Context context) {
        super(context);
    }

    @Override
    public boolean isValid(String password) {
        if (Utilits.isNullOrEmpty(password)) {
            message = context.getString(R.string.Validator_ErrorMessage_NoPassword);
            return false;
        }

        if (password.length() < MIN_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MinLengthPassword);
            return false;
        }

        if (password.length() > MAX_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MaxLengthPassword);
            return false;
        }

        return true;
    }
}
