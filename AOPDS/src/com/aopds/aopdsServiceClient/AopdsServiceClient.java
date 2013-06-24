package com.aopds.aopdsServiceClient;

import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.aopds.aopdsData.domain.Suggestion;
import com.aopds.aopdsData.domain.User;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceMalformedResponseException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.tools.AopdsLogger;

public class AopdsServiceClient {

	private AopdsServiceConnector connector;

	public AopdsServiceClient() {
		connector = new AopdsServiceConnector();
	}

	public User signUpUser(String userEmail, String userPassword,
			String userFirstName, String userLastName)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceException, AopdsServiceServerException {

		AopdsLogger.info(getClass().getSimpleName(), "Signing up user ...");

		AopdsServiceParams p = new AopdsServiceParams();

		p.addString("userEmail", userEmail);
		p.addString("userPassword", userPassword);
		p.addString("userFirstName", userFirstName);
		p.addString("userLastName", userLastName);

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_SIGN_UP_USER, p);

		AopdsServiceReturnHandler results = null;

		try {
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {

			User returnValue = null;

			try {
				returnValue = (User) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ returnValue.getClass() + " for function "
								+ AopdsServiceConnector.FUNCTION_SIGN_UP_USER,
						e);
			}

			return returnValue;

		}
	}

	public User modifyUser(int userId, String firstName, String lastName,
			String newPassword, AopdsServiceAuthorizationToken token)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceException, AopdsServiceServerException {

		AopdsLogger.info(getClass().getSimpleName(),
				"Modifying user " + token.getUserId());

		AopdsServiceParams p = new AopdsServiceParams();
		p.addString("userId", Integer.toString(userId));
		p.addString("userFirstName", firstName);
		p.addString("userLastName", lastName);
		p.addString("userNewPassword", newPassword);

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_MODIFY_USER, p, token);

		AopdsServiceReturnHandler results = null;

		try {
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {

			User returnValue = null;

			try {
				returnValue = (User) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ returnValue.getClass() + " for function "
								+ AopdsServiceConnector.FUNCTION_MODIFY_USER, e);
			}

			return returnValue;

		}
	}

	public User connectUser(AopdsServiceAuthorizationToken token)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceException, AopdsServiceServerException {

		AopdsLogger.info(getClass().getSimpleName(),
				"Connecting user " + token.getUserId());

		AopdsServiceParams p = new AopdsServiceParams();

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_CONNECT_USER, p, token);

		AopdsServiceReturnHandler results = null;

		try {
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {

			User returnValue = null;

			try {
				returnValue = (User) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ returnValue.getClass() + " for function "
								+ AopdsServiceConnector.FUNCTION_CONNECT_USER,
						e);
			}

			return returnValue;

		}
	}

	public List<Dictionary> getDictionaries(AopdsServiceAuthorizationToken token)
			throws AopdsServiceException,
			AopdsServiceConnectionImpossibleException,
			AopdsServiceServerException {

		AopdsLogger
				.info(getClass().getSimpleName(), "Getting all dictionaries");

		AopdsServiceParams p = new AopdsServiceParams();

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_GET_DICTIONARIES, p, token);

		AopdsServiceReturnHandler results = null;

		try {
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {

			List<Dictionary> returnValue = null;

			try {
				returnValue = (List<Dictionary>) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ returnValue.getClass()
								+ " for function "
								+ AopdsServiceConnector.FUNCTION_GET_DICTIONARIES,
						e);
			}

			return returnValue;

		}

	}

	public int getNbSuggestion(long userId, AopdsServiceAuthorizationToken token)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceException, AopdsServiceServerException {
		AopdsServiceParams p = new AopdsServiceParams();

		p.addString("userId", Long.toString(userId));

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_GET_NB_SUGGESTION, p, token);

		AopdsServiceReturnHandler results = null;

		try {
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return 0;
		} else {
			int returnValue = 0;
			try {
				returnValue = (Integer) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ Integer.class.getClass()
								+ " for function "
								+ AopdsServiceConnector.FUNCTION_GET_NB_SUGGESTION,
						e);
			}

			return returnValue;
		}
	}

	public List<Suggestion> getAllSuggestion(int userId,
			AopdsServiceAuthorizationToken token)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceException, AopdsServiceServerException {
		AopdsServiceParams p = new AopdsServiceParams();

		p.addString("userId", Long.toString(userId));

		String res = connector.callFunction(
				AopdsServiceConnector.FUNCTION_GET_ALL_SUGGESTION, p, token);

		AopdsServiceReturnHandler results = null;

		try {
			AopdsLogger.info("RES", res);
			results = new AopdsServiceReturnHandler(res);
		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {
			List<Suggestion> returnValue = null;
			try {
				returnValue = (List<Suggestion>) results.getReturnValue();
				AopdsLogger.info("RES", returnValue.toString());
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ Integer.class.getClass()
								+ " for function "
								+ AopdsServiceConnector.FUNCTION_GET_ALL_SUGGESTION,
						e);
			}

			return returnValue;
		}
	}

	public List<Suggestion> synchronizeSuggestions(
			List<Suggestion> unSynchronized, List<Long> pending,
			AopdsServiceAuthorizationToken token, Long userID)
			throws AopdsServiceConnectionImpossibleException,
			AopdsServiceServerException, AopdsServiceException {

		AopdsServiceParams p = new AopdsServiceParams();

		if (pending.isEmpty()) {
			p.addString("pending[0]", "");
		} else {
			p.addLongList("pending", pending);
		}

		if (unSynchronized.isEmpty()) {
			p.addString("unSynchronized[0]", "");
		} else {
			p.addSuggestionList("unSynchronized", unSynchronized);
		}

		p.addString("userId", Long.toString(userID));

		String res = connector
				.callFunction(
						AopdsServiceConnector.FUNCTION_SYNCHRONIZE_SUGGESTION,
						p, token);

		AopdsServiceReturnHandler results = null;

		try {
			AopdsLogger.info("RES", res);
			results = new AopdsServiceReturnHandler(res);

		} catch (AopdsServiceServerException e) {
			throw e;
		}

		if (results.getReturnValue() == null) {
			return null;
		} else {
			List<Suggestion> returnValue = null;
			try {
				returnValue = (List<Suggestion>) results.getReturnValue();
			} catch (ClassCastException e) {
				throw new AopdsServiceException(
						"The distant service responded with unexpected data type :"
								+ Integer.class.getClass()
								+ " for function "
								+ AopdsServiceConnector.FUNCTION_SYNCHRONIZE_SUGGESTION,
						e);
			}

			return returnValue;
		}
	}
}
