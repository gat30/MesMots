package com.aopds.aopdsData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.AopdsDataException.DataBaseRuntimeException;
import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Language;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.tools.AopdsLogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

/**
 * Singleton<br><br>
 * 
 * Main Database handler/connector for AOPDS application.<br><br>
 * 
 * This class is a helper providing methods to access data stored on the phone.
 * This data may be stored in a SQLite DB and/or XML.
 * 
 * All data used in AOPDS must be managed by this class, which is actually 
 * hiding the way the data is stored to the upper layers.
 * 
 * @author Julien Wollscheid | July 2011
 *
 */
public final class AopdsDatabase {

	/**
	 * Singleton instance.
	 */
	private static AopdsDatabase instance;
	
	/*
	 * Debug
	 */
	
	/**
	 * Tag for logging/debugging system
	 */
	private final static String LOG_TAG = "AopdsDatabase";
	
	/*
	 * SQLite DB information
	 */
	
	/**
	 * SQLite file name
	 */
	private final static String DATABASE_NAME = "aopdsDB1";
	
	/**
	 * SQLite DB version
	 */
	private final static int DATABASE_VERSION = 1;
	
	/*
	 * SQLite DB tables and fields names.
	 */
	
	// table language
	
	private final static String TABLE_LANGUAGE = "LANGUAGE";
	private final static String TF_LANGUAGE_CODE = "CODE";
	private final static String TF_LANGUAGE_ABREVIATION = "ABREVIATION";
	private final static String TF_LANGUAGE_NAME = "NAME";
	private final static String TF_LANGUAGE_NAME_IN_ENGLISH = "NAME_IN_ENGLISH";
	
	// table headword
	
	private final static String TABLE_HEADWORD = "HEADWORD";
	private final static String TF_HEADWORD_ID = "ID";
	private final static String TF_HEADWORD_DICT_ID = "DICTIONARY_ID";
	private final static String TF_HEADWORD_WORD = "WORD";
	private final static String TF_HEADWORD_ENTRY = "ENTRY";
	private final static String TF_HEADWORD_PHONETIC = "PHONETIC";
	private final static String TF_HEADWORD_PRONUN_EXISTS = "PRONUNCIATION_EXISTS";
	
	// table suggestion
	
	private final static String TABLE_SUGGESTION = "SUGGESTION";
	private final static String TF_SUGGESTION_ID = "ID";
	private final static String TF_SUGGESTION_HEADWORD_ID = "HEADWORD_ID";
	private static final String TF_SUGGESTION_DICT_ID = "DICTIONARY_ID";
	private final static String TF_SUGGESTION_WORD = "WORD";
	private final static String TF_SUGGESTION_PHONETIC = "PHONETIC";
	private final static String TF_SUGGESTION_ENTRY = "ENTRY";
	private final static String TF_SUGGESTION_ACT_TYPE = "ACTION_TYPE";
	private final static String TF_SUGGESTION_PRONUN_REC = "PRONUNCIATION_RECORDED";
	private final static String TF_SUGGESTION_SYNCH_STAT = "SYNCHRO_STATUS";
	private final static String TF_SUGGESTION_CREATION_DATE = "CREATION_DATE";
	private final static String TF_SUGGESTION_DICT_VERSION = "DICTIONARY_VERSION";
	
	public static final String ACTION_TYPE_ADDITION = "a";
	public static final String ACTION_TYPE_MODIFICATION = "m";
	public static final String ACTION_TYPE_DELETION = "d";
	
	// table installed dictionary
	
	private final static String TABLE_INSTALLED_DICTIONARY = "INSTALLED_DICTIONARY";
	private final static String TF_ID_ID = "ID";
	private final static String TF_ID_LANGUAGE_TO = "LANGUAGE_TO";
	private final static String TF_ID_LANGUAGE_FROM = "LANGUAGE_FROM";
	private final static String TF_ID_NAME = "NAME";
	private final static String TF_ID_VERSION = "VERSION";
	
	/**
	 * SQLIte DB connector. Use this connector to access the SQLite DB.
	 */
	DictionaryOpenHelper db;
	
	/* *************************************************************************
	 * Constructor and singleton system
	 */
	
	/**
	 * Creates the AOPDS database manager.
	 * 
	 * @param context The android application context.
	 */
	private AopdsDatabase(Context context) {
		db = new DictionaryOpenHelper(context);
	}
	
	/**
	 * Singleton system. Get the instance of the database manager.
	 * 
	 * @param context The android applciation context.
	 * 
	 * @return The instance of the AOPDS database manager.
	 */
	public static AopdsDatabase getInstance(Context context) {
		
		if ( instance == null ) {
			instance = new AopdsDatabase(context);
		}
		return instance;
	}

	/* *************************************************************************
	 * Data source management methods
	 */
	
	/**
	 * Closes all opened data sources. MUST BE CALLED WHEN THE APPLICATION 
	 * IS SHUT DOWN to safely close every opened connector.
	 */
	public void closeDataSource() {
		
		AopdsLogger.info( 
			LOG_TAG, 
			"Closing data source."
		);
		
		// getting a connection
		SQLiteDatabase connection = db.getReadableDatabase();
		
		// closing the connection if existing
		if (connection != null) {
			connection.close();
		}
		
	}
	
	/* *************************************************************************
	 * Data access helpers
	 */
	
