package net.sourceforge.marathon.runtime.api;

public class MarathonRuntimeException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

   public MarathonRuntimeException(String message, Throwable cause) {
       super(message, cause);
   }
}
