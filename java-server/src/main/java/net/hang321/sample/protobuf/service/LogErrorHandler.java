package net.hang321.sample.protobuf.service;

import net.hang321.sample.protobuf.exception.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

/**
 * @Author: Steve Chan
 **/
@Service("logErrorHandler")
public class LogErrorHandler implements ErrorHandler {

  private static final Logger logger = LoggerFactory.getLogger(LogErrorHandler.class);

  public void handleError(Throwable t) {
    if (t instanceof MessagingException &&
        (t.getCause() instanceof RetriableException) ) {
      logger.info("retriable error occurred: {}", t.getMessage());
    } else {
      logger.error("Error occurred: ", t);
    }
  }

}
