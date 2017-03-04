package de.codereview.springboot.fileserver;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Thanks, https://blog.jdriven.com/2016/06/spicy-spring-custom-error-json-response-with-errorattributes/
 */
@Component
public class MyCustomErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);

		Throwable throwable = getError(requestAttributes);
		if (throwable != null)
		{
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
