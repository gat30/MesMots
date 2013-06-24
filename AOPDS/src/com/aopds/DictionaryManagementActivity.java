/**
 * 
 */
package com.aopds;

import com.aopds.aopdsData.domain.Dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Gaetan
 *
 */
public class DictionaryManagementActivity extends AopdsActivity {

	
	Dictionary dictionary;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dictionary = (Dictionary) getIntent().getSerializableExtra("com.aopds.concernedDictionary");
		initGui();
	}

	/**
	 * 
	 */
	private void initGui() {
		setContentView( R.layout.dictionary_management_gui );
		
		TextView mainTitle = (TextView) findViewById(R.id.dictionaryManagementTitle);
		mainTitle.setText(dictionary.getName());
		
		TextView languageInfo = (TextView) findViewById(R.id.dictionaryManagementLanguageInformation);
		languageInfo.setText(dictionary.getLanguageFrom().getName()+" / "+dictionary.getLanguageTo().getName());
	
	
		Button searchWord = (Button) findViewById(R.id.dictionaryManagementSeachWordButton);
		searchWord.setOnClickListener(new View.OnClickListener() {
	 		
	     	   public void onClick(View v) {
	     		   
	     		  Intent intent = new Intent(
							getApplicationContext(),
							SearchWordActivity.class
					);

					intent.putExtra("com.aopds.dictionaryToBrowse", dictionary );
					startActivity( intent );
	     	   }
	        });
		
		Button addWord = (Button) findViewById(R.id.dictionaryManagementAddWordButton);
		addWord.setOnClickListener(new View.OnClickListener() {
	 		
	     	   public void onClick(View v) {
	     		   
	     		  Intent intent = new Intent(
							getApplicationContext(),
							AddWordActivity.class
					);

					intent.putExtra("com.aopds.concernedDictionary", dictionary );
					startActivity( intent );
	     	   }
	        });
	}
	
	
}