	/**
	 * Get a Headword by its id. The headword is retrieved WITHOUT its dictionary.
	 * 
	 * @param id The id of the searched word.
	 * @return The headword with the provided id or null if it doesn't exist.
	 * @throws AopdsDatabaseException If the data cannot be accessed.
	 */
	public Headword getWordById (long id) throws AopdsDatabaseException {
		
		/*
		 * Generating the SQL query
		 * 
		 * Getting all fields of the table HEADWORD for the specified id.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// the table to query
     	qb.setTables(TABLE_HEADWORD);
     	
     	// the fields we ask
     	String[] fields = { 
     		TF_HEADWORD_ID,
     		TF_HEADWORD_DICT_ID,
     		TF_HEADWORD_WORD,
     		TF_HEADWORD_ENTRY,
     		TF_HEADWORD_PHONETIC,
     		TF_HEADWORD_PRONUN_EXISTS
     	};
     	
     	// the condition on the id, the ? will be replaced on execution
     	// by the searchd value.
     	String whereClause = 
     		TF_HEADWORD_ID + " = ?"
     	;
     	
     	// the values of the condition to replace the ?.
     	String[] selectArgs = {
     		Long.toString(id)
     	};
     	
     	/*
     	 * Launching the query
     	 */
     	
     	try {
     		// getting a connector for reading only
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// launching the query !!!
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		selectArgs,
 	     		null,
 	     		null,
 	     		TF_HEADWORD_WORD + " ASC" // ascendant ordering
 	     	);
 	     	
     		// verifying the contained data
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // the cursor is containing data
 	     		
 	     		// mapping into a headword object 
 	     		
 	     		// pronunciation
 	 			Boolean pronuncExists = false;
 	 			if ( res.getInt(5) != 0 ) {
 	 				pronuncExists = true;
 	 			}
 	 			
 	 			Headword headword = new Headword(
 	 				res.getInt(0), // id
 	 				res.getString(2), // word
 	 				res.getString(3), // entry
 	 				res.getString(4), // phonetics
 	 				null, // dictionary id not needed
 	 				pronuncExists
 	 			);
 	 			
 	 			// closing the cursor
 	     		res.close();
 	     		
 	     		return headword;
 	     		
 	     	} else { // the cursor is not containing data
 	     		return null;
 	     	}
     		
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}
     	
	}
	
	
	/**
     * Add a word in the database (just the word and its entry for the moment)
     */
    public void addSuggestion(Suggestion s) throws AopdsDatabaseException
    {
    	SQLiteDatabase conn = db.getWritableDatabase();
    	
    	AopdsLogger.info( 
    		getClass().getSimpleName(),
    		"Saving suggestion " + s
    	);
    	
    	String table = TABLE_SUGGESTION;
    	String nullColumnHack = null;
    	ContentValues values = new ContentValues();
    	
    	values.put(TF_SUGGESTION_WORD, s.getWord() );
    	values.put(TF_SUGGESTION_ENTRY, s.getEntry() );
    	values.put(TF_SUGGESTION_PHONETIC, s.getPhonetic() );
    	values.put(TF_SUGGESTION_PRONUN_REC, s.getPronunciationRecorded() );
    	values.put(TF_SUGGESTION_SYNCH_STAT, s.getSynchroStatus() );
    	values.put(TF_SUGGESTION_ACT_TYPE, s.getActionType() );
    	values.put(TF_SUGGESTION_DICT_VERSION, s.getDictionaryVersion());
    	
    	if ( s.getCreationDate() != null ) {
    		values.put(TF_SUGGESTION_CREATION_DATE, s.getCreationDate().getTime() );
    	}
    	if ( s.getDictionary() != null ) {
    		values.put(TF_SUGGESTION_DICT_ID, s.getDictionary().getCode() );
    	}
    	
    	if ( s.getHeadword() != null && !s.isAddActionType() ) {
    		values.put(TF_SUGGESTION_HEADWORD_ID, s.getHeadword().getId() );
    	}
    	
    	
    	// Launch the insert request
    	long res = conn.insert(table, nullColumnHack, values);
    	
    	if(res < 0)
    	{
    		throw new DataBaseRuntimeException(DATABASE_NAME, DATABASE_VERSION, null);
    	} else {
    		s.setId( res );
    	}
    	
    }
    
    
    public void modifySuggestion(Suggestion s) throws AopdsDatabaseException {
    	
    	SQLiteDatabase conn = db.getWritableDatabase();
    	
    	AopdsLogger.info( 
    		getClass().getSimpleName(),
    		"Modifying suggestion " + s
    	);
    	
    	String table = TABLE_SUGGESTION;
    	ContentValues values = new ContentValues();
    	
    	values.put(TF_SUGGESTION_WORD, s.getWord() );
    	values.put(TF_SUGGESTION_ENTRY, s.getEntry() );
    	values.put(TF_SUGGESTION_PHONETIC, s.getPhonetic() );
    	values.put(TF_SUGGESTION_PRONUN_REC, s.getPronunciationRecorded() );
    	values.put(TF_SUGGESTION_SYNCH_STAT, s.getSynchroStatus() );
    	values.put(TF_SUGGESTION_ACT_TYPE, s.getActionType() );
    	values.put(TF_SUGGESTION_DICT_VERSION, s.getDictionaryVersion());
    	
    	if ( s.getCreationDate() != null ) {
    		values.put(TF_SUGGESTION_CREATION_DATE, s.getCreationDate().getTime() );
    	}
    	
    	if ( s.getDictionary() != null ) {
    		values.put(TF_SUGGESTION_DICT_ID, s.getDictionary().getCode() );
    	}
    	
    	if ( s.getHeadword() != null ) {
    		values.put(TF_SUGGESTION_HEADWORD_ID, s.getHeadword().getId() );
    	}
    	
    	String[] whereValues = {
    		Long.toString( s.getId() )
    	};
    	
    	// Launch the insert request
    	long res = conn.update(table, values, TF_SUGGESTION_ID + "= ?", whereValues );
    }
	
	/**
	 * 
	 * Search a word/a list of words matching a string in a dictionary. The matching 
	 * is done ignoring the case. The result is ordered lexicographically. <br><br>
	 * 
	 * The dictionary of the headwords is not retrieved.
	 * 
	 * @param dictionaryId The dictionary to look up in.
	 * @param wordToMatch [must not be empty] The word to match in the dictionary.
	 * @param exactMatch If true, only the words matching exactly the string 'wordToMatch' will be 
	 * retrieved. If false, all the words BEGINNING by the string 'wordToMatch' will be retrieved.
	 * @return The list of all words matching the parameter 'wordToMatch' in the specified dictionary or null
	 * if there is no matching. The list id ordered lexicographically.
	 * @throws AopdsDatabaseException If the data cannot be accessed.
	 */
	public ArrayList<AbstractWord> searchWord(
		int dictionaryId,
		String wordToMatch,
		Boolean exactMatch,
		Boolean addSuggestions
	) throws AopdsDatabaseException {
		
		// checking if the word to match is not an empty string
		if ( wordToMatch.length() <= 0 ) {
			throw new InvalidParameterException(
				LOG_TAG + ".searchWord: The 'wordToMatch' parameter MUST NOT BE EMPTY."
			);
		}
		
     	/*
		String discriminator = "WORD_TYPE";
		
     	// asked fields
		
		String[] headWordFieldsArray = {
			TF_HEADWORD_PRONUN_EXISTS + " AS WORD_PRONUNC",
			discriminator,
			TF_HEADWORD_ID + " AS WORD_ID",
     		TF_HEADWORD_DICT_ID + " AS WORD_DICT_ID",
     		TF_HEADWORD_WORD + " AS WORD_WORD",
     		TF_HEADWORD_ENTRY + " AS WORD_ENTRY",
     		TF_HEADWORD_PHONETIC + " AS WORD_PHONETIC",
     		"NULL AS WORD_SYNCH_STAT",
     		"NULL AS WORD_ACTION_TYPE",
     		
     		TF_SUGGESTION_ID + " AS WORD_LM_SUGG_ID",
     		TF_SUGGESTION_WORD + " AS WORD_LM_SUGG_WORD",
     		TF_SUGGESTION_ENTRY + " AS WORD_LM_SUGG_ENTRY",
     		TF_SUGGESTION_PHONETIC + " AS WORD_LM_SUGG_PHONETIC",
     		TF_SUGGESTION_SYNCH_STAT + " AS WORD_LM_SUGG_SYNCH_STAT",
     		TF_SUGGESTION_ACT_TYPE + " AS WORD_LM_SUGG_ACTION_TYPE"
		};
		
		String[] simpleHeadWordFieldsArray = {
			
			TF_HEADWORD_ID,
     		TF_HEADWORD_DICT_ID,
     		TF_HEADWORD_WORD,
     		TF_HEADWORD_ENTRY,
     		TF_HEADWORD_PHONETIC,
     		TF_HEADWORD_PRONUN_EXISTS
		};
		
		HashSet<String> headWordFields = new HashSet<String>();
		
		for (String currentField: headWordFieldsArray) {
			headWordFields.add( currentField );
		}
     	
		String[] suggestionFieldsArray = {
			TF_SUGGESTION_PRONUN_REC + " AS WORD_PRONUNC",
			discriminator,
			TF_SUGGESTION_ID + " AS WORD_ID",
     		TF_SUGGESTION_DICT_ID + " AS WORD_DICT_ID",
     		TF_SUGGESTION_WORD + " AS WORD_WORD",
     		TF_SUGGESTION_ENTRY + " AS WORD_ENTRY",
     		TF_SUGGESTION_PHONETIC + " AS WORD_PHONETIC",
     		TF_SUGGESTION_SYNCH_STAT + " AS WORD_SYNCH_STAT",
     		TF_SUGGESTION_ACT_TYPE + " AS WORD_ACTION_TYPE",
     		
     		"NULL AS WORD_LM_SUGG_ID",
     		"NULL AS WORD_LM_SUGG_WORD",
     		"NULL AS WORD_LM_SUGG_ENTRY",
     		"NULL AS WORD_LM_SUGG_PHONETIC",
     		"NULL AS WORD_LM_SUGG_SYNCH_STAT",
     		"NULL AS WORD_LM_SUGG_ACTION_TYPE"
		};
		
     // asked fields
		HashSet<String> suggestionFields = new HashSet<String>();
		
		for (String currentField: suggestionFieldsArray) {
			suggestionFields.add( currentField );
		}
     	
		
     	
     	//
     	// generating the where clause
     	//
     	
     	// we filter on a specific dictionary
     	String headwordWhereClause = 
     		TF_HEADWORD_DICT_ID + " = ? AND "
     	;
     	
     	String suggestionWhereClause = 
     		TF_SUGGESTION_DICT_ID + " = ? AND "
     	;
     	
     	if ( exactMatch ) {
     		// we search every exaclty matching words
     		headwordWhereClause += TF_HEADWORD_WORD + " = ? COLLATE NOCASE"; 
     		suggestionWhereClause += TF_SUGGESTION_WORD + " = ? COLLATE NOCASE"; 
     		// COLLATE NOCASE --> ingnore the case to compare
     	} else {
     		// we will use LIKE to search the words beginning by the search string
     		headwordWhereClause += TF_HEADWORD_WORD + " LIKE ? COLLATE NOCASE";
     		suggestionWhereClause += TF_SUGGESTION_WORD + " LIKE ? COLLATE NOCASE";
     	}
     	
     	// generating the value of the parameter to bind
     	String computedWordToMatch = "";
     	if ( exactMatch ) { // nothing to do
     		computedWordToMatch = wordToMatch;
     	} else {
     		// LIKE wordToMatch% --> beginning by wordToMatch
     		computedWordToMatch = wordToMatch + "%";
     	}
     	
     	String[] headwordSelectArgs = {
     		Integer.toString(dictionaryId), // filter by dictionary
     		computedWordToMatch
     	};
         	
     	
     	String[] headwordAndSuggestionsSelectArgs = {
     		Integer.toString(dictionaryId), // filter by dictionary
     		computedWordToMatch, // string to match
     		Integer.toString(dictionaryId),
     		computedWordToMatch
     	};
     	
     	String[] selectArgs;
     	
     	
     	String query;
     	
     	if ( addSuggestions ) {
     		
     		SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
     		SQLiteQueryBuilder hqb = new SQLiteQueryBuilder();
     		SQLiteQueryBuilder uqb = new SQLiteQueryBuilder();
     		
     		sqb.setTables(TABLE_SUGGESTION);
     		
     		hqb.setTables(TABLE_HEADWORD);
     		hqb.setTables(TABLE_SUGGESTION);
     
     		String headwordSubQuery = hqb.buildUnionSubQuery(
     			discriminator, 
     			headWordFieldsArray, 
     			headWordFields, 
     			1, 
     			"h", 
     			headwordWhereClause, 
     			headwordAndSuggestionsSelectArgs, 
     			null, 
     			null
     		);
     		
     		String suggestionSubQuery = sqb.buildUnionSubQuery(
 				discriminator, 
 				suggestionFieldsArray, 
     			suggestionFields, 
     			1, 
     			"s", 
     			suggestionWhereClause, 
     			headwordAndSuggestionsSelectArgs, 
     			null, 
     			null
     		);
     		
     		query = uqb.buildUnionQuery(
     			new String [] {headwordSubQuery, suggestionSubQuery}, 
     			"WORD_WORD ASC", 
     			null
     		);
     		
     		selectArgs = headwordAndSuggestionsSelectArgs;
     		
     	} else {
     		
     		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
     		
     		qb.setTables(TABLE_HEADWORD);
     		
     		query = qb.buildQuery(
     			simpleHeadWordFieldsArray, 
     			headwordWhereClause, 
     			headwordSelectArgs, 
     			null, 
     			null, 
     			TF_HEADWORD_WORD + " ASC", // ordering ascendant, 
     			null
     		);
     		
     		selectArgs = headwordSelectArgs;
     		
     	}
     	
     	*/
		
		
		String[] selectArgs = {
			ACTION_TYPE_DELETION,
			ACTION_TYPE_MODIFICATION,
			Integer.toString( dictionaryId ),
			wordToMatch + "%",
			Integer.toString( dictionaryId ),
			wordToMatch + "%",
			ACTION_TYPE_MODIFICATION
		};
		
		String query = 
		"SELECT * FROM (" +
		"SELECT " +
		"'h' AS WORD_TYPE ," +
		TABLE_HEADWORD + "." + TF_HEADWORD_ID + " AS WORD_ID ," +
		TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID + " AS WORD_DICT_ID ," +
		TABLE_HEADWORD + "." + TF_HEADWORD_WORD + " AS WORD_WORD ," +
		TABLE_HEADWORD + "." + TF_HEADWORD_ENTRY + " AS WORD_ENTRY ," +
		TABLE_HEADWORD + "." + TF_HEADWORD_PHONETIC + " AS WORD_PHONETIC ," +
		"NULL AS WORD_SYNCH_STAT ," +
		"NULL AS WORD_ACTION_TYPE ," +
		"EXISTS ( " + 
			"SELECT " + TABLE_SUGGESTION + "." + TF_SUGGESTION_ID + 
			" FROM " + TABLE_SUGGESTION + " " + 
			" WHERE " + TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID + " = " + TABLE_HEADWORD + "." + TF_HEADWORD_ID +
			" AND SUBSTR( " + TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE + " , 1, 1) = ? " +
		") AS WORD_DELETED ," +
		
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_ID + " AS WORD_LM_SUGG_ID ," +
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD + " AS WORD_LM_SUGG_WORD ," +
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY + " AS WORD_LM_SUGG_ENTRY ," +
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC + " AS WORD_LM_SUGG_PHONETIC ," +
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT + " AS WORD_LM_SUGG_SYNCH_STAT ," +
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE + " AS WORD_LM_SUGG_ACTION_TYPE " +
 		
 		" FROM " + TABLE_HEADWORD + " " + 
 		
 		"LEFT OUTER JOIN " + TABLE_SUGGESTION + " ON " + 
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID + " = " + 
 		TABLE_HEADWORD + "." + TF_HEADWORD_ID + " AND " +
 		"SUBSTR(" + TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE + ", 1, 1) = ? " +
 		
 		" WHERE " + TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID + " = ? " +
 		" AND " + TABLE_HEADWORD + "." + TF_HEADWORD_WORD + " LIKE ? " +
 		
 		" GROUP BY " + TABLE_HEADWORD + "." + TF_HEADWORD_ID + " " +
 		" HAVING " + TABLE_SUGGESTION + "." + TF_SUGGESTION_CREATION_DATE + " = " + 
 		" MAX (" + TABLE_SUGGESTION + "." + TF_SUGGESTION_CREATION_DATE + ") OR " + 
 		TABLE_SUGGESTION + "." + TF_SUGGESTION_CREATION_DATE + " IS NULL " +
 		
 		" UNION ALL " +
			
 		"SELECT " + 
 		"'s' AS WORD_TYPE ," +
 		TF_SUGGESTION_ID + " AS WORD_ID ," +
 		TF_SUGGESTION_DICT_ID + " AS WORD_DICT_ID ," +
 		TF_SUGGESTION_WORD + " AS WORD_WORD ," +
 		TF_SUGGESTION_ENTRY + " AS WORD_ENTRY ," +
 		TF_SUGGESTION_PHONETIC + " AS WORD_PHONETIC ," +
 		TF_SUGGESTION_SYNCH_STAT + " AS WORD_SYNCH_STAT ," +
 		TF_SUGGESTION_ACT_TYPE + " AS WORD_ACTION_TYPE ," +
 		"NULL AS WORD_DELETED ," +
 		
 		"NULL AS WORD_LM_SUGG_ID ," +
 		"NULL AS WORD_LM_SUGG_WORD ," +
 		"NULL AS WORD_LM_SUGG_ENTRY ," +
 		"NULL AS WORD_LM_SUGG_PHONETIC ," +
 		"NULL AS WORD_LM_SUGG_SYNCH_STAT ," +
 		"NULL AS WORD_LM_SUGG_ACTION_TYPE " + 
 		
 		"FROM " + TABLE_SUGGESTION + " " + 
 		" WHERE " + TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID + " = ? " +
 		" AND " + TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD + " LIKE ? AND " + 
 		" SUBSTR(" + TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE + ", 1, 1) <> ? " +
 		
 		") ORDER BY WORD_WORD ASC "
		;
		
		
    	/*
     	 * Launching the query
     	 */
     	
     	try {
     		
     		// getting the connector for read only.
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		Cursor res = connection.rawQuery(query, selectArgs );
     		
     		// verifying the cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // the cursor is containing data
 	     		
 	     		// to store what we're going to return 
 	     		ArrayList<AbstractWord> returningRes = new ArrayList<AbstractWord>();
 	     		
 	     		
 	     		if ( addSuggestions ) {
	     				
 	     			do { // there is at least one headword in the cursor
 	 	     			
 	 	     			//
 	 	     			// mapping the word
 	 	     			//
 	 	     			
 	 	     			char type = res.getString(0).charAt(0);
 	 	     			
 	 	     			// pronunciation
 	 	     			Boolean pronuncExists = false;
 	 	     			
 	 	     			
 	 	     			AbstractWord word;
 	 	     			
 	 	     			if ( type == 's' ) {
 	 	     				
 	 	     				Suggestion suggestion = new Suggestion(
 	     						res.getInt(1), // id
 	    	     				res.getString(3), // word
 	    	     				res.getString(4), // entry
 	    	     				res.getString(5), // phonetics
 	    	     				null, // dictionary id, not needed
 	    	     				pronuncExists
 	 	     				);
 	 	     				
 	 	     				suggestion.setSynchroStatus( res.getString(6) );
 	 	     				suggestion.setActionType( res.getString(7) );
 	 	     				word = suggestion;
 	 	     				
 	 	     			} else {
 	 	     				
 	 	     				Headword headword = new Headword(
 	    	     				res.getInt(1), // id
 	    	     				res.getString(3), // word
 	    	     				res.getString(4), // entry
 	    	     				res.getString(5), // phonetics
 	    	     				null, // dictionary id, not needed
 	    	     				pronuncExists
 	    	     			);
 	 	     				
 	 	     				if ( res.getInt(8) == 1 ) {
 	 	     					headword.setHasDeletionBeenSuggested(true);
 	 	     				} else {
 	 	     					headword.setHasDeletionBeenSuggested(false);
 	 	     				}
 	 	     				
 	 	     				if ( !res.isNull(9) ) {
 	 	     					Suggestion s = new Suggestion();
 	 	     					
 	 	     					s.setId( res.getInt(9) );
 	 	     					s.setWord( res.getString(10) );
 	 	     					s.setEntry( res.getString(11) );
 	 	     					s.setPhonetic( res.getString(12) );
 	 	     					s.setSynchroStatus( res.getString(13) );
 	 	     					s.setActionType( res.getString(14) );
 	 	     					
 	 	     					s.setHeadword(headword);
 	 	     					headword.setLastModification( s );
 	 	     					
 	 	     				}
 	 	     				
 	 	     				word = headword;
 	 	     				
 	 	     			}
 	 	     			
 	 	     			
 	 	     			
 	 	     			// adding to final results list
 	 	     			returningRes.add(word);
 	 	     			
 	 	     		} while ( res.moveToNext() );
 	 	     		
 	 	     		// closing cursor
 	 	     		res.close();
 	     			
 	     			
	     		} else {
	     			
	     			
	     			do { // there is at least one headword in the cursor
	 	     			
	 	     			//
	 	     			// mapping the word
	 	     			//
	 	     			
	 	     			
	 	     			// pronunciation
	 	     			Boolean pronuncExists = false;
	 	     			if ( res.getInt(5) != 0 ) {
	 	     				pronuncExists = true;
	 	     			}
	 	     			
	 	     			
 	     				Headword headword = new Headword(
    	     				res.getInt(0), // id
    	     				res.getString(2), // word
    	     				res.getString(3), // entry
    	     				res.getString(4), // phonetics
    	     				null, // dictionary id, not needed
    	     				pronuncExists
    	     			);
	 	     				
	 	     			
	 	     			// adding to final results list
	 	     			returningRes.add(headword);
	 	     			
	 	     		} while ( res.moveToNext() );
	 	     		
	 	     		// closing cursor
	 	     		res.close();
	     			
	     			
	     		}
 	     		
 	     		
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // the cursor is not containing data
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e ) {
     		throw handleError(e);
     	}
	}
	
	/**
	 * Get an ordered (lexicographically) word list specifying a dictionary. This method allows to 
	 * get all words of a dictionary but with a 'page per page system', we mean specifying the position
	 * of the first word you want and the number of those you want.
	 * 
	 * This method is useful to display the whole dictionary, as it gives you the words in the order 
	 * they would appear in a real dictionary book.
	 * 
	 * @param dictionaryId The dictionary to look up in.
	 * @param from [must be positive]The position of the first word you want in the ordered list of all words.
	 * @param howMany [must be positive]The number of words you want to retrieve.
	 * @return A list of words ordered lexicographically corresponding to a part of the dictionary.
	 * @throws AopdsDatabaseException If data cannot be accessed.
	 */
	public ArrayList<Headword> getOrderedWordList(
		int dictionaryId,
		long from,
		int howMany
	) throws AopdsDatabaseException {
		
		// verifying args
		if ( (from < 0) || (howMany < 0) ) {
			throw new InvalidParameterException(
				"The parameters from and howMany must be like : from > 0 and howMany > 0 ..." + 
				"from = " + from + " and howMany = " + howMany + " given."
			);
		}
		
		/*
		 * Generating SQL query
		 * 
		 * Getting all words from a dictionary with a LIMIT clause.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// setting the table
     	qb.setTables(TABLE_HEADWORD);
     	
     	// asked fields
     	String[] fields = { 
     		TF_HEADWORD_ID,
     		TF_HEADWORD_DICT_ID,
     		TF_HEADWORD_WORD,
     		TF_HEADWORD_ENTRY,
     		TF_HEADWORD_PHONETIC,
     		TF_HEADWORD_PRONUN_EXISTS
     	};
     	
     	// generating where clause
     	// filtering by dictionary
     	String whereClause = 
     		TF_HEADWORD_DICT_ID + " = ?"
     	;
     	String[] selectArgs = {
     		Integer.toString(dictionaryId)
     	};
     	
     	/*
     	 * Launching query
     	 */
     	
     	try {
     		// getting a connection for read only.
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		selectArgs,
 	     		null,
 	     		null,
 	     		TF_HEADWORD_WORD + " ASC", // ordering ascendant
 	     		Long.toString(from) + ", " + Integer.toString(howMany) // LIMIT clause
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor is containing data
 	     		
 	     		// we'll put the results in it
 	     		ArrayList<Headword> returningRes = new ArrayList<Headword>();
 	     		
 	     		do { // at least one word in the results
 	     			
 	     			//
 	     			// mapping the word
 	     			//
 	     			
 	     			// pronunciation
 	     			Boolean pronuncExists = false;
 	     			if ( res.getInt(5) != 0 ) {
 	     				pronuncExists = true;
 	     			}
 	     			
 	     			Headword headword = new Headword(
 	     				res.getInt(0), // id
 	     				res.getString(2), // word
 	     				res.getString(3), // entry
 	     				res.getString(4), // phonetics
 	     				null, // dictionary id
 	     				pronuncExists
 	     			);
 	     			
 	     			returningRes.add(headword);
 	     			
 	     		} while ( res.moveToNext() );
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no data in cursor
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}	
	}
	
	
	/**
	 * Get the installed dictionary by its languages.
	 * 
	 * @param LanguageFromId The origin language of the searched dictionary.
	 * @param LanguageToId The target language of the searched dictionary.
	 * @return The dictionary whose languages are the arguments provided or null if it's not installed
	 * on the phone.
	 * @throws AopdsDatabaseException If data cannot be accessed.
	 */
	public Dictionary getDictionaryByLanguages(
		int LanguageFromId,
		int LanguageToId
	) throws AopdsDatabaseException {
		
		/*
		 * Generating SQL query
		 * 
		 * Retrieving the searched dictionary and its languages.
		 */
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// generating where clause
		
		// joining language table for the two languages (origin and target)
		// and filtering by searched languages : 
		//
		// dictionary.languageFrom = language.id AND dictionary.languageFrom = searchedLanguage ...
		String whereClause = 
     		"LANGUAGE_TO." + TF_LANGUAGE_CODE + " = " + 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " AND " + 
     		"LANGUAGE_FROM." + TF_LANGUAGE_CODE + " = " +
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " AND " + 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " = ? AND " + 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " = ?" ;
     	
		// setting tables, aliasing table languages to distinguish
		// origin and target languages joins.
     	qb.setTables( 
     		TABLE_INSTALLED_DICTIONARY + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_FROM" 
     	);
		
     	// asked fields
     	// we have to alias the fields with the good table name (because there is two joins)
     	String[] fields = { 
     		// dictionary
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION,
     		// target language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO,
     		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH",
     		
     		// origin language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM,
     		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH",
     	};
     	
     	// where clause values (replacing the ?s)
     	String[] selectArgs = {
     		Integer.toString(LanguageToId),
     		Integer.toString(LanguageFromId)
     	};
     	
     	/*
     	 * Launching query
     	 */
     	
     	try {
     		// getting connector for read only
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		selectArgs,
 	     		null,
 	     		null,
 	     		null // no order
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing on dictionary
 	     		
 	     		//
 	     		// mapping dictionary
 	     		//
 	     		
 	     		// target language
 	     		Language languageTo = new Language(
 	 				res.getInt(3), // id
 	 				res.getString(4), // abbreviation ISO-639
 	 				res.getString(5), // name
 	 				res.getString(6)  // name in English
 	         	);
 	 			
 	     		// origin language
 	 			Language languageFrom = new Language(
 	 				res.getInt(7), // id
 	 				res.getString(8), // abbreviation ISO-639
 	 				res.getString(9), // name
 	 				res.getString(10) // name in english
 	 			);
 	 			
 	 			// dictionary
 	 			Dictionary dictionary = new Dictionary(
 	 				res.getInt(0), // id
 	 				languageTo,
 	 				languageFrom,
 	 				res.getString(1), // name
 	 				res.getInt(2)
 	 			);
 	     		
 	 			// closing cursor
 	     		res.close();
 	     		
 	     		return dictionary;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}
		
     	
	}
	
	/**
	 * Get the Installed Dictionary on the application, according to the dictionary 
	 * id given in parameter.
	 * 
	 * @param id The id of the looked up dictionary.
	 * @return InstalledDictionary The dictionary found or null if doesn't exist.
	 * @throws AopdsDatabaseException Id data cannot be accessed.
	 */
	public Dictionary getDictionaryById(
		int id
	) throws AopdsDatabaseException {
		
		/*
		 * Generating query
		 * 
		 * Getting the dictionary and its languages.
		 */
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// generating where clause
		
		// joining languages and filtering by dictionary id
		String whereClause = 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + 
     		" = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + 
     		" = LANGUAGE_FROM." + TF_LANGUAGE_CODE + " AND " +
     		TF_ID_ID + " = ?";
     	
		// dictionary and aliasing language table for joining
     	qb.setTables( 
     		TABLE_INSTALLED_DICTIONARY + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_FROM" 
     	);
		
     	// asked fields
     	String[] fields = { 
     		// dictionary
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION,
     		// target language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO,
     		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH",
     		
     		// origin language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM,
     		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH",
     	};
     	
     	// where values
     	String[] selectArgs = {
     		Integer.toString(id)
     	};
     	
     	/*
     	 * Launching queries
     	 */
     	
     	try {
     		// get connector for read only
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		selectArgs,
 	     		null,
 	     		null,
 	     		null // no order
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing one dictionary
 	     		
 	     		//
 	     		// mapping dictionary
 	     		//
 	     		
 	     		// target language
 	     		Language languageTo = new Language(
 	 				res.getInt(3), // id
 	 				res.getString(4), // abbreviation
 	 				res.getString(5), // name 
 	 				res.getString(6) // name in English
 	         	);
 	 			
 	     		// origin language
 	 			Language languageFrom = new Language(
 	 				res.getInt(7), // id
 	 				res.getString(8), // abbreviation
 	 				res.getString(9), // name
 	 				res.getString(10) // name in english
 	 			);
 	 			
 	 			// dictionary
 	 			Dictionary dictionary = new Dictionary(
 	 				res.getInt(0), // id
 	 				languageTo,
 	 				languageFrom,
 	 				res.getString(1), // name
 	 				res.getInt(2)
 	 			);
 	     			
 	     		res.close();
 	     		
 	     		return dictionary;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}
     	
		
	}
	public ArrayList<Suggestion> getAllModifyDeleteSuggestions() throws AopdsDatabaseException
	{
		/*
		 * Generating SQL query
		 * 
		 * Getting all suggestions.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// asked fields
     	String[] fields = { 
     		// suggestion
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ID, //0
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID, //1
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID, //2
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD, //3
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC, //4
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY, //5
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE, //6 
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PRONUN_REC, //7
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT, //8
     		//Headword	
     			TABLE_HEADWORD + "." + TF_HEADWORD_ID, //9
     			TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID, //10
     			TABLE_HEADWORD + "." + TF_HEADWORD_WORD, //11
     			TABLE_HEADWORD + "." + TF_HEADWORD_ENTRY, //12
     			TABLE_HEADWORD + "." + TF_HEADWORD_PHONETIC, //13
     			TABLE_HEADWORD + "." + TF_HEADWORD_PRONUN_EXISTS, //14
     		//Dictionary	
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID, //15
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //16
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //17
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME, //18
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION, //19
     		// target language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //20
         		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION", //21
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME", //22
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH", //23	
         	// origin language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //24
         		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION", //25
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME", //26
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH" //27
     	};
     	
     	// joining with languages
	     	String whereClause = 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " = LANGUAGE_FROM." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID  + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID  + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID + " = " + TABLE_HEADWORD + "." + TF_HEADWORD_ID;
	     	
	     	// tables suggestion
	     	qb.setTables(	TABLE_INSTALLED_DICTIONARY + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_FROM" + "," + 
	         				TABLE_SUGGESTION + "," +
	         				TABLE_HEADWORD);
     	
     	/* Launching SQL query */
     	
     	try {
     		// getting connector for read only 
     		SQLiteDatabase connection = db.getReadableDatabase();
     		/*String query = SQLiteQueryBuilder.buildQueryString(
     				true,
     				qb.getTables(), 
     				fields,
     				whereClause,
     	     		null, 
     				null, 
     				null, 
     				null
     				);
     		
     		AopdsLogger.info("QUERY", query);*/
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		ArrayList<Suggestion> returningRes = new ArrayList<Suggestion>();
 	     		
 	     		 do
 	     		 {   			
 	     			 // target language
	  	     			Language languageTo = new Language(
	  	     				res.getInt(20), // id
	  	     				res.getString(21), // abbreviation
	  	     				res.getString(22), // name
	  	     				res.getString(23) // name in English
	  	             	);
	  	     			
	  	     			// origin language
	  	     			Language languageFrom = new Language(
	  	     				res.getInt(24), // id
	  	     				res.getString(24), // abbreviation
	  	     				res.getString(26), // name
	  	     				res.getString(27) // name in English
	  	     			);
	  	     			
	  	     			// dictionary
	  	     			Dictionary dictionary = new Dictionary(
	  	     				res.getInt(15), // id
	  	     				languageTo,
	  	     				languageFrom,
	  	     				res.getString(18), // name
	  	     				res.getInt(19)
	  	     			);
 	     			 
 	     			//Headword
 	     			 Headword headword =new Headword(
 	     					res.getInt(9), //id
 	     					res.getString(11),//word
 	     					res.getString(13), // entry
 	     					res.getString(13), //phonetic
 	     					dictionary,
 	     					Boolean.valueOf(Integer.toString(res.getInt(14))) //pronunciationExists
 	     			);
 	     			 
 	     			 
 	     			//Suggestion
 	     			Suggestion suggestion = new Suggestion();
 	     			suggestion.setId(res.getInt(0));
 	     			suggestion.setDictionary(dictionary);
 	     			suggestion.setHeadword(headword);
 	     			suggestion.setWord(res.getString(3));
 	     			suggestion.setPhonetic(res.getString(4));
 	     			suggestion.setEntry(res.getString(5));
 	     			suggestion.setActionType(res.getString(6));
 	     			suggestion.setPronunciationRecorded(Boolean.valueOf(Integer.toString(res.getInt(7))));
 	     			suggestion.setSynchroStatus(res.getString(8));
 	
 	     			
 	     			returningRes.add( suggestion );
 	     			
 	     		}while ( res.moveToNext());
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	} 	
	}
	
	public ArrayList<Suggestion> getAllAddSuggestions() throws AopdsDatabaseException
	{
		/*
		 * Generating SQL query
		 * 
		 * Getting all suggestions.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// asked fields
     	String[] fields = { 
     		// suggestion
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ID, //0
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID, //1
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID, //2
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD, //3
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC, //4
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY, //5
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE, //6 
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PRONUN_REC, //7
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT, //8
     		
     		//Dictionary	
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID, //9
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //10
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //11
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME, //12
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION, //13
     		// target language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //14
         		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION", //15
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME", //16
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH", //17	
         	// origin language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //18
         		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION", //19
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME", //20
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH" //21
     	};
     	
     	// joining with languages
	     	String whereClause = 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " = LANGUAGE_FROM." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE + " = '" + ACTION_TYPE_ADDITION + "'";
	     	
	     	// tables suggestion
	     	qb.setTables(	TABLE_INSTALLED_DICTIONARY + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_FROM" + "," + 
	         				TABLE_SUGGESTION);
     	
     	/* Launching SQL query */
     	
     	try {
 
         		// getting connector for read only 
         		SQLiteDatabase connection = db.getReadableDatabase();
         		/*String query = SQLiteQueryBuilder.buildQueryString(
         				true,
         				qb.getTables(), 
         				fields,
         				whereClause,
         	     		null, 
         				null, 
         				null, 
         				null
         				);
         		
         		AopdsLogger.info("QUERY", query);*/
     		
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		ArrayList<Suggestion> returningRes = new ArrayList<Suggestion>();
 	     		
 	     		 do
 	     		 {   			
 	     			 // target language
	  	     			Language languageTo = new Language(
	  	     				res.getInt(14), // id
	  	     				res.getString(15), // abbreviation
	  	     				res.getString(16), // name
	  	     				res.getString(17) // name in English
	  	             	);
	  	     			
	  	     			// origin language
	  	     			Language languageFrom = new Language(
	  	     				res.getInt(18), // id
	  	     				res.getString(19), // abbreviation
	  	     				res.getString(20), // name
	  	     				res.getString(21) // name in English
	  	     			);
	  	     			
	  	     			// dictionary
	  	     			Dictionary dictionary = new Dictionary(
	  	     				res.getInt(9), // id
	  	     				languageTo,
	  	     				languageFrom,
	  	     				res.getString(12), // name
	  	     				res.getInt(13)
	  	     			);
 	     			 			 
 	     			 
 	     			//Suggestion
 	     			Suggestion suggestion = new Suggestion();
 	     			suggestion.setId(res.getInt(0));
 	     			suggestion.setDictionary(dictionary);
 	     			suggestion.setHeadword(null);
 	     			suggestion.setWord(res.getString(3));
 	     			suggestion.setPhonetic(res.getString(4));
 	     			suggestion.setEntry(res.getString(5));
 	     			suggestion.setActionType(res.getString(6));
 	     			suggestion.setPronunciationRecorded(Boolean.valueOf(Integer.toString(res.getInt(7))));
 	     			suggestion.setSynchroStatus(res.getString(8));
 	
 	     			
 	     			returningRes.add( suggestion );
 	     			
 	     		}while ( res.moveToNext());
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	} 	
	}
	
	public Suggestion getModifyDeleteSuggestion(long idSuggestion) throws AopdsDatabaseException
	{
		/*
		 * Generating SQL query
		 * 
		 * Getting all suggestions.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// asked fields
     	String[] fields = { 
     		// suggestion
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ID, //0
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID, //1
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID, //2
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD, //3
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC, //4
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY, //5
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE, //6 
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PRONUN_REC, //7
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT, //8
     		//Headword	
     			TABLE_HEADWORD + "." + TF_HEADWORD_ID, //9
     			TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID, //10
     			TABLE_HEADWORD + "." + TF_HEADWORD_WORD, //11
     			TABLE_HEADWORD + "." + TF_HEADWORD_ENTRY, //12
     			TABLE_HEADWORD + "." + TF_HEADWORD_PHONETIC, //13
     			TABLE_HEADWORD + "." + TF_HEADWORD_PRONUN_EXISTS, //14
     		//Dictionary	
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID, //15
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //16
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //17
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME, //18
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION, //19
     		// target language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //20
         		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION", //21
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME", //22
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH", //23	
         	// origin language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //24
         		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION", //25
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME", //26
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH" //27
     	};
     	
     	// joining with languages
	     	String whereClause = 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " = LANGUAGE_FROM." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_HEADWORD + "." + TF_HEADWORD_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID  + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID  + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID + " = " + TABLE_HEADWORD + "." + TF_HEADWORD_ID + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_ID + " = " + idSuggestion;
	     	
	     	// tables suggestion
	     	qb.setTables(	TABLE_INSTALLED_DICTIONARY + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_FROM" + "," + 
	         				TABLE_SUGGESTION + "," +
	         				TABLE_HEADWORD);
     	
     	/* Launching SQL query */
     	
     	try {
     		// getting connector for read only 
     		SQLiteDatabase connection = db.getReadableDatabase();
     		/*String query = SQLiteQueryBuilder.buildQueryString(
     				true,
     				qb.getTables(), 
     				fields,
     				whereClause,
     	     		null, 
     				null, 
     				null, 
     				null
     				);
     		AopdsLogger.info("requete", query);*/
     		
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		Suggestion returningRes = new Suggestion();
 	     		
 	     		   			
 	     			 // target language
	  	     			Language languageTo = new Language(
	  	     				res.getInt(20), // id
	  	     				res.getString(21), // abbreviation
	  	     				res.getString(22), // name
	  	     				res.getString(23) // name in English
	  	             	);
	  	     			
	  	     			// origin language
	  	     			Language languageFrom = new Language(
	  	     				res.getInt(24), // id
	  	     				res.getString(24), // abbreviation
	  	     				res.getString(26), // name
	  	     				res.getString(27) // name in English
	  	     			);
	  	     			
	  	     			
	  	     			// dictionary
	  	     			Dictionary dictionary = new Dictionary(
	  	     				res.getInt(15), // id
	  	     				languageTo,
	  	     				languageFrom,
	  	     				res.getString(18), // name
	  	     				res.getInt(19)
	  	     			);
 	     			 
 	     			//Headword
 	     			 Headword headword =new Headword(
 	     					res.getInt(9), //id
 	     					res.getString(11),//word
 	     					res.getString(13), // entry
 	     					res.getString(13), //phonetic
 	     					dictionary,
 	     					Boolean.valueOf(Integer.toString(res.getInt(14))) //pronunciationExists
 	     			);
 	     			 
 	     			 
 	     			//Suggestion
 	     			Suggestion suggestion = new Suggestion();
 	     			suggestion.setId(res.getInt(0));
 	     			suggestion.setDictionary(dictionary);
 	     			suggestion.setHeadword(headword);
 	     			suggestion.setWord(res.getString(3));
 	     			suggestion.setPhonetic(res.getString(4));
 	     			suggestion.setEntry(res.getString(5));
 	     			suggestion.setActionType(res.getString(6));
 	     			suggestion.setPronunciationRecorded(Boolean.valueOf(Integer.toString(res.getInt(7))));
 	     			suggestion.setSynchroStatus(res.getString(8));
 	
 	     			
 	     			returningRes = suggestion;
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}

     	
	}
	
	public Suggestion getAddSuggestion(long idSuggestion) throws AopdsDatabaseException
	{
		/*
		 * Generating SQL query
		 * 
		 * Getting all suggestions.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// asked fields
     	String[] fields = { 
     		// suggestion
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ID, //0
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID, //1
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID, //2
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD, //3
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC, //4
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY, //5
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE, //6 
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PRONUN_REC, //7
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT, //8
     		//Dictionary	
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID, //9
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //10
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //11
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME, //12
     			TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION, //13
     		// target language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO, //14
         		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION", //15
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME", //16
         		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH", //17	
         	// origin language
         		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM, //18
         		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION", //19
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME", //20
         		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH" //21
     	};
     	
     	// joining with languages
	     	String whereClause = 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + " = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + " = LANGUAGE_FROM." + TF_LANGUAGE_CODE + " AND " + 
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID + " = " + TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID  + " AND " +
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_ID + " = " + idSuggestion;
	     	
	     	// tables suggestion
	     	qb.setTables(	TABLE_INSTALLED_DICTIONARY + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
	         				TABLE_LANGUAGE + " LANGUAGE_FROM" + "," + 
	         				TABLE_SUGGESTION);
     	
     	/* Launching SQL query */
     	
     	try {
     		// getting connector for read only 
     		SQLiteDatabase connection = db.getReadableDatabase();
     		/*String query = SQLiteQueryBuilder.buildQueryString(
     				true,
     				qb.getTables(), 
     				fields,
     				whereClause,
     	     		null, 
     				null, 
     				null, 
     				null
     				);
     		*/
     		
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		Suggestion returningRes = new Suggestion();
 	     		
 	     		   			
 	     			 // target language
	  	     			Language languageTo = new Language(
	  	     				res.getInt(14), // id
	  	     				res.getString(15), // abbreviation
	  	     				res.getString(16), // name
	  	     				res.getString(17) // name in English
	  	             	);
	  	     			
	  	     			// origin language
	  	     			Language languageFrom = new Language(
	  	     				res.getInt(18), // id
	  	     				res.getString(19), // abbreviation
	  	     				res.getString(20), // name
	  	     				res.getString(21) // name in English
	  	     			);
	  	     			
	  	     			
	  	     			// dictionary
	  	     			Dictionary dictionary = new Dictionary(
	  	     				res.getInt(9), // id
	  	     				languageTo,
	  	     				languageFrom,
	  	     				res.getString(12), // name
	  	     				res.getInt(13)
	  	     			);
 	     			 
 	     			//Suggestion
 	     			Suggestion suggestion = new Suggestion();
 	     			suggestion.setId(res.getInt(0));
 	     			suggestion.setDictionary(dictionary);
 	     			suggestion.setHeadword(null);
 	     			suggestion.setWord(res.getString(3));
 	     			suggestion.setPhonetic(res.getString(4));
 	     			suggestion.setEntry(res.getString(5));
 	     			suggestion.setActionType(res.getString(6));
 	     			suggestion.setPronunciationRecorded(Boolean.valueOf(Integer.toString(res.getInt(7))));
 	     			suggestion.setSynchroStatus(res.getString(8));
 	
 	     			
 	     			returningRes = suggestion;
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}

     	
	}
	
	public Suggestion getSuggestion(long idSuggestion) throws AopdsDatabaseException
	{
		/*
		 * Generating SQL query
		 * 
		 * Getting all suggestions.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		// asked fields
     	String[] fields = { 
     		// suggestion
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ID, //0
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_HEADWORD_ID, //1
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_DICT_ID, //2
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_WORD, //3
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PHONETIC, //4
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ENTRY, //5
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_ACT_TYPE, //6 
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_PRONUN_REC, //7
     			TABLE_SUGGESTION + "." + TF_SUGGESTION_SYNCH_STAT, //8
     	};
     	
     	// joining with languages
	     	String whereClause = 
	     		TABLE_SUGGESTION + "." + TF_SUGGESTION_ID + " = " + idSuggestion;
	     	
	     	// tables suggestion
	     	qb.setTables(TABLE_SUGGESTION);
     	
     	/* Launching SQL query */
     	
     	try {
     		// getting connector for read only 
     		SQLiteDatabase connection = db.getReadableDatabase();
     		/*String query = SQLiteQueryBuilder.buildQueryString(
     				true,
     				qb.getTables(), 
     				fields,
     				whereClause,
     	     		null, 
     				null, 
     				null, 
     				null
     				);
     		*/
     		
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		Suggestion returningRes = new Suggestion();
 	     		
 	     			 
     			//Suggestion
     			Suggestion suggestion = new Suggestion();
     			suggestion.setId(res.getInt(0));
     			suggestion.setHeadword(null);
     			suggestion.setWord(res.getString(3));
     			suggestion.setPhonetic(res.getString(4));
     			suggestion.setEntry(res.getString(5));
     			suggestion.setActionType(res.getString(6));
     			//suggestion.setPronunciationRecorded(Boolean.valueOf(Integer.toString(res.getInt(7))));
     			suggestion.setSynchroStatus(res.getString(8));

     			
     			returningRes = suggestion;
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}

     	
	}
	
	
	public int updateSuggestionSynchroStatus(Suggestion s)
	{
		ContentValues updateSuggestion = new ContentValues();
		updateSuggestion.put(TF_SUGGESTION_SYNCH_STAT, s.getSynchroStatus());
		String whereClause = TF_SUGGESTION_ID + " = ?" ;
		String[] whereValues= {
				Long.toString(s.getId())	
		};
		SQLiteDatabase connection = db.getWritableDatabase();
		int nbRowAffected = connection.update(TABLE_SUGGESTION, updateSuggestion, whereClause, whereValues);
		
		return nbRowAffected;
		
	}
	
	/**
	 * Retrieve all installed dictionaries.
	 * 
	 * @return All installed dictionaries or null if no dictionary is installed.
	 * @throws AopdsDatabaseException Id data cannot be installed.
	 */
	public ArrayList<Dictionary> getAllDictionaries()
	throws AopdsDatabaseException {
		
		/*
		 * Generating SQL query
		 * 
		 * Getting all dictionaries and their languages.
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
     	
     	// asked fields
     	String[] fields = { 
     		// dictionary
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_ID,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_NAME,
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_VERSION,
     		// target language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO,
     		"LANGUAGE_TO." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_TO_ABREVIATION",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME + " AS LANGUAGE_TO_NAME",
     		"LANGUAGE_TO." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_TO_NAME_IN_ENGLISH",
     		
     		// origin language
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM,
     		"LANGUAGE_FROM." + TF_LANGUAGE_ABREVIATION + " AS LANGUAGE_FROM_ABREVIATION",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME + " AS LANGUAGE_FROM_NAME",
     		"LANGUAGE_FROM." + TF_LANGUAGE_NAME_IN_ENGLISH + " AS LANGUAGE_FROM_NAME_IN_ENGLISH",
     	};
     	
     	// joining with languages
     	String whereClause = 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_TO + 
     		" = LANGUAGE_TO." + TF_LANGUAGE_CODE + " AND " + 
     		TABLE_INSTALLED_DICTIONARY + "." + TF_ID_LANGUAGE_FROM + 
     		" = LANGUAGE_FROM." + TF_LANGUAGE_CODE;
     	
     	// tables dictionary and the two languages
     	qb.setTables( 
     		TABLE_INSTALLED_DICTIONARY + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_TO" + "," + 
     		TABLE_LANGUAGE + " LANGUAGE_FROM" 
     	);
     	
     	/*
     	 * Launching SQL query
     	 */
     	
     	try {
     		// getting connector for read only 
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		whereClause,
 	     		null,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing cursor
 	     		
 	     		// result set
 	     		ArrayList<Dictionary> returningRes = new ArrayList<Dictionary>();
 	     		
 	     		do { // at least one dictionary
 	     			
 	     			//
 	     			// mapping dictionary
 	     			//
 	     			
 	     			// target language
 	     			Language languageTo = new Language(
 	     				res.getInt(3), // id
 	     				res.getString(4), // abbreviation
 	     				res.getString(5), // name
 	     				res.getString(6) // name in English
 	             	);
 	     			
 	     			// origin language
 	     			Language languageFrom = new Language(
 	     				res.getInt(7), // id
 	     				res.getString(8), // abbreviation
 	     				res.getString(9), // name
 	     				res.getString(10) // name in English
 	     			);
 	     			
 	     			// dictionary
 	     			Dictionary dictionary = new Dictionary(
 	     				res.getInt(0), // id
 	     				languageTo,
 	     				languageFrom,
 	     				res.getString(1), // name
 	     				res.getInt(2)
 	     			);
 	     			
 	     			returningRes.add( dictionary );
 	     			
 	     		} while ( res.moveToNext() );
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no dictionary found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}

	}
	
	/**
	 * Get a language by its abbreviation code ISO-639 ('en' for English, 'fr' for French ...) or null
	 * if not found. 
	 * 
	 * @param abreviation The abbreviation of the searched language.
	 * @return The language with the specified abbreviation or null if not found.
	 * @throws AopdsDatabaseException Id data cannot be accessed.
	 */
	public Language getLanguageByAbreviation(
		String abreviation
	) throws AopdsDatabaseException {
		
		/* 
		 * Generating SQL query
		 * 
		 * Getting a language filtering with the abbreviation
		 */
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
     	qb.setTables(TABLE_LANGUAGE);
		
     	// asked fields
     	String[] fields = { 
     		TF_LANGUAGE_CODE,
     		TF_LANGUAGE_ABREVIATION,
     		TF_LANGUAGE_NAME,
     		TF_LANGUAGE_NAME_IN_ENGLISH
     	};
     	
     	// where values replacing ?s
     	String[] selectArgs = {
     		abreviation
     	};
     	
     	/*
     	 * Launching query
     	 */
     	
     	try {
     		
     		// getting connector for read only
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		TF_LANGUAGE_ABREVIATION + " = ?",
 	     		selectArgs,
 	     		null,
 	     		null,
 	     		null
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // one language found
 	     		
 	     		// mapping language 
 	     		
 	 			Language temp = new Language(
 	 				res.getInt(0), // id
 	 				res.getString(1), // abbreviation
 	 				res.getString(2), // name
 	 				res.getString(3) // name in English
 	 			);
 	     		
 	     		res.close();
 	     		
 	     		return temp;
 	     		
 	     	} else { // no data found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}
	
	}
	
	/**
	 * Get a language by its internal code.
	 * 
	 * @param code The code of the language to search.
	 * @return The language found from its code or null if doen't exist.
	 * @throws AopdsDatabaseException If data cannot be accessed.
	 */
	public Language getLanguageByCode(
			int code
		) throws AopdsDatabaseException {
			
		/*
		 * Getting a language by its code.
		 */
		
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	     	qb.setTables(TABLE_LANGUAGE);
			
	     	// asked fields
	     	String[] fields = { 
	     		TF_LANGUAGE_CODE,
	     		TF_LANGUAGE_ABREVIATION,
	     		TF_LANGUAGE_NAME,
	     		TF_LANGUAGE_NAME_IN_ENGLISH
	     	};
	     	
	     	String[] selectArgs = {
	     		Integer.toString(code)
	     	};
	     	
	     	/*
	     	 * Launching query
	     	 */
	     	try {
	     		// getting connector for read only
	     		SQLiteDatabase connection = db.getReadableDatabase();
	     		
	     		// performing query
	     		Cursor res = qb.query(
	 	     		connection,
	 	     		fields,
	 	     		TF_LANGUAGE_CODE + " = ?",
	 	     		selectArgs,
	 	     		null,
	 	     		null,
	 	     		null
	 	     	);
	 	     	
	     		// verifying cursor
	 	     	res = handleCursor(res);
	 	     	
	 	     	if ( res != null ) { // on language retrieved
	 	     		
	 	     		// mapping language
	 	 			Language temp = new Language(
	 	 				res.getInt(0), // id
	 	 				res.getString(1), // abbreviation
	 	 				res.getString(2), // name
	 	 				res.getString(3) // name in English
	 	 			);
	 	     		
	 	 			// closing cursor
	 	     		res.close();
	 	     		
	 	     		return temp;
	 	     		
	 	     	} else { // no language found
	 	     		return null;
	 	     	}
	     		
	     	} catch (SQLiteException e) {
	     		throw handleError(e);
	     	}

		}
	
	/**
	 * Get all languages.
	 * 
	 * @return All languages or null if no languages are installed.
	 * @throws AopdsDatabaseException If data cannot be accessed.
	 */
	public ArrayList<Language> getAllLanguages() throws AopdsDatabaseException {
     	
		/*
		 * Generating SQL query
		 * 
		 * getting all languages
		 */
     	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
     	
     	qb.setTables(TABLE_LANGUAGE);
     	
     	// asked fields
     	String[] fields = { 
     		TF_LANGUAGE_CODE,
     		TF_LANGUAGE_ABREVIATION,
     		TF_LANGUAGE_NAME,
     		TF_LANGUAGE_NAME_IN_ENGLISH
     	};
     	
     	/*
     	 * Launching query
     	 */
     	try {
     		// getting connector for read only
     		SQLiteDatabase connection = db.getReadableDatabase();
     		
     		// performing query
     		Cursor res = qb.query(
 	     		connection,
 	     		fields,
 	     		null,
 	     		null,
 	     		null,
 	     		null,
 	     		TF_LANGUAGE_NAME_IN_ENGLISH + " ASC"
 	     	);
 	     	
     		// verifying cursor
 	     	res = handleCursor(res);
 	     	
 	     	if ( res != null ) { // cursor containing data
 	     		
 	     		// result set
 	     		ArrayList<Language> returningRes = new ArrayList<Language>();
 	     		
 	     		do { // at least one language
 	     			
 	     			// mapping language
 	     			
 	     			Language temp = new Language(
 	     				res.getInt(0), // id
 	     				res.getString(1), // abbreviation
 	     				res.getString(2), // name
 	     				res.getString(3) // name in english
 	     			);
 	     			
 	     			returningRes.add(temp);
 	     			
 	     		} while ( res.moveToNext() );
 	     		
 	     		// closing cursor
 	     		res.close();
 	     		
 	     		return returningRes;
 	     		
 	     	} else { // no language found
 	     		return null;
 	     	}
     		
     	} catch (SQLiteException e) {
     		throw handleError(e);
     	}
     	
     	
     	
    }
	
	
	/* *************************************************************************
	 * Private tools
	 */
	
	/**
	 * Handles a SQLite cursor and verifiy if it contains data.
	 * 
	 * @param c The cursor to verify.
	 * @return The cursor provided as an argument or null if the argument is not containing data.
	 */
	private static Cursor handleCursor(Cursor c) {
		
		if ( c == null ) {
     		return null;
     	} else {
     		if ( !c.moveToFirst() ) { // if true , c is containing data
     			c.close();
     			return null;
     		} else return c;
     	}
		
	}
	
	/**
	 * Use this methods to transform a Exception to a AopdsDatabaseException.
	 * This is useful to hide all different types of exceptions the data sources 
	 * may raise to the upper layers.
	 * 
	 * @param e The exception to transform.
	 * @return An AopdsDatabaseException describing the problem.
	 */
	private AopdsDatabaseException handleError(Exception e) {
		
 		return new DataBaseRuntimeException(DATABASE_NAME, DATABASE_VERSION, e);
 		
	}
	
	/**
	 * 
	 * Connector for AOPDS SQLite DB. 
	 * 
	 * Creates the DB in the first launch and provide access to this.
	 * 
	 * @author Julien Wollscheid | July 2011
	 *
	 */
    private static class DictionaryOpenHelper extends SQLiteOpenHelper {

    	/**
    	 * Android application context.
    	 */
        private final Context dictionaryContext;
        
        /**
         * Script of the database.
         */
        private final static String[] DATABASE_SCRIPT = {
        	
        	"CREATE TABLE " + TABLE_LANGUAGE + " (" +
        	  " " + TF_LANGUAGE_CODE + " INTEGER NOT NULL  ," +
        	  " " + TF_LANGUAGE_ABREVIATION + " TEXT ," +
        	  " " + TF_LANGUAGE_NAME + " TEXT  ," +
        	  " " + TF_LANGUAGE_NAME_IN_ENGLISH + " TEXT " +
        	  " , PRIMARY KEY (" + TF_LANGUAGE_CODE + ") );"
        	 , 
        	 "CREATE TABLE " + TABLE_INSTALLED_DICTIONARY + " (" +
        	  " " + TF_ID_ID + " INTEGER NOT NULL  ," +
        	  " " + TF_ID_LANGUAGE_TO + " INTEGER NOT NULL  ," +
        	  " " + TF_ID_LANGUAGE_FROM + " INTEGER NOT NULL  ," +
        	  " " + TF_ID_NAME + " TEXT , " +
        	  " " + TF_ID_VERSION + " INTEGER NOT NULL , " +
        	  " PRIMARY KEY (" + TF_ID_ID + ") ," +
        	  " FOREIGN KEY (" + TF_ID_LANGUAGE_TO + ") REFERENCES " + TABLE_LANGUAGE + " (" + TF_LANGUAGE_CODE + ")," +
        	  " FOREIGN KEY (" + TF_ID_LANGUAGE_FROM + ") REFERENCES " + TABLE_LANGUAGE + " (" + TF_LANGUAGE_CODE + ") );"
        	,
        	"CREATE TABLE " + TABLE_HEADWORD + " (" +
        	  " " + TF_HEADWORD_ID + " INTEGER NOT NULL  ," +
        	  " " + TF_HEADWORD_DICT_ID + " INTEGER NOT NULL  ," +
        	  " " + TF_HEADWORD_WORD + " TEXT NOT NULL  ," +
        	  " " + TF_HEADWORD_ENTRY + " TEXT ," +
        	  " " + TF_HEADWORD_PHONETIC + " TEXT ," +
        	  " " + TF_HEADWORD_PRONUN_EXISTS + " INTEGER," +
        	  " PRIMARY KEY (" + TF_HEADWORD_ID + ")," +
        	  " FOREIGN KEY(" + TF_HEADWORD_DICT_ID + ") REFERENCES " + TABLE_INSTALLED_DICTIONARY + " (" + TF_ID_ID + ") );"
        	,
        	"CREATE TABLE " + TABLE_SUGGESTION + " (" +
        	  " " + TF_SUGGESTION_ID + " INTEGER NOT NULL," +
        	  " " + TF_SUGGESTION_HEADWORD_ID + " INTEGER ," +
        	  " " + TF_SUGGESTION_DICT_ID + " INTEGER ," +
        	  " " + TF_SUGGESTION_WORD + " TEXT NOT NULL  ," +
        	  " " + TF_SUGGESTION_PHONETIC + " TEXT  ," +
        	  " " + TF_SUGGESTION_ENTRY + " TEXT ," +
        	  " " + TF_SUGGESTION_ACT_TYPE + " TEXT," +
        	  " " + TF_SUGGESTION_PRONUN_REC + " INTEGER," +
        	  " " + TF_SUGGESTION_SYNCH_STAT + " TEXT , " +
        	  " " + TF_SUGGESTION_CREATION_DATE + " INTEGER , " +
        	  " " + TF_SUGGESTION_DICT_VERSION + " INTEGER , " +
        	  " PRIMARY KEY (" + TF_SUGGESTION_ID + ")," +
        	  " FOREIGN KEY (" + TF_SUGGESTION_HEADWORD_ID + ") REFERENCES " + TABLE_HEADWORD + " (" + TF_HEADWORD_ID + ")" + 
        	  " FOREIGN KEY (" + TF_SUGGESTION_DICT_ID + ") REFERENCES " + TABLE_INSTALLED_DICTIONARY + " (" + TF_ID_ID + ") );"
        	,
        	"CREATE INDEX I_FK_SUGGESTION_HEADWORD ON " + TABLE_SUGGESTION + " (" + TF_HEADWORD_ID + " ASC);",
        	"CREATE INDEX I_FK_INSTALLED_DICTIONARY_LANGUAGE_TO ON " + TABLE_INSTALLED_DICTIONARY + " (" + TF_ID_LANGUAGE_TO + " ASC);",
        	"CREATE INDEX I_FK_INSTALLED_DICTIONARY_LANGUAGE_FROM ON " + TABLE_INSTALLED_DICTIONARY + " (" + TF_ID_LANGUAGE_FROM + " ASC);",
        	"CREATE INDEX I_FK_HEADWORD_INSTALLED_DICTIONARY ON " + TABLE_HEADWORD + " (" + TF_HEADWORD_DICT_ID + " ASC);",
        	"CREATE INDEX I_HEADWORD_WORD ON " + TABLE_HEADWORD + " (" + TF_HEADWORD_WORD + " ASC);",
        	"CREATE INDEX I_SUGGESTION_WORD ON " + TABLE_SUGGESTION+ " (" + TF_SUGGESTION_WORD + " ASC);"
        };
        
        /**
         * First inserts
         */
        private final static String[] DATABASE_INSERTS = {
        	
        	"INSERT INTO LANGUAGE VALUES ( 1, 'aa', 'Afaraf' , 'Afar' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 2, 'ab', 'Аҧсуа' , 'Abkhazian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 3, 'ae', 'Avesta' , 'Avestan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 4, 'af', 'Afrikaans' , 'Afrikaans' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 5, 'ak', 'Akan' , 'Akan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 6, 'am', 'አማርኛ' , 'Amharic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 7, 'an', 'Aragonés' , 'Aragonese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 8, 'ar', 'العربية' , 'Arabic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 9, 'as', 'অসমীয়া' , 'Assamese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 10, 'av', 'авар мацӀ ; магӀарул мацӀ' , 'Avaric' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 11, 'ay', 'Aymar aru' , 'Aymara' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 12, 'az', 'Azərbaycan dili' , 'Azerbaijani' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 13, 'ba', 'башҡорт теле' , 'Bashkir' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 14, 'be', 'Беларуская' , 'Belarusian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 15, 'bg', 'български език' , 'Bulgarian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 16, 'bh', 'भोजपुरी' , 'Bihari' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 17, 'bi', 'Bislama' , 'Bislama' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 18, 'bm', 'Bamanankan' , 'Bambara' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 19, 'bn', 'বাংলা' , 'Bengali' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 20, 'bo', 'བོད་ཡིག' , 'Tibetan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 21, 'br', 'Brezhoneg' , 'Breton' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 22, 'bs', 'Bosanski jezik' , 'Bosnian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 23, 'ca', 'Català' , 'Catalan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 24, 'ce', 'нохчийн мотт' , 'Chechen' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 25, 'ch', 'Chamoru' , 'Chamorro' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 26, 'co', 'Corsu' , 'Corsican' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 27, 'cr', 'ᓀᐦᐃᔭᐍᐏᐣ' , 'Cree' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 28, 'cs', 'Česky ; čeština' , 'Czech' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 29, 'cu', 'Словѣньскъ' , 'Old Church Slavonic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 30, 'cv', 'чӑваш чӗлхи' , 'Chuvash' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 31, 'cy', 'Cymraeg' , 'Welsh' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 32, 'da', 'Dansk' , 'Danish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 33, 'de', 'Deutsch' , 'German' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 34, 'dv', 'ދިވެހި' , 'Divehi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 35, 'dz', 'རྫོང་ཁ' , 'Dzongkha' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 36, 'ee', 'Ɛʋɛgbɛ' , 'Ewe' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 37, 'el', 'Ελληνικά' , 'Greek' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 38, 'en', 'English' , 'English' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 39, 'eo', 'Esperanto' , 'Esperanto' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 40, 'es', 'Español' , 'Spanish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 41, 'et', 'Eesti keel' , 'Estonian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 42, 'eu', 'Euskara' , 'Basque' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 43, 'fa', 'فارسی' , 'Persian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 44, 'ff', 'Fulfulde' , 'Fulah' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 45, 'fi', 'Suomen kieli' , 'Finnish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 46, 'fj', 'Vosa Vakaviti' , 'Fijian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 47, 'fo', 'Føroyskt' , 'Faroese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 48, 'fr', 'Français' , 'French' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 49, 'fy', 'Frysk' , 'Western Frisian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 50, 'ga', 'Gaeilge' , 'Irish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 51, 'gd', 'Gàidhlig' , 'Scottish Gaelic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 52, 'gl', 'Galego' , 'Galician' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 53, 'gn', 'Avañe''ẽ' , 'Guarani' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 54, 'gu', 'ગુજરાતી' , 'Gujarati' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 55, 'gv', 'Ghaelg' , 'Manx' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 56, 'ha', 'هَوُسَ' , 'Hausa' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 57, 'he', 'עברית' , 'Hebrew' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 58, 'hi', 'हिन्दी' , 'Hindi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 59, 'ho', 'Hiri Motu' , 'Hiri Motu' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 60, 'hr', 'Hrvatski' , 'Croatian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 61, 'ht', 'Kreyòl ayisyen' , 'Haitian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 62, 'hu', 'Magyar' , 'Hungarian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 63, 'hy', 'Հայերեն' , 'Armenian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 64, 'hz', 'Otjiherero' , 'Herero' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 65, 'ia', 'Interlingua' , 'Interlingua (International Auxiliary Language Association)' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 66, 'id', 'Bahasa Indonesia' , 'Indonesian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 67, 'ie', 'Interlingue' , 'Interlingue, Occidental' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 68, 'ig', 'Igbo' , 'Igbo' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 69, 'ii', 'ꆇꉙ' , 'Sichuan Yi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 70, 'ik', 'Iñupiaq ; Iñupiatun' , 'Inupiaq' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 71, 'io', 'Ido' , 'Ido' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 72, 'is', 'Íslenska' , 'Icelandic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 73, 'it', 'Italiano' , 'Italian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 74, 'iu', 'ᐃᓄᒃᑎᑐᑦ' , 'Inuktitut' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 75, 'ja', '日本語 (にほんご)' , 'Japanese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 76, 'jv', 'basa Jawa' , 'Javanese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 77, 'ka', 'ქართული' , 'Georgian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 78, 'kg', 'KiKongo' , 'Kongo' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 79, 'ki', 'Gĩkũyũ' , 'Kikuyu' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 80, 'kj', 'Kuanyama' , 'Kwanyama' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 81, 'kk', 'Қазақ тілі' , 'Kazakh' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 82, 'kl', 'kalaallisut, kalaallit oqaasii' , 'Kalaallisut, Greenlandic' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 83, 'km', 'ភាសាខ្មែរ' , 'Khmer' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 84, 'kn', 'ಕನ್ನಡ' , 'Kannada' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 85, 'ko', '한국어 (韓國語)' , 'Korean' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 86, 'kr', 'Kanuri' , 'Kanuri' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 87, 'ks', 'कश्मीरी ; كشميري' , 'Kashmiri' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 88, 'ku', 'Kurdî ; كوردی' , 'Kurdish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 89, 'kv', 'коми кыв' , 'Komi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 90, 'kw', 'Kernewek' , 'Cornish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 91, 'ky', 'кыргыз тили' , 'Kirghiz' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 92, 'la', 'Latine' , 'Latin' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 93, 'lb', 'Lëtzebuergesch' , 'Luxembourgish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 94, 'lg', 'Luganda' , 'Ganda' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 95, 'li', 'Limburgs' , 'Limburgish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 96, 'ln', 'Lingála' , 'Lingala' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 97, 'lo', 'ພາສາລາວ' , 'Lao' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 98, 'lt', 'Lietuvių kalba' , 'Lithuanian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 99, 'lu', 'cilubà ' , 'Luba-Katanga' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 100, 'lv', 'Latviešu valoda' , 'Latvian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 101, 'mg', 'Fiteny malagasy' , 'Malagasy' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 102, 'mh', 'Kajin M̧ajeļ' , 'Marshallese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 103, 'mi', 'Te reo Māori' , 'Maori' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 104, 'mk', 'македонски јазик' , 'Macedonian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 105, 'ml', 'മലയാളം' , 'Malayalam' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 106, 'mn', 'Монгол' , 'Mongolian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 107, 'mo', 'лимба молдовеняскэ' , 'Moldavian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 108, 'mr', 'मराठी' , 'Marathi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 109, 'ms', 'Bahasa Melayu ; بهاس ملايو' , 'Malay' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 110, 'mt', 'Malti' , 'Maltese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 111, 'my', 'Burmese' , 'Burmese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 112, 'na', 'Ekakairũ Naoero' , 'Nauru' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 113, 'nb', 'Norsk bokmål' , 'Norwegian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 114, 'nd', 'isiNdebele' , 'North Ndebele' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 115, 'ne', 'नेपाली' , 'Nepali' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 116, 'ng', 'Owambo' , 'Ndonga' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 117, 'nl', 'Nederlands' , 'Dutch' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 118, 'nn', 'Norsk nynorsk' , 'Norwegian Nynorsk' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 119, 'no', 'Norsk' , 'Norwegian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 120, 'nr', 'Ndébélé' , 'South Ndebele' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 121, 'nv', 'Diné bizaad ; Dinékʼehǰí' , 'Navajo' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 122, 'ny', 'ChiCheŵa ; chinyanja' , 'Chichewa' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 123, 'oc', 'Occitan' , 'Occitan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 124, 'oj', 'ᐊᓂᔑᓈᐯᒧᐎᓐ' , 'Ojibwa' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 125, 'om', 'Afaan Oromoo' , 'Oromo' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 126, 'or', 'ଓଡ଼ିଆ' , 'Oriya' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 127, 'os', 'Ирон æвзаг' , 'Ossetian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 128, 'pa', 'ਪੰਜਾਬੀ ; پنجابی' , 'Panjabi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 129, 'pi', 'पािऴ' , 'Pāli' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 130, 'pl', 'Polski' , 'Polish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 131, 'ps', 'پښتو' , 'Pashto' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 132, 'pt', 'Português' , 'Portuguese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 133, 'qu', 'Runa Simi, Kichwa' , 'Quechua' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 134, 'rm', 'Rumantsch grischun' , 'Raeto-Romance' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 135, 'rn', 'kiRundi' , 'Kirundi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 136, 'ro', 'Română' , 'Romanian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 137, 'ru', 'русский язык' , 'Russian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 138, 'rw', 'Kinyarwanda' , 'Kinyarwanda' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 139, 'sa', 'संस्कृतम्' , 'Sanskrit' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 140, 'sc', 'sardu' , 'Sardinian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 141, 'sd', 'सिन्धी ; سنڌي، سندھی' , 'Sindhi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 142, 'se', 'Davvisámegiella' , 'Northern Sami' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 143, 'sg', 'Yângâ tî sängö' , 'Sango' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 144, 'si', 'සිංහල' , 'Sinhalese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 145, 'sk', 'Slovenčina' , 'Slovak' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 146, 'sl', 'Slovenščina' , 'Slovenian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 147, 'sm', 'Gagana fa''a Samoa' , 'Samoan' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 148, 'sn', 'chiShona' , 'Shona' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 149, 'so', 'Soomaaliga, af Soomaali' , 'Somali' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 150, 'sq', 'Shqip' , 'Albanian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 151, 'sr', 'српски језик' , 'Serbian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 152, 'ss', 'SiSwati' , 'Swati' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 153, 'st', 'seSotho' , 'Sotho Southern' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 154, 'su', 'Basa Sunda' , 'Sundanese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 155, 'sv', 'Svenska' , 'Swedish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 156, 'sw', 'Kiswahili' , 'Swahili' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 157, 'ta', 'தமிழ்' , 'Tamil' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 158, 'te', 'తెలుగు' , 'Telugu' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 159, 'tg', 'тоҷикӣ ; toğikī ; تاجیکی' , 'Tajik' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 160, 'th', 'ไทย' , 'Thai' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 161, 'ti', 'ትግርኛ' , 'Tigrinya' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 162, 'tk', 'Türkmen ; Түркмен' , 'Turkmen' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 163, 'tl', 'Tagalog' , 'Tagalog' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 164, 'tn', 'seTswana' , 'Tswana' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 165, 'to', 'faka Tonga' , 'Tonga' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 166, 'tr', 'Türkçe' , 'Turkish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 167, 'ts', 'xiTsonga' , 'Tsonga' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 168, 'tt', 'татарча ; tatarça ; تاتارچا' , 'Tatar' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 169, 'tw', 'Twi' , 'Twi' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 170, 'ty', 'Reo Mā`ohi' , 'Tahitian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 171, 'ug', 'Uyƣurqə ; ئۇيغۇرچ' , 'Uighur' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 172, 'uk', 'українська мова' , 'Ukrainian' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 173, 'ur', 'اردو' , 'Urdu' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 174, 'uz', 'O''zbek ; Ўзбек ; أۇزبېك' , 'Uzbek' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 175, 've', 'tshiVenḓa' , 'Venda' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 176, 'vi', 'Tiếng Việt' , 'Vietnamese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 177, 'vo', 'Volapük' , 'Volapük' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 178, 'wa', 'Walon' , 'Walloon' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 179, 'wo', 'Wollof' , 'Wolof' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 180, 'xh', 'isiXhosa' , 'Xhosa' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 181, 'yi', 'ייִדיש' , 'Yiddish' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 182, 'yo', 'Yorùbá' , 'Yoruba' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 183, 'za', 'Saɯ cueŋƅ ; Saw cuengh' , 'Zhuang, Chuang' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 184, 'zh', '中文, 汉语, 漢語' , 'Chinese' ); " ,
        	"INSERT INTO LANGUAGE VALUES ( 185, 'zu', 'IsiZulu' , 'Zulu' );" ,

        	"INSERT INTO INSTALLED_DICTIONARY VALUES (1, 48, 38, 'Let''s learn french !', 0); " ,
        	"INSERT INTO INSTALLED_DICTIONARY VALUES (2, 38, 48, 'L''anglais facile !', 0); " ,
        	"INSERT INTO INSTALLED_DICTIONARY VALUES (3, 38, 50, 'Irish for dummys !', 0);"
        	
        };
        
        /**
         * @param context Android application context
         */
        DictionaryOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            dictionaryContext = context;
        }   
    
        /**
         * Loads first test data. Gaelic dictionary.
         * 
         * @param db the db connector.
         * @throws IOException If any problem.
         */
        private void loadWords(SQLiteDatabase db) throws IOException {
        	
        	AopdsLogger.info( 
    			LOG_TAG, 
    			"Loading Gaelic dictionary for tests ! ..."
    		);
             
             InputStream inputStream = 
            	 dictionaryContext.
            	 	getAssets().
            	 		open("irish_english_dictionary_test.txt");
             
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

             try {
                 String line;
                 int id = -1;
                 
                 SQLiteStatement s = db.compileStatement(
                     "INSERT INTO HEADWORD VALUES ( ? , 3, ? , ? , '', 0 );"
                 );
                 
                 while ((line = reader.readLine()) != null) {
                     String[] strings = TextUtils.split(line, "#");
                     if (strings.length < 2) continue;
                     
                     id++;
                    
                   
                    s.bindLong(1, id);
                    s.bindString(2, strings[1] );
                    s.bindString(3, strings[0] );
                    
                    s.executeInsert();
                    s.clearBindings();
                 }
                 AopdsLogger.info( 
         			LOG_TAG, 
         			"Test data loading finished !"
         		);
                 
             } finally {
                 reader.close();
             }
             

        	
        	
        }
        
       
        /**
         * Creates the database and loads the first data.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        	AopdsLogger.info( 
    			LOG_TAG, 
    			"Creating Database ..."
    		);
        	
        	// creating database
        	// this should work !!!
    		for (String statement : DATABASE_SCRIPT) {
				db.execSQL(statement);
			}
        	
    		// performing first inserts
        	for (String statement : DATABASE_INSERTS) {
        		db.execSQL(statement);
			}
            
        	// test data 
            try {
            	loadWords(db);
        	} catch (Exception e) {
        		AopdsLogger.error(LOG_TAG, "Impossible to load test data !!! cause : " + e.getMessage() , e);
        	}
        	
        	AopdsLogger.info( 
    			LOG_TAG, 
    			"Database created ..."
    		);
        	
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, 
							  int oldVersion, 
							  int newVersion) {}
	 

    }

	
}
