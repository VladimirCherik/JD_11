package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@WebServlet("/time")
public class ThymeleafTestController extends HttpServlet {
    public static final int DATA_FORMAT_LENGTH = 19;
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getServletContext().getRealPath("/WEB-INF/templates/"));
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        ThymeleafTestController timeServlet = new ThymeleafTestController();


        String timezone = req.getParameter("timezone");
        if(timezone != null) {
            timezone = timezone.replaceAll("\\s", "+");
            resp.addCookie(new Cookie("lastTimezone", timezone));
        }else if(req.getCookies() != null){
            Cookie[] cookies = req.getCookies();
            timezone = cookies[cookies.length-1].getValue();
        }else{
            timezone = "UTC+0";
        }

        String dataForContext = timeServlet.setTime(timezone);

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", dataForContext)
        );
        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private String setTime (String timeZone){
        int timeZoneInt = Integer.parseInt(timeZone.substring(3).trim());
        timeZone = timeZoneInt == 0 ? "UTC" : timeZone;
        String time = LocalDateTime.now(ZoneOffset.ofHours(timeZoneInt)).toString();
        return time.replace("T", " ").substring(0, DATA_FORMAT_LENGTH) + " " + timeZone;
    }
}
