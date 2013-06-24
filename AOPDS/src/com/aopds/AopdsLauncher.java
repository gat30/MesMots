package com.aopds;

import com.aopds.tools.AopdsLogger;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AopdsLauncher extends AopdsActivity {

	public static final int TASK_SEARCH_WORD = 0;
	public static final int TASK_MANAGE_DICTIONARY = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		AopdsLogger.info(getClass().getSimpleName(), "Starting AOPDS ...");

		super.onCreate(savedInstanceState);

		initGui();
		updateAccountButton();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// setWelcomeLabelVisibility();

		updateAccountButton();
	}

	@Override
	public void onLanguageChanged(String language) {
		// TODO Auto-generated method stub
		super.onLanguageChanged(language);
		initGui();
	}

	private void updateAccountButton() {

		Button logInSignUpButton = (Button) findViewById(R.id.mainLogInSignUpButton);

		// if the user is connected, we refresh the text of the button Log in /
		// Sign up
		if (getUserManager().isUserPersisted()) {
			logInSignUpButton.setText(getString(R.string.LABEL_MY_ACCOUNT));
		} else {
			logInSignUpButton.setText(getString(R.string.LABEL_LOGIN));
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TASK_MANAGE_DICTIONARY) {
			if (resultCode == RESULT_OK) {

				Intent intent = new Intent(getApplicationContext(),
						DictionaryManagementActivity.class);

				intent.putExtra("com.aopds.concernedDictionary", data
						.getSerializableExtra("com.aopds.pickedOutDictionary"));

				startActivity(intent);
			}
		}

		if (requestCode == TASK_SEARCH_WORD) {
			if (resultCode == RESULT_OK) {

				Intent intent = new Intent(getApplicationContext(),
						SearchWordActivity.class);

				intent.putExtra("com.aopds.dictionaryToBrowse", data
						.getSerializableExtra("com.aopds.pickedOutDictionary"));

				startActivity(intent);
			}
		}

	}

	public void initGui() {

		setContentView(R.layout.main);

		Button preferencesButton = (Button) findViewById(R.id.mainSettingsButton);
		// Button signInButton = (Button) findViewById( R.id.signInButton );
		Button dictionariesButton = (Button) findViewById(R.id.mainDictionariesButton);
		// Button dictionaryUpdateButton = (Button) findViewById(
		// R.id.mainDictionaryUpdateButton );
		Button searchWordButton = (Button) findViewById(R.id.mainWordButton);

		Button logInSignUpButton = (Button) findViewById(R.id.mainLogInSignUpButton);

		/*
		 * if (getUserManager().isUserPersisted()){
		 * logInSignUpButton.setText(getString(R.string.LABEL_MY_ACCOUNT)); }
		 */

		// Button addWordButton = (Button) findViewById(R.id.mainAddWordButton);
		Button synchronizeButton = (Button) findViewById(R.id.mainSynchronizeButton);

		preferencesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						PreferencesActivity.class);

				startActivity(intent);

			}
		});

		/*
		 * signInButton.setOnClickListener(new View.OnClickListener() { public
		 * void onClick(View v) {
		 * 
		 * Intent intent = new Intent( getApplicationContext(), SignIn.class );
		 * 
		 * startActivity( intent ); } });
		 */

		searchWordButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						DictionariesActivity.class);
				startActivityForResult(intent, TASK_SEARCH_WORD);
			}
		});

		dictionariesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						DictionariesActivity.class);
				intent.putExtra("requestCode", TASK_MANAGE_DICTIONARY);
				startActivityForResult(intent, TASK_MANAGE_DICTIONARY);
			}
		});

		/*
		 * addWordButton.setOnClickListener(new View.OnClickListener() { public
		 * void onClick(View v) {
		 * 
		 * Intent intent = new Intent( getApplicationContext(),
		 * DictionariesActivity.class );
		 * 
		 * startActivityForResult(intent, TASK_ADD_WORD);
		 * 
		 * } });
		 */

		/*
		 * synchronizeButton.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) {
		 * 
		 * Intent intent = new Intent( getApplicationContext(),
		 * SynchronizeActivity.class );
		 * 
		 * startActivity( intent );
		 * 
		 * } });
		 */

		/*
		 * logInSignUpButton.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) {
		 * 
		 * //If the user is connected, the program launches account activity if
		 * (getUserManager().isUserPersisted()) { Intent intent = new Intent(
		 * getApplicationContext(), AccountActivity.class );
		 * 
		 * startActivity( intent ); } else //otherwise, it will be the log in
		 * activity { Intent intent = new Intent( getApplicationContext(),
		 * UserLogInActivity.class );
		 * 
		 * startActivity( intent );
		 * 
		 * }
		 * 
		 * 
		 * } });
		 */

		/*
		 * dictionaryUpdateButton.setOnClickListener(new View.OnClickListener()
		 * { public void onClick(View v) {
		 * 
		 * Intent intent = new Intent( getApplicationContext(),
		 * DictionaryUpdateActivity.class );
		 * 
		 * startActivity( intent ); } });
		 */

		// setWelcomeLabelVisibility();
	}

	/*
	 * private void setWelcomeLabelVisibility() {
	 * 
	 * TextView labelWelcome = (TextView) findViewById( R.id.mainWelcome);
	 * TextView labelName = (TextView) findViewById( R.id.mainLabelNameET);
	 * 
	 * if( getUserManager().isUserPersisted() ) { User u =
	 * getUserManager().getUser(); labelWelcome.setVisibility(View.VISIBLE);
	 * labelName.setVisibility(View.VISIBLE);
	 * labelName.setText(u.getFirstName()); } else {
	 * labelWelcome.setVisibility(View.INVISIBLE);
	 * labelName.setVisibility(View.INVISIBLE); } }
	 */

}