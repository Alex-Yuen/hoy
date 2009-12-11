package msg;

public interface Message {

	static final String INTERNAL_ERROR = "INTERNAL_ERROR";

	static final String DATA_NOT_FOUND = "DATA_NOT_FOUND";

	static final String DATA_EXIST_ALREADY = "DATA_EXIST_ALREADY";

	static final String DATA_CANNOT_BE_NULL = "DATA_CANNOT_BE_NULL";

	static final String NO_PRIVILEGE = "NO_PRIVILEGE";

	static final String INVALID_DATA = "INVALID_DATA";

	static final String DATA_IN_USE = "DATA_IN_USE";

	static final String INVALID_SESSION = "INVALID_SESSION";

	static final String INVALID_USERNAME_PASSWORD = "INVALID_USERNAME_PASSWORD";

	static final String MEMBER_NOT_EXIST = "MEMBER_NOT_EXIST";

	static final String BET_FAILED = "BET_FAILED";
	
	static final String BET_AMOUNT_LESS_THAN_MIN_BET_LIMIT = "BET_AMOUNT_LESS_THAN_MIN_BET_LIMIT";
	
	static final String BET_AMOUNT_GREATER_THAN_MAX_BET_LIMIT = "BET_AMOUNT_GREATER_THAN_MAX_BET_LIMIT";

	static final String TICKET_CANCELLED = "TICKET_CANCELLED";

	static final String INSUFFICIENT_CREDIT = "INSUFFICIENT_CREDIT";

	static final String INVALID_DRAW_STATUS = "INVALID_DRAW_STATUS";

	// ========================================================================

	static final String AGENT_EXIST_ALREADY = "AGENT_EXIST_ALREADY";

	static final String CREDIT_MUST_NOT_BE_LESS_THAN_ZERO = "CREDIT_MUST_NOT_BE_LESS_THAN_ZERO";

	static final String INVALID_GAME_COMMISSION_RATE = "INVALID_GAME_COMMISSION_RATE";

	static final String CUSTOMER_EXIST_ALREADY = "CUSTOMER_EXIST_ALREADY";

	static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";

	static final String INVALID_GAME_SHARE_RATE = "INVALID_GAME_SHARE_RATE";

	static final String AGENT_USER_EXIST_ALREADY = "AGENT_USER_EXIST_ALREADY";

	static final String SYS_USER_EXIST_ALREADY = "SYS_USER_EXIST_ALREADY";

	static final String CALCULATING_ALREADY = "CALCULATING_ALREADY";

	static final String PRIZE_NOT_READY = "PRIZE_NOT_READY";

}
