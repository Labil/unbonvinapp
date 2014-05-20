package sunshake.apps.unbonvinapp;

import android.app.Application;
import android.widget.RadioButton;

public class WineApp extends Application {
	//Default to empty string, that way the main activity will perform an open search on launch (when no search
	//has been entered by the user yet)
	private String mSearchText = "-1";
	private int mRadioButtonCheckedID = 0;

    public String getSearchText() {
        return mSearchText;
    }
    
    public int getLastCheckedRadioButtonID() {
        return mRadioButtonCheckedID;
    }

    public void setSearchText(String searchText) {
        this.mSearchText = searchText;
    }
    
    public void setRadioButtonCheckedID(int id) {
        this.mRadioButtonCheckedID = id;
    }
}
