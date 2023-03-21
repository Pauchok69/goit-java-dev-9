package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = "/time")
public class TimeServlet extends HttpServlet {
    public static final String DEFAULT_TIME_ZONE = "UTC";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh-mm-ss VV";
    public static final String QUERY_PARAM_TIMEZONE = "timezone";
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
//        Optional<String> timezoneOptional = Optional.ofNullable(req.getParameter(QUERY_PARAM_TIMEZONE));
//        ZoneId zoneId = ZoneId.of(timezoneOptional.orElse(DEFAULT_TIME_ZONE));
//        ZonedDateTime now = ZonedDateTime.now(zoneId);
//
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding("utf-8");
//        resp.getWriter().write(now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
//        resp.getWriter().close();
        resp.setContentType("text/html");

        Map<String, String[]> parameterMap = req.getParameterMap();

        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
            params.put(keyValue.getKey(), keyValue.getValue()[0]);
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("queryParams", params)
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
