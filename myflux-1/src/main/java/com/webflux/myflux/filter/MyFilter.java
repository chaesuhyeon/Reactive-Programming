package com.webflux.myflux.filter;

import com.webflux.myflux.notify.EventNotify;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter implements Filter {

    private EventNotify eventNotify;

    public MyFilter(EventNotify eventNotify) {
        this.eventNotify = eventNotify;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("필터 실행됨");

        HttpServletResponse servletResponse = (HttpServletResponse) response;
        servletResponse.setContentType("text/event-stream; charset=utf-8");

        PrintWriter out = servletResponse.getWriter();
        for (int i = 0; i < 5; i++) {
            out.println("응답: "+i);
            out.flush(); // 버퍼를 비움
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (true) {
            try {
                if (eventNotify.getChange()) {
                    int lastIndex = eventNotify.getEvents().size() -1;
                    out.println("응답: "+eventNotify.getEvents().get(lastIndex));
                    out.flush(); // 버퍼를 비움
                    eventNotify.setChange(false);
                }

                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
