package sunshake.apps.unbonvinapp;

import android.app.Application;

public class WineApp extends Application {
	//Default to empty string, that way the main activity will perform an open search on launch (when no search
	//has been entered by the user yet)
	private String mSearchText = "-1";

    public String getSearchText() {
        return mSearchText;
    }

    public void setSearchText(String searchText) {
        this.mSearchText = searchText;
    }
}
