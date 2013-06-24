package com.aopds;

import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.AopdsServiceClient;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.error.AopdsFormError;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;
import com.aopds.user.AopdsUserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AopdsActivity {
	
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText emailConfirm;
    EditText psw;
    EditText pswConfirm;
    
    TextView starfirstName;
    TextView starlastName;
    TextView staremail;
    TextView staremailConfirm;
    TextView starpsw;
    TextView starpswConfirm;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        initGUI();
           
   }
    
    private void initGUI()
    {
    	firstName = (EditText) findViewById(R.id.SignUpActivityFirstNameEditText);
    	lastName  = (EditText) findViewById(R.id.SignUpActivityLastNameEditText);
    	email = (EditText) findViewById(R.id.SignUpActivityEmailEditText);
    	emailConfirm = (EditText) findViewById(R.id.SignUpActivityEmailConfirmEditText);
    	psw = (EditText) findViewById(R.id.SignUpActivityPasswordEditText);
    	pswConfirm = (EditText) findViewById(R.id.SignUpActivityPasswordConfirmEditText);    	
    	
    	starfirstName = (TextView) findViewById(R.id.SignUpActivityStarFirstNameTextView);
    	starlastName = (TextView) findViewById(R.id.SignUpActivityStarLastNameTextView);
    	staremail = (TextView) findViewById(R.id.SignUpActivityStarEmailTextView);
    	staremailConfirm = (TextView) findViewById(R.id.SignUpActivityStarEmailConfirmTextView);
    	starpsw = (TextView) findViewById(R.id.SignUpActivityStarPasswordTextView);
    	starpswConfirm = (TextView) findViewById(R.id.SignUpActivityStarPasswordConfirmTextView);
    	
    	starfirstName.setVisibility(TextView.INVISIBLE);
    	starlastName.setVisibility(TextView.INVISIBLE);
    	staremail.setVisibility(TextView.INVISIBLE);
    	staremailConfirm.setVisibility(TextView.INVISIBLE);
    	starpsw.setVisibility(TextView.INVISIBLE);
    	starpswConfirm.setVisibility(TextView.INVISIBLE);
    	
        //Sign Up Button
        final Button signUp = (Button) findViewById(R.id.SignUpActivitySignUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	removeStars(); //hides stars due to errors
            	
            	/*checks the errors, if there are, sets the stars visible. 
            	 * Returns an object indicating whether there has been 
            	 * a user error or not and what kind of error it is.*/
            	AopdsFormError errorinform = checkForm(); 
            	
            	AopdsLogger.info(getClass().getSimpleName(), "error in form? "+Boolean.toString(errorinform.getErrorcode()));
            	
            	if (!errorinform.getErrorcode())
            		{
            			saveUser();
            			AopdsLogger.info(getClass().getSimpleName(), "usersaved");
            		}
            	else{
            		TextView tv = (TextView) findViewById(R.id.SignUpActivityErrorTextView);
            		tv.setText(errorinform.getErrordisplay());
            		tv.setVisibility(0);
            		AopdsLogger.info(getClass().getName(), "error :\n"+errorinform.getErrordisplay());
            	}
            	
            }


        }); 
    	
    }
    
    //remove all displayed stars in the form
    private void removeStars() {
    	starfirstName = (TextView) findViewById(R.id.SignUpActivityStarFirstNameTextView);
    	starlastName = (TextView) findViewById(R.id.SignUpActivityStarLastNameTextView);
    	staremail = (TextView) findViewById(R.id.SignUpActivityStarEmailTextView);
    	staremailConfirm = (TextView) findViewById(R.id.SignUpActivityStarEmailConfirmTextView);
    	starpsw = (TextView) findViewById(R.id.SignUpActivityStarPasswordTextView);
    	starpswConfirm = (TextView) findViewById(R.id.SignUpActivityStarPasswordConfirmTextView);
    	
    	starfirstName.setVisibility(TextView.INVISIBLE);
    	starlastName.setVisibility(TextView.INVISIBLE);
    	staremail.setVisibility(TextView.INVISIBLE);
    	staremailConfirm.setVisibility(TextView.INVISIBLE);
    	starpsw.setVisibility(TextView.INVISIBLE);
    	starpswConfirm.setVisibility(TextView.INVISIBLE);
				
	}
    
    //returns a boolean value saying if the email address typed is well formed
    private boolean isEmail(){

    	boolean condition1 = email.getText().toString().indexOf("@") > 0; //true if the address contains a @
    	boolean condition2 = email.getText().toString().indexOf(".") > 0; //true if the address contains a .

    	return (condition1 && condition2);
    }
    
    
    /* checks the signup form and returns an object that contains
     * - a boolean saying if the form is well filled
     * - a string saying what is wrong if something is wrong*/
    private AopdsFormError checkForm()
    {
    	AopdsFormError error = new AopdsFormError();
    	
    	error.setErrorcode(false);
    	
    	if(firstName.getText().toString().compareTo("") == 0){ 
      		starfirstName.setVisibility(TextView.VISIBLE);
      		error.addToErrordisplay(getString(R.string.LABEL_EMPTY_BLANK)+" "+getString(R.string.LABEL_FIRST_NAME)+".\n");
    		error.setErrorcode(true);
      	}
      	
      	if(lastName.getText().toString().compareTo("") == 0){
    		starlastName.setVisibility(TextView.VISIBLE);
    		error.addToErrordisplay(getString(R.string.LABEL_EMPTY_BLANK)+" "+getString(R.string.LABEL_LAST_NAME)+".\n");
    		error.setErrorcode(true);
    	}    	
    	    	
    	if(!isEmail())
    	{
    		staremail.setVisibility(TextView.VISIBLE);
    		staremailConfirm.setVisibility(TextView.VISIBLE);
    		error.addToErrordisplay(getString(R.string.LABEL_UNCORRECT_ENTRY)+" "+getString(R.string.LABEL_YOUR_EMAIL_ADDRESS)+".\n");
    		error.setErrorcode(true);
    	}
    	
    	if(email.getText().toString().compareTo(emailConfirm.getText().toString()) != 0) 
    	{
    		error.addToErrordisplay(getString(R.string.LABEL_DIFFERENT_ADDRESSES)+".\n");
    		staremail.setVisibility(TextView.VISIBLE);
    		staremailConfirm.setVisibility(TextView.VISIBLE);
    		error.setErrorcode(true);
    	}
    	
    	if (psw.getText().toString().compareTo("") == 0){
    		starpsw.setVisibility(TextView.VISIBLE);
    		starpswConfirm.setVisibility(TextView.VISIBLE);
    		
    		error.addToErrordisplay(getString(R.string.LABEL_EMPTY_BLANK)+" "+getString(R.string.LABEL_PASSWORD)+".\n");
    		
    		error.setErrorcode(true);
    	}
    	
    	if(psw.getText().toString().compareTo(pswConfirm.getText().toString()) != 0) 
    	{
    		error.addToErrordisplay(getString(R.string.LABEL_DIFFERENT_PASSWORDS)+".\n");
    		starpsw.setVisibility(TextView.VISIBLE);
    		starpswConfirm.setVisibility(TextView.VISIBLE);
    		error.setErrorcode(true);
    	}
    	
    	return error;
    }
    
    
	// this function is called to save the user if the blanks are ok
    private void saveUser()
    {
    	AopdsUserManager userManager = AopdsUserManager.getInstance(getApplicationContext());
    	
    	AopdsServiceClient client = new AopdsServiceClient();
    	
    	try {
    		
			User user = client.signUpUser(email.getText().toString(), psw.getText().toString(), firstName.getText().toString(), lastName.getText().toString());
			user.setPassword(psw.getText().toString());
			
			getUserManager().setUser(user);
			
			getUserManager().persistUser();

			AopdsLogger.info(getClass().getName(), "email : "+email.getText());
			AopdsLogger.info(getClass().getName(), "psw : "+psw.getText());
			AopdsLogger.info(getClass().getName(), "firstName : "+firstName.getText());
			AopdsLogger.info(getClass().getName(), "lastName : "+lastName.getText());
		    	
			finish();
			
			Intent intent = new Intent(
					getApplicationContext(), 
					AccountActivity.class
			);

			startActivity( intent );
			
		} catch (AopdsServiceConnectionImpossibleException e) {
			// manage a connexion error
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.CONNECTION_ERROR_DEFAULT, this);
			
		} catch (AopdsServiceException e) {
			//big programming error
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.SERVICE_ERROR_DEFAULT, this);
			
		} catch (Exception e) {
			AopdsLogger.error(getClass().getName(), "An unexpected error occured", e);
			// juju te donnera une super exception pour ca 
		}

    }
    
    /*private void fillSpinner( Spinner s)
    {
    	AopdsDatabase db= AopdsDatabase.getInstance( getApplicationContext() );
    	ArrayAdapter<String> adapterForSpinner;
    	
    	ArrayList<Language> allLanguage;
		try {
			
			allLanguage = db.getAllLanguages();
			Iterator<Language> il = allLanguage.iterator();
			

			adapterForSpinner = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
			adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
			s.setAdapter(adapterForSpinner);
    	
    			                					      
	    	while (il.hasNext()) {
	    		adapterForSpinner.add(il.next().getName());
	    	}
    
	    } catch (AopdsDatabaseException e) {
			e.printStackTrace();
		}
    }*/
}