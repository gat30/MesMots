package com.aopds;

import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.AopdsServiceAuthorizationToken;
import com.aopds.aopdsServiceClient.AopdsServiceClient;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.error.AopdsFormError;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;
import com.aopds.user.userManagerException.UserPersistingImpossible;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivity extends AopdsActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initGui();

	}

	public void initGui() {

		setContentView(R.layout.account_activity_gui);

		AopdsFormError error = new AopdsFormError();
		error.setErrorcode(false);

		// Check if the
		if (getUserManager().isUserPersisted()) {
			AopdsLogger.info(getClass().getName(), "id : "
					+ getUserManager().getUser().getId());
			AopdsLogger.info(getClass().getName(), "nom : "
					+ getUserManager().getUser().getLastName());
			AopdsLogger.info(getClass().getName(), "prenom : "
					+ getUserManager().getUser().getFirstName());
			AopdsLogger.info(getClass().getName(), "mail: "
					+ getUserManager().getUser().getEmail());
			AopdsLogger.info(getClass().getName(), "mdp : "
					+ getUserManager().getUser().getPassword());

			// display user's informations.
			EditText firstName = (EditText) findViewById(R.id.accountFirstNameTextBox);
			firstName.setText(getUserManager().getUser().getFirstName());

			EditText lastName = (EditText) findViewById(R.id.accountLastNameTextBox);
			lastName.setText(getUserManager().getUser().getLastName());

			EditText email = (EditText) findViewById(R.id.accountEMailTextBox);
			email.setText(getUserManager().getUser().getEmail());
			// The user can't change his email address
			boolean bool = false;
			email.setEnabled(bool);

			// When the user click on the update button
			Button modifButton = (Button) findViewById(R.id.AccountUpdateButton);
			modifButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					saveModifUser();
				}
			});

			// When the user click on the logOut button
			Button logoutButton = (Button) findViewById(R.id.AccountLogoutButton);
			logoutButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					logOut();

				}
			});

		} else {
			error.addToErrordisplay(getString(R.string.LABEL_YOU_ARE_NOT_CONNECTED)
					+ ".\n");
			error.setErrorcode(true);
		}

	}

	public void saveModifUser() {
		AopdsServiceClient client = new AopdsServiceClient();

		AopdsFormError error = new AopdsFormError();
		error.setErrorcode(false);

		TextView displayError = (TextView) findViewById(R.id.accountActivityErrorLabel);

		// retrieve the new informations typed by the user
		EditText firstName = (EditText) findViewById(R.id.accountFirstNameTextBox);
		String fn = firstName.getText().toString();

		EditText lastName = (EditText) findViewById(R.id.accountLastNameTextBox);
		String ln = lastName.getText().toString();

		EditText password = (EditText) findViewById(R.id.accountPasswordTextBox);
		String pw = password.getText().toString();

		if (getUserManager().isUserPersisted()) {
			AopdsLogger.info(getClass().getName(),
					"L'utilisateur est enregistre");
			AopdsLogger.info(getClass().getName(), "id : "
					+ getUserManager().getUser().getId());
			AopdsLogger.info(getClass().getName(), "nom : "
					+ getUserManager().getUser().getLastName());
			AopdsLogger.info(getClass().getName(), "mdp : "
					+ getUserManager().getUser().getPassword());
			// We retrieve the recorded password for this user
			String recordedPW = getUserManager().getUser().getPassword();
			if (recordedPW != null && recordedPW.equals(pw)) {
				AopdsServiceAuthorizationToken token = new AopdsServiceAuthorizationToken(
						getUserManager().getUser().getEmail(), recordedPW);
				// We retrieve and check the new password
				EditText newPassword = (EditText) findViewById(R.id.accountNewPasswordTextBox);
				String newPW = newPassword.getText().toString();

				EditText confirmNewPassword = (EditText) findViewById(R.id.accountNewPasswordConfTextBox);
				String confNewPW = confirmNewPassword.getText().toString();

				if (newPW.equals(confNewPW)) {
					try {
						User user = client.modifyUser((int) getUserManager()
								.getUser().getId(), fn, ln, newPW, token);

						getUserManager().setUser(user);

						getUserManager().persistUser();

						// console display
						AopdsLogger.info(getClass().getName(),
								"Vérification du mot de passe effectuee");
						AopdsLogger.info(getClass().getName(), "psw : "
								+ password.getText());
						AopdsLogger.info(getClass().getName(), "newpassword :"
								+ newPassword.getText());
						AopdsLogger.info(getClass().getName(), "firstName : "
								+ firstName.getText());
						AopdsLogger.info(getClass().getName(), "lastName : "
								+ lastName.getText());

						finish();

						Intent intent = new Intent(getApplicationContext(),
								AopdsLauncher.class);

						startActivity(intent);
					} catch (AopdsServiceConnectionImpossibleException e) {
						// manage a connection error
						AopdsErrorHandler.handleError(e,
								AopdsErrorHandler.CONNECTION_ERROR_DEFAULT,
								this);

					} catch (AopdsServiceException e) {
						// big programming error
						AopdsErrorHandler.handleError(e,
								AopdsErrorHandler.SERVICE_ERROR_DEFAULT, this);

					} catch (UserPersistingImpossible e) {
						AopdsErrorHandler.handleError(e,
								AopdsErrorHandler.USER_PERSIST_ERROR_DEFAULT,
								this);
						// user persisting impossible
					} catch (AopdsServiceServerException e) {
						AopdsErrorHandler.handleError(e,
								AopdsErrorHandler.SERVICE_ERROR_DEFAULT, this);
						// server exception
					} catch (Exception e) {
						e.printStackTrace();
						AopdsLogger.error(getClass().getName(),
								"An unexpected error occured", e);
						// unexpected error
					}

				} else {
					error.addToErrordisplay(getString(R.string.LABEL_DIFFERENT_PASSWORDS)
							+ ".\n");
					error.setErrorcode(true);
					displayError.setText(error.getErrordisplay());
					displayError.setVisibility(TextView.VISIBLE);
				}
			} else {
				error.addToErrordisplay(getString(R.string.LABEL_WRONG_PASSWORD)
						+ ".\n");
				error.setErrorcode(true);
				displayError.setText(error.getErrordisplay());
				displayError.setVisibility(TextView.VISIBLE);
			}
		} else {
			// There isn't saved user
			AopdsLogger.info(getClass().getName(), "Pas d'utilisateur");
		}
	}

	public void logOut() {
		try {
			getUserManager().setUser(null);
			getUserManager().discardUser();
			Intent intent = new Intent(getApplicationContext(),
					UserLogInActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			AopdsLogger.error(getClass().getName(),
					"An unexpected error occured", e);
		}

	}

}
