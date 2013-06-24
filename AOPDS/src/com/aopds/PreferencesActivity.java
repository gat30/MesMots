package com.aopds;

import java.util.List;

import com.aopds.aopdsData.domain.Language;
import com.aopds.guiAdapters.LanguageSpinnerAdapter;
import com.aopds.tools.AopdsLogger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class PreferencesActivity extends AopdsActivity {

	
	Spinner languageSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences_gui);
		
		initGui();
	}

	
	private void onPreferencesChanged() {
		
		try {
			getPreferencesManager().setLanguage(
				((Language) languageSpinner.getSelectedItem()).getAbreviation()
			);
			
			getPreferencesManager().persistPreferences();
			
		} catch (Exception e) {
			
			Log.e("----", e.getMessage(), e);
			
		}
		
	}
	
	private void initGui() {
		
    	LanguageSpinnerAdapter languageAdapter;
    	List<Language> allLanguages;
    	

		allLanguages = getPreferencesManager().getSupportedLanguages();
		AopdsLogger.info(allLanguages.toString(), "ok");
		
		languageAdapter = new LanguageSpinnerAdapter(
			getApplicationContext(), 
			R.layout.language_spinner_item, 
			allLanguages
		);
	
		languageAdapter.setDropDownViewResource( R.layout.language_drop_down_spinner_item );
		
		languageSpinner = (Spinner) findViewById( R.id.preferencesLanguageSpinner );
		
		languageSpinner.setAdapter( languageAdapter );
		
		languageSpinner.setSelection( 
			languageAdapter.getPosition( 
				languageAdapter.getLanguageByAbreviation( 
					getPreferencesManager().getLanguage() 
				) 
			) 
		);
			                					      
    	Button confirmButton = (Button) findViewById(R.id.preferencesConfirmButton);

    	confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	onPreferencesChanged();
            	finish();
            }
        });

		
	}
	
	
}
