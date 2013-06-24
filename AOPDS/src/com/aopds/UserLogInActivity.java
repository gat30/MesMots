package com.aopds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.AopdsServiceAuthorizationToken;
import com.aopds.aopdsServiceClient.AopdsServiceClient;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.tools.AopdsDialog;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.user.userManagerException.UserPersistingImpossible;

public class UserLogInActivity extends AopdsActivity {

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		initGui();
	}
	
	public void launchAccountActivity()
	{
		Intent intent = new Intent(
				getApplicationContext(),
				AccountActivity.class
		);

		startActivity(intent);
	}

	public void connectUser()
	{
		AopdsServiceClient client = new AopdsServiceClient();

		//Retrieves the mail and the password typed by the user
		TextView fieldEmail = (TextView) findViewById(R.id.UserLogInEmailEditText);
		TextView fieldPassword = (TextView) findViewById(R.id.UserLogInPasswordEditText);

		//Tries to establish a connection to the server
		try {
			User user = client.connectUser(
					new AopdsServiceAuthorizationToken(
							(fieldEmail.getText()).toString(), 
							(fieldPassword.getText()).toString()
					)
			);
			
			user.setPassword(fieldPassword.getText().toString());

			//Connexion is OK -> we persist the user

			getUserManager().setUser(user);

			try {
				getUserManager().persistUser();
			} catch (UserPersistingImpossible e) {
				AopdsErrorHandler.handleError(e, AopdsErrorHandler.USER_PERSIST_ERROR_DEFAULT, this);
			}
			
			AopdsDialog.displayAlertOkDialog(getString(R.string.LABEL_YOU_ARE_CONNECTED) + " " + 
					getUserManager().getUser().getFirstName() + " " +
					getUserManager().getUser().getLastName() + ".", this);

			this.finish();


			//if the connection fails
		} catch (AopdsServiceConnectionImpossibleException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.CONNECTION_ERROR_DEFAULT, this);

		} catch (AopdsServiceServerException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.SERVICE_SERVER_ERROR_DEFAULT, this);

		} catch (AopdsServiceException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.SERVICE_ERROR_DEFAULT, this);
		}



	}

	public void initGui() {

		setContentView(R.layout.user_log_in_gui);

		Button signUpButton = (Button) findViewById( R.id.UserLogInSignUpButton);
		Button logInButton = (Button) findViewById (R.id.UserLogInLogInButton);

		//to launch "connectUser()" when the user press OK on the Keyboard
    	EditText search = (EditText) findViewById(R.id.UserLogInPasswordEditText);
    	search.setOnEditorActionListener(new OnEditorActionListener() {
    	    @Override
    	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	        if (actionId == EditorInfo.IME_ACTION_DONE) {
    	        	 //close the Keybord
    	     		  InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
    	     		  inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    	     		  
    	     		 connectUser();
    	        }
    	        return false;
    	    }
    	});
    	
		signUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(
						getApplicationContext(), 
						SignUpActivity.class
				);

				startActivity( intent );   	
			}
		});

		
		logInButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				connectUser();
			}
		});
	}

}
