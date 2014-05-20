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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private WineColorScheme colorScheme = null;
	private CustomStringList wineTypes, wineCountries;
	private DatabaseHandler dbHandler;
	private static final String TAG = "[Search Activity]";
	private Typeface mNameFont;
	private RadioButton alphabeticalRB, typeRB, priceRB;
	private RadioGroup filterRadioGroup;
	private String lastQuery = "";
	private RadioButton lastChosenRB = null;
	private LinearLayout mResultLayout;
	private LinearLayout mMessageContainer;
	private int mThirdWidth, mTitleColor, mSubtitleColor, mBgColor;
	private float mTitleFontSize, mSubtitleFontSize;
	private int[] mTitlePadding = {20, 20, 20, 10};
	private int[] mSubPadding = {20, 0, 20, 20};
	private int[] mMsgPadding = {20, 20, 20, 20};
	
	//private List<RadioButton> radioButtons = new ArrayList<RadioButton>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
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
	 	
	 	init();
	}
	
	private void init(){
		mResultLayout = (LinearLayout) findViewById(R.id.results);
		mMessageContainer = (LinearLayout) findViewById(R.id.messages);
		//Get the width of the display to setup the layout
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int padding = 40;
		mThirdWidth = (width/3) - padding;
		mTitleColor = Color.WHITE;
		mSubtitleColor = Color.parseColor("#D7D7D7"); //Grayer white for the non-title text
		mBgColor = colorScheme.getColor("Background");
		mTitleFontSize = 24;
		mSubtitleFontSize = 22;
		
		wineTypes = dbHandler.getListOfProperty("type");
	 	wineCountries = dbHandler.getListOfProperty("country");
	 	
	 	alphabeticalRB = (RadioButton) findViewById(R.id.alphabetical);
	 	typeRB = (RadioButton) findViewById(R.id.type);
	 	priceRB = (RadioButton) findViewById(R.id.price);
	 	filterRadioGroup = (RadioGroup) findViewById(R.id.radioFilter);
	 	
	 	lastChosenRB =  alphabeticalRB;
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
		mResultLayout.removeAllViews();
		mMessageContainer.removeAllViews();
		
		if(lastQuery == "" || lastQuery == "-1"){
			String title = "Velkommen til UnBonVinApp!";
			String msg = "Klikk på forstørrelsesglasset på oppgavelinja og skriv inn navn på vin, type, land, eller pris for å søke." +
					" Du kan og bruke snarveiene 'Nyeste' og 'Best & billigst'.";
			addTextView(mMessageContainer, title, mTitleFontSize, mMsgPadding, mTitleColor, null, mNameFont);
			addTextView(mMessageContainer, msg, mSubtitleFontSize, mMsgPadding, mSubtitleColor, null, null);
		}
		else{
			addTextView(mMessageContainer, "Du søkte etter '" + lastQuery + "', sortert etter " + lastChosenRB.getTag() + ".  " 
					+ wines.size() + " viner matchet søket.", mTitleFontSize, mMsgPadding, mTitleColor, null, mNameFont);
			if(wines.size() == 0){
				addTextView(mMessageContainer, "Beklager, ingen viner ble funnet. Prøv igjen med et annet søkeord!", mTitleFontSize, mMsgPadding, mTitleColor, null, mNameFont);
				mResultLayout.setVisibility(View.GONE);
			}
			else{
				mResultLayout.setVisibility(View.VISIBLE);
			}
		}
		
		for(Wine w : wines){
			
			/******************* Layouts ***************************/
			//Main container, one per wine
			LinearLayout wineContainer = new LinearLayout(this);
			wineContainer.setOrientation(LinearLayout.VERTICAL);
			wineContainer.setClickable(true);
			wineContainer.setBackgroundColor(mBgColor);
			mResultLayout.addView(wineContainer);
			
			//Wine short info container
        	LinearLayout shortInfo = new LinearLayout(this);
        	shortInfo.setOrientation(LinearLayout.HORIZONTAL);
        	
        	// Hidden content - pops up when user clicks to display more info
         	LinearLayout info = new LinearLayout(this);
         	info.setOrientation(LinearLayout.VERTICAL);
         	info.setVisibility(View.GONE);
			
        	/****************** Content data **********************/
			//Wine title
			addTextView(wineContainer, w.getName(), mTitleFontSize, mTitlePadding, mTitleColor, null, mNameFont);
			
			//Must add it after the title is added, else the layout is in the wrong order
			wineContainer.addView(shortInfo);
			
        	String y = w.getYear();
        	if(y == null){ y = "?"; }
        	addTextView(shortInfo, "År: " + y, mSubtitleFontSize, mSubPadding, mSubtitleColor, new LinearLayout.LayoutParams(
        			mThirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	addTextView(shortInfo, w.getStars() + " stjerner", mSubtitleFontSize, mSubPadding, mSubtitleColor, new LinearLayout.LayoutParams(
        			mThirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	addTextView(shortInfo, w.getPrice() + " Kr", mSubtitleFontSize, mSubPadding, mSubtitleColor, new LinearLayout.LayoutParams(
        			mThirdWidth, LinearLayout.LayoutParams.WRAP_CONTENT), null);
        	
        	//Must add after the other layouts, to make it display beneath the others
        	wineContainer.addView(info);
        	
        	addTextView(info, "Type: " + w.getType(), mSubtitleFontSize, mSubPadding, mSubtitleColor, null, null);
        	
        	if(w.getCountry() != null){
        		addTextView(info, "Land: " + w.getCountry(), mSubtitleFontSize, mSubPadding, mSubtitleColor, null, null);
        	}
        	
        	if(w.getRegion() != null){
        		addTextView(info, "Region: " + w.getRegion(), mSubtitleFontSize, mSubPadding, mSubtitleColor, null, null);
        	}
        	
        	String desc = "";
        	String aroma = w.getAroma();
        	String taste = w.getTaste();
        	String concl = w.getConclusion();
        	if(aroma != null) desc += aroma + ". ";
        	if(taste != null) desc += taste + ". ";
        	if(concl != null) desc += concl + ". ";
        	
        	addTextView(info, "Beskrivelse: " + desc, (float)20, mSubPadding, mSubtitleColor, null, null);
        	
        	wineContainer.setOnClickListener(onWineClick(info));
        	
        	/////Horizontal rule between the wines
        	int borderCol = colorScheme.getColor(w.getType());
        	View ruler = new View(this); 
        	ruler.setBackgroundColor(borderCol);
        	mResultLayout.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 4));
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
	     // get selected radio button from radioGroup
	     int selectedId = filterRadioGroup.getCheckedRadioButtonId();
	     Log.d(TAG, "Selected radio btn: " + Integer.toString(selectedId));
  		
  		 //and save it for when activity resumes after search
	     ((WineApp)this.getApplicationContext()).setRadioButtonCheckedID(selectedId);
	     startSearch(null, false, appData, false);
	     return true;
	 }
	
	protected void onResume() {
	    super.onResume();
	    
	    int radioButtonId = ((WineApp)this.getApplicationContext()).getLastCheckedRadioButtonID();
	    // find the radiobutton by returned id
        
        
	    String searchText = ((WineApp)this.getApplicationContext()).getSearchText();
	    
	    //The default value of the searchText at application launch (before search has been made)
	    if(searchText.compareTo("-1") == 0){
	    	Log.d(TAG, "This was first time starting app");
	    	//Send in lastQuery which by default is empty, 
	    	//thus fetching all wines by alphabetical order at startup
	    	handleSearch(lastQuery);
	    }
	    else{
	    	//setContentView(R.layout.activity_search);
	    	handleSearch(searchText);
	    	RadioButton selectBtn = (RadioButton) findViewById(radioButtonId);
	    	if(selectBtn != null){
	        	selectBtn.performClick();
	        	//selectBtn.toggle();
	        }
	    }
	    lastQuery = searchText;
	}
	
	public void requestBestCheapWines(View v){
		List<Wine> wines = dbHandler.getBestCheapWines(getFilterParam());
		populateView(wines);
	}
	
	public void requestNewestWines(View v){
		List<Wine> wines = dbHandler.getNewestWines();
		populateView(wines);
	}
	
	//Checks what sort of search filter should be applied
	private String getFilterParam(){
		if(alphabeticalRB.isChecked()){
			lastChosenRB = alphabeticalRB;
			return "alpha";
		}
		else if(typeRB.isChecked()){
			lastChosenRB = typeRB;
			return "type";
		}
		else if(priceRB.isChecked()){
			lastChosenRB = priceRB;
			return "price";
		}
		return "";
	}
	
	private void handleSearch(String query){
		
		if(query.isEmpty()){
			Log.d(TAG, "The query was empty");
			//Hardcoding in param for now until I make checkboxes
			List<Wine> wines = dbHandler.getWines(getFilterParam());
			populateView(wines);
		}
		//If the query matches any number 4 times in a row (year)
		else if(query.matches("(\\d{4})")){
			Log.d(TAG, "The query is for a year");
			List<Wine> wines = dbHandler.getWinesByYear(query, getFilterParam());
			populateView(wines);
		}
		//Three- and two-digit numbers are considered search for price
		else if(query.matches("(\\d{3})") || query.matches("(\\d{2})")){
			Log.d(TAG, "The query is for a price");
			List<Wine> wines = dbHandler.getWinesByPrice(query, getFilterParam());
			populateView(wines);
		}
		else if(wineTypes.contains(query)){
			Log.d(TAG, "The query was for type");
			List<Wine> wines = dbHandler.getWinesByType(query, getFilterParam());
			populateView(wines);
		}
		else if(wineCountries.contains(query)){
			Log.d(TAG, "The query is for a country");
			List<Wine> wines = dbHandler.getWinesByCountry(query, getFilterParam());
			populateView(wines);
		}
		else if(query.matches(".{1,}")){
			Log.d(TAG, "The query was for a name");
			List<Wine> wines = dbHandler.getWinesByName(query, getFilterParam());
			populateView(wines);
		}
	}
	
}

