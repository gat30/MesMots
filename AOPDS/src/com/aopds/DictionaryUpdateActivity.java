package com.aopds;

import java.util.ArrayList;
import java.util.List;

import com.aopds.guiAdapters.DictionaryListAdapter;
import com.aopds.tools.AopdsErrorHandler;
import com.aopds.tools.AopdsLogger;

import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsServiceClient.AopdsServiceAuthorizationToken;
import com.aopds.aopdsServiceClient.AopdsServiceClient;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;



public class DictionaryUpdateActivity extends AopdsActivity {

	DictionaryListAdapter adapter;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		List<Dictionary> dl =  new ArrayList<Dictionary>();

		adapter = new DictionaryListAdapter(getApplicationContext(), R.layout.dictionary_update_list_item, dl);
		initGui();
	}

	public void onRefresh() 
	{
		AopdsServiceClient client = new AopdsServiceClient();

		try 
		{
			User guy = getUserManager().getUser();
			if(guy!=null)
			{
				List<Dictionary> returnedList = client.getDictionaries(new AopdsServiceAuthorizationToken(Long.toString(guy.getId()), guy.getPassword()));

				if ( returnedList != null ) 
				{
					adapter.clear();
					adapter.addAll( returnedList );
					adapter.notifyDataSetChanged();
				}
			}
			else
			{
				AopdsLogger.info("DictionaryUpdateActivity", "No user connected");
				AlertDialog msgAlert = new AlertDialog.Builder(this).create();
		    	msgAlert.setTitle("Not connected");
		    	msgAlert.setMessage("You are not connected !");
		    	msgAlert.setButton("OK", new DialogInterface.OnClickListener() 
		    	{  
		    	   public void onClick(DialogInterface dialog, int which) 
		    	   {  
		    		   dialog.cancel();
		    	   } 
		    	});
		    	msgAlert.show();
			}
		} 
		catch (AopdsServiceException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.SERVICE_ERROR_DEFAULT, this);
		} 
		catch (AopdsServiceServerException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.SERVICE_SERVER_ERROR_DEFAULT, this);
		}
		catch (AopdsServiceConnectionImpossibleException e) {
			AopdsErrorHandler.handleError(e, AopdsErrorHandler.CONNECTION_ERROR_DEFAULT, this);
		}
		
	}

	public void initGui() {

		setContentView( R.layout.dictionary_update_gui );

		Button refreshButton = (Button) findViewById( R.id.dictionaryUpdateRefreshButton );

		ListView dictionaryList = (ListView) findViewById( R.id.dictionaryUpdateDictionaryList );

		dictionaryList.setAdapter( adapter );

		refreshButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				onRefresh();

			}
		});  

	}

}
