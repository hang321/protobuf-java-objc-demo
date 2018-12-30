package net.hang321.sample.protobuf.exception;

/**
 * @Author: Steve Chan
 **/
public class RetriableException extends RuntimeException {

  private static final long serialVersionUID = 7510711894425103263L;

  public RetriableException() {
    super();
  }

  public RetriableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RetriableException(String message, Throwable cause) {
    super(message, cause);
  }

  public RetriableException(String message) {
    super(message);
  }

  public RetriableException(Throwable cause) {
    super(cause);
  }

}
