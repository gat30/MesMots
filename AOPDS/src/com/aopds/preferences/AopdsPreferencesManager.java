package com.aopds.preferences;

import java.io.File;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.aopds.R;
import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.Language;
import com.aopds.tools.AopdsLogger;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

/**
 * 
 * Singleton<br>
 * Observable<br>
 * <br>
 * 
 * Preferences manager for AOPDS application.
 * 
 * This class manages all preferences and the way they are stored. Use this to
 * change any preference. <br>
 * <br>
 * 
 * Each time a preference is changed, the instance of this class will act as an
 * observable notifying all preferences listeners that a preference has been
 * changed.<br>
 * <br>
 * 
 * The preferences are stored in a XML file.
 * 
 * @author Julien Wollscheid | July 2011
 * 
 */
public class AopdsPreferencesManager {

	/*
	 * Singleton
	 */

	/**
	 * Singleton instance.
	 */
	private static AopdsPreferencesManager instance;

	/*
	 * Observable system
	 */

	/**
	 * List of listeners to notify in case of preferences changing.
	 */
	private ArrayList<AopdsPreferencesListener> listeners;

	/*
	 * Required system configuration
	 */

	/**
	 * Android application context
	 */
	private Context context;

	/*
	 * Preference storage ( XML )
	 */

	/**
	 * File where the preferences are stored.
	 */
	private static final String PREFERENCES_FILE_URL = "preferences.xml";

	/**
	 * XML tag name for language preference.
	 */
	private static final String LANGUAGE_TAG = "language";

	private static final String PREFERENCES_TAG = "preferences";

	/*
	 * Preferences
	 */

	/**
	 * Language of the application.
	 */
	private String language;

	/**
	 * Authorized languages. Loaded from resources.
	 */
	private ArrayList<Language> supportedLanguages;

	/* *************************************************************
	 * Constructor and singleton system
	 */

	/**
	 * Creates the preference manager.
	 * 
	 * @param context
	 *            The android application context.
	 */
	private AopdsPreferencesManager(Context context) {

		this.context = context;

		listeners = new ArrayList<AopdsPreferencesListener>();

		supportedLanguages = new ArrayList<Language>();

	}

	/**
	 * When called the first time, loads the preferences. If the preferences
	 * file is not existing or corrupted, load default preferences.
	 * 
	 * @param context
	 *            The android application context.
	 * @return The instance of the preferences manager.
	 */
	public static AopdsPreferencesManager getInstance(Context context) {

		if (instance == null) {

			instance = new AopdsPreferencesManager(context);

			// loading supported languages
			instance.loadAuthorizedLanguages();

			// loading preferences
			try {
				if (instance.preferencesPersisted()) {
					instance.loadPreferences();
				} else {
					AopdsLogger
							.info(instance.getClass().getSimpleName(),
									"Preferences not persisted, loading default ones ...");
					instance.loadDefaultPreferences();
				}

			} catch (Exception e) {

				AopdsLogger
						.info(instance.getClass().getSimpleName(),
								"Impossible to load preferences, loading default ones ...");

				// if any problem loading the XML file, we load the
				// default preferences.
				instance.loadDefaultPreferences();

			}
		}

		return instance;
	}

	/* *************************************************************
	 * Language preference
	 */

	/**
	 * Get the current language of the application.
	 * 
	 * @return The language of the application, in ISO-639 format.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set the language of the application. All listeners will be notified of
	 * the changing.
	 * 
	 * Note that this method doesn't persist the changing, you must call
	 * persistPreferences() to persist it.
	 * 
	 * @param language
	 *            The new language to set in ISO-639 language.
	 * @throws Exception
	 *             If the language is not supported.
	 */
	public void setLanguage(String language) throws Exception {

		if (!language.equals(this.language)) {

			// verifying if the language is supported
			if (isLanguageSupported(language)) {

				AopdsLogger.info(getClass().getSimpleName(),
						"Changing language to " + language);

				this.language = language;
				notifyLanguageChanged(); // notifying listeners
			} else {
				throw new Exception("Hello");
			}

		}

	}

	/**
	 * Get the list of supported languages.
	 * 
	 * @return A list containing all supported languages.
	 */
	public List<Language> getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 * Verifying if a Language is supported.
	 * 
	 * @param language
	 *            The language is verify in ISO-639 format.
	 * @return True if the language is supported, false otherwise.
	 */
	private Boolean isLanguageSupported(String language) {

		Iterator<Language> it = supportedLanguages.iterator();
		Boolean found = false;

		// searching in the list
		while (it.hasNext() && !found) {
			if (it.next().getAbreviation().equals(language)) {
				found = true;
			}
		}

		return found;

	}

