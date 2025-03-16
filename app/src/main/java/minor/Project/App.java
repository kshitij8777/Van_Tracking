package minor.Project;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Back4App
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("e5GtmnPWd72hvrwohVasca64Qx6pRdWkIUdlJx5a")
                .clientKey("ozrEPdRzciRpjZIYfNYvwLhgmXqNHTzgQLkggwRg")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
