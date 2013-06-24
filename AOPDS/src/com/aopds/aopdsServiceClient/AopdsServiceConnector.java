package com.aopds.aopdsServiceClient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.tools.AopdsLogger;

public class AopdsServiceConnector {

	private static final String SERVICE_URL = "http://10.0.2.2/aopdsService/webService/index.php?";
	
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int SOCKET_TIMEOUT = 5000;
	
	protected static final String FUNCTION_SIGN_UP_USER = "signUpUser";
	protected static final String FUNCTION_CONNECT_USER = "connectUser";
	protected static final String FUNCTION_MODIFY_USER = "modifyUser";
	protected static final String FUNCTION_GET_DICTIONARIES = "getDictionaries";
	protected static final String FUNCTION_GET_NB_SUGGESTION = "getNbSuggestion";
	protected static final String FUNCTION_GET_ALL_SUGGESTION = "getAllSuggestion";
	protected static final String FUNCTION_SYNCHRONIZE_SUGGESTION = "synchronizeSuggestions";
	
	private HttpClient client;
	
	public AopdsServiceConnector() {
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(httpParameters, true);

		
		client = new DefaultHttpClient( httpParameters );
	}

	public String callFunction(String function,
							   AopdsServiceParams params) throws AopdsServiceConnectionImpossibleException {
    	
    	HttpPost post = new HttpPost(SERVICE_URL);
		params.addString("function", function);
		
    	post.setEntity( params.getFormEntity() );
    	
    	
    	HttpResponse res;
    	
		try {
			res = client.execute( post );
			return EntityUtils.toString( res.getEntity(), "UTF-8" );
		} catch (ClientProtocolException e) {
			throw new AopdsServiceConnectionImpossibleException(
				"Impossible to call distant web service. HTTP connection problem", e
			);
		} catch (IOException e) {
			throw new AopdsServiceConnectionImpossibleException(
				"Impossible to call distant web service. HTTP connection problem", e
			);
		}
    	
    	
    	
	}
	
	public String callFunction(String function,
			   				   AopdsServiceParams params,
			   				   AopdsServiceAuthorizationToken token) throws AopdsServiceConnectionImpossibleException {

		params.addString("aopdsAuthUserEmail", token.getUserId() );
		params.addString("aopdsAuthUserPassword", token.getUserPassword() );
		
		return callFunction(function, params);
	}
	
	
}
