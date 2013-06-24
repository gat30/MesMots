package com.aopds.aopdsServiceClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.tools.AopdsLogger;

public class AopdsServiceParams {

	List<BasicNameValuePair> effectiveParams;

	public AopdsServiceParams() {
		this.effectiveParams = new ArrayList<BasicNameValuePair>();
	}
	
	protected UrlEncodedFormEntity getFormEntity(){
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(effectiveParams, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {}
		
		return entity;
	}
	
	public void addAuthorizationToken(AopdsServiceAuthorizationToken token) {
		
		BasicNameValuePair userId = 
			new BasicNameValuePair( "aopdsAuthUserEmail" , token.getUserId() );
		
		BasicNameValuePair userPsw = 
			new BasicNameValuePair( "aopdsAuthUserPassword", token.getUserPassword() );	
		
		effectiveParams.add(userId);
		effectiveParams.add(userPsw);
	}
	
	public void addString(String paramName, String paramValue) {
		BasicNameValuePair stringPair = 
			new BasicNameValuePair( paramName, paramValue );
		effectiveParams.add(stringPair);
	}

	
	public void addUser(String paramName, User u) {
		
		BasicNameValuePair userId = 
			new BasicNameValuePair( paramName + "[id]", Long.toString( u.getId() ) );
		
		BasicNameValuePair userEmail = 
			new BasicNameValuePair( paramName + "[email]", u.getEmail() );
		
		BasicNameValuePair userPassword = 
			new BasicNameValuePair( paramName + "[password]", u.getPassword() );
		
		BasicNameValuePair userFirstName = 
			new BasicNameValuePair( paramName + "[firstName]", u.getFirstName() );
		
		BasicNameValuePair userLastName = 
			new BasicNameValuePair( paramName + "[lastName]", u.getLastName() );
		
		effectiveParams.add( userId );
		effectiveParams.add( userEmail );
		effectiveParams.add( userPassword );
		effectiveParams.add( userFirstName );
		effectiveParams.add( userLastName );
		
	}
	
	public void addLongList(String paramName, List<Long> li)
	{
		Iterator<Long> it = li.iterator();
		int i=0;
		
		while (it.hasNext())
		{
			long current = it.next();
			
			BasicNameValuePair value  = 
				new BasicNameValuePair( paramName + "["+ i + "]", Long.toString(current));
		
			effectiveParams.add(value);
			i++;
		}	
	}
	
	public void addSuggestionList(String paramName, List<Suggestion> ls) {
		
		Iterator<Suggestion> it = ls.iterator();
		int i = 0;
		
		while (it.hasNext())
		{
			Suggestion current = it.next();
			
			BasicNameValuePair idLocalSuggestion = 
				new BasicNameValuePair( paramName + "["+ i + "][idLocalSuggestion]", Long.toString(current.getId()));
			
			BasicNameValuePair actionType = 
				new BasicNameValuePair( paramName + "["+ i + "][actionType]", current.getActionType());
			
			BasicNameValuePair word = 
				new BasicNameValuePair( paramName + "["+ i + "][word]",current.getWord());
			
			BasicNameValuePair entry = 
				new BasicNameValuePair( paramName + "["+ i + "][entry]", current.getEntry());
			
			BasicNameValuePair phonetic = 
				new BasicNameValuePair( paramName + "["+ i + "][phonetic]", current.getPhonetic());
			
			BasicNameValuePair prononciationSent = 
				new BasicNameValuePair( paramName + "["+ i + "][prononciationSent]", Boolean.toString(current.getPronunciationRecorded()));
			
			BasicNameValuePair idDictionary = 
				new BasicNameValuePair( paramName + "["+ i + "][idDictionary]", Integer.toString(current.getDictionary().getCode()));
			
			BasicNameValuePair idHeadword;
			if(current.isAddActionType())
			{
				idHeadword = new BasicNameValuePair( paramName + "["+ i + "][idHeadword]", "null");
			}
			else
			{
				idHeadword = new BasicNameValuePair( paramName + "["+ i + "][idHeadword]", Long.toString(current.getHeadword().getId()));	
			}
						
			effectiveParams.add( idLocalSuggestion );
			effectiveParams.add( actionType );
			effectiveParams.add( word );
			effectiveParams.add( entry );
			effectiveParams.add( phonetic );
			effectiveParams.add( prononciationSent );
			effectiveParams.add( idDictionary );
			effectiveParams.add( idHeadword );
			
			i++;
		}
	}
}
