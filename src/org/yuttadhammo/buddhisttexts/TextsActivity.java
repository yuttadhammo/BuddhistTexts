package org.yuttadhammo.buddhisttexts;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

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
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class TextsActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	public static final String TAG = "TextsActivity";

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

	private static SharedPreferences prefs;

	public int PAGE_COUNT = 2;
	
	private int chapter = 1;

	public int MAX;

	public static String[] files = new String[2];

	private ListView idxList;

	private int lastPosition;

	protected static WebView currentWebView;

	protected int currentPage;

	private int set;
	
	private String[] slugs = {"dn","mn","sn","an","dhpa","ja","vi"};
	private int[] arrays = {R.array.dn_names,R.array.mn_names,R.array.sn_names,R.array.an_names,R.array.dh_names,R.array.ja_names,R.array.vi_names};
	SparseIntArray map = new SparseIntArray();

	private int lastPage;

	private int API;

	private ActionBar actionBar;

	private Button showActionBar;

	private SlidingMenu slideMenu;
	
	public static WebView[] webviews = new WebView[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actionBar = getSupportActionBar();
		
		int mapi = 0;
		map.put(R.id.menu_DN, mapi++);
		map.put(R.id.menu_MN, mapi++);
		map.put(R.id.menu_SN, mapi++);
		map.put(R.id.menu_AN, mapi++);
		map.put(R.id.menu_DH, mapi++);
		map.put(R.id.menu_JA, mapi++);
		map.put(R.id.menu_VI, mapi++);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		setContentView(R.layout.main);
		
		set = prefs.getInt("set", 0);
		currentPage = prefs.getInt("page", 0);
		chapter = prefs.getInt("chapter", 1);
		lastPosition = chapter-1;
			

        slideMenu = new SlidingMenu(this);
        slideMenu.setMode(SlidingMenu.LEFT);
        slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //menu.setShadowWidthRes(0);
        //menu.setShadowDrawable(R.drawable.shadow);
        slideMenu.setBehindWidthRes(R.dimen.slide_width);
        slideMenu.setFadeDegree(0.35f);
        slideMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slideMenu.setMenu(R.layout.slide);
		slideMenu.setSlidingEnabled(false);
	
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.removeAllTabs();
		
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.pali))
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.english))
				.setTabListener(this));

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setTitle(getString(R.string.app_name)+" - "+getResources().getStringArray(R.array.set_names)[set]);

		showActionBar = (Button) findViewById(R.id.showactionbar);

		showActionBar.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				actionBar.show();
				showActionBar.setVisibility(View.GONE);
			}
		});
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						currentPage = position;
						currentWebView = webviews[position];
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
		
		
		idxList = (ListView) slideMenu.findViewById(R.id.contents);
		idxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				slideMenu.toggle();
				
				changePosition(position);
		  	}
		});
		
		// check for directory, zip file
		
		File testFile = new File(prefs.getString("archive_dir", 
				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts")
			);
		if(!testFile.exists())
			testFile = new File(
					Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts"
				);
		
		if(!testFile.exists()) {
			testFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ File.separator + "BuddhistTexts.zip");
			if(testFile.exists()) {
		        new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.verify_unzip)
		        .setMessage(R.string.verify_unzip_message)
		        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	uncompressFile();
		            }

		        })
		        .setNegativeButton(android.R.string.no, null)
		        .show();	
			}
				
		}
		
		updatePage();
	}
	@Override
	protected void onResume() {
		super.onResume();

	}
	@Override
    protected void onPause() {
		super.onPause();
		if(currentWebView == null)
			currentWebView = webviews[currentPage];
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("set", set);
		editor.putInt("chapter", chapter);
    	editor.putInt("page", currentPage);
		if(currentWebView != null) {
			//editor.putFloat("zoom_"+currentPage, currentWebView.getScale());
		}
    	editor.commit();
	}

	private void updatePage() {
		Log.i(TAG,"updating pages to chapter "+chapter);
		
		IndexListAdapter adapter = new IndexListAdapter(this, 
				R.layout.index_list_item, 
				R.id.title, 
				Arrays.asList(getResources().getStringArray(arrays[set])), 
				lastPosition);
		lastPage = adapter.getCount()-1;
		idxList.setAdapter(adapter);
		
		if(currentWebView == null)
			currentWebView = webviews[currentPage];
		if(currentWebView != null) {
			prefs.edit().putFloat("zoom_"+currentPage, currentWebView.getScale()).commit();
		}
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(currentPage);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu, menu);
	    Menu sub = menu.findItem(R.id.menu_text).getSubMenu();
	    sub.setGroupCheckable(R.id.group_text, true, true);
		sub.getItem(set).setChecked(true);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		super.onOptionsItemSelected(item);
		
		
		if(item.isCheckable()) {
			item.setChecked(true);
			set = map.get(item.getItemId());
			setTitle(getString(R.string.app_name)+" - "+getResources().getStringArray(R.array.set_names)[set]);
			lastPosition = 0;
			chapter = 1;
			updatePage();
			return false;
		}
		
		//SharedPreferences.Editor editor = prefs.edit();
		Intent intent;
		int size;
		switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            slideMenu.toggle();
	            return true;
			case (int)R.id.menu_settings:
				intent = new Intent(this, TextsSettingsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			case (int)R.id.menu_search:
				searchInPage();
				return true;
			case (int)R.id.menu_back:
				if(lastPosition == 0)
					Toast.makeText(this, R.string.start_book, Toast.LENGTH_SHORT).show();
				else
					changePosition(lastPosition-1);
				return true;
			case (int)R.id.menu_forward:
				if(lastPosition == lastPage)
					Toast.makeText(this, R.string.end_book, Toast.LENGTH_SHORT).show();
				else
					changePosition(lastPosition+1);
				return true;
			case (int)R.id.menu_font_minus:
				size = prefs.getInt("text_size", 18);
				size = size - 2;
				if(size < 8)
					size = 8;
				updateFontSize(size);
				return true;
			case (int)R.id.menu_font_plus:
				size = prefs.getInt("text_size", 18);
				size = size + 2;
				updateFontSize(size);
				return true;
			case (int)R.id.menu_fullscreen:
				actionBar.hide();
				showActionBar.setVisibility(View.VISIBLE);
				return true;
		}
		return false;
	}
	
	private void updateFontSize(int size) {
		String data = "* { font-size:"+size+"px; }\n";
		File file = new File(prefs.getString("archive_dir", Environment.getExternalStorageDirectory().getAbsolutePath() + "/BuddhistTexts")
		+ "/css/"+(currentPage==0?"pali":"english")+"_custom.css");
		Log.e(TAG,file.getAbsolutePath());
        try {
            FileWriter out = new FileWriter(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		Editor editor = prefs.edit();
		editor.putInt("text_size", size);
		editor.commit();
		
		updatePage();
	}
	
	private void changePosition(int position) {
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

	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		private SparseArray<SectionFragment> mPageReferenceMap = new SparseArray<SectionFragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			//Log.i(TAG,"updating pager adapter to chapter "+chapter);

			// pali file
			
			File testFile = new File(prefs.getString("archive_dir", 
    				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts")
    			+ File.separator +slugs[set]+"/"+slugs[set]+"_p_"+chapter+".htm");

			if(!testFile.exists()) {
				Log.d(TAG,"File not found: "+testFile.getAbsolutePath().toString());
				testFile = new File( 
	    				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts"
	    			+ File.separator +slugs[set]+"/"+slugs[set]+"_p_"+chapter+".htm"
	    		);
			}
			
			if(!testFile.exists())
				files[0] = "file:///android_asset/not_found.htm";
			else
				files[0] = "file://"
					+testFile.getAbsolutePath().toString();

			// english file
			
			testFile = new File(prefs.getString("archive_dir", 
    				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts")
    		+ File.separator +slugs[set]+"/"+slugs[set]+"_e_"+chapter+".htm");

			if(!testFile.exists()) {
				testFile = new File( 
	    				Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts"
	    			+ File.separator +slugs[set]+"/"+slugs[set]+"_e_"+chapter+".htm"
	    		);
			}
			
			
			if(!testFile.exists())
				files[1] = "file:///android_asset/not_found.htm";
			else
				files[1] = "file://"
						+testFile.getAbsolutePath().toString();
			
			Log.d(TAG,"File location: "+testFile.getAbsolutePath().toString());
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			SectionFragment fragment = new SectionFragment();
			Bundle args = new Bundle();
			Log.i(TAG,"getting item "+files[position]);
			args.putInt("page", position);

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

	public static class SectionFragment extends Fragment {

		public SectionFragment() {
		}

		@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
	        ViewGroup rootView = (ViewGroup) inflater.inflate(
	                R.layout.page, container, false);
			final WebView ewv = (WebView) rootView.findViewById(R.id.content);
			Bundle args = getArguments();
			int page = args.getInt("page");
			String file = files[page];

			//Log.i(TAG,"loading file "+file);
			ewv.getSettings().setBuiltInZoomControls(true);
			ewv.getSettings().setSupportZoom(true);
	        ewv.getSettings().setJavaScriptEnabled(true); // enable javascript

	        //float zoom = prefs.getFloat("zoom_"+page, 1f);
			ewv.setInitialScale(100);
	        
			ewv.setWebViewClient(new MyWebViewClient());
			
			ewv.loadUrl(file);
			ewv.clearCache(true);
			webviews[page] = ewv;

			return rootView;
		}
		private class MyWebViewClient extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		    public boolean flag = false;
		    public void onPageFinished(WebView view, String url) {

            }

		}

	}	
	
	boolean firstSearch = true;
	private String currentSearchText;
	private EditText findBox;
	
    public void searchInPage(){
    	findBox = new EditText(this);
    	firstSearch = true;
    	
	    final LinearLayout container = (LinearLayout)findViewById(R.id.search);  
	    container.removeAllViews();
	    
	    Button nextButton = new Button(this);  
	    nextButton.setText("Next");  
	    nextButton.setOnClickListener(new OnClickListener(){  
	    	@Override  
	    	public void onClick(View v){
	    		
	    		if(firstSearch || !findBox.getText().toString().equals(currentSearchText))
	    			doFirstSearch();
	    		else {
		    		if(currentWebView == null)
		    			currentWebView = webviews[currentPage];
		    		currentWebView.findNext(true);
	    		}
	    	}  
	    });  
	    container.addView(nextButton);  
	      
	    Button closeButton = new Button(this);  
	    closeButton.setText("Close");
	    closeButton.setOnClickListener(new OnClickListener(){  
	    	@Override  
	    	public void onClick(View v){
	    		currentWebView.clearMatches();
	    		container.removeAllViews();  
	    	}  
    	}); 
	    container.addView(closeButton);  
	      
	    findBox.setOnKeyListener(new OnKeyListener(){  
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		    	if((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))){  
		    		doFirstSearch();
				}  
				return false;  
			}  
    	}); 
		findBox.setMinEms(30);  
		findBox.setSingleLine(true);
		findBox.setId(57185718);
		container.addView(findBox);  
    }  
	
    void doFirstSearch() {
    	currentSearchText = findBox.getText().toString();
		if(currentWebView == null)
			currentWebView = webviews[currentPage];
		currentWebView.findAll(toUni(currentSearchText));  
		  
		try{  
		    for(Method m : WebView.class.getDeclaredMethods()){
		        if(m.getName().equals("setFindIsUp")){
		            m.setAccessible(true);
		            m.invoke(currentWebView, true);
		    		Log.i(TAG,"set find is up");
		            break;
		        }
		    }
			firstSearch = false;
		}catch(Exception e){
    		e.printStackTrace();
		} 
    }
    
	public static String toUni(String string) {
		string = string.replace("aa", "ā").replace("ii", "ī").replace("uu", "ū").replace(".t", "ṭ").replace(".d", "ḍ").replace("\"n", "ṅ").replace(".n", "ṇ").replace(".m", "ṃ").replace("~n", "ñ").replace(".l", "ḷ");
		return string;
	}

	public static String toVel(String string) {
		string = string.replaceAll("ā", "aa").replaceAll("ī", "ii").replaceAll("ū", "uu").replaceAll("ṭ", ".t").replaceAll("ḍ", ".d").replaceAll("ṅ", "\"n").replaceAll("ṇ", ".n").replaceAll("[ṃṁ]", ".m").replaceAll("ñ", "~n").replaceAll("ḷ", ".l").replaceAll("Ā", "AA").replaceAll("Ī", "II").replaceAll("Ū", "UU").replaceAll("Ṭ", ".T").replaceAll("Ḍ", ".D").replaceAll("Ṅ", "\"N").replaceAll("Ṇ", ".N").replaceAll("[ṂṀ]",".M").replaceAll("Ñ", "~N").replaceAll("Ḷ", ".L");
		return string;
	}	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(currentWebView == null)
			currentWebView = webviews[currentPage];
		if (currentWebView != null && keyCode == KeyEvent.KEYCODE_BACK && currentWebView.canGoBack()) {
			Log.i(TAG,"going back");
			currentWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		if(tab != null && mViewPager != null)
			mViewPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	private Handler handler = new Handler();
	   
    public void uncompressFile() {
    	final Activity activity = this;
    	
    	final Decompress d = new Decompress(
    			Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ File.separator + "BuddhistTexts.zip", 
				Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ File.separator); 
    	final ProgressDialog unzipProgressDialog = new ProgressDialog(this);
    	unzipProgressDialog.setCancelable(false);
    	unzipProgressDialog.setMessage(getString(R.string.unzipping));
    	Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				d.unzip();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if(unzipProgressDialog.isShowing()) {
							unzipProgressDialog.dismiss();
							Toast.makeText(activity, activity.getString(R.string.unzipped), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
    	thread.start();
    	if (!activity.isFinishing()) {
        	unzipProgressDialog.show();
        }
    	    
    }

}
