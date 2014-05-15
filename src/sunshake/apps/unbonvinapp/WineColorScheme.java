package sunshake.apps.unbonvinapp;

import java.util.HashMap;
import android.content.Context;

public class WineColorScheme {
	private Context context = null;
	
	private HashMap<String, Integer> colors = new HashMap<String, Integer>();
 	
	//Constructor, must get passed context of the activity so it can access getResources() method
	public WineColorScheme(Context ctx){
		this.context = ctx;
		setupColorScheme();
	}
	
	private void setupColorScheme(){
		//Color.parseColor("#b80000");
	 	colors.put("Rød", this.context.getResources().getColor(R.color.Rød));
	 	colors.put("Rose", this.context.getResources().getColor(R.color.Rose));
	 	colors.put("Hvit", this.context.getResources().getColor(R.color.Hvit));
	 	colors.put("Champagne", this.context.getResources().getColor(R.color.Champagne));
	 	colors.put("Dessertvin", this.context.getResources().getColor(R.color.Dessertvin));
	 	colors.put("Søtvin", this.context.getResources().getColor(R.color.Søtvin));
	 	colors.put("Sherry", this.context.getResources().getColor(R.color.Sherry));
	 	colors.put("Musserende", this.context.getResources().getColor(R.color.Musserende));
	 	colors.put("Akevitt", this.context.getResources().getColor(R.color.Akevitt));
	 	colors.put("Annet", this.context.getResources().getColor(R.color.Annet));
	}
	
	public int getColor(String name){
		return (colors.get(name) != null) ? colors.get(name) : colors.get("Annet");
	}

}
