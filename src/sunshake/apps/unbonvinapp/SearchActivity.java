package sunshake.apps.unbonvinapp;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private WineColorScheme colorScheme = null;
	private CustomStringList wineTypes, wineCountries;
	private DatabaseHandler dbHandler;
	private static final String TAG = "[Search Activity]";
	private Typeface mNameFont;
	private RadioButton alphabeticalCB, typeCB, priceCB, bestcheapCB, newestCB;
	private RadioGroup filterRadioGroup;
	private String lastQuery = "";
	private RadioButton lastChosenRB = null;
	//private List<RadioButton> radioButtons = new ArrayList<RadioButton>();
	
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
	 	wineTypes = dbHandler.getListOfProperty("type");
	 	wineCountries = dbHandler.getListOfProperty("country");
	 	
	 	alphabeticalCB = (RadioButton) findViewById(R.id.alphabetical);
	 	typeCB = (RadioButton) findViewById(R.id.type);
	 	priceCB = (RadioButton) findViewById(R.id.price);
	 	bestcheapCB = (RadioButton) findViewById(R.id.bestcheap);
	 	newestCB = (RadioButton) findViewById(R.id.newest);
	 	filterRadioGroup = (RadioGroup) findViewById(R.id.radioFilter);
	 	
	}
	
	View.OnClickListener onWineClick(final LinearLayout info)  {
	    return new View.OnClickListener() {
	        public void onClick(View v) {
	        	info.setVisibility(info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
	        }
	    };
	}
	
	private TextView addTextView(LinearLayout parent, String txt, float textSize, int[] padding, int textColor,
			LinearLayout.LayoutParams params, Typeface typeface){
		
		TextView tv = new TextView(this);
		if(params != null) tv.setLayoutParams(params);
		if(typeface != null) tv.setTypeface(typeface);
		tv.setText(txt);
		tv.setTextSize(textSize);
		tv.setTextColor(textColor);
		tv.setPadding(padding[0], padding[1], padding[2], padding[3]);
		parent.addView(tv);
		
		return tv;
	}
	
	private void populateView(List<Wine> wines){
		LinearLayout layout = (LinearLayout) findViewById(R.id.container);
		//layout.removeAllViews();
		
		//Get the width of the display to setup the layout
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int padding = 40;
		int thirdWidth = (width/3) - padding;
		
		int titleColor = Color.WHITE;
		int subColor = Color.parseColor("#D7D7D7"); //Grayer white for the non-title text
		int bgColor = colorScheme.getColor("Background");
		float titleFontSize = 24;
		float subFontSize = 22;
		int[] titlePadding = {20, 20, 20, 10};
		int[] subPadding = {20, 0, 20, 20};
		int[] msgPadding = {20, 20, 20, 20};
		
		if(wines.size() == 0){
			LinearLayout messageContainer = new LinearLayout(this);
			messageContainer.setOrientation(LinearLayout.VERTICAL);
			messageContainer.setBackgroundColor(bgColor);
			layout.addView(messageContainer);
			addTextView(messageContainer, "Beklager, ingen viner ble funnet. Prøv igjen med et annet søkeord!", titleFontSize, msgPadding, titleColor, null, mNameFont);
		}
		
		for(Wine w : wines){
			
			/******************* Layouts ***************************/
			//Main container, one per wine
			LinearLayout wineContainer = new LinearLayout(this);
			wineContainer.setOrientation(LinearLayout.VERTICAL);
			wineContainer.setClickable(true);
			wineContainer.setBackgroundColor(bgColor);
			layout.addView(wineContainer);
			
			//Wine short info container
        	LinearLayout shortInfo = new LinearLayout(this);
        	shortInfo.setOrientation(LinearLayout.HORIZONTAL);
        	
        	// Hidden content - pops up when user clicks to display more info
         	LinearLayout info = new LinearLayout(this);
         	info.setOrientation(LinearLayout.VERTICAL);
         	info.setVisibility(View.GONE);
			
        	/****************** Content data **********************/
			//Wine title
			TextView name = addTextView(wineContainer, w.getName(), titleFontSize, titlePadding, titleColor, null, mNameFont);
			
			//Must add it after the title is added, else the layout is in the wrong order
			wineContainer.addView(shortInfo);
			
        	String y = w.getYear();
        	if(y == null){ y = "?"; }
        	addTextView(shortInfo, "År: " + y, subFontSize, subPadding, subColor, new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	addTextView(shortInfo, w.getStars() + " stjerner", subFontSize, subPadding, subColor, new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	addTextView(shortInfo, w.getPrice() + " Kr", subFontSize, subPadding, subColor, new LinearLayout.LayoutParams(
        			thirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	
        	//Must add after the other layouts, to make it display beneath the others
        	wineContainer.addView(info);
        	
        	addTextView(info, "Type: " + w.getType(), subFontSize, subPadding, subColor, null, null);
        	
        	if(w.getCountry() != null){
        		addTextView(info, "Land: " + w.getCountry(), subFontSize, subPadding, subColor, null, null);
        	}
        	
        	if(w.getRegion() != null){
        		addTextView(info, "Region: " + w.getRegion(), subFontSize, subPadding, subColor, null, null);
        	}
        	
        	String desc = "";
        	String aroma = w.getAroma();
        	String taste = w.getTaste();
        	String concl = w.getConclusion();
        	if(aroma != null) desc += aroma + ". ";
        	if(taste != null) desc += taste + ". ";
        	if(concl != null) desc += concl + ". ";
        	
        	addTextView(info, "Beskrivelse: " + desc, (float)20, subPadding, subColor, null, null);
        	
        	wineContainer.setOnClickListener(onWineClick(info));
        	
        	/////Horizontal rule between the wines
        	int borderCol = colorScheme.getColor(w.getType());
        	View ruler = new View(this); 
        	ruler.setBackgroundColor(borderCol);
        	layout.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 4));
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
	    getMenuInflater().inflate(R.menu.action_bar_menu, menu);
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
	    	handleSearch(searchText);
	    }
	}
	
	private void handleSearch(String query){
		
		if(query.isEmpty()){
			Log.d(TAG, "The query was empty");
			//Hardcoding in param for now until I make checkboxes
			List<Wine> wines = dbHandler.getWines("alpha");
			populateView(wines);
		}
		//If the query matches any number 4 times in a row (year)
		else if(query.matches("(\\d{4})")){
			Log.d(TAG, "The query is for a year");
			List<Wine> wines = dbHandler.getWinesByYear(query, "alpha");
			populateView(wines);
		}
		//Three- and two-digit numbers are considered search for price
		else if(query.matches("(\\d{3})") || query.matches("(\\d{2})")){
			Log.d(TAG, "The query is for a price");
			List<Wine> wines = dbHandler.getWinesByPrice(query, "");
			populateView(wines);
		}
		else if(wineTypes.contains(query)){
			Log.d(TAG, "The query was for type");
			List<Wine> wines = dbHandler.getWinesByType(query, "alpha");
			populateView(wines);
		}
		else if(wineCountries.contains(query)){
			Log.d(TAG, "The query is for a country");
			List<Wine> wines = dbHandler.getWinesByCountry(query, "");
			populateView(wines);
		}
		else if(query.matches(".{1,}")){
			Log.d(TAG, "The query was for a name");
			List<Wine> wines = dbHandler.getWinesByName(query, "price");
			populateView(wines);
		}
	}
}

