package com.hongyuan.talk;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import nl.justobjects.pushlet.core.Dispatcher;
import nl.justobjects.pushlet.core.Event;

import com.hongyuan.core.WebServlet;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.PageFans;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.PageFansBean;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.javabeans.weibo.Company;
import com.qq.connect.oauth.Oauth;

public class TalkServlet extends WebServlet {

	public TalkBean talkBean;
			
	@Override
	public void initPage() {
		Object userInfo = request.getSession().getAttribute("userInfo");
		if(userInfo!=null){
			talkPage();
		}else{
			try {
	            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

	            String accessToken   = null,
	                   openID        = null;
	            long tokenExpireIn = 0L;

	            if (accessTokenObj.getAccessToken().equals("")) {
	                System.out.print("没有获取到响应参数");
	                loginPage();
	            } else {
	            	
	                accessToken = accessTokenObj.getAccessToken();
	                tokenExpireIn = accessTokenObj.getExpireIn();

	                request.getSession().setAttribute("demo_access_token", accessToken);
	                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

	                // 利用获取到的accessToken 去获取当前用的openid -------- start
	                OpenID openIDObj =  new OpenID(accessToken);
	                openID = openIDObj.getUserOpenID();

	                System.out.println("欢迎你，代号为 ______" + openID + "______ 的用户!");
	                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
	                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
	                if (userInfoBean.getRet() == 0) {
	                    System.out.println(userInfoBean.getNickname() + "<br/>");
	                    System.out.println(userInfoBean.getGender() + "<br/>");
	                    System.out.println("黄钻等级： " + userInfoBean.getLevel() + "<br/>");
	                    System.out.println("会员 : " + userInfoBean.isVip() + "<br/>");
	                    System.out.println("黄钻会员： " + userInfoBean.isYellowYearVip() + "<br/>");
	                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
	                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
	                    System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
	                } else {
	                	System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
	                }
	                
	                Map<String,Object> userInfo2=talkBean.getThirdUserInfo(openID);
	                if(userInfo2 == null){
	                	String userName = userInfoBean.getNickname();
	                	String password = "123";
	                	if(talkBean.saveUser(userName, password)){
	                		Map<String,Object> user = talkBean.getUserInfo(userName, password);
	                		if(talkBean.saveThirdUser(Integer.parseInt(user.get("id").toString()), openID, "qq")){
	                			userInfo2 = talkBean.getThirdUserInfo(openID);
	                		}
	                	}
	                }
	    			if(userInfo2!=null){
	    				//将用户信息存入session
	    				request.getSession().setAttribute("userInfo",userInfo2);
	    			}
	                talkPage();
	            }
	        } catch (QQConnectException e) {
	        	
	        }
		}
	}
	
	//进入登陆页面
	public void loginPage(){
		show("login.jsp");
	}
	
	//进入注册页面
	public void regPage(){
		show("reg.jsp");
	}
	
	//登录
	public void login() throws IOException{
		String userName=this.get("userName","");
		String password=this.get("password","");
		if(!"".equals(userName)&&!"".equals(password)){
			//提取用户信息
			Map<String,Object> userInfo=talkBean.getUserInfo(userName, password);
			if(userInfo!=null){
				//将用户信息存入session
				request.getSession().setAttribute("userInfo",userInfo);
				response.sendRedirect("./talkService.srv?action=talkPage");
				return;
			}
		}
		show("login.jsp");
	}
	
	//注册
	public void reg() throws IOException{
		String userName=this.get("userName","");
		String password=this.get("password","");
		String passConfirm=this.get("passConfirm","");
		if(!"".equals(userName)&&!"".equals(password)&&password.equals(passConfirm)){
			if(talkBean.saveUser(userName, password)){
				response.sendRedirect("./talkService.srv?action=loginPage");
				return;
			}
		}
		show("reg.jsp");
	}
	
	//进入聊天页面
	public void talkPage(){
		Object userInfo = request.getSession().getAttribute("userInfo");
		if(userInfo!=null){
			Map<String,Object> info=(Map<String,Object>)userInfo;
			this.put("userName",info.get("user_name"));
			show("talk.jsp");
			return;
		}
		show("login.jsp");
	}
	
	//发送消息
	public void sendMsg() throws UnsupportedEncodingException{
		String msg=this.get("message","");
		System.err.println(((Map<String, Object>) request.getSession().getAttribute("userInfo")).get("user_name")+":"+msg);
		if(!"".equals(msg)){
			Event event=Event.createDataEvent("/message/world");
			
			Object userInfo = request.getSession().getAttribute("userInfo");
			if(userInfo!=null){
				Map<String,Object> info=(Map<String,Object>)userInfo;
				event.setField("userName",new String(info.get("user_name").toString().getBytes("utf-8"),"iso-8859-1"));
			}
			event.setField("message",new String(msg.getBytes("utf-8"),"iso-8859-1"));
			
			Dispatcher.getInstance().multicast(event);
		}
	}
}
