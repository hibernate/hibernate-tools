package org.hibernate.tool.ide.completion;

/**
 * Exception that can be thrown when the lexer encounters errors (such as syntax errors etc.)
 * 
 * @author Max Rydahl Andersen
 *
 */
public class SimpleLexerException extends RuntimeException {

	public SimpleLexerException() {
		super();
	}

	public SimpleLexerException(String message, Throwable cause) {
		super( message, cause );
	}

	public SimpleLexerException(String message) {
		super( message );
	}

	public SimpleLexerException(Throwable cause) {
		super( cause );
	}

}
