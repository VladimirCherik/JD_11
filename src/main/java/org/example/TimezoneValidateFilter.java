package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter (value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String timeZone = req.getParameter("timezone");

        if(timeZone != null){

            try {
                int timeZoneInt = Integer.parseInt(timeZone.substring(3).trim());

                if (timeZone.startsWith("UTC") && Math.abs(timeZoneInt) <= 12 ){
                    chain.doFilter(req, res);
                } else {
                    throw new RuntimeException();
                }
            }catch (Exception e){
                res.setStatus(400);
                res.getWriter().write("Invalid timezone, HTTP code 400");
                res.getWriter().close();
            }
        }
        chain.doFilter(req, res);
    }
}
