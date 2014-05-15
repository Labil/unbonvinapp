package sunshake.apps.unbonvinapp;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class Search extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	    handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String searchText = intent.getStringExtra(SearchManager.QUERY);
	      ((WineApp)this.getApplicationContext()).setSearchText(searchText);
	      this.finish();
	    }
	}
}
