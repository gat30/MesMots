package com.aopds.aopdsServiceClient;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Language;
import com.aopds.aopdsData.domain.ServerComputedSuggestion;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceMalformedResponseException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.tools.AopdsLogger;

import android.util.Log;

public class AopdsServiceReturnHandler {

	private static final String LOG_TAG = "AopdsServiceReturnHandler (XML parser)";

	private static final String TAG_ENVELOPPE = "functionReturn";

	private static final String TAG_ARRAY = "array";

	private static final String TAG_INT = "int";
	private static final String TAG_BOOLEAN = "boolean";
	private static final String TAG_LONG = "long";
	private static final String TAG_STRING = "string";

	private static final String TAG_ERROR = "error";
	private static final String TAG_ERROR_CODE = "code";
	private static final String TAG_ERROR_MESSAGE = "message";

	private static final String TAG_USER = "u";
	private static final String TAG_USER_ID = "id";
	private static final String TAG_USER_EMAIL = "em";
	private static final String TAG_USER_FIRST_NAME = "fn";
	private static final String TAG_USER_LAST_NAME = "ln";

	private static final String TAG_DICTIONARY = "d";
	private static final String TAG_DICTIONARY_CODE = "id";
	private static final String TAG_DICTIONARY_NAME = "n";
	private static final String TAG_DICTIONARY_VERSION = "v";
	private static final String TAG_DICTIONARY_LANGUAGE_TO = "lt";
	private static final String TAG_DICTIONARY_LANGUAGE_FROM = "lf";

	private static final String TAG_LANGUAGE = "l";
	private static final String TAG_LANGUAGE_CODE = "id";
	private static final String TAG_LANGUAGE_NAME = "n";
	private static final String TAG_LANGUAGE_ABBREVIATION = "ab";

	private static final String TAG_SUGGESTION = "s";
	private static final String TAG_SUGGESTION_ID = "id";
	private static final String TAG_SUGGESTION_ID_LOCAL = "il";
	private static final String TAG_SUGGESTION_ACTION_TYPE = "at";
	private static final String TAG_SUGGESTION_WORD = "w";
	private static final String TAG_SUGGESTION_ENTRY = "e";
	private static final String TAG_SUGGESTION_PHONETIC = "p";
	private static final String TAG_SUGGESTION_PRONONCIATION_SENT = "ps";
	private static final String TAG_SUGGESTION_ADMIN_DECISION = "ad";
	private static final String TAG_SUGGESTION_ID_USER = "iu";
	private static final String TAG_SUGGESTION_USER = "u";
	private static final String TAG_SUGGESTION_ID_ADMIN = "ia";
	private static final String TAG_SUGGESTION_ADMIN = "a";
	private static final String TAG_SUGGESTION_ID_HEADWORD = "ih";
	private static final String TAG_SUGGESTION_HEADWORD = "h";
	private static final String TAG_SUGGESTION_ID_DICTIONARY = "idd";
	private static final String TAG_SUGGESTION_DICTIONARY = "d";

	private static final String ARRAY_INNER_TYPE_INT = "int";
	private static final String ARRAY_INNER_TYPE_DICTIONARY = "Dictionary";
	private static final String ARRAY_INNER_TYPE_USER = "User";
	private static final String ARRAY_INNER_TYPE_STRING = "string";
	private static final String ARRAY_INNER_TYPE_SUGGESTION = "Suggestion";

	private String source;

	private Object returnValue;

	public AopdsServiceReturnHandler(String source)
			throws AopdsServiceException,
			AopdsServiceMalformedResponseException, AopdsServiceServerException {
		this.source = source;

		try {
			parseSource();

			if (returnValue == null) {
				AopdsLogger.info(LOG_TAG, "returned value = null");
			} else {
				AopdsLogger.info(LOG_TAG, returnValue.toString());
			}

		} catch (IOException e) {
			// anyway, we shouldn't get this one, cause we're working on strings
			AopdsLogger.error(LOG_TAG, e.getMessage(), e);
		}

	}

	public Object getReturnValue() {
		return returnValue;
	}

