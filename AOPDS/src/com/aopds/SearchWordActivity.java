package com.aopds;

import java.util.ArrayList;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.guiAdapters.WordListAdapter;
import com.aopds.tools.AopdsErrorHandler;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchWordActivity extends AopdsActivity {

	AopdsDatabase data;
	WordListAdapter headwordListAdapter;
	ListView resultListView;
	Dictionary dictionary;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_word_gui);

		dictionary = (Dictionary) getIntent().getSerializableExtra(
				"com.aopds.dictionaryToBrowse");

		if (dictionary == null) {
			finish();
			// TODO: gérer l'erreur
		}

		data = AopdsDatabase.getInstance(getApplicationContext());

		headwordListAdapter = new WordListAdapter(getApplicationContext(),
				R.layout.search_word_list_item, new ArrayList<AbstractWord>(),
				dictionary);
		
		initGUI();

	}
	
	private void suggestDeletion(Suggestion s) {
		AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
		s.setIsDeleteActionType();
		try {
			db.addSuggestion(s);
			} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delete(int pos) {

		AbstractWord w = (AbstractWord)resultListView.getAdapter().getItem(pos);
		Log.i("selectedWord",w.toString());
		w.setDictionary(dictionary);
		
		if (w instanceof Headword) {
			
			System.out.println("ok");
			Suggestion sugg = new Suggestion();
			sugg.setIsDeleteActionType();
			sugg.setHeadword((Headword) w);
			sugg.setDictionary(w.getDictionary());
			sugg.setWord(w.getWord());
			sugg.setEntry(w.getEntry());
			sugg.setPhonetic(w.getPhonetic());
			sugg.setPronunciationRecorded(null);
			sugg.setSynchroStatus(Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED);
			sugg.setDictionaryVersion(w.getDictionary()
					.getVersion());
			Log.i("suggestion",sugg.toString());
			suggestDeletion(sugg);
			
		}

	}
	
	public void modify(int pos) {
		
		AbstractWord w = (AbstractWord)resultListView.getAdapter().getItem(pos);
		Log.i("selectedWord",w.toString());
		w.setDictionary(dictionary);

		Intent intent = new Intent(getApplicationContext(),
				ModifyWordActivity.class);

		intent.putExtra("com.aopds.wordToModify", w);
		startActivity(intent);
	}

	
	@Override
	protected void onRestart() {
		super.onRestart();

		headwordListAdapter.clear();

		headwordListAdapter.notifyDataSetChanged();

		resultListView.setSelectionAfterHeaderView();

	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.long_click_menu, menu);
	}

	
		public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	            .getMenuInfo();
	    
	    System.out.println(info.position);
	    switch (item.getItemId()) {
	    
	    case R.id.remove_item:delete(info.position);
	    onSearchWord();
	    return true;
	    	    
	    case R.id.modify_item:modify(info.position);
	   return true;
	    
	    default:
            return false;
	    
	    }
	    
	}
	
	
	public void initGUI() {

		TextView dictionaryName = (TextView) findViewById(R.id.searchDictionaryName);
		dictionaryName.setText(dictionary.getName());

		TextView dictionaryLanguages = (TextView) findViewById(R.id.searchDictionaryLanguages);
		dictionaryLanguages.setText(dictionary.getLanguageFrom().getName()
				+ " => " + dictionary.getLanguageTo().getName());

		// to launch "onSearchWord() when the user press OK on the Keyboard
		EditText search = (EditText) findViewById(R.id.wordToSearchEditText);
		search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// close the Keybord
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					onSearchWord();
				}
				return false;
			}
		});

		Button searchButton = (Button) findViewById(R.id.searchWordSearchButton);
		searchButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// close the keybord
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				onSearchWord();
			}
		});

		Button addWordButton = (Button) findViewById(R.id.searchButtonAddWord);
		addWordButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent addWord = new Intent(getApplicationContext(),
						AddWordActivity.class);

				addWord.putExtra("com.aopds.concernedDictionary", dictionary);

				startActivity(addWord);
			}
		});

		resultListView = (ListView) findViewById(R.id.searchResultsList);

		resultListView.setAdapter(headwordListAdapter);

		resultListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View v,
							int position, long id) {

						AbstractWord w = (AbstractWord) adapter
								.getItemAtPosition(position);

						onClickOnWord(w);

					}
				});

		resultListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener 
				(){ 
            @Override 
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) { 
            	System.out.println(pos);
            	
            	AbstractWord w = (AbstractWord) av.getItemAtPosition(pos);
            	Log.i("selectedWord",w.toString());
            	return false; 
            	} 
			}); 

		TextView searchWordResultsText = (TextView) findViewById(R.id.searchWordResultsText);
		searchWordResultsText.setVisibility(TextView.INVISIBLE);

	}

	public void showNoMatchFoundDialog(String wordNotFound) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"No match found for the word " + wordNotFound + " !!")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void onClickOnWord(AbstractWord word) {

		Intent seeWord = new Intent(getApplicationContext(),
				SeeWordActivity.class);

		word.setDictionary(dictionary);

		seeWord.putExtra("com.aopds.wordToShow", word);

		startActivity(seeWord);

	}

	public void onSearchWord() {

		EditText wordToSearchEditText = (EditText) findViewById(R.id.wordToSearchEditText);

		String text = wordToSearchEditText.getText().toString();

		if (text.length() > 0) {

			try {
				ArrayList<AbstractWord> results = data.searchWord(
						dictionary.getCode(), text, false, true);

				if (results == null) {

					showNoMatchFoundDialog(text);

				} else {

					headwordListAdapter.clear();
					headwordListAdapter.addAll(results);

					headwordListAdapter.notifyDataSetChanged();

					resultListView.setSelectionAfterHeaderView();
				}

			} catch (AopdsDatabaseException e) {
				AopdsErrorHandler.handleError(e,
						AopdsErrorHandler.DATABASE_ERROR_DEFAULT, this);
			}

		}

		TextView searchWordResultsText = (TextView) findViewById(R.id.searchWordResultsText);
		searchWordResultsText.setVisibility(TextView.VISIBLE);
		registerForContextMenu(resultListView);///////////////////////////////////////////////////////////////a changer...

	}

}
