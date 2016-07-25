package tsar.tomcat.status;

import java.io.IOException;
import java.nio.charset.Charset;
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

    static AtomicLong        IN         = new AtomicLong(0);

    static AtomicLong        OUT        = new AtomicLong(0);

    public static AtomicLong COST       = new AtomicLong(0);

    String                   status_url = "/tomcat_status";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String path = filterConfig.getInitParameter("status_url");
        if (path != null && path.trim().length() > 0) {
            this.status_url = path.trim();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
        }

        IN.incrementAndGet();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            if (status_url.equals(httpRequest.getRequestURI())) {
                printResult(httpResponse);
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            OUT.incrementAndGet();
        }
    }

    private void printResult(HttpServletResponse response) throws IOException {
        long out = OUT.get();
        long in = IN.get();
        long cost = COST.get();

        StringBuilder sb = new StringBuilder();
        sb.append("Active connections: ").append(in - out).append("\n");
        sb.append("Accept connections: ").append(in).append("\n");
        sb.append("Handle connections: ").append(out).append("\n");
        sb.append("Cost ms: ").append(cost).append("\n");
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
    public void destroy() {

    }
}