	private void parseSource() throws AopdsServiceException,
			AopdsServiceMalformedResponseException, IOException,
			AopdsServiceServerException {

		// xml parser factory
		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			// xml parser in pull mode (means that we have to
			// cross the xml ourself and the parser will give
			// an event each time a new tag is found
			xpp = factory.newPullParser();

			// setting input file
			xpp.setInput(new StringReader(source));

		} catch (XmlPullParserException e) {
			throw new AopdsServiceException("Cannot initiate XML parser.", e);
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing results.");

		// current event
		int eventType;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml response.", source, e);
		}

		// current tag parsed
		String currentTag = "";

		Boolean enveloppeFound = false;

		Object result = null;

		// until the end of the document
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();
				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_ENVELOPPE)) {
					enveloppeFound = true;
					AopdsLogger.info(LOG_TAG,
							"Found envoloppe, start parsing entities.");
				} else {
					if (enveloppeFound) {
						if (currentTag.equals(TAG_ARRAY)) {
							result = handleArray(xpp, null);
						} else if (currentTag.equals(TAG_INT)) {
							result = handleInt(xpp, null);
						} else if (currentTag.equals(TAG_STRING)) {
							result = handleString(xpp, null);
						} else if (currentTag.equals(TAG_USER)) {
							result = handleUser(xpp, null);
						} else if (currentTag.equals(TAG_DICTIONARY)) {
							result = handleDictionary(xpp, null);
						} else if (currentTag.equals(TAG_SUGGESTION)) {
							result = handleSuggestion(xpp, null);
						} else if (currentTag.equals(TAG_ERROR)) {
							result = handleError(xpp, null);
							throw (AopdsServiceServerException) result;
						} else {
							AopdsLogger.info(LOG_TAG, "Found unknown tag '"
									+ currentTag + "', ingnoring it.");
						}
					} else {
						AopdsLogger.info(LOG_TAG, "Found tag '" + currentTag
								+ "' before the enveloppe, ignoring it.");
					}

				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)
				break;
			case XmlPullParser.TEXT: // found a tag value
				break;
			default:
				break;

			}

			// next event
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				throw new AopdsServiceMalformedResponseException(
						"Malformed xml response. Last tag was : " + currentTag,
						source, e);
			}

		}

		if (!enveloppeFound) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml response. No Enveloppe found.", source, null);
		} else {
			AopdsLogger.info(LOG_TAG, "Parsing finished.");
			returnValue = result;
		}

	}

	private AopdsServiceServerException handleError(XmlPullParser xpp,
			String tagName) throws AopdsServiceMalformedResponseException,
			IOException {

		int eventType = 0;
		String currentTag = "";
		String currentValue = "";
		Boolean stop = false;

		String tagNameToUse = TAG_ERROR;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing Error (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for Error definition, cannot map to Exception class.",
					source, e);
		}

		AopdsServiceServerException res = new AopdsServiceServerException();

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();
				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_ERROR_CODE)) {
					res.setServerCode(handleInt(xpp, TAG_ERROR_CODE));
				} else if (currentTag.equals(TAG_ERROR_MESSAGE)) {
					res.setMessage(handleString(xpp, TAG_ERROR_MESSAGE));
				} else {
					AopdsLogger.info(LOG_TAG, "Unknown tag " + currentTag
							+ " in error, ignoring it.");
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			case XmlPullParser.TEXT:

				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {

					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for Error definition, cannot map to Exception class."
									+ "Current tag parsed was : " + currentTag
									+ ", current value for this tag was : "
									+ currentValue, source, e);

				}
			}

		}

		AopdsLogger.info(LOG_TAG, "Error parsing finished.");

		return res;

	}

	private User handleUser(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		String currentTag = "";
		String currentValue = "";
		Boolean stop = false;

		String tagNameToUse = TAG_USER;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing User (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for User definition, cannot map to user class.",
					source, e);
		}

		User res = new User();

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();
				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_USER_ID)) {
					res.setId(handleLong(xpp, TAG_USER_ID));
				} else if (currentTag.equals(TAG_USER_EMAIL)) {
					res.setEmail(handleString(xpp, TAG_USER_EMAIL));
				} else if (currentTag.equals(TAG_USER_FIRST_NAME)) {
					res.setFirstName(handleString(xpp, TAG_USER_FIRST_NAME));
				} else if (currentTag.equals(TAG_USER_LAST_NAME)) {
					res.setLastName(handleString(xpp, TAG_USER_LAST_NAME));
				} else {
					AopdsLogger.info(LOG_TAG, "Unknown tag " + currentTag
							+ " in user, ignoring it.");
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			case XmlPullParser.TEXT:

				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {

					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for User definition, cannot map to User class."
									+ "Current tag parsed was : " + currentTag
									+ ", current value for this tag was : "
									+ currentValue, source, e);

				}
			}

		}

		AopdsLogger.info(LOG_TAG, "User parsing finished.");

		return res;

	}

	private Suggestion handleSuggestion(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		String currentTag = "";
		String currentValue = "";
		Boolean stop = false;

		String tagNameToUse = TAG_SUGGESTION;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing Suggestion (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for User definition, cannot map to user class.",
					source, e);
		}

		ServerComputedSuggestion res = new ServerComputedSuggestion();
		res.setUser(new User());
		res.setAdmin(new User());
		res.setHeadword(new Headword());
		res.setDictionary(new Dictionary());

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();
				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_SUGGESTION_ID)) {
					res.setServerId(handleInt(xpp, TAG_SUGGESTION_ID));
				} else if (currentTag.equals(TAG_SUGGESTION_ID_LOCAL)) {
					res.setId(handleInt(xpp, TAG_SUGGESTION_ID_LOCAL));
				} else if (currentTag.equals(TAG_SUGGESTION_ACTION_TYPE)) {
					res.setActionType(handleString(xpp,
							TAG_SUGGESTION_ACTION_TYPE));
				} else if (currentTag.equals(TAG_SUGGESTION_WORD)) {
					res.setWord(handleString(xpp, TAG_SUGGESTION_WORD));
				} else if (currentTag.equals(TAG_SUGGESTION_ENTRY)) {
					res.setEntry(handleString(xpp, TAG_SUGGESTION_ENTRY));
				} else if (currentTag.equals(TAG_SUGGESTION_PHONETIC)) {
					res.setPhonetic(handleString(xpp, TAG_SUGGESTION_PHONETIC));
				} else if (currentTag.equals(TAG_SUGGESTION_PRONONCIATION_SENT)) {
					res.setPronunciationRecorded(handleBoolean(xpp,
							TAG_SUGGESTION_PRONONCIATION_SENT));
				} else if (currentTag.equals(TAG_SUGGESTION_ADMIN_DECISION)) {
					res.setSynchroStatus(handleString(xpp,
							TAG_SUGGESTION_ADMIN_DECISION));
				} else if (currentTag.equals(TAG_SUGGESTION_ID_USER)) {
					res.getUser().setId(handleInt(xpp, TAG_SUGGESTION_ID_USER));
				} else if (currentTag.equals(TAG_SUGGESTION_USER)) {
					res.setUser(handleUser(xpp, TAG_SUGGESTION_USER));
				} else if (currentTag.equals(TAG_SUGGESTION_ID_ADMIN)) {
					res.getAdmin().setId(
							handleInt(xpp, TAG_SUGGESTION_ID_ADMIN));
				} else if (currentTag.equals(TAG_SUGGESTION_ADMIN)) {
					res.setAdmin(handleUser(xpp, TAG_SUGGESTION_ADMIN));
				} else if (currentTag.equals(TAG_SUGGESTION_ID_HEADWORD)) {
					res.getHeadword().setId(
							handleInt(xpp, TAG_SUGGESTION_ID_HEADWORD));
				} else if (currentTag.equals(TAG_SUGGESTION_ID_DICTIONARY)) {
					res.getDictionary().setCode(
							handleInt(xpp, TAG_SUGGESTION_ID_DICTIONARY));
				} else if (currentTag.equals(TAG_SUGGESTION_DICTIONARY)) {
					res.setDictionary(handleDictionary(xpp,
							TAG_SUGGESTION_DICTIONARY));
				} else {
					AopdsLogger.info(LOG_TAG, "Unknown tag " + currentTag
							+ " in suggestion, ignoring it.");
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			case XmlPullParser.TEXT:

				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {

					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for Suggestion definition, cannot map to Suggestion class."
									+ "Current tag parsed was : " + currentTag
									+ ", current value for this tag was : "
									+ currentValue, source, e);

				}
			}

		}

		AopdsLogger.info(LOG_TAG, "Suggestion parsing finished.");

		return res;

	}

	private String handleString(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		String result = "";

		String tagNameToUse = TAG_STRING;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing string (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();

			if (eventType == XmlPullParser.TEXT) {
				result = xpp.getText();
				eventType = xpp.next();
			}

			AopdsLogger.info(LOG_TAG, "String parsing finished.");

			return result;

		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for string definition.", source, e);
		}

	}

	private int handleInt(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		int result;
		String tagNameToUse = TAG_INT;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing int (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();

			if (eventType == XmlPullParser.TEXT) {

				result = Integer.parseInt(xpp.getText());

			} else
				throw new AopdsServiceMalformedResponseException(
						"Malformed xml for int definition. Empty <int> tag.",
						source, null);

			eventType = xpp.next();

			AopdsLogger.info(LOG_TAG, "int parsing finished.");

			return result;

		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for int definition.", source, e);
		} catch (NumberFormatException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for int definition. Impossible to parse int from '"
							+ xpp.getText() + "'", source, e);
		}

	}

	private Boolean handleBoolean(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		int result;
		String tagNameToUse = TAG_BOOLEAN;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing boolean (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();

			if (eventType == XmlPullParser.TEXT) {

				result = Integer.parseInt(xpp.getText());

				if (!(result == 1) && !(result == 0)) {
					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for int definition. Impossible to parse boolean from '"
									+ xpp.getText() + "'", source, null);
				}

			} else
				throw new AopdsServiceMalformedResponseException(
						"Malformed xml for int definition. Empty <boolean> tag.",
						source, null);

			eventType = xpp.next();

			AopdsLogger.info(LOG_TAG, "boolean parsing finished.");

			return (result == 1);

		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for int definition.", source, e);
		} catch (NumberFormatException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for int definition. Impossible to parse boolean from '"
							+ xpp.getText() + "'", source, e);
		}

	}

	private long handleLong(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		int result;
		String tagNameToUse = TAG_LONG;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing long (tagName = "
				+ tagNameToUse + ").");

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for long definition.", source, e);
		}

		if (eventType == XmlPullParser.TEXT) {
			try {
				result = Integer.parseInt(xpp.getText());
			} catch (NumberFormatException e) {
				throw new AopdsServiceMalformedResponseException(
						"Malformed xml for long definition. Impossible to parse long from '"
								+ xpp.getText() + "'", source, e);
			}

		} else
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for long definition. Empty <long> tag.",
					source, null);

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for long definition.", source, e);
		}

		AopdsLogger.info(LOG_TAG, "long parsing finished.");

		return result;

	}

	private Dictionary handleDictionary(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		String currentTag = "";
		String currentValue = "";
		Boolean stop = false;

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for dictionary definition.", source, e);
		}

		String tagNameToUse = TAG_DICTIONARY;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing Dictionary (tagName="
				+ tagNameToUse + ").");

		Dictionary res = new Dictionary();

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();

				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_DICTIONARY_LANGUAGE_FROM)) {
					res.setLanguageFrom(handleLanguage(xpp,
							TAG_DICTIONARY_LANGUAGE_FROM));
				} else if (currentTag.equals(TAG_DICTIONARY_LANGUAGE_TO)) {
					res.setLanguageTo(handleLanguage(xpp,
							TAG_DICTIONARY_LANGUAGE_TO));
				} else if (currentTag.equals(TAG_DICTIONARY_CODE)) {
					res.setCode(handleInt(xpp, TAG_DICTIONARY_CODE));
				} else if (currentTag.equals(TAG_DICTIONARY_NAME)) {
					res.setName(handleString(xpp, TAG_DICTIONARY_NAME));
				} else if (currentTag.equals(TAG_DICTIONARY_VERSION)) {
					res.setVersion(handleInt(xpp, TAG_DICTIONARY_VERSION));
				} else {
					AopdsLogger.info(LOG_TAG, "Unknown tag " + currentTag
							+ " found in dictionary, ignoring it.");
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			case XmlPullParser.TEXT:

				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {
					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for Dictionary definition, cannot map to Dictionary class."
									+ "Current tag parsed was : " + currentTag
									+ ", current value for this tag was : "
									+ currentValue, source, e);
				}
			}

		}

		AopdsLogger.info(LOG_TAG, "Finished parsing Dictionary.");

		return res;

	}

	private Language handleLanguage(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		int eventType = 0;
		String currentTag = "";
		String currentValue = "";
		Boolean stop = false;

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for Language definition.", source, e);
		}

		String tagNameToUse = TAG_LANGUAGE;

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing Language (tagName = "
				+ tagNameToUse + ").");

		Language res = new Language();

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				currentTag = xpp.getName();
				AopdsLogger.info(LOG_TAG, "Found tag " + currentTag);

				if (currentTag.equals(TAG_LANGUAGE_CODE)) {
					res.setCode(handleInt(xpp, TAG_LANGUAGE_CODE));
				} else if (currentTag.equals(TAG_LANGUAGE_NAME)) {
					res.setName(handleString(xpp, TAG_LANGUAGE_NAME));
				} else if (currentTag.equals(TAG_LANGUAGE_ABBREVIATION)) {
					res.setAbreviation(handleString(xpp,
							TAG_LANGUAGE_ABBREVIATION));
				} else {
					AopdsLogger.info(LOG_TAG, "Unknown tag " + currentTag
							+ " in language, ignoring it.");
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			case XmlPullParser.TEXT:
				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {
					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for Language definition, cannot map to Language class."
									+ "Current tag parsed was : " + currentTag
									+ ", current value for this tag was : "
									+ currentValue, source, e);
				}
			}
		}

		AopdsLogger.info(LOG_TAG, "Finished parsing Language.");

		return res;

	}

	private String getXmlAttribute(XmlPullParser xpp, String attributeName) {

		int attributeCount = xpp.getAttributeCount();

		if (attributeCount <= 0)
			return null;
		else {

			int index = 0;
			Boolean found = false;

			while ((index < attributeCount) && !found) {
				if (xpp.getAttributeName(index).equals(attributeName)) {
					found = true;
				} else
					index++;
			}

			if (found)
				return xpp.getAttributeValue(index);
			else
				return null;
		}

	}

	private Object handleArray(XmlPullParser xpp, String tagName)
			throws AopdsServiceMalformedResponseException, IOException {

		String innerType = getXmlAttribute(xpp, "innerType");
		int eventType = 0;
		Boolean stop = false;
		String tagNameToUse = TAG_ARRAY;

		if (innerType == null) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for array definition. Arrays must declare innerType attribute.",
					source, null);
		}

		try {
			eventType = xpp.next();
		} catch (XmlPullParserException e) {
			throw new AopdsServiceMalformedResponseException(
					"Malformed xml for array definition.", source, e);
		}

		if (tagName != null) {
			tagNameToUse = tagName;
		}

		AopdsLogger.info(LOG_TAG, "Starting parsing Array(innerType="
				+ innerType + ") (tagName=" + tagNameToUse + ").");

		ArrayList<Object> result = new ArrayList<Object>();

		// until the end of the document
		while ((eventType != XmlPullParser.END_DOCUMENT) && !stop) {

			switch (eventType) {
			case XmlPullParser.START_TAG: // found a tag

				if (innerType.equals(ARRAY_INNER_TYPE_INT)) {
					result.add(handleInt(xpp, null));
				} else if (innerType.equals(ARRAY_INNER_TYPE_STRING)) {
					result.add(handleString(xpp, null));
				} else if (innerType.equals(ARRAY_INNER_TYPE_DICTIONARY)) {
					result.add(handleDictionary(xpp, null));
				} else if (innerType.equals(ARRAY_INNER_TYPE_USER)) {
					result.add(handleUser(xpp, null));
				} else if (innerType.equals(ARRAY_INNER_TYPE_SUGGESTION)) {
					result.add(handleSuggestion(xpp, null));
				}

				break;
			case XmlPullParser.END_TAG: // finishing a tag (value parsed)

				if (xpp.getName().equals(tagNameToUse)) {
					stop = true;
				}

				break;
			default:
				break;

			}

			// next event
			if (!stop) {
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException e) {
					throw new AopdsServiceMalformedResponseException(
							"Malformed xml for Array definition, cannot map to List class."
									+ "Current tag parsed was : "
									+ xpp.getName()
									+ ", current value for this tag was : "
									+ xpp.getText()
									+ ", innerType for this array was : "
									+ innerType, source, e);
				}
			}

		}

		AopdsLogger.info(LOG_TAG, "Array parsing Finished.");

		return result;
	}

}
