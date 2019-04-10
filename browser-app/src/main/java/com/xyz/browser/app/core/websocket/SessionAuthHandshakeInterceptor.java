package com.xyz.browser.app.core.websocket;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@Component
public class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {

//    @Autowired
//    private UserTokenService userTokenService;
//    @Autowired
//    private UserService userService;
	//private ClientuserService clientuserService = (ClientuserService) SpringContextUtil.getBean("clientuserService");
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest= serverHttpRequest.getServletRequest();
//            HttpSession session = servletRequest.getSession();
//			System.out.println("session id:"+session.getId());
////            Map parameterMap = serverHttpRequest.getServletRequest().getParameterMap();
////            System.out.println(parameterMap);
//            if (session != null) {
//                map.put("userId", session.getAttribute("userId"));
//            }
            try{
                //Shiro里已做检验，此处直接获取用户信息
                //取得参数
                String accessToken=servletRequest.getParameter("token");
                //String ID = request.getURI().toString().split("ID=")[1];
//                UserToken ut = new UserToken();
//                ut.setToken(accessToken);
//                //根据accessToken，查询用户信息
//                UserToken tokenEntity = userTokenService.selectOne(ut);//.queryByToken(accessToken);
                if(StringUtils.isNotBlank(accessToken))
                    map.put("user",accessToken);
                else
                    map.put("user", UUID.randomUUID().toString());
            }catch(Exception e){
                return false;
            }


        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}