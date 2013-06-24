package com.aopds.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.aopds.aopdsData.domain.User;
import com.aopds.tools.AopdsLogger;
import com.aopds.user.userManagerException.UserPersistingImpossible;

import android.content.Context;
import android.util.Xml;

/**
 * Singleton<br>
 * <br>
 * 
 * 
 * User manager for APDS application. Manager the current user of the
 * application and the way it is persisted.
 * 
 * The user is persisted in a XML file.
 * 
 * @author Julien Wollscheid | July 2011
 * 
 */
public class AopdsUserManager {

	/**
	 * The singleton instance.
	 */
	private static AopdsUserManager instance;

	/**
	 * XML file where the user is persisted.
	 */
	private static final String USER_FILE = "user.xml";

	/**
	 * Name of the XML tag for the user.
	 */
	private static final String USER_TAG = "user";

	/**
	 * Name of the XML tag for the user id.
	 */
	private static final String ID_TAG = "id";

	/**
	 * Name of the XML tag for the user first name.
	 */
	private static final String FIRST_NAME_TAG = "first_name";

	/**
	 * Name of the XML tag for the user last name.
	 */
	private static final String LAST_NAME_TAG = "last_name";

	/**
	 * Name of the XML tag for the user e-mail.
	 */
	private static final String E_MAIL_TAG = "e_mail";

	/**
	 * Name of the XML tag for the user password.
	 */
	private static final String PASSWORD_TAG = "password";

	/**
	 * The android application context.
	 */
	private Context context;

	/*
	 * Observable system
	 */

	/**
	 * List of listeners to notify in case of preferences changing.
	 */
	private ArrayList<AopdsUserChangedListener> listeners;

	/**
	 * The current user of the application or null if no user registered.
	 */
	private User user;

	/**
	 * Creates the user manager.
	 * 
	 * @param context
	 *            The android application context.
	 */
	private AopdsUserManager(Context context) {
		this.context = context;
		listeners = new ArrayList<AopdsUserChangedListener>();
	}

	/**
	 * Loads the user at first call if it is persisted.
	 * 
	 * @param context
	 *            The android application context.
	 * @return The user manager instance.
	 */
	public static AopdsUserManager getInstance(Context context) {

		if (instance == null) {
			instance = new AopdsUserManager(context);
			try {
				// loading user if it exists
				if (instance.isUserPersisted()) {
					instance.loadUser();
				} else {
					AopdsLogger.info(instance.getClass().getSimpleName(),
							"User not persisted, no user loaded ...");
				}
			} catch (Exception e) {

				instance.user = null;

				AopdsLogger.info(instance.getClass().getSimpleName(),
						"Can't load User ...");
			}
		}

		return instance;
	}

	public void registerUserChangedListener(AopdsUserChangedListener listener) {
		listeners.add(listener);
	}

	public void notifyUserChanged() {
		for (AopdsUserChangedListener listener : listeners) {
			listener.onUserChanged(user);
		}
	}

	public void discardUser() {
		user = null;

		if (isUserPersisted()) {
			File uf = context.getFileStreamPath(USER_FILE);
			uf.delete();
		}
	}

	/**
	 * Loads the user from the XML file.
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException.
	 */
	private void loadUser() throws XmlPullParserException, IOException {

		// xml parser factory
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);

		// xml parser
		XmlPullParser xpp = factory.newPullParser();

		// setting input file
		xpp.setInput(new InputStreamReader(context.openFileInput(USER_FILE)));

		// creating user
		user = new User();

		// current XML event
		int eventType = xpp.getEventType();

		// current tag parsed
		String currentTag = "";

		// current tag value parsed
		String currentValue = "";

		// crossing the XML document
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT: // beginning of doc
				break;
			case XmlPullParser.START_TAG: // beginning a tag
				currentTag = xpp.getName();
				break;
			case XmlPullParser.END_TAG: // tag fully parsed

				if (!xpp.getName().equals(USER_TAG)) {
					// handling user field
					handleUserField(currentTag, currentValue);
				}
				break;
			case XmlPullParser.TEXT: // tag value
				currentValue = xpp.getText();
				break;
			default:
				break;
			}

			// next event
			eventType = xpp.next();

		}

	}

	/**
	 * Handles a user field from the XML and map it into the current user.
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The value of the field.
	 */
	private void handleUserField(String name, String value) {

		if (name.equals(FIRST_NAME_TAG)) {
			user.setFirstName(value);
		} else if (name.equals(LAST_NAME_TAG)) {
			user.setLastName(value);
		} else if (name.equals(E_MAIL_TAG)) {
			user.setEmail(value);
		} else if (name.equals(ID_TAG)) {
			user.setId(Long.parseLong(value));
		} else if (name.equals(PASSWORD_TAG)) {
			user.setPassword(value);
		} else {
			AopdsLogger.info(getClass().getSimpleName(),
					"Getting unknown user field '" + name + "' with value '"
							+ value + "'");
		}

	}

	/**
	 * Get the current user.
	 * 
	 * @return The current user registered in the application.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return True if the user is persisted.
	 */
	public Boolean isUserPersisted() {
		File uf = context.getFileStreamPath(USER_FILE);
		return uf.exists();
	}

	/**
	 * Persists the current user of the application.
	 * 
	 * @throws Exception
	 */
	public void persistUser() throws UserPersistingImpossible {

		AopdsLogger.info(getClass().getSimpleName(), "Persisting user ...");

		if (user == null) {
			throw new UserPersistingImpossible(null);
		}

		// xml generator
		XmlSerializer serializer = Xml.newSerializer();

		// output file
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(context.openFileOutput(USER_FILE,
					Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			throw new UserPersistingImpossible(e);
		}

		// Writing XML
		try {

			/*
			 * XML format :
			 * 
			 * <user> <first_name>Jean</first_name> ...
			 * 
			 * </user>
			 */

			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);

			serializer.startTag("", USER_TAG);

			serializer.startTag("", ID_TAG);
			serializer.attribute("", "type", "long");
			serializer.text(Long.toString(user.getId()));
			serializer.endTag("", ID_TAG);

			serializer.startTag("", FIRST_NAME_TAG);
			serializer.attribute("", "type", "String");
			serializer.text(user.getFirstName());
			serializer.endTag("", FIRST_NAME_TAG);

			serializer.startTag("", LAST_NAME_TAG);
			serializer.attribute("", "type", "String");
			serializer.text(user.getLastName());
			serializer.endTag("", LAST_NAME_TAG);

			serializer.startTag("", E_MAIL_TAG);
			serializer.attribute("", "type", "String");
			serializer.text(user.getEmail());
			serializer.endTag("", E_MAIL_TAG);

			serializer.startTag("", PASSWORD_TAG);
			serializer.attribute("", "type", "String");
			serializer.text(user.getPassword());
			serializer.endTag("", PASSWORD_TAG);

			/*
			 * serializer.startTag("", LANGUAGE_TAG); serializer.attribute("",
			 * "type", "String" ); serializer.text(
			 * Integer.toString((user.getLanguage().getCode())));
			 * serializer.endTag("", LANGUAGE_TAG);
			 */

			serializer.endTag("", USER_TAG);
			serializer.endDocument();

		} catch (Exception e) {
			throw new UserPersistingImpossible(e);
		}

	}

	/**
	 * Change the user to a new one. Note that this method is not persisting the
	 * user. You have to call persistUser() to persist the changing.
	 * 
	 * @param user
	 *            The new user.
	 */
	public void setUser(User user) {
		this.user = user;
		notifyUserChanged();

		if (user == null) {
			discardUser();
		}

	}

}
