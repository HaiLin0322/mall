package com.hailin.mall.filter;

import com.hailin.mall.common.Constant;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

//管理员校验过滤器
public class AdminFilter implements Filter {
    @Autowired
    UserService userService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User currentUser=(User) session.getAttribute(Constant.HAILIN_MALL_USER);
        if(currentUser==null){
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"+"    \"status\":10007,\n"+"    \"msg\":\"NEED_LOGIN\",\n"+"    \"data\":null\n"+"}");
            out.flush();
            out.close();
            return;
        }
        //校验是否为管理员，是则执行操作
        if (userService.checkAdminRole(currentUser)){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"+"    \"status\":10009,\n"+"    \"msg\":\"NEED_ADMIN\",\n"+"    \"data\":null\n"+"}");
            out.flush();
            out.close();
        }
    }

    @Override
    public void destroy() {
    }
}
