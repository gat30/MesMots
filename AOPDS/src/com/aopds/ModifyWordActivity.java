package com.aopds;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.tools.AopdsDialog;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;

public class ModifyWordActivity extends AopdsActivity {

	private AbstractWord modifiedWord;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		modifiedWord = (AbstractWord) getIntent().getExtras().getSerializable(
				"com.aopds.wordToModify");

		if (modifiedWord == null) {
			AopdsErrorHandler.handleError(new Exception(""), 0, this);
			finish();
		} else {
			if (modifiedWord.getDictionary() == null) {
				AopdsErrorHandler.handleError(new Exception(""), 0, this);
				finish();
			}
		}

		initGui();

	}

	private void modifyHeadword(String word, String entry) {

		Suggestion s = new Suggestion();

		s.setWord(word);
		s.setEntry(entry);

		s.setCreationDate(new Date());
		s.setDictionaryVersion(modifiedWord.getDictionary().getVersion());

		s.setHeadword((Headword) modifiedWord);
		s.setDictionary(modifiedWord.getDictionary());

		String fieldsModified = "";

		if (!word.equals(modifiedWord.getWord())) {
			fieldsModified += Suggestion.ACTION_TYPE_FIELD_WORD;
		}

		if (!entry.equals(modifiedWord.getEntry())) {
			fieldsModified += Suggestion.ACTION_TYPE_FIELD_ENTRY;
		}

		s.setIsModifyActionType(fieldsModified);

		s.setPronunciationRecorded(false);
		s.setSynchroStatus(Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED);

		try {
			AopdsDatabase.getInstance(getApplicationContext()).addSuggestion(s);
		} catch (AopdsDatabaseException e) {
			AopdsErrorHandler.handleError(e, 0, this);
		}
	}

	private void modifySuggestion(String word, String entry) {

		Suggestion modifiedSuggestion = (Suggestion) modifiedWord;

		if (modifiedSuggestion.getSynchroStatus().equals(
				Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED)) {

			modifiedSuggestion.setWord(word);
			modifiedSuggestion.setEntry(entry);

			if (modifiedSuggestion.isModifyActionType()) {

				String fieldsModified = "";

				if (!word.equals(modifiedSuggestion.getWord())) {
					fieldsModified += Suggestion.ACTION_TYPE_FIELD_WORD;
				}

				if (!entry.equals(modifiedSuggestion.getEntry())) {
					fieldsModified += Suggestion.ACTION_TYPE_FIELD_ENTRY;
				}

				modifiedSuggestion.mergeModifyActionType(fieldsModified);

			}

			try {
				AopdsDatabase.getInstance(getApplicationContext())
						.modifySuggestion(modifiedSuggestion);
			} catch (AopdsDatabaseException e) {
				AopdsErrorHandler.handleError(e, 0, this);
			}

		} else {

			Suggestion s = new Suggestion();

			s.setWord(word);
			s.setEntry(entry);

			s.setCreationDate(new Date());
			s.setDictionaryVersion(modifiedSuggestion.getDictionary()
					.getVersion());

			s.setHeadword(modifiedSuggestion.getHeadword());
			s.setDictionary(modifiedSuggestion.getDictionary());

			if (modifiedSuggestion.isModifyActionType()) {

				String fieldsModified = "";

				if (!word.equals(modifiedSuggestion.getWord())) {
					fieldsModified += Suggestion.ACTION_TYPE_FIELD_WORD;
				}

				if (!entry.equals(modifiedSuggestion.getEntry())) {
					fieldsModified += Suggestion.ACTION_TYPE_FIELD_ENTRY;
				}

				s.setIsModifyActionType(fieldsModified);

			} else {
				s.setActionType(modifiedSuggestion.getActionType());
			}

			s.setPronunciationRecorded(false);
			s.setSynchroStatus(Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED);

			try {
				AopdsDatabase.getInstance(getApplicationContext())
						.addSuggestion(s);
			} catch (AopdsDatabaseException e) {
				AopdsErrorHandler.handleError(e, 0, this);
			}

		}

	}

	private void suggestDeletion(Suggestion s) {
		AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
		s.setIsDeleteActionType();
		try {
			db.addSuggestion(s);
		} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onConfirmModification() {

		EditText fieldModifiedWord = (EditText) findViewById(R.id.ModifyWordWordEditText);
		EditText fieldModifiedEntry = (EditText) findViewById(R.id.ModifyWordEntryEditText);

		String word = fieldModifiedWord.getText().toString();
		String entry = fieldModifiedEntry.getText().toString();

		if (!(word.equals(modifiedWord.getWord()) && entry.equals(modifiedWord
				.getEntry()))) {

			if (modifiedWord instanceof Headword) {
				modifyHeadword(word, entry);
			} else if (modifiedWord instanceof Suggestion) {
				modifySuggestion(word, entry);
			}

			finish();
		} else {
			AopdsLogger.info("ERR", "faut le modif le mot qd meme");
		}

	}

	public void initGui() {

		setContentView(R.layout.modify_word_gui);

		TextView oldWord = (TextView) findViewById(R.id.ModifyWordWordTextLang);
		oldWord.setText(" ("
				+ modifiedWord.getDictionary().getLanguageFrom().getName()
				+ ")");
		TextView newWord = (TextView) findViewById(R.id.ModifyWordEntryTextLang);
		newWord.setText(" ("
				+ modifiedWord.getDictionary().getLanguageTo().getName() + ")");

		EditText fieldModifiedWord = (EditText) findViewById(R.id.ModifyWordWordEditText);
		EditText fieldModifiedEntry = (EditText) findViewById(R.id.ModifyWordEntryEditText);

		if (this.modifiedWord != null) {
			fieldModifiedWord.setText(modifiedWord.getWord());
			fieldModifiedEntry.setText(modifiedWord.getEntry());
		}

		Button buttonConfirm = (Button) findViewById(R.id.ModifyWordConfirmButton);
		Button buttonCancel = (Button) findViewById(R.id.ModifyWordCancelButton);

		buttonConfirm.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				onConfirmModification();

			}
		});

		buttonCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

	}

}
