package com.aopds;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Language;
import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.AopdsServiceAuthorizationToken;
import com.aopds.aopdsServiceClient.AopdsServiceClient;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceMalformedResponseException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.guiAdapters.LanguageSpinnerAdapter;
import com.aopds.tools.AopdsLogger;

public class CreateDictionaryActivity extends AopdsActivity {
	
	/**
	 * Provides an interface for the user to create a new dictionary 
	 * @author Anthony Morales
	 *
	 */
	
	private Spinner languagesSpinnerTo;
	private Spinner languagesSpinnerFrom;
	private EditText editName;
	private CheckBox checkMonolingualDictionary;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	    initGui();
		
	}
	
	public void initGui() {
		
		setContentView(R.layout.create_dictionary_gui);
		
		checkMonolingualDictionary = (CheckBox) findViewById(R.id.CheckBoxMonolingual);
		languagesSpinnerFrom = (Spinner) findViewById(R.id.new_dictionary_language_from);
		languagesSpinnerTo = (Spinner) findViewById(R.id.new_dictionary_language_to);
		LanguageSpinnerAdapter languageAdapter = new LanguageSpinnerAdapter(getApplicationContext(), 
				R.layout.language_spinner_item, 
				new ArrayList<Language>());
		
		Button submitButton = (Button) findViewById(R.id.OkNewDictionaryButton);
	    editName = (EditText) findViewById(R.id.editTextDictionaryName);
		
		
		try {
			languageAdapter.addAll(AopdsDatabase.getInstance(getApplicationContext()).getAllLanguages());
		} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		languagesSpinnerFrom.setAdapter(languageAdapter);
		languagesSpinnerTo.setAdapter(languageAdapter);
		
		checkMonolingualDictionary.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					
					CreateDictionaryActivity.this.setSpinnerLanguageToEnable(false);
				}
				else {
					
					CreateDictionaryActivity.this.setSpinnerLanguageToEnable(true);
				}
				
			}
		});
		
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				onSubmit();
			}
		});
		
		
		
	}

	protected void onSubmit() {
		
		Dictionary newDico = new Dictionary();
		
		User u = getUserManager().getUser();
		if(u != null) {
			
			AopdsServiceAuthorizationToken token = new AopdsServiceAuthorizationToken(u.getEmail(), u.getPassword());
			
			newDico.setName(editName.getText().toString());
			newDico.setLanguageFrom((Language)languagesSpinnerFrom.getSelectedItem());
		
			if(!checkMonolingualDictionary.isChecked()) {
			
				newDico.setLanguageTo((Language)languagesSpinnerTo.getSelectedItem());
			}
			else {
				newDico.setLanguageTo(newDico.getLanguageFrom());
			}
		
			AopdsServiceClient client = new AopdsServiceClient();
			/*try {
				newDico = client.addDictionary(newDico, token, u.getId());
				AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
				db.saveDictionary(newDico);
				Toast.makeText(getApplicationContext(), getString(R.string.LABEL_DICTIONARY_ADDED), Toast.LENGTH_SHORT).show();
				this.finish();
			} catch (AopdsServiceConnectionImpossibleException e) {
				// TODO Auto-generated catch block
				AopdsLogger.error("ERR", e.getMessage(), e);
				Toast.makeText(getApplicationContext(), getString(R.string.LABEL_CONNEXION_IMPOSSIBLE), Toast.LENGTH_SHORT).show();
			} catch (AopdsServiceMalformedResponseException e) {
				// TODO Auto-generated catch block
				AopdsLogger.error("ERR", e.getMessage(), e);
			} catch (AopdsServiceException e) {
				// TODO Auto-generated catch block
				AopdsLogger.error("ERR", e.getMessage(), e);
			} catch (AopdsServiceServerException e) {
				// TODO Auto-generated catch block
				AopdsLogger.error("ERR", e.getMessage(), e);
			}*/
		
		}
		else {
			
			//Toast.makeText(getApplicationContext(), getString(R.string.LABEL_NOT_CONNECTED), Toast.LENGTH_SHORT).show();
		}
	}

	protected void setSpinnerLanguageToEnable(boolean b) {
		
		this.languagesSpinnerTo.setEnabled(b);
		
	}
	
	

}
