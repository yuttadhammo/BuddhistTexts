package org.yuttadhammo.visuddhimagga;


import java.lang.reflect.Method;
import java.util.Arrays;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class VismActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String TAG = "VismActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	static SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	static ViewPager mViewPager;

	private SharedPreferences prefs;

	public int PAGE_COUNT = 2;
	
	private int chapter = 1;

	public int MAX;

	private ActionBar actionBar;

	public String[] files;

	private ListView idxList;

	private int lastPosition;

	protected static WebView currentWebView;

	protected int currentPage;

	private static FragmentManager fm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		fm = getSupportFragmentManager();
		setContentView(R.layout.main);
		// Set up the action bar.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		files = new String[2];
		

		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						currentPage = position;
						// For each of the sections in the app, add a tab to the action bar.
						for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
							// Create a tab with text corresponding to the page title defined by
							// the adapter. Also specify this Activity object, which implements
							// the TabListener interface, as the callback (listener) for when
							// this tab is selected.
							actionBar.getTabAt(i)
								.setText(mSectionsPagerAdapter.getPageTitle(i));
						}

						actionBar.setSelectedNavigationItem(position);
					}
				});
		
		actionBar.removeAllTabs();
		
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.pali))
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.english))
				.setTabListener(this));

		chapter = prefs.getInt("chapter", 1);
		lastPosition = chapter-1;
		
		idxList = (ListView) findViewById(R.id.contents);
		idxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				chapter = position+1;
				lastPosition = position;
				
			     // save index and top position
				
		        int index = idxList.getFirstVisiblePosition();
		        View v = idxList.getChildAt(0);
		        int top = (v == null) ? 0 : v.getTop();
		
				updatePage();
		
		        // restore
		        idxList.setSelectionFromTop(index, top);
		  	}
		});
		updatePage();
	}
	@Override
	protected void onResume() {
		super.onResume();

	}
	@Override
    protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt("chapter", chapter);
    	editor.commit();
	}

	private void updatePage() {
		Log.i("Vism","updating pages to chapter "+chapter);
		IndexListAdapter adapter = new IndexListAdapter(this, 
				R.layout.index_list_item, 
				R.id.title, 
				Arrays.asList(getResources().getStringArray(R.array.pali)), 
				lastPosition);
		idxList.setAdapter(adapter);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vism_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		super.onOptionsItemSelected(item);
		
		//SharedPreferences.Editor editor = prefs.edit();
		Intent intent;
		switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            finish();
	            return true;
			case (int)R.id.menu_settings:
				intent = new Intent(this, VismSettingsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			case (int)R.id.menu_search:
				search();
				return true;
		}
		return false;
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		private SparseArray<SectionFragment> mPageReferenceMap = new SparseArray<SectionFragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			Log.i(TAG,"updating pager adapter to chapter "+chapter);
			files[0] = "pali_"+chapter+".htm";
			files[1] = "english_"+chapter+".htm";

		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			SectionFragment fragment = new SectionFragment();
			Bundle args = new Bundle();
			Log.i(TAG,"getting item "+files[position]);
			args.putString(SectionFragment.ARG_SECTION_NUMBER, files[position]);

			// if current
			if (position == currentPage)
				args.putString("isCurrent", "yes");
				
			fragment.setArguments(args);
		    mPageReferenceMap.put(position, fragment);
			return fragment;
		}
		
		public SectionFragment getFragment(int key) {
			    return mPageReferenceMap.get(key);
		}
		
		@Override
		public int getCount() {
			return PAGE_COUNT ;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(position == 0)
				return getString(R.string.pali);
			else
				return getString(R.string.english);

		}
	}
	protected static SparseArray<String> newTexts = new SparseArray<String>();

	public static class SectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment. 
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public SectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
	        ViewGroup rootView = (ViewGroup) inflater.inflate(
	                R.layout.page, container, false);
			final WebView ewv = (WebView) rootView.findViewById(R.id.content);
			Bundle args = getArguments();
			final String file = args.getString(ARG_SECTION_NUMBER);
			String current = args.getString("isCurrent");

			Log.i(TAG,"loading file "+file);
			ewv.getSettings().setBuiltInZoomControls(true);
			ewv.getSettings().setSupportZoom(true);
	        ewv.setWebViewClient(new MyWebViewClient());
			ewv.loadUrl("file:///android_asset/" +file);
			
			if(current != null)
				currentWebView = ewv;

			return rootView;
		}
		private class MyWebViewClient extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {

			}
		}

	}	
	


    public void search(){  
	    final LinearLayout container = (LinearLayout)findViewById(R.id.search);  
	      
	    Button nextButton = new Button(this);  
	    nextButton.setText("Next");  
	    nextButton.setOnClickListener(new OnClickListener(){  
	    	@Override  
	    	public void onClick(View v){  
	    		currentWebView.findNext(true);  
	    	}  
	    });  
	    container.addView(nextButton);  
	      
	    Button closeButton = new Button(this);  
	    closeButton.setText("Close");
	    closeButton.setOnClickListener(new OnClickListener(){  
	    	@Override  
	    	public void onClick(View v){  
	    	container.removeAllViews();  
	    	  
	    	}  
    	}); 
	    container.addView(closeButton);  
	      
	    final EditText findBox = new EditText(this);
	    findBox.setOnKeyListener(new OnKeyListener(){  
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		    	if((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))){  
		    		Log.i(TAG,"is searching in "+currentWebView.getUrl());
		    		currentWebView.findAllAsync(toUni(findBox.getText().toString()));  
					  
					try{  
					    for(Method m : WebView.class.getDeclaredMethods()){
					        if(m.getName().equals("setFindIsUp")){
					            m.setAccessible(true);
					            m.invoke(currentWebView, true);
					    		Log.i(TAG,"set find is up");
					            break;
					        }
					    } 
					}catch(Exception e){
			    		e.printStackTrace();
					}  
				}  
				return false;  
			}  
    	}); 
		findBox.setMinEms(30);  
		findBox.setSingleLine(true);
		container.addView(findBox);  
    }  
	
	public static String toUni(String string) {
		string = string.replace("aa", "ā").replace("ii", "ī").replace("uu", "ū").replace(".t", "ṭ").replace(".d", "ḍ").replace("\"n", "ṅ").replace(".n", "ṇ").replace(".m", "ṃ").replace("~n", "ñ").replace(".l", "ḷ");
		return string;
	}

	public static String toVel(String string) {
		string = string.replaceAll("ā", "aa").replaceAll("ī", "ii").replaceAll("ū", "uu").replaceAll("ṭ", ".t").replaceAll("ḍ", ".d").replaceAll("ṅ", "\"n").replaceAll("ṇ", ".n").replaceAll("[ṃṁ]", ".m").replaceAll("ñ", "~n").replaceAll("ḷ", ".l").replaceAll("Ā", "AA").replaceAll("Ī", "II").replaceAll("Ū", "UU").replaceAll("Ṭ", ".T").replaceAll("Ḍ", ".D").replaceAll("Ṅ", "\"N").replaceAll("Ṇ", ".N").replaceAll("[ṂṀ]",".M").replaceAll("Ñ", "~N").replaceAll("Ḷ", ".L");
		return string;
	}	
}
