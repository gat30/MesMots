package com.aopds.tools;

import com.aopds.AopdsActivity;
import com.aopds.R;
import com.aopds.aopdsData.AopdsDataException.AopdsDatabaseException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceConnectionImpossibleException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceException;
import com.aopds.aopdsServiceClient.aopdsServiceException.AopdsServiceServerException;
import com.aopds.user.userManagerException.UserPersistingImpossible;

/**
 * 
 * @author Emmanuel DAMIANO
 * @author Julien COUDSI
 * 
 * Handles the exception and manages the type of display for them
 *
 */

public class AopdsErrorHandler {

	private static final int DIALOG = 0;
	private static final int LOGCAT = 1;

	public static final int CONNECTION_ERROR_DEFAULT = DIALOG;
	public static final int SERVICE_ERROR_DEFAULT = DIALOG;
	public static final int SERVICE_SERVER_ERROR_DEFAULT = DIALOG;
	public static final int DATABASE_ERROR_DEFAULT = DIALOG;
	public static final int USER_PERSIST_ERROR_DEFAULT = DIALOG;

	public static void handleError(Exception e, int handlingMode, AopdsActivity activity){
		
		if (e instanceof AopdsServiceConnectionImpossibleException){
			handleHttpError(e, handlingMode, activity);
		}
		else if (e instanceof AopdsServiceException){
			handleServiceError(e, handlingMode, activity);
		}
		else if (e instanceof AopdsServiceServerException)
		{
			handleServiceServerError(e, handlingMode, activity);
		}
		else if (e instanceof AopdsDatabaseException)
		{
			handleDatabaseError(e, handlingMode, activity);
		}
		else if (e instanceof UserPersistingImpossible)
		{
			handleUserPersistError(e, handlingMode, activity);
		} else {
			handleSimpleError(e, handlingMode, activity);
		}
	}

	private static void handleSimpleError(Exception e, int mode, AopdsActivity activity) {
		AopdsLogger.error(activity.getClass().getSimpleName(), e.getMessage(), e);
	}
	
	private static void handleHttpError(Exception e, int mode, AopdsActivity activity){

		switch(mode) {
		case DIALOG :
			AopdsDialog.displayAlertOkDialog(activity.getString(R.string.LABEL_CONNEXION_IMPOSSIBLE), activity);
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_CONNEXION_IMPOSSIBLE), e);
			break;
		case LOGCAT : 
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_CONNEXION_IMPOSSIBLE), e);
			break;
		default :
			//recalls the function with default mode
			handleHttpError(e, CONNECTION_ERROR_DEFAULT, activity);
			break;
		}
	}

	private static void handleServiceError(Exception e, int mode, AopdsActivity activity){

		switch(mode) {
		case DIALOG :
			AopdsDialog.displayAlertOkDialog(activity.getString(R.string.LABEL_SERVICE_EXCEPTION), activity);
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_SERVICE_EXCEPTION), e);
			break;
		case LOGCAT : 
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_SERVICE_EXCEPTION), e);
			break;
		default :
			//recalls the function with default mode
			handleHttpError(e, SERVICE_ERROR_DEFAULT, activity);
			break;
		}
	}

	private static void handleServiceServerError(Exception e, int mode, AopdsActivity activity){


		String message = activity.getString(R.string.LABEL_SERVER_ERROR) + " : ";

		switch((((AopdsServiceServerException) e).getServerCode())) {

		case AopdsServiceServerException.MISSING_PARAMETER:

			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_MISSING_PARAMETER), activity);	
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_MISSING_PARAMETER), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_MISSING_PARAMETER), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}


			break;

		case AopdsServiceServerException.UNDEFINED_FUNCTION:
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_UNDEFINED_FUNCTION), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNDEFINED_FUNCTION), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNDEFINED_FUNCTION), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}
			
			break;

		case AopdsServiceServerException.UNKNOWN_FUNCTION:
			
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_UNKNOWN_FUNCTION), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNKNOWN_FUNCTION), e);	
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNKNOWN_FUNCTION), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}
			
			break;

		case AopdsServiceServerException.FUNCTION_CALL_FAILED:
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_FUNCTION_CALL_FAILED), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_FUNCTION_CALL_FAILED), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_FUNCTION_CALL_FAILED), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}

			break;

		case AopdsServiceServerException.UNAUTHORIZED_ACCESS:
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_UNAUTHORIZED_ACCESS), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNAUTHORIZED_ACCESS), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_UNAUTHORIZED_ACCESS), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}
			
			break;

		case AopdsServiceServerException.INTERNAL_SERVER_ERROR:
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_INTERNAL_SERVER_ERROR), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_INTERNAL_SERVER_ERROR), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_INTERNAL_SERVER_ERROR), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}
			
			break;

		case AopdsServiceServerException.WRONG_PARAMETER_TYPE:
			
			switch(mode) {
			case DIALOG :
				AopdsDialog.displayAlertOkDialog(message + activity.getString(R.string.LABEL_WRONG_PARAMETER_TYPE), activity);
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_WRONG_PARAMETER_TYPE), e);
				break;
			case LOGCAT : 
				AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_WRONG_PARAMETER_TYPE), e);
				break;
			default :
				//recalls the function with default mode
				handleServiceServerError(e, SERVICE_SERVER_ERROR_DEFAULT, activity);
				break;
			}
			
			
			break;
		}

	}
	
	private static void handleDatabaseError(Exception e, int mode, AopdsActivity activity) {
		
		switch (mode) {
		
		case DIALOG:
			AopdsDialog.displayAlertOkDialog(activity.getString(R.string.LABEL_DATABASE_ERROR), activity);
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_DATABASE_ERROR), e);
			break;
			
		case LOGCAT:
			AopdsLogger.error(activity.getClass().getName(), activity.getString(R.string.LABEL_DATABASE_ERROR), e);
			break;
		}
	}
	
	private static void handleUserPersistError(Exception e, int mode, AopdsActivity activity) {
		
		switch (mode) {
		
		case DIALOG:
			AopdsDialog.displayAlertOkDialog(e.getMessage(), activity);
			AopdsLogger.error(activity.getClass().getName(), e.getMessage(), e);
			break;
			
		case LOGCAT:
			AopdsLogger.error(activity.getClass().getName(),e.getMessage(), e);
			break;
		}
		
	}

}
