package sunshake.apps.unbonvinapp;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private WineColorScheme colorScheme = null;
	private DatabaseHandler dbHandler;
	private static String TAG = "[Search Activity]";
	private Typeface mNameFont;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		colorScheme = new WineColorScheme(this);
		mNameFont = Typeface.createFromAsset(getAssets(), "fonts/startlng.ttf");
        
		this.dbHandler = new DatabaseHandler(this);
        try {
        	dbHandler.createAndUpdateDatabase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 	try {
	 		dbHandler.openDatabaseRead();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}
		
	}
	View.OnClickListener onWineClick(final LinearLayout info)  {
	    return new View.OnClickListener() {
	        public void onClick(View v) {
	        	info.setVisibility(info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
	        }
	    };
	}
	
	/*private TextView addTextView(View parent, String txt, float textSize, int[] padding, int textColor,
			LinearLayout.LayoutParams params){
		
		TextView tv = new TextView(this);
		if(params != null) tv.setLayoutParams(params);
		tv.setText(txt);
		tv.setTextSize(textSize);
		tv.setTextColor(textColor);
		tv.setPadding(padding[0], padding[1], padding[2], padding[3]);
		parent.addView(tv);
		
		return tv;
		
	}*/
	
	private void populateView(List<Wine> wines){
		LinearLayout layout = (LinearLayout) findViewById(R.id.container);
		layout.removeAllViews();
		
		//Get the width of the display to setup the layout
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int padding = 40;
		int thirdWidth = (width/3) - padding;
		
		int mainTextCol = Color.WHITE;
		int subTextCol = Color.parseColor("#e7e7e7");
		
		for(Wine w : wines){
			
			int col = colorScheme.getColor(w.getType());
			
			LinearLayout wineContainer = new LinearLayout(this);
			wineContainer.setOrientation(LinearLayout.VERTICAL);
			wineContainer.setClickable(true);
			wineContainer.setBackgroundColor(col);
			layout.addView(wineContainer);
			
			//Wine title
			TextView name = new TextView(this);
			name.setText(w.getName());
			name.setTypeface(mNameFont);
        	name.setTextSize((float)30);
        	name.setTextColor(mainTextCol);
        	name.setPadding(20, 20, 20, 10);
        	wineContainer.addView(name);
        	
        	//Wine short info
        	LinearLayout shortInfo = new LinearLayout(this);
        	shortInfo.setOrientation(LinearLayout.HORIZONTAL);
        	wineContainer.addView(shortInfo);
        	
        	TextView year = new TextView(this);
        	year.setText("År: " + w.getYear());
        	year.setTextSize((float)22);
        	year.setTextColor(subTextCol);
        	year.setPadding(20, 0, 20, 20);
        	year.setLayoutParams(new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        	
        	TextView stars = new TextView(this);
        	stars.setLayoutParams(new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        	stars.setText(w.getStars() + " stjerner");
        	stars.setTextSize((float)22);
        	stars.setTextColor(subTextCol);
        	stars.setPadding(20, 0, 20, 20);
        	
        	
        	TextView price = new TextView(this);
        	price.setText(w.getPrice() + " Kr");
        	price.setTextSize((float)22);
        	price.setTextColor(subTextCol);
        	price.setPadding(20, 0, 20, 20);
        	price.setLayoutParams(new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        	
        	
        	shortInfo.addView(year);
        	shortInfo.addView(stars);
        	shortInfo.addView(price);
        	
        	////////// Hidden content //////////////////////
        	LinearLayout info = new LinearLayout(this);
        	info.setOrientation(LinearLayout.VERTICAL);
        	info.setVisibility(View.GONE);
        	wineContainer.addView(info);
        	
        	TextView type = new TextView(this);
        	type.setText("Type: " + w.getType());
        	type.setTextSize((float)22);
        	type.setTextColor(subTextCol);
        	type.setPadding(20, 0, 20, 20);
        	info.addView(type);
        	
        	if(w.getCountry() != null){
	        	TextView country = new TextView(this);
	        	country.setText("Land: " + w.getCountry());
	        	country.setTextSize((float)22);
	        	country.setTextColor(subTextCol);
	        	country.setPadding(20, 0, 20, 20);
	        	info.addView(country);
        	}
        	
        	if(w.getRegion() != null){
	        	TextView region = new TextView(this);
	        	region.setText("Region: " + w.getRegion());
	        	region.setTextSize((float)22);
	        	region.setTextColor(subTextCol);
	        	region.setPadding(20, 0, 20, 20);
	        	info.addView(region);
        	}
        	
        	String desc = "";
        	String aroma = w.getAroma();
        	String taste = w.getTaste();
        	String concl = w.getConclusion();
        	if(aroma != null) desc += aroma + ". ";
        	if(taste != null) desc += taste + ". ";
        	if(concl != null) desc += concl + ". ";
        	
        	TextView description = new TextView(this);
        	description.setText("Beskrivelse: " + desc);
        	description.setTextSize((float)20);
        	description.setTextColor(subTextCol);
        	description.setPadding(20, 0, 20, 20);
        	info.addView(description);
        	
        	wineContainer.setOnClickListener(onWineClick(info));
        	
        	/////Horizontal rule between the wines
        	View ruler = new View(this); 
        	ruler.setBackgroundColor(Color.parseColor("#ffffff"));
        	layout.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 2));
        
        }
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. 
			NavUtils.navigateUpFromSameTask(this);
			return true;
		
		case R.id.action_settings:
			//openSettings();
			return true;
		case R.id.action_search:
			onSearchRequested();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onSearchRequested() {
	     Bundle appData = new Bundle();
	     //Data to pass to search activity
	    // appData.putBoolean(SearchActivity.JARGON, true);
	     startSearch(null, false, appData, false);
	     return true;
	 }
	
	protected void onResume() {
	    super.onResume();

	    String searchText = ((WineApp)this.getApplicationContext()).getSearchText();
	    //The default value of the searchText at application launch (before search has been made)
	    if(searchText.compareTo("-1") == 0){
	    	Log.d(TAG, "This was first time starting app");
	    }
	    else{
	    	setContentView(R.layout.activity_search);
	    	search(searchText);
	    }
	}
	
	private void search(String query){
		
		if(query.isEmpty()){
			Log.d(TAG, "The query was empty");
			List<Wine> wines = dbHandler.getWinesAlphabetically();
			populateView(wines);
		}
		//If the query matches any number 4 times in a row (year)
		else if(query.matches("(\\d{4})")){
			Log.d(TAG, "The query is for a year");
			List<Wine> wines = dbHandler.getWinesByYear(query);
			populateView(wines);
		}
		//Three-digit numbers are considered search for price
		else if(query.matches("(\\d{3})")){
			Log.d(TAG, "The query is for a price");
			List<Wine> wines = dbHandler.getWinesByPriceAsc(query);
			populateView(wines);
		}
		//TODO: Fix a dictionary or something that makes sure every type in db is here
		else if(query.compareTo("Rød") == 0 || query.compareTo("Hvit") == 0 ||
				query.compareTo("Champagne") == 0 || query.compareTo("Rose") == 0 ||
				query.compareTo("Dessertvin") == 0 || query.compareTo("Søtvin") == 0 ||
				query.compareTo("Sherry") == 0 || query.compareTo("Akevitt") == 0 ||
				query.compareTo("Musserende") == 0 || query.compareTo("Tokaji") == 0 ||
				query.compareTo("Portvin") == 0 || query.compareTo("Portvin") == 0 ||
				query.compareTo("Hetvin") == 0 || query.compareTo("Cognac") == 0 ||
				query.compareTo("Oransje") == 0 || query.compareTo("Madeira") == 0 ||
				query.compareTo("Rom") == 0){
			
			Log.d(TAG, "The query was for type");
			List<Wine> wines = dbHandler.getWinesByType(query);
			populateView(wines);
		}
		else if(query.matches("\\w")){
			Log.d(TAG, "The query was for a name");
			List<Wine> wines = dbHandler.getWinesByName(query);
			populateView(wines);
		}
	}
	
	
}

