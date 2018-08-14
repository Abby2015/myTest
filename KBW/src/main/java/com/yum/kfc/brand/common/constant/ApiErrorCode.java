package com.yum.kfc.brand.common.constant;

/**
 * 错误编码
 * @author luolix
 *
 */
public class ApiErrorCode {
	
	public static final int GENERAL_SERVER_SUCCESS = 	0;
	
	public static final int GENERAL_SERVER_ERROR = 		500000;
	public static final int UNCAUGHT_ERROR = 			GENERAL_SERVER_ERROR + 1;
	public static final int NOT_DATA_FOUND_ERROR = 		GENERAL_SERVER_ERROR + 2;
	public static final int GENERAL_SERVER_ERROR_AZS =	GENERAL_SERVER_ERROR + 3;//Microsoft azure storage
	
	public static final int GENERAL_CLIENT_ERROR = 		600000;
	public static final int SNS_TOKEN_INVALID = 		GENERAL_CLIENT_ERROR + 1;
	public static final int SNS_TOKEN_NOT_MATCH = 		GENERAL_CLIENT_ERROR + 2;
	public static final int INVALID_TOKEN = 			GENERAL_CLIENT_ERROR + 3;
	public static final int MISSING_TOKEN = 			GENERAL_CLIENT_ERROR + 4;
	public static final int CHINNEL_TYPE_ISNULL = 		GENERAL_CLIENT_ERROR + 5;
	public static final int CHINNEL_TYPE_INCORRECT =	GENERAL_CLIENT_ERROR + 6;
	public static final int DEVICE_TYPE_ISNULL = 		GENERAL_CLIENT_ERROR + 7;
	public static final int DEVICE_TYPE_INCORRECT = 	GENERAL_CLIENT_ERROR + 8;
	public static final int DEVICE_UUID_ISNULL = 		GENERAL_CLIENT_ERROR + 9;
	public static final int	CHOICE_ISNULL = 			GENERAL_CLIENT_ERROR + 10;
	public static final int CHOICE_INCORRECT = 			GENERAL_CLIENT_ERROR + 11;
	public static final int	CHOICE_RIGHT_ISNULL = 		GENERAL_CLIENT_ERROR + 12;
	public static final int CHOICE_RIGHT_INCORRECT = 	GENERAL_CLIENT_ERROR + 13;
	public static final int	MEDIA_TYPE_ISNULL = 		GENERAL_CLIENT_ERROR + 14;
	public static final int MEDIA_TYPE_INCORRECT = 		GENERAL_CLIENT_ERROR + 15;
	public static final int	SHARE_RESULT_ISNULL = 		GENERAL_CLIENT_ERROR + 16;
	public static final int SHARE_RESULT_INCORRECT = 	GENERAL_CLIENT_ERROR + 17;
	public static final int DRAW_STARTTIME_INCORRECT = 	GENERAL_CLIENT_ERROR + 18;
	public static final int DRAW_ENDTIME_INCORRECT = 	GENERAL_CLIENT_ERROR + 19;
	public static final int DRAW_PHONE_ISNULL = 		GENERAL_CLIENT_ERROR + 20;
	public static final int DRAW_PHONE_INCORRECT = 		GENERAL_CLIENT_ERROR + 21;
	public static final int DRAW_PHONE_WINAWARD = 		GENERAL_CLIENT_ERROR + 22;
	public static final int	ACK_TYPE_ISNULL = 			GENERAL_CLIENT_ERROR + 23;
	public static final int ACK_TYPE_INCORRECT = 		GENERAL_CLIENT_ERROR + 24;
	public static final int	SHARE_CONTENT_ISNULL = 		GENERAL_CLIENT_ERROR + 25;
	public static final int DRAW_NAME_ISNULL = 			GENERAL_CLIENT_ERROR + 26;
	public static final int DRAW_ADDRESS_ISNULL = 		GENERAL_CLIENT_ERROR + 28;
	public static final int USERID_ISNULL = 			GENERAL_CLIENT_ERROR + 29;
	public static final int REDIRECT_URL_ISNULL = 		GENERAL_CLIENT_ERROR + 30;
	public static final int USER_HAVE_DRAW = 			GENERAL_CLIENT_ERROR + 31;
	public static final int UPLOAD_FILE_PROBLEM = 		GENERAL_CLIENT_ERROR + 32;
	public static final int INVALID_URL = 				GENERAL_CLIENT_ERROR + 33;
	public static final int USER_HAVE_ATTEND = 			GENERAL_CLIENT_ERROR + 34;
	public static final int MISSING_BASE64CODE = 		GENERAL_CLIENT_ERROR + 35;
	public static final int	SID_ISNULL = 				GENERAL_CLIENT_ERROR + 36;
	public static final int CALL_TOO_FREQUENTLY = 		GENERAL_CLIENT_ERROR + 37;	
	public static final int CAMPAIGN_NOT_START = 		GENERAL_CLIENT_ERROR + 38;
	public static final int TOOMANY_IP_DRAWTIMES = 		GENERAL_CLIENT_ERROR + 39;
	public static final int TOOMANY_USER_DRAWTIMES = 	GENERAL_CLIENT_ERROR + 40;
	public static final int USER_HAVE_WON = 			GENERAL_CLIENT_ERROR + 41;
	public static final int DRAW_REACH_LIMIT = 			GENERAL_CLIENT_ERROR + 41;
	
	
}
