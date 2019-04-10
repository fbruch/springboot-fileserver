package de.codereview.springboot.fileserver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Thanks,
 * https://blog.jdriven.com/2016/06/spicy-spring-custom-error-json-response-with-errorattributes/
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {

  public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

    Throwable throwable = getError(webRequest);
    if (throwable != null) {
      Throwable cause = throwable.getCause();
      if (cause != null) {
        Map<String, Object> causeErrorAttributes = new HashMap<>();
        causeErrorAttributes.put("exception", cause.getClass().getName());
        causeErrorAttributes.put("message", cause.getMessage());
        errorAttributes.put("cause", causeErrorAttributes);
      }
    }
    return errorAttributes;
  }
}
