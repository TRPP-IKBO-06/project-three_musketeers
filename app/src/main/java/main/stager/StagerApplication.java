package main.stager;

import android.app.Application;

import main.stager.utils.LocaleController;

public class StagerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocaleController.init(this);
    }
}