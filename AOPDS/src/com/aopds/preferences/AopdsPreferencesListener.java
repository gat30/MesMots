package com.aopds.preferences;


/**
 * A listener for preferences changings. Any class implementing this interface
 * can be notified by a PreferenceManager that some preferences have been changed.
 * 
 * @author Julien Wollsheid | July 2011
 *
 */
public interface AopdsPreferencesListener {

	/**
	 * Called when the user changes the language for another in the preferences 
	 * of the application.
	 * 
	 * @param language The language newly set by the user, in ISO-639 format.
	 */
	public void onLanguageChanged(String language);
	
}
