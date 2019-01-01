package com.order.order.interceptor;

import com.order.order.util.IpUtil;
import com.order.order.util.ZkBasedSnowFlakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lujiafeng on 2018/10/12.
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    @Autowired
    private ZkBasedSnowFlakeIdGenerator zkBasedSnowFlakeIdGenerator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String userId = request.getParameter("userId");
        String money = request.getParameter("money");
        String isFlag = request.getParameter("trxId");

        if (isFlag == null) {
            long trxId = zkBasedSnowFlakeIdGenerator.getCurrentTrxId("");
            String redirectUrl = request.getContextPath() + uri + "?userId=" + userId
                    + "&money=" + money + "&trxId=" + trxId;
            response.sendRedirect(redirectUrl);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
