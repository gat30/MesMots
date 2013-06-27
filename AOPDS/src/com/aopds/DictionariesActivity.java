package com.aopds;

import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.guiAdapters.DictionaryListAdapter;
import com.aopds.tools.AopdsErrorHandler;

public class DictionariesActivity extends AopdsActivity {

	public static final int TASK_SEARCH_WORD = 0;
	public static final int TASK_MANAGE_DICTIONARY = 1;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGui();
	}

	public void onClickOnDictionary(Dictionary d) {

		// Intent searchWord = new Intent(
		// getApplicationContext(),
		// SearchWordActivity.class
		// );
		//
		// searchWord.putExtra("com.aopds.dictionaryToBrowse", d );
		//
		// startActivity(searchWord);

		Intent data = new Intent();
		data.putExtra("com.aopds.pickedOutDictionary", d);
		setResult(RESULT_OK, data);
		finish();
	}

	public void initGui() {

		setContentView(R.layout.dictionaries_gui);

		AopdsDatabase db = AopdsDatabase.getInstance(getApplicationContext());
		DictionaryListAdapter adapter;

		Button createDictionaryButton = (Button) findViewById(R.id.dictionaryCreateDictionariesButton);
		Button addDictionaryButton = (Button) findViewById(R.id.dictionaryAddDictionaryButton);
		Button supprDictionaryButton = (Button) findViewById(R.id.dictionaryUpdateDictionariesButton);

		createDictionaryButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						CreateDictionaryActivity.class);
				startActivity(intent);
			}
		});

		//if (getIntent().getIntExtra("requestCode", 0) == TASK_SEARCH_WORD) {
			createDictionaryButton.setVisibility(View.GONE);
			addDictionaryButton.setVisibility(View.GONE);
			supprDictionaryButton.setVisibility(View.GONE);
		//}
		try {

			List<Dictionary> allDictionaries = db.getAllDictionaries();

			adapter = new DictionaryListAdapter(getApplicationContext(),
					R.layout.dictionary_list_item, allDictionaries);

			ListView dictionaryListView = (ListView) findViewById(R.id.dictionariesDictionariesList);

			dictionaryListView.setAdapter(adapter);

			dictionaryListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> adapter, View v,
								int position, long id) {

							Dictionary d = (Dictionary) adapter
									.getItemAtPosition(position);

							onClickOnDictionary(d);

						}
					});

		} catch (AopdsDatabaseException e) {
			AopdsErrorHandler.handleError(e,
					AopdsErrorHandler.DATABASE_ERROR_DEFAULT, this);
		}

	}

}