package core.exception;

public class SemanticException extends Exception {

	private String message;

	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @author Simone Spaccarotella
	 * @param msg
	 *            Costruttore parametrizzato. Accetta un messaggio, che verrà
	 *            poi stampato nel caso in cui questa eccezione viene generata
	 */
	public SemanticException(String msg) {
		message = msg;
	}

}
