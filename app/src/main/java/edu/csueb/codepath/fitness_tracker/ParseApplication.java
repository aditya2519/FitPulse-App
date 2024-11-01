package edu.csueb.codepath.fitness_tracker;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Workout.class); //We will need to connect to the database of workout here.


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ylF6BSldJ3SBgU0PM9cxptNIHtASIWCNmSZzPRV7")
                .clientKey("y1f17iFDCKDAW7CZr5tfOw16gb9RP7dmuklfMQib")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
