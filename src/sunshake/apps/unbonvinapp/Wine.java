package sunshake.apps.unbonvinapp;

public class Wine {
	//Private variables
	int mId;
	String mName;
	String mType;
	String mYear;
	String mGrape;
	String mCountry;
	String mRegion;
	String mScore;
	String mProductnum;
	String mSelection;
	String mPrice;
	String mStars;
	String mSweetness;
	String mAroma;
	String mTaste;
	String mConclusion;
	String mSource;
	String mSourceDate;
	String mNote;
	int mVersion;
	
	//Empty constructor
	public Wine(){
		
	}
	//constr w/ id
	public Wine(int id, String name, String type, String year, String country, String region, String score, String prodnum,
			String price, String stars, String aroma, String taste, String conclusion, String source,
			String sourcedate){
		this.mId = id;
		this.mName = name;
		this.mType = type;
		this.mYear = year;
		this.mCountry = country;
		this.mRegion = region;
		this.mScore = score;
		this.mProductnum = prodnum;
		this.mPrice = price;
		this.mStars = stars;
		this.mAroma = aroma;
		this.mTaste = taste;
		this.mConclusion = conclusion;
		this.mSource = source;
		this.mSourceDate = sourcedate;
		
	}
	//constr without setting id
	public Wine(String name, String type, String year, String country, String region, String score, String prodnum,
			String price, String stars, String aroma, String taste, String conclusion, String source,
			String sourcedate){
		this.mName = name;
		this.mType = type;
		this.mYear = year;
		this.mCountry = country;
		this.mRegion = region;
		this.mScore = score;
		this.mProductnum = prodnum;
		this.mPrice = price;
		this.mStars = stars;
		this.mAroma = aroma;
		this.mTaste = taste;
		this.mConclusion = conclusion;
		this.mSource = source;
		this.mSourceDate = sourcedate;
	}
	
	//constr w/ ALL fields
	public Wine(int id, String name, String type, String year, String grape, String country, String region, 
			String score, String prodnum, String selection, String price, String stars, String sweetness,
			String aroma, String taste, String conclusion, String source,String sourcedate, 
			String note, int updateVersion){
		this.mId = id;
		this.mName = name;
		this.mType = type;
		this.mYear = year;
		this.mGrape = grape;
		this.mCountry = country;
		this.mRegion = region;
		this.mScore = score;
		this.mProductnum = prodnum;
		this.mSelection = selection;
		this.mPrice = price;
		this.mStars = stars;
		this.mSweetness = sweetness;
		this.mAroma = aroma;
		this.mTaste = taste;
		this.mConclusion = conclusion;
		this.mSource = source;
		this.mSourceDate = sourcedate;
		this.mNote = note;
		this.mVersion = updateVersion;
		
	}
	
	//Getters n' setters
	public int getID(){ return this.mId; }
    public void setID(int id){ this.mId = id;}
    public String getName(){ return this.mName; }
    public void setName(String name){ this.mName = name; }
    public String getType(){ return this.mType; }
    public void setType(String type){ this.mType = type; }
    public String getYear(){ return this.mYear; }
    public void setYear(String year){ this.mYear = year;}
    public String getCountry(){ return this.mCountry; }
    public void setCountry(String country){ this.mCountry = country; }
    public String getRegion(){ return this.mRegion; }
    public void setRegion(String region){ this.mRegion = region; }
    public String getScore(){ return this.mScore; }
    public void setScore(String score){ this.mScore = score; }
    public String getProdNum(){ return this.mProductnum; }
    public void setProdNum(String prodnum){ this.mProductnum = prodnum; }
    public String getPrice(){ return this.mPrice; }
    public void setPrice(String price){ this.mPrice = price; }
    public String getStars(){ return this.mStars; }
    public void setStars(String stars){ this.mStars = stars; }
    public String getAroma(){ return this.mAroma; }
    public void setAroma(String aroma){ this.mAroma = aroma; }
    public String getTaste(){ return this.mTaste; }
    public void setTaste(String taste){ this.mTaste = taste; }
    public String getConclusion(){ return this.mConclusion; }
    public void setConclusion(String conclusion){ this.mConclusion = conclusion; }
    public String getSource(){ return this.mSource; }
    public void setSource(String source){ this.mSource = source; }
    public String getSourceDate(){ return this.mSourceDate; }
    public void setSourceDate(String sourceDate){ this.mSourceDate = sourceDate; }
    public String getGrape(){ return this.mGrape; }
    public void setGrape(String grape){ this.mGrape = grape; }
    public String getSelection(){ return this.mSelection; }
    public void setSelection(String sel){ this.mSelection = sel; }
    public String getSweetness(){ return this.mSweetness; }
    public void setSweetness(String sweetness){ this.mSweetness = sweetness; }
    public String getNote(){ return this.mNote; }
    public void setNote(String note){ this.mNote = note; }
    public int getVersion(){ return this.mVersion; }
    public void setVersion(int version){ this.mVersion = version; }
}