	/**
	 * Loads all supported languages from the Aopds DB.
	 * 
	 * We actually need the full language (not only the ISO-639 abbreviation) to
	 * provide the list to the preferences user interface.
	 */
	private void loadAuthorizedLanguages() {

		AopdsLogger.info(getClass().getSimpleName(),
				"Loading authorized languages ...");

		/**
		 * supported languages abbreviations in ISO-639
		 */
		String[] languages = context.getResources().getStringArray(
				R.array.authorizedLanguages);

		AopdsDatabase db = AopdsDatabase.getInstance(context);

		// getting full languages from database.
		for (String language : languages) {
			try {
				supportedLanguages.add(db.getLanguageByAbreviation(language));
			} catch (AopdsDatabaseException e) {
				Log.e("", e.getMessage(), e);
			}
		}

	}

	/* *************************************************************
	 * Observable system
	 */

	/**
	 * Register as a preferences listener. Any object registering will be
	 * notified of any preference changing.
	 * 
	 * @param pl
	 *            The instance which has to listen to preferences changing.
	 */
	public void registerPreferencesListener(AopdsPreferencesListener pl) {
		this.listeners.add(pl);
	}

	/**
	 * Notify all listeners that the language preference has changed.
	 */
	private void notifyLanguageChanged() {
		for (AopdsPreferencesListener pl : listeners) {
			pl.onLanguageChanged(language);
		}
	}

	/* *************************************************************
	 * Load and persist preference system
	 */

	/**
	 * @return True if the preferences are stored, false if nothing is
	 *         persisted.
	 */
	public Boolean preferencesPersisted() {
		File f = context.getFileStreamPath(PREFERENCES_FILE_URL);
		return f.exists();
	}

	/**
	 * Load all default preferences from resources, may not fail.
	 */
	private void loadDefaultPreferences() {
		this.language = context.getString(R.string.default_language);
	}

	/**
	 * Loads preferences from an XML file.
	 * 
	 * @throws Exception
	 *             If loading is impossible.
	 */
	private void loadPreferences() throws Exception {

		AopdsLogger.info(getClass().getSimpleName(), "Loading preferences ...");

		// xml parser factory
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);

		// xml parser in pull mode (means that we have to
		// cross the xml ourself and the parser will give
		// an event each time a new tag is found
		XmlPullParser xpp = factory.newPullParser();

		// setting input file
		xpp.setInput(new InputStreamReader(context
				.openFileInput(PREFERENCES_FILE_URL)));

		// current event
		int eventType = xpp.getEventType();

		// current tag parsed
		String currentTag = "";

		// current tag value parsed
		String currentValue = "";

		// true if all preferences are found in the file
		// if the file is empty, we can throw an exception
		// to force loading default values.
		Boolean allPreferencesFound = false;

		// until the end of the document
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG: // found a tag
				currentTag = xpp.getName();
				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (!xpp.getName().equals(PREFERENCES_TAG)) {

					if (currentTag.equals(LANGUAGE_TAG)) {
						allPreferencesFound = true;
					}

					// getting the preference
					handlePreference(currentTag, currentValue);
				}

				break;
			case XmlPullParser.TEXT: // found a tag value
				currentValue = xpp.getText();
				break;
			default:
				break;

			}

			// next event
			eventType = xpp.next();

		}

		if (!allPreferencesFound) {
			throw new Exception();
		}

	}

	/**
	 * Persists the preferences.
	 * 
	 * @throws Exception
	 *             If persisting failed.
	 */
	public void persistPreferences() throws Exception {

		AopdsLogger.info(getClass().getSimpleName(),
				"Persisting preferences ...");

		/*
		 * Persisting in XML format:
		 * 
		 * <preferences>
		 * 
		 * <language type='String' >fr</language>
		 * 
		 * </preferences>
		 */

		// xml generator
		XmlSerializer serializer = Xml.newSerializer();

		// the output file, opened in private mode (only AOPDS can access it)
		OutputStreamWriter writer = new OutputStreamWriter(
				context.openFileOutput(PREFERENCES_FILE_URL,
						Context.MODE_PRIVATE));

		// persisting ...
		try {
			// setting the output file
			serializer.setOutput(writer);

			// UTF-8 encoding
			serializer.startDocument("UTF-8", true);

			serializer.startTag("", PREFERENCES_TAG);

			serializer.startTag("", LANGUAGE_TAG);
			serializer.attribute("", "type", "String");
			serializer.text(language);
			serializer.endTag("", LANGUAGE_TAG);

			serializer.endTag("", PREFERENCES_TAG);
			serializer.endDocument();

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Handle a preference, verifying if it is existing, and setting it.
	 * 
	 * @param preferenceName
	 *            The name of the preference to set.
	 * @param value
	 *            The value of the preference.
	 * @throws Exception
	 *             If the preference is not existing or not correct.
	 */
	private void handlePreference(String preferenceName, String value)
			throws Exception {

		if (value == null) {
			throw new Exception("");
		}

		if (preferenceName.equals(LANGUAGE_TAG)) {

			setLanguage(value);
		}

	}

}
