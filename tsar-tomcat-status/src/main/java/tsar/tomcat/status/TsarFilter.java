package tsar.tomcat.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by peiliping on 16-7-25.
 */
public class TsarFilter implements Filter {

    static AtomicLong    IN         = new AtomicLong(0);

    static AtomicLong    OUT        = new AtomicLong(0);

    static AtomicLong    COST       = new AtomicLong(0);

    static AtomicLong[]  HTTPCODES  = new AtomicLong[5];

    static AtomicBoolean HEALTH     = new AtomicBoolean(true);

    {
        for (int i = 0; i < 5; i++)
            HTTPCODES[i] = new AtomicLong(0);
    }

    String               status_url = "/tomcat_status";

    String               health_url = "/health_status";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String tomcat_status_path = filterConfig.getInitParameter("status_url");
        if (tomcat_status_path != null && tomcat_status_path.trim().length() > 0) {
            this.status_url = tomcat_status_path.trim();
        }

        String health_status_path = filterConfig.getInitParameter("health_url");
        if (health_status_path != null && health_status_path.trim().length() > 0) {
            this.health_url = health_status_path.trim();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
        }
        TsarFilter.IN.incrementAndGet();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            if (status_url.equals(httpRequest.getRequestURI())) {
                printStatusResult(httpResponse);
            } else if (health_url.equals(httpRequest.getRequestURI())) {
                if (httpRequest.getParameter("status") != null) {
                    HEALTH.set(Boolean.valueOf(httpRequest.getParameter("status")));
                }
                printHealthResult(httpResponse);
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            TsarFilter.OUT.incrementAndGet();
        }
    }

    private void printHealthResult(HttpServletResponse response) throws IOException {
        boolean health = HEALTH.get();

        StringBuilder sb = new StringBuilder();
        sb.append("health:").append(health).append("\n");

        byte[] result = sb.toString().getBytes(Charset.forName("UTF-8"));

        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=UTF-8");
        response.setContentLength(result.length);
        response.getOutputStream().write(result);
        response.flushBuffer();
    }

    private void printStatusResult(HttpServletResponse response) throws IOException {
        long out = OUT.get();
        long in = IN.get();
        long cost = COST.get();

        StringBuilder sb = new StringBuilder();
        sb.append("active:").append(in - out).append("\n");
        sb.append("accepts:").append(in).append("\n");
        sb.append("handled:").append(out).append("\n");
        sb.append("1XX:").append(HTTPCODES[0].get()).append("\n");
        sb.append("2XX:").append(HTTPCODES[1].get()).append("\n");
        sb.append("3XX:").append(HTTPCODES[2].get()).append("\n");
        sb.append("4XX:").append(HTTPCODES[3].get()).append("\n");
        sb.append("5XX:").append(HTTPCODES[4].get()).append("\n");
        sb.append("request_time:").append(cost).append("\n");
        byte[] result = sb.toString().getBytes(Charset.forName("UTF-8"));

        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=UTF-8");
        response.setContentLength(result.length);
        response.getOutputStream().write(result);
        response.flushBuffer();
    }

    @Override
    public void destroy() {}
}
