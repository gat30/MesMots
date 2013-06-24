package com.aopds;

import java.util.Locale;

import com.aopds.aopdsData.domain.User;
import com.aopds.preferences.AopdsPreferencesManager;
import com.aopds.preferences.AopdsPreferencesListener;
import com.aopds.tools.AopdsLogger;
import com.aopds.user.AopdsUserChangedListener;
import com.aopds.user.AopdsUserManager;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * 
 * Abstract activity for AOPDS application.<br><br>
 * 
 * Extends the Android basic activity adding the management of 
 * AOPDS preferences and user. Every activity extending this class 
 * will be provided the preferences and user manager.<br><br>
 * Moreover, the activity is automatically bound to a 'preferences listener'.
 * We mean that each time a preference changes (such as the language), the activity is 
 * notified about that.
 * 
 * 
 * 
 * @author Julien Wollscheid | July 2011
 *
 */
public abstract class AopdsActivity extends Activity implements AopdsPreferencesListener,AopdsUserChangedListener {

	/**
	 * The application preferences manager. Use this to get/set 
	 * the preferences of the application.
	 */
	private AopdsPreferencesManager preferencesManager;
	
	/**
	 * The application user manager. Use this to get/change the current 
	 * user of the application.
	 */
	private AopdsUserManager userManager;
	
	public AopdsActivity() {}

	/**
	 * @return The preferences manager of the application.
	 */
	public final AopdsPreferencesManager getPreferencesManager() {
		return preferencesManager;
	}
	
	/**
	 * @return The user manager of the application.
	 */
	public final AopdsUserManager getUserManager() {
		return userManager;
	}
	
	/**
	 * Creating the preferences and user managers, then bind the activity to 
	 * the preferences listener. Turn the language of the activity to 
	 * the language loaded by the preferences manager.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// creating the preferences manager
		preferencesManager = AopdsPreferencesManager.
								getInstance( getApplicationContext() );
		
		// creating the user manager
		userManager = AopdsUserManager.
								getInstance( getApplicationContext() );
		
		// set the language and load the layout text
		onLanguageChanged( preferencesManager.getLanguage() );
		
		// listen to preferences changings !
		preferencesManager.registerPreferencesListener(this);
		
		// listen to user changings
		userManager.registerUserChangedListener(this);
		
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * Called when the user is changed through the user manager.
	 */
	public void onUserChanged(User user) {}

	/**
	 * Changes the Locale (so the language) of the android 
	 * application configuration.
	 * 
	 * {@inheritDoc}
	 */
	public void onLanguageChanged(String language) {
		
		AopdsLogger.info( 
			getClass().getSimpleName(), 
			"Changing Language to " + language 
		);
		
		// getting android resources and configurations
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration config = res.getConfiguration(); 
		
		// creating new Locale with the newly provided language
		Locale newLanguage = new Locale(language);
		
		// reset the default language to use
		Locale.setDefault(newLanguage);

		// updating android configurations
		config.locale = newLanguage;
		res.updateConfiguration(config, dm); 
		
		
	}
	
}
