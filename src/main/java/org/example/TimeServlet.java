package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet(urlPatterns = "/time")
public class TimeServlet extends HttpServlet {
    public static final String DEFAULT_TIME_ZONE = "UTC";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh-mm-ss VV";
    public static final String QUERY_PARAM_TIMEZONE = "timezone";
    public static final String LAST_TIMEZONE_COOKIE = "lastTimezone";
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ZoneId zoneId = defineZoneId(req, resp);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        resp.setContentType("text/html");

        Context simpleContext = new Context(req.getLocale());
        simpleContext.setVariable("time", now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private static ZoneId defineZoneId(HttpServletRequest req, HttpServletResponse resp) {
        Optional<String> timeZoneFromRequest = getTimeZoneFromRequest(req);

        if (timeZoneFromRequest.isPresent()) {
            String timezone = timeZoneFromRequest.get();
            resp.addCookie(new Cookie(LAST_TIMEZONE_COOKIE, timezone));

            return ZoneId.of(timezone);
        }
        Optional<String> timeZoneFromCookie = getTimeZoneFromCookie(req);

        if (timeZoneFromCookie.isPresent()) {
            String timezone = timeZoneFromCookie.get();

            try {
                return ZoneId.of(timezone);
            } catch (DateTimeException ex) {
                return ZoneId.of(DEFAULT_TIME_ZONE);
            }
        }

        return ZoneId.of(DEFAULT_TIME_ZONE);
    }

    private static Optional<String> getTimeZoneFromRequest(HttpServletRequest req) {
        return Optional.ofNullable(req.getParameter(QUERY_PARAM_TIMEZONE));
    }

    private static Optional<String> getTimeZoneFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(LAST_TIMEZONE_COOKIE)) {
                return Optional.ofNullable(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
