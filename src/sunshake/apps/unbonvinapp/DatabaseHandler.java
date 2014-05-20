package sunshake.apps.unbonvinapp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sunshake.apps.unbonvinapp.SearchActivity.OnLoadListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private SQLiteDatabase mDatabase; 
    private final Context mContext;
    private int mUpdateVersion = 0;
	
	private static final String TAG = "DatabaseHandler";
    private static String DB_PATH = "";
    private static String DB_COMPLETE_PATH = "";	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "wineDB.sqlite";
	//The two tables in the database
	private static final String TABLE_WINES =  "wines";
	private static final String TABLE_UPDATE = "update_version";
	//Keys in database
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_TYPE = "type";
	private static final String KEY_YEAR = "year";
	private static final String KEY_GRAPE = "grape";
	private static final String KEY_COUNTRY = "country";
	private static final String KEY_REGION = "region";
	private static final String KEY_SCORE = "score";
	private static final String KEY_PRODNUM = "productnum";
	private static final String KEY_SELECTION = "selection";
	private static final String KEY_PRICE = "price";
	private static final String KEY_STARS = "stars";
	private static final String KEY_SWEETNESS = "sweetness";
	private static final String KEY_AROMA = "aroma";
	private static final String KEY_TASTE = "taste";
	private static final String KEY_CONCLUSION = "conclusion";
	private static final String KEY_SOURCE = "source";
	private static final String KEY_SOURCEDATE = "sourcedate";
	private static final String KEY_NOTE = "note";
	private static final String KEY_VERSION = "updateVersion";
	private static final String NEW_THRESHOLD = "940"; //Arbitrary for now
	private static final String CHEAP_PRICE = "100";
	
	private static final String KEY_VERSION_IN_TABLE_UPDATE = "updateNum";
	//private static final int RESULT_LIMIT = 100;

	/**
     * Constructor
     * Takes and keeps a reference of the passed context of the activity that
     * created it in order to access to the application assets and resources.
     * @param context
     */
	public DatabaseHandler(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.mContext = context;
		//Android's default system path of the application database.
		if(android.os.Build.VERSION.SDK_INT >= 17){
	       DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
	    }
	    else
	    {
	       //DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	       DB_PATH = context.getFilesDir().getPath();
	    }
		DB_COMPLETE_PATH = DB_PATH + DB_NAME;
		
	}
	
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createAndUpdateDatabase() throws IOException{
    	boolean dbExist = checkDatabase();
    	
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
    		//An empty database will be created into the default system path of application
    		this.getReadableDatabase().close();
        	try {
    			copyDatabase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
        	}
    	}
    	initUpdateVersion();
    	
    	//Checking what version local db is at, and querying the server for wines that has a newer (higher) version
	 	Log.d(TAG, "Value of db version: " + String.valueOf(mUpdateVersion));
	 	new FetchDatabaseFromServer().execute("http://plainbrain.net/unbonvinapp/queryLastUpdated.php?updateVersion="
	 			+ String.valueOf(mUpdateVersion));
	 	
	 	//Resetting db update version to 0 - hack for dev
	 	//updateVersion();
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		checkDB = SQLiteDatabase.openDatabase(DB_COMPLETE_PATH, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    	//to reset database once
    	//return false;
    }
 
    /**
     * Copies database from local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     * */
    private void copyDatabase() throws IOException{
    	//Open your local db as the input stream
    	InputStream input = mContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_COMPLETE_PATH;
 
    	//Open the empty db as the output stream
    	OutputStream output = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = input.read(buffer))>0){
    		output.write(buffer, 0, length);
    	}
    	//Close the streams
    	output.flush();
    	output.close();
    	input.close();
    }
 
    public void openDatabaseRead() throws SQLException{
    	mDatabase = SQLiteDatabase.openDatabase(DB_COMPLETE_PATH, null, SQLiteDatabase.OPEN_READONLY);
 
    }
    public void openDatabaseWrite() throws SQLException{
    	mDatabase = SQLiteDatabase.openDatabase(DB_COMPLETE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
    @Override
	public synchronized void close() {
    	    if(mDatabase != null)
    		    mDatabase.close();
    	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
	
		//Database creation must wait as this runs before everything is setup (I think)
		//Call methods from the calling activity
	}
	
	//Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Drop older table if existed
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_WINES);
		//create tables again
		//onCreate(db);
	}
	
	private void initUpdateVersion(){ this.mUpdateVersion = queryUpdateVersion(); }
	
	public int getUpdateVersion(){ return this.mUpdateVersion; }
	
	public String getSortQry(String param){
		if(param == "alpha") return " ORDER BY " + KEY_NAME + " ASC "; //default asc
		else if(param == "type") return " ORDER BY " + KEY_TYPE + " ASC ";
		else if(param == "price") return " ORDER BY " + KEY_PRICE + " ASC ";
		else if(param == "new") return " AND " + KEY_ID + " >= " + NEW_THRESHOLD + " ORDER BY " + KEY_ID + " DESC";
		else if(param == "bestcheap") return " AND " + KEY_PRICE + " <= " + CHEAP_PRICE + " AND " + KEY_STARS + " =6";
		else return " ";
	}
	
	public CustomStringList getListOfProperty(String columnName){
		String qry = "SELECT " + columnName + " FROM " + TABLE_WINES + " GROUP BY " + columnName;
		CustomStringList typeList = new CustomStringList();
		openDatabaseRead();
		Cursor cursor = mDatabase.rawQuery(qry, null);
		
		if(cursor.moveToFirst()){
			do{
				String s = cursor.getString(0);
				typeList.add(s);
			}while(cursor.moveToNext());
		}
		else Log.d(TAG, "No result when querying wine properties!");
		close();
		return typeList;
	}
	 
	// Get wine by name LIKE 'name%' -> in list.
	public List<Wine> getWinesByName(String name, String param) {
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_NAME + " LIKE '" + name + "%' COLLATE NOCASE" + getSortQry(param);
		return queryWines(qry);
	}
	
	public List<Wine> getWinesByPrice(String price, String param){
		String sortQry = getSortQry(param);
		if(param == "price"){
			sortQry = "";
		}
		//I can't get it to return wines where value is equal to query, for example query for 100 returns only wines below 100 in price.
		//hack solution
		int iPrice = Integer.parseInt(price) + 1;
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_PRICE + "<=" + iPrice + sortQry;
		return queryWines(qry);
	}
	
	public List<Wine> getWinesByType(String type, String param){
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_TYPE + "=\"" + type +
				"\" COLLATE NOCASE" + getSortQry(param);
		return queryWines(qry);
	}
	
	public List<Wine> getWinesByCountry(String country, String param){
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_COUNTRY +
				"=\"" + country + "\" COLLATE NOCASE" + getSortQry(param);
		return queryWines(qry);
	}
	
	public List<Wine> getWines(String param){
		String qry = "SELECT * FROM " + TABLE_WINES + getSortQry(param);
		return queryWines(qry);
	}
	
	public List<Wine> getNewestWines(){
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_ID + ">=" + NEW_THRESHOLD +
				" ORDER BY " + KEY_ID + " DESC";
		return queryWines(qry);
	}
	
	public List<Wine> getBestCheapWines(){
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_PRICE +
				"<=" + CHEAP_PRICE + " AND " + KEY_STARS + "=6";
		return queryWines(qry);
	}
	
	public List<Wine> getWinesByYear(String year, String param){
		String qry = "SELECT * FROM " + TABLE_WINES + " WHERE " + KEY_YEAR + "=" + year + getSortQry(param);
		return queryWines(qry);
	}
	
	private List<Wine> queryWines(String qry){
		List<Wine> wineList = new ArrayList<Wine>();
		openDatabaseRead();
		Cursor cursor = mDatabase.rawQuery(qry, null);
		
		if(cursor.moveToFirst()){
			do{
				Wine wine = new Wine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
						 cursor.getString(5), cursor.getString(6), cursor.getString(7),
						 cursor.getString(8), cursor.getString(10), cursor.getString(11), cursor.getString(13), 
						 cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17));
				wineList.add(wine);
			}while(cursor.moveToNext());
		}
		else Log.d(TAG, "No result found by that query.");
		close();
		return wineList;
	}
	 
	// Getting wines Count
	public int getWinesCount() {
		String countQuery = "SELECT * FROM " + TABLE_WINES;
		openDatabaseRead();
		Cursor cursor = mDatabase.rawQuery(countQuery, null);
		Integer count = cursor.getCount();
		cursor.close();
		
		return count;
	}
	
	/* Gets the database current update version */
	private int queryUpdateVersion(){
		String qry = "SELECT * FROM " + TABLE_UPDATE + " WHERE _id = 1";
		openDatabaseRead();
		Cursor cursor = mDatabase.rawQuery(qry, null);
		int num = 0;
		if(cursor.moveToFirst()){
			Log.d(TAG, "Found entry version: " + String.valueOf(cursor.getInt(1)));
			num = cursor.getInt(1); //1 is the second column, the key updateNum that I want
			close();
			return num;
		}
		Log.d(TAG, "No version entry was found, returning 0");
		close();
		return 0;
	}
	
	public int updateVersion(){
		Log.d(TAG, "Updating version num");
		mUpdateVersion++;
		openDatabaseWrite();
		/*String qry = "UPDATE update_version SET updateNum =" + String.valueOf(mUpdateVersion);
		mDatabase.execSQL(qry);*/ // this also works
		
		ContentValues val = new ContentValues();
		val.put(KEY_VERSION_IN_TABLE_UPDATE, mUpdateVersion);
		int retVal = mDatabase.update(TABLE_UPDATE, val, null, null);//1 is id of only entry
		if(retVal == -1) Log.d(TAG, "update version failed");
		close();
		return retVal;
	}
	
	/** 
	 * Adding new wine to database
	 */
	public int addWine(Wine wine) {
		openDatabaseWrite();
		
		//first checks if the wine already is inserted
		String qry = "SELECT _id FROM wines WHERE _id = " + String.valueOf(wine.getID());
		Cursor cursor = mDatabase.rawQuery(qry, null);
		if(cursor.moveToFirst()){
			Log.d(TAG, "wine by that id already exists");
			close();
			return -1;
		}
		
		ContentValues values = new ContentValues();
		values.put(KEY_ID, wine.getID());
		values.put(KEY_NAME, wine.getName());
		values.put(KEY_TYPE, wine.getType());
		values.put(KEY_YEAR, wine.getYear());
		values.put(KEY_GRAPE, wine.getGrape());
		values.put(KEY_COUNTRY, wine.getCountry());
		values.put(KEY_REGION, wine.getRegion());
		values.put(KEY_SCORE, wine.getScore());
		values.put(KEY_PRODNUM, wine.getProdNum());
		values.put(KEY_SELECTION, wine.getSelection());
		values.put(KEY_PRICE, wine.getPrice());
		values.put(KEY_STARS, wine.getStars());
		values.put(KEY_SWEETNESS, wine.getSweetness());
		values.put(KEY_AROMA, wine.getAroma());
		values.put(KEY_TASTE, wine.getTaste());
		values.put(KEY_CONCLUSION, wine.getConclusion());
		values.put(KEY_SOURCE, wine.getSource());
		values.put(KEY_SOURCEDATE, wine.getSourceDate());
		values.put(KEY_NOTE, wine.getNote());
		values.put(KEY_VERSION, wine.getVersion() + 1); //Adding one to the current updateVersion
		
		//Inserting row
		try{
			mDatabase.insert(TABLE_WINES, null, values);
		}
		catch(SQLException e){
			Log.d(TAG, "Error inserting in db: " + e.getMessage());
			close();
			return -1;
		}
		Log.d(TAG, "Inserted wine with id: " + String.valueOf(wine.getID() + " to db."));
		close(); //Important to close database connection when done inserting
		return 0;
	}
	
	//Requesting data from a server from the main activity is one of the things that 
		//(newer versions of) Android prohibits, because it can cause lag in you application if the user has
		//to wait for possibly long processes. Use AsyncTask in stead, and make sure Internet is turned on
		//in the app manifest
		private class FetchDatabaseFromServer extends AsyncTask<String, Void, List<JSONObject>> {
			private Exception exception;
			private String tag = "[SERVER connection]";
			
			protected List<JSONObject> doInBackground(String... urls){
		    	
				String url = urls[0];
		    	InputStream content = null;
		        try{
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpResponse response = httpclient.execute(new HttpGet(url));
		            content = response.getEntity().getContent();
		        }catch(Exception e){
		        	this.exception = e;
		            Log.e(tag, "Error in http connection: " + e.toString());
		            return null;
		        }
		        
			    return formatDatabaseContent(content);
		    }
			
			protected void onPostExecute(List<JSONObject> data) {
				Log.d(TAG, "Length of JSON data: " + data.size());
		        if(this.exception == null && data.size() > 0){
		        	Log.d(tag, "Stream fetching successful.");
		        	updateDatabase(data);
		        	updateVersion();
		        }
		    }
	        protected void onPreExecute() {}

	        protected void onProgressUpdate(Void... values) {}
	        
	        private List<JSONObject> formatDatabaseContent(InputStream is){
	    		StringBuilder sb=null;
	            String result=null;
	    		
	            try{
	    	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
	    	        sb = new StringBuilder();
	    	        sb.append(reader.readLine() + "\n");
	    	        String line="0";
	          
	    	        while ((line = reader.readLine()) != null) {
	    	            sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	    	    }
	            catch(Exception e){
	    	        Log.e("tag", "Error converting result "+e.toString());
	    	    }
	            List<JSONObject> dataArray = new ArrayList<JSONObject>();
	    	    try{
	    		    JSONArray jsonArray = new JSONArray(result);
	    		    JSONObject jsonObj = null;
	    		    
	    		    for(int i=0;i<jsonArray.length();i++){
	    		    	jsonObj = jsonArray.getJSONObject(i);
	    		            Log.d(tag, jsonObj.getString("name"));
	    		            dataArray.add(jsonObj);
	    		    }
	    	     
	    	    }catch(JSONException e1){
	    	    	Log.d(tag, "No wines found / parsed");
	    	    }catch (ParseException e1){
	    	        e1.printStackTrace();
	    	    }
	    	    return dataArray;
	    	}
	    }
		
		private void updateDatabase(List<JSONObject> data){
			if(data == null || data.size() == 0){
				Log.d(TAG, "No data");
				return;
			}

			for (int i = 0; i < data.size(); i++) {
	            JSONObject obj = data.get(i);
	            try{
	            	Wine wine = new Wine(obj.getInt("_id"), obj.getString("name"), obj.getString("type"),
	                		obj.getString("year"), obj.getString("grape"), obj.getString("country"), obj.getString("region"),
	                		obj.getString("score"), obj.getString("productnum"), obj.getString("selection"), 
	                		obj.getString("price"), obj.getString("stars"), obj.getString("sweetness"),
	                		obj.getString("aroma"), obj.getString("taste"), obj.getString("conclusion"),
	                		obj.getString("source"), obj.getString("sourcedate"), obj.getString("note"),
	                		obj.getInt("updateVersion"));
	            	addWine(wine);
	            }
	            catch(JSONException e){
	            	Log.d(TAG, "JSON exception: " + e);
	            }
	            
	        }
		}
	
	// Updating single wine
	/*public int updateWine(Wine wine) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, wine.getName());
		
		return db.update(TABLE_WINES, values, KEY_ID + " = ?", new String[] { String.valueOf(wine.getID()) });
	}*/
	 
	// Deleting single wine
	/*public void deleteWine(Wine wine) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WINES, KEY_ID + " = ?", new String[]{ String.valueOf(wine.getID()) });
		db.close();
	}*/
	
	/*public void save(List<JSONObject> objects) throws SQLException {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
    	openDatabaseWrite();
        connection = mDatabase.getConnection();
        statement = connection.prepareStatement(SQL_INSERT);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject obj = objects.get(i);
            statement.setString(1, obj.getString(i));
            // ...
            statement.addBatch();
            if ((i + 1) % 1000 == 0) {
                statement.executeBatch(); // Execute every 1000 items.
            }
        }
        statement.executeBatch();
    } finally {
        if (statement != null) try { statement.close(); } catch (SQLException logOrIgnore) {}
        if (connection != null) try { connection.close(); } catch (SQLException logOrIgnore) {}
    }
}*/

}
