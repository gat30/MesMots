package com.aopds;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Language;

public class AopdsDatabaseTest {

	private final static String LOG_TAG = "AopdsDatabase TESTS";

	public static void testAll(AopdsDatabase data) {

		try {
			getAllLanguagesTest(data);

			getAllDictionariesTest(data);
			getLanguageByCodeTest(data);
			getDictionaryByIdTest(data);
			getDictionaryByLanguagesTest(data);
			searchWordTest(data);
			getOrderedWordListTest(data);
		} catch (AopdsDatabaseException e) {
			Log.e(LOG_TAG, e.getFullDebugMessage());
		}
	}

	public static void getOrderedWordListTest(AopdsDatabase data)
			throws AopdsDatabaseException {
		// gaelic /english dico
		Dictionary dic = data.getDictionaryByLanguages(2, 1);

		Log.i(LOG_TAG,
				"Starting tests for getOrderedWordList() -------------------------- ");
		Log.i(LOG_TAG, "Using this dictionary : " + dic.toString());

		ArrayList<Headword> words;

		Log.i(LOG_TAG, "Getting words from 30 to 40 : ");

		words = data.getOrderedWordList(dic.getCode(), 30, 10);

		Iterator<Headword> it = words.iterator();

		while (it.hasNext()) {
			Headword h = it.next();
			Log.i(LOG_TAG, h.getId() + " - " + h.getWord());
		}

		// ----

		Log.i(LOG_TAG, "Getting words from 1000 to 1100 : ");

		words = data.getOrderedWordList(dic.getCode(), 1000, 100);

		it = words.iterator();

		while (it.hasNext()) {
			Headword h = it.next();
			Log.i(LOG_TAG, h.getId() + " - " + h.getWord());
		}

		// ----

		Log.i(LOG_TAG, "Getting words from 100000 to 100010 (not existing) : ");

		words = data.getOrderedWordList(dic.getCode(), 100000, 10);

		if (words == null) {
			Log.i(LOG_TAG, "Retrieved null list ...");
		} else {
			it = words.iterator();

			while (it.hasNext()) {
				Headword h = it.next();
				Log.i(LOG_TAG, h.getId() + " - " + h.getWord());
			}
		}

		// ----

		Log.i(LOG_TAG, "Getting words from -10 to -1 : ");

		try {
			words = data.getOrderedWordList(dic.getCode(), -10, -1);
			Log.i(LOG_TAG, " Strange it worked ...");
		} catch (Exception e) {
			Log.i(LOG_TAG, " Ok getting this exception : "
					+ e.getClass().getName() + " " + e.getMessage());
		}

		Log.i(LOG_TAG,
				"Ending tests for getOrderedWordList() -------------------------- ");
	}

	public static void searchWordTest(AopdsDatabase data)
			throws AopdsDatabaseException {
		/*
		 * // gaelic /english dico Dictionary dic =
		 * data.getDictionaryByLanguages(2, 1);
		 * 
		 * Log.i(LOG_TAG,
		 * "Starting tests for searchWord() -------------------------- ");
		 * Log.i(LOG_TAG, "Using this dictionary : " + dic.toString() );
		 * 
		 * ArrayList<Headword> words;
		 * 
		 * Log.i(LOG_TAG, "Trying to search an empty word : ");
		 * 
		 * try { words = data.searchWord(dic.getCode(), "", true);
		 * Log.i(LOG_TAG, "Strange, no exception throwed !"); } catch (Exception
		 * e) { words = null; Log.i(LOG_TAG,
		 * "Normal ! got this exception type = " + e.getClass().getName() ); }
		 * 
		 * Log.i(LOG_TAG, "Testing exact matching : -  -  -  -  -  -  -  -   ");
		 * 
		 * // ----- Log.i(LOG_TAG, "Searching 'dronuilleog', should exists :");
		 * words = data.searchWord(dic.getCode(), "dronuilleog", true);
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * 
		 * // -----
		 * 
		 * Log.i(LOG_TAG, "Searching 'gabhann', should exists :"); words =
		 * data.searchWord(dic.getCode(), "gabhann", true);
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * // -----
		 * 
		 * Log.i(LOG_TAG,
		 * "Searching 'GABHANN' (same than previous but with MAJ), should exists :"
		 * ); words = data.searchWord(dic.getCode(), "GABHANN", true);
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * // -----
		 * 
		 * Log.i(LOG_TAG,
		 * "Searching 'fústráil' (with accents), should exists :"); words =
		 * data.searchWord(dic.getCode(), "fústráil", true );
		 * 
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * // -----
		 * 
		 * Log.i(LOG_TAG, "Searching 'generalisation' , should NOT exists :");
		 * words = data.searchWord(dic.getCode(), "generalisation", true);
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * // -----
		 * 
		 * Log.i(LOG_TAG, "Searching 'timide' , should NOT exists :"); words =
		 * data.searchWord(dic.getCode(), "timide", true);
		 * 
		 * if ( words != null ) { Log.i(LOG_TAG, "Retrieved " + words.size() +
		 * " words"); Log.i(LOG_TAG, "Retrieved " + words.toString() ); } else {
		 * Log.i(LOG_TAG, "Retrieved null list ..."); }
		 * 
		 * Log.i(LOG_TAG,
		 * "Ending tests for searchWord() -------------------------- ");
		 */
	}

