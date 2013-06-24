package com.aopds;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;

public class SeeWordActivity extends AopdsActivity {

	AbstractWord displayedWord;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.see_word);

		displayedWord = (AbstractWord) getIntent().getSerializableExtra("com.aopds.wordToShow");

		if ( displayedWord != null ) {
			
			if ( displayedWord.getDictionary() != null ) {
				
				initGui();
				
			} else {
				AopdsErrorHandler.handleError(new Exception(""), 0, this );
				finish();
			}
			
		} else {
			AopdsErrorHandler.handleError(new Exception(""), 0, this );
			finish();
		}


	}

	
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		AbstractWord temp = null;
		
		if ( displayedWord instanceof Headword ) {
			try {
				temp = AopdsDatabase.
							getInstance(getApplicationContext()).
								getWordById( displayedWord.getId() );
			} catch (AopdsDatabaseException e) {
				AopdsErrorHandler.handleError(e, AopdsErrorHandler.DATABASE_ERROR_DEFAULT, this);
			}
		} else {
			
			try {
				temp = AopdsDatabase.
							getInstance(getApplicationContext()).
								getSuggestion( displayedWord.getId() );
			} catch (AopdsDatabaseException e) {
				AopdsErrorHandler.handleError(e, AopdsErrorHandler.DATABASE_ERROR_DEFAULT, this);
			}
			
		}
		
		if ( temp != null ) {
			AopdsLogger.info("test", "stting new word");
			temp.setDictionary( displayedWord.getDictionary() );
			displayedWord = temp;
			
			TextView wordView = (TextView) findViewById(R.id.SeeWordWord);
			TextView entryView = (TextView) findViewById(R.id.SeeWordEntry);

			wordView.setText( displayedWord.getWord() );
			entryView.setText( displayedWord.getEntry() );
			
		}
		
		
		
	}

	private void suggestDeletion(Suggestion s){
		AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
		s.setIsDeleteActionType();
		try {
			db.addSuggestion(s);
		} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initGui()
	{

		
		TextView wordView = (TextView) findViewById(R.id.SeeWordWord);
		TextView entryView = (TextView) findViewById(R.id.SeeWordEntry);
		TextView wordViewLang = (TextView) findViewById(R.id.SeeWordWordLang);
		TextView entryViewLang = (TextView) findViewById(R.id.SeeWordEntryLang);
		
		
		wordView.setText( displayedWord.getWord() );
		entryView.setText( displayedWord.getEntry() );
		wordViewLang.setText(" ("+displayedWord.getDictionary().getLanguageFrom().getName()+")");
		entryViewLang.setText(" ("+displayedWord.getDictionary().getLanguageTo().getName()+")");
		
				
		View backgroundimage = findViewById(R.id.layoutLight);
		Drawable background = backgroundimage.getBackground();
		background.setAlpha(98);
		
		View backgroundimage1 = findViewById(R.id.layoutLight1);
		Drawable background1 = backgroundimage1.getBackground();
		background1.setAlpha(98);
		
	
		Button buttonSuggest = (Button) findViewById(R.id.seeWordButtonSuggest);
		buttonSuggest.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(
						getApplicationContext(),
						ModifyWordActivity.class
				);

				intent.putExtra("com.aopds.wordToModify", displayedWord);
				startActivity( intent );  
			}

		});
		
		Button buttonDelete = (Button) findViewById(R.id.seeWordButtonDelete);
		buttonDelete.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				if (displayedWord instanceof Headword)
				{
					Suggestion sugg = new Suggestion();
					sugg.setIsDeleteActionType();
					sugg.setHeadword((Headword)displayedWord);
					sugg.setDictionary(displayedWord.getDictionary());
					sugg.setWord(displayedWord.getWord());
					sugg.setEntry(displayedWord.getEntry());
					sugg.setPhonetic(displayedWord.getPhonetic());
					sugg.setPronunciationRecorded(null);
					sugg.setSynchroStatus(Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED);
					sugg.setDictionaryVersion(displayedWord.getDictionary().getVersion());			    	
					suggestDeletion(sugg);
					finish();
				}
				
			}

		});
	}



}
