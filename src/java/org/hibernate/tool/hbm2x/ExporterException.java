/*
 * Created on 02-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

/**
 * Exception to use in Exporters.
 * @author max
 *
 */
public class ExporterException extends RuntimeException {

	public ExporterException() {
		super();
	}
	public ExporterException(String message) {
		super(message);
	}
	public ExporterException(String message, Throwable cause) {
		super(message, cause);
	}
	public ExporterException(Throwable cause) {
		super(cause);
	}
}
