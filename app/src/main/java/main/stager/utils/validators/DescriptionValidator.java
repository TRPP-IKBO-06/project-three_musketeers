package main.stager.utils.validators;

import android.content.Context;

import main.stager.R;

public class DescriptionValidator extends AValidator<String> {
    private static final int MAX_LENGTH = 255;

    public DescriptionValidator(Context context) {
        super(context);
    }

    @Override
    public boolean isValid(String description) {
        if (description == null) {
            message = context.getString(R.string.Validator_ErrorMessage_NullDescription);
            return false;
        }

        if (description.length() > MAX_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MaxLengthDescription);
            return false;
        }

        return true;
    }
}