	public static void getDictionaryByLanguagesTest(AopdsDatabase data)
			throws AopdsDatabaseException {

		Log.i(LOG_TAG,
				"Starting tests for getDictionaryByLanguages() -------------------------- ");

		ArrayList<Language> langs = data.getAllLanguages();

		int nbLanguages = langs.size();
		int current = 0;
		int afterCurrent = 0;

		Log.i(LOG_TAG,
				"Calling getAllLanguages() then crossing all possible language combinations ...");

		Log.i(LOG_TAG, "For info : " + nbLanguages + " languages returned");

		while (current < nbLanguages) {
			afterCurrent = 0;

			Language l1 = langs.get(current);

			while (afterCurrent < nbLanguages) {

				Language l2 = langs.get(afterCurrent);

				Log.i(LOG_TAG,
						"Getting dictionary for languages : (from: "
								+ l1.getCode() + "|" + l1.getNameInEnglish()
								+ " )" + " - (to: " + l2.getCode() + "|"
								+ l2.getNameInEnglish() + ")");

				Dictionary dic = data.getDictionaryByLanguages(l1.getCode(),
						l2.getCode());

				if (dic == null) {
					Log.i(LOG_TAG, "Retrieved null dictionary");
				} else {
					Log.i(LOG_TAG, "Retrieved :" + dic.toString());
				}

				afterCurrent++;

			}

			current++;
		}

		Log.i(LOG_TAG,
				"Trying to get a dictionary from non existing languages with codes 654321 and 123456");

		Dictionary d3 = data.getDictionaryByLanguages(654321, 123456);

		if (d3 == null) {
			Log.i(LOG_TAG, "Retrieved null dictionary for those languages");
		} else {
			Log.i(LOG_TAG,
					"Strange !! Retrieving this dictionary for the dummy languages : "
							+ d3.toString());
		}

		Log.i(LOG_TAG,
				"Ending tests for getDictionaryByLanguages() ------------------------------");

	}

	public static void getDictionaryByIdTest(AopdsDatabase data)
			throws AopdsDatabaseException {

		Log.i(LOG_TAG,
				"Starting tests for getDictionaryById() -------------------------- ");

		ArrayList<Dictionary> dics = data.getAllDictionaries();

		Iterator<Dictionary> it = dics.iterator();

		Log.i(LOG_TAG,
				"Calling getAllDictionaries() then getting each dictionary alone ...");

		Log.i(LOG_TAG, "For info : " + dics.size()
				+ " dictionaries returned ...");

		while (it.hasNext()) {
			Dictionary d1 = it.next();
			Dictionary d2 = data.getDictionaryById(d1.getCode());
			Log.i(LOG_TAG, "Searched code : " + d1.getCode()
					+ " Retrieved dictionary: " + d2.toString());
		}

		Log.i(LOG_TAG, "Trying to get a non existing dictionary : code 123456");

		Dictionary d3 = data.getDictionaryById(123456);

		if (d3 == null) {
			Log.i(LOG_TAG, "Retrieved null dictionary for code 123456");
		} else {
			Log.i(LOG_TAG,
					"Strange !! Retrieving this for code 123456 : "
							+ d3.toString());
		}

		Log.i(LOG_TAG,
				"Ending tests for getDictionaryById() ------------------------------");

	}

	public static void getLanguageByCodeTest(AopdsDatabase data)
			throws AopdsDatabaseException {

		Log.i(LOG_TAG,
				"Starting tests for getLanguageByCode() -------------------------- ");

		ArrayList<Language> langs = data.getAllLanguages();

		Iterator<Language> it = langs.iterator();

		Log.i(LOG_TAG,
				"Calling getAllLanguages() then getting each language alone ...");

		Log.i(LOG_TAG, "For info : " + langs.size() + " languages returned ...");

		while (it.hasNext()) {
			Language l1 = it.next();
			Language l2 = data.getLanguageByCode(l1.getCode());
			Log.i(LOG_TAG, "Searched code : " + l1.getCode()
					+ " Retrieved language: " + l2.toString());
		}

		Log.i(LOG_TAG, "Trying to get a non existing language : code 123456");

		Language l3 = data.getLanguageByCode(123456);

		if (l3 == null) {
			Log.i(LOG_TAG, "Retrieved null language for code 123456");
		} else {
			Log.i(LOG_TAG,
					"Strange !! Retrieving this for code 123456 : "
							+ l3.toString());
		}

		Log.i(LOG_TAG,
				"Ending tests for getLanguageByCode() ------------------------------");

	}

	public static void getAllLanguagesTest(AopdsDatabase data)
			throws AopdsDatabaseException {

		Log.i(LOG_TAG,
				"Starting tests for getAllLanguages() -------------------------- ");

		ArrayList<Language> langs = data.getAllLanguages();

		Iterator<Language> it = langs.iterator();

		Log.i(LOG_TAG, "Printing languages list ... " + langs.size()
				+ " elements returned !");

		while (it.hasNext()) {
			Log.i(LOG_TAG, it.next().toString());
		}

		Log.i(LOG_TAG,
				"Ending tests for getAllLanguages() ------------------------------");

	}

	public static void getAllDictionariesTest(AopdsDatabase data)
			throws AopdsDatabaseException {

		Log.i(LOG_TAG,
				"Starting tests for getAllDictionaries() -------------------------- ");

		ArrayList<Dictionary> dics = data.getAllDictionaries();

		Iterator<Dictionary> it = dics.iterator();

		Log.i(LOG_TAG, "Printing dictionary list ... " + dics.size()
				+ " elements returned");

		while (it.hasNext()) {
			Log.i(LOG_TAG, it.next().toString());
		}

		Log.i(LOG_TAG,
				"Ending tests for getAllDictionaries() ------------------------------");

	}

}
