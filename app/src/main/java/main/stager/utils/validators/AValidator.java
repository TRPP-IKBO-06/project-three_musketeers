package main.stager.utils.validators;

import android.content.Context;

import lombok.Getter;

public abstract class AValidator<T> {
    @Getter protected String message;
    protected Context context;

    protected abstract boolean isValid(T item);

    public AValidator(Context context) {
        this.context = context.getApplicationContext();
    }
}
