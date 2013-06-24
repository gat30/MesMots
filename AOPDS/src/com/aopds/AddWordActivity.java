package com.aopds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;

public class AddWordActivity extends AopdsActivity 
{
	
	Dictionary concernedDictionary;
	
	 /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    concernedDictionary = 
		    	(Dictionary) getIntent().getSerializableExtra("com.aopds.concernedDictionary");
	    initGui();
	}
	
	public void initGui() 
	{
		setContentView( R.layout.add_word_gui );
		
		TextView languageFrom = (TextView) findViewById(R.id.AddWordLanguageTextView);
		languageFrom.setText(" ("+concernedDictionary.getLanguageFrom().getName()+")");
		
		TextView languageTo = (TextView) findViewById(R.id.AddWordNewEntryLanguageTextView);
		languageTo.setText(" ("+concernedDictionary.getLanguageTo().getName()+")");
		
		Button btAdd = (Button) findViewById(R.id.AddWordOkButton);
		
		
		btAdd.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	TextView tvWord = (TextView) findViewById(R.id.AddWordNewWordEditText);
            	TextView tvEntry = (TextView) findViewById(R.id.AddWordNewEntryEditText);
            	
            	Suggestion s = new Suggestion();
            	
            	s.setWord( tvWord.getText().toString() );
            	s.setEntry( tvEntry.getText().toString() );
            	s.setDictionary( concernedDictionary );
            	s.setPronunciationRecorded(false);
            	s.setSynchroStatus( Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED );
            	s.setPhonetic("");
            	s.setIsAddActionType();
            	
            	// Get the database and insert the word
            	AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
            	try 
            	{
            		// Add the suggestion in the local database
					db.addSuggestion( s );
					// Display the result in the LogCat
	            	AopdsLogger.info( 
	            		getClass().getSimpleName() , 
	            		"Adding suggestion : " + s
	            	);
	            	// Display the result for the user
	            	displayMessageOK( "The word " + s.getWord() + " has been added ! " );
				} 
            	catch (AopdsDatabaseException e) 
            	{
					AopdsErrorHandler.handleError(e, AopdsErrorHandler.DATABASE_ERROR_DEFAULT, AddWordActivity.this);
				}
            }
        }); 
	}
	
	public void displayMessageOK(String msg)
	{
		AlertDialog msgAlert = new AlertDialog.Builder(this).create();
    	msgAlert.setTitle("Word suggestion added");
    	msgAlert.setMessage(msg);
    	msgAlert.setButton("OK", new DialogInterface.OnClickListener() 
    	{  
    	   public void onClick(DialogInterface dialog, int which) 
    	   {  
    		   dialog.cancel();
    		   finish();
    	   } 
    	});  
    	msgAlert.show();
	}
}
