package com.aopds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.aopds.guiAdapters.WordListAdapter;
import com.aopds.tools.AopdsLogger;

import com.aopds.aopdsData.AopdsDatabase;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Suggestion;
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



public class SynchronizeActivity extends AopdsActivity {
	
	AopdsDatabase data; 
	WordListAdapter adapter;
	ArrayList<Suggestion> allSugg;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		ArrayList<AbstractWord> sl =  new ArrayList<AbstractWord>();
		
		//adapter = new SuggestionListAdapter(getApplicationContext(), R.layout.suggestion_list_item, sl);
		adapter = new WordListAdapter(getApplicationContext(), R.layout.search_word_list_item, sl,null);
		initGui();
	}
	
	public void initGui() {

		setContentView( R.layout.synchronize_suggestion_gui );

		Button synchronizeButton = (Button) findViewById( R.id.suggestionSynchronizeButton );

		ListView suggestionList = (ListView) findViewById( R.id.synchronizeSuggestionList );

		suggestionList.setAdapter( adapter );

		synchronizeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				onRefresh();

			}
		});  
		
		try {
		
			data = AopdsDatabase.getInstance(getApplicationContext());
			
			 ArrayList<Suggestion> modifyDeleteSuggestions = data.getAllModifyDeleteSuggestions();
			
			 ArrayList<Suggestion> addSuggestions = data.getAllAddSuggestions();
	
			
			 allSugg = new ArrayList<Suggestion>();
			
			if(modifyDeleteSuggestions != null)
				allSugg.addAll(modifyDeleteSuggestions);
			if(addSuggestions != null)
				allSugg.addAll(addSuggestions);
			
			Collections.reverse(allSugg);
			
			
			if ( allSugg != null ) 
			{
				adapter.clear();
				for( Suggestion s:allSugg)
				{
					adapter.add(s);
				}
				adapter.notifyDataSetChanged();
			}
		} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			AopdsLogger.error("ERR", e.getMessage(), e);
		}

	}

	public void onRefresh() 
	{
		AopdsServiceClient client = new AopdsServiceClient();
		try 
		{
			//Current user
			User u = getUserManager().getUser();
			
			if(u!=null && u.getEmail() != null && u.getEmail() != null)
			{
				//token
				AopdsServiceAuthorizationToken token = new AopdsServiceAuthorizationToken(u.getEmail(), u.getPassword());
				
				int nbSuggServer = client.getNbSuggestion(u.getId(), token);
	
				data = AopdsDatabase.getInstance(getApplicationContext());
				
				
				
				
				if ( allSugg != null && allSugg.size() >= nbSuggServer ) 
				{
		
					Iterator<Suggestion> it = allSugg.iterator();
					
					Iterator<Suggestion> unSynchronizedListIterator;
					ArrayList<Suggestion> unSynchronized = new ArrayList<Suggestion>();
					
					List<Long> pending = new ArrayList<Long>();
										
					while (it.hasNext())
					{
						Suggestion current = it.next();
						String synchroStatut = current.getSynchroStatus();
						
						if(synchroStatut.equals(Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED))
						{
							current.setSynchroStatus(Suggestion.SYNCHRO_STATUS_WAITING);
							unSynchronized.add(current);
						}
						else
						{
							if (synchroStatut.equals(Suggestion.SYNCHRO_STATUS_WAITING))
							{
								pending.add(current.getId());
							}
										
						}	
					}
					//AopdsLogger.info("SynchroActuvity", unSynchronized.toString());
									
					//Server Synchronisation
					List<Suggestion> returnedList = client.synchronizeSuggestions(unSynchronized, pending, token, u.getId());
					
					
					// Updating the synchro_status in the database from unsynchronized (u) to pending (p)
					unSynchronizedListIterator = unSynchronized.iterator();
					while (unSynchronizedListIterator.hasNext())
					{
						Suggestion currentUnSynchronized = unSynchronizedListIterator.next();
						data.updateSuggestionSynchroStatus(currentUnSynchronized);
					}
					
					ArrayList<Suggestion> displayList = new ArrayList<Suggestion>() ;
					
					if(!returnedList.isEmpty())
					{
						Iterator<Suggestion> ite = returnedList.iterator();
						while(ite.hasNext())
						{
							
							Suggestion current = ite.next();
							if(current.getId() == -1)
							{
								AlertDialog msgAlert = new AlertDialog.Builder(this).create();
						    	msgAlert.setTitle("Suggestion");
						    	msgAlert.setMessage("No pending suggestion found");
						    	msgAlert.setButton("OK", new DialogInterface.OnClickListener() 
						    	{  
						    	   public void onClick(DialogInterface dialog, int which) 
						    	   {  
						    		   dialog.cancel();
						    	   } 
						    	});
						    	msgAlert.show();
							}
							else
							{
								data.updateSuggestionSynchroStatus(current);
								if(current.isAddActionType())
								{
									current = data.getAddSuggestion(current.getId());
								}
								else
								{
									current = data.getModifyDeleteSuggestion(current.getId());
								}
								displayList.add(current);
							}
						}
					}
					initGui();
				} 
				else
				{
					ArrayList<Suggestion> allMySuggFromServer = new ArrayList<Suggestion>();
					Iterator<Suggestion> iterator;
					
					allMySuggFromServer = (ArrayList<Suggestion>) client.getAllSuggestion((int) u.getId(), token);
					//AopdsLogger.info("LA LISTE", allMySuggFromServer.toString());
					iterator = allMySuggFromServer.iterator();
					
					while(iterator.hasNext())
					{
						Suggestion current = iterator.next();
						//AopdsLogger.info("LA LISTE", current.toString());
						
						data.addSuggestion(current);
					}
					initGui();
				}
			}
			else
			{
				AopdsLogger.info("SynchronizeActivity", "No user connected");
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
		catch (AopdsServiceException e) 
		{
			AopdsLogger.error("ERR", e.getMessage(), e);
		} 
		catch (AopdsServiceServerException e) {
			AopdsLogger.error("ERR", e.getMessage(), e);
		}
		catch (AopdsServiceConnectionImpossibleException e) 
		{
			AopdsLogger.error("ERR", e.getMessage(), e);
		} catch (AopdsDatabaseException e) {
			// TODO Auto-generated catch block
			AopdsLogger.error("ERR", e.getMessage(), e);
		}
	}
	
	



}
