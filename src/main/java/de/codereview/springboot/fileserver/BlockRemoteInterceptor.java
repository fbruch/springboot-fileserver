package de.codereview.springboot.fileserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BlockRemoteInterceptor extends HandlerInterceptorAdapter
{
    private static Logger log = LoggerFactory.getLogger(BlockRemoteInterceptor.class);

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception {

        String remoteAddr = getRemoteAddr(request);
        boolean isLocal = "127.0.0.1".equals(remoteAddr) // JUnit
            || "0:0:0:0:0:0:0:1".equals(remoteAddr);
        if (!isLocal) response.setStatus(HttpStatus.FORBIDDEN.value());
        return isLocal;
    }

    /** see http://www.baeldung.com/spring-mvc-handlerinterceptor */
    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}
