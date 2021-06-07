package main.stager.utils.validators;

import android.content.Context;

import main.stager.R;
import main.stager.utils.Utilits;

public class ActionNameValidator extends AValidator<String>{
    private static final int MAX_LENGTH = 24;
    private static final String NAME_REGEX = "[\\s\\wА-яёЁ]*";
    private static final String SPACES_REGEX = "\\s{2,}";

    public ActionNameValidator(Context context) {
        super(context);
    }

    @Override
    public boolean isValid(String name) {
        if (Utilits.isNullOrEmpty(name)) {
            message = context.getString(R.string.Validator_ErrorMessage_NoName);
            return false;
        }

        if (name.length() > MAX_LENGTH) {
            message = context.getString(R.string.Validator_ErrorMessage_MaxLengthActionName);
            return false;
        }

        if (!name.matches(NAME_REGEX)) {
            message = context.getString(R.string.Validator_ErrorMessage_WrongName);
            return false;
        }

        if (name.matches(SPACES_REGEX)) {
            message = context.getString(R.string.Validator_ErrorMessage_Spaces);
            return false;
        }

        return true;
    }
}