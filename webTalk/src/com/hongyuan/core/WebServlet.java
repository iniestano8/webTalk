package com.hongyuan.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class WebServlet extends HttpServlet {

	protected HttpServletRequest request=null;
	protected HttpServletResponse response=null;
	protected Map<String,String> cfgParams=new HashMap<String,String>();
	
	/**
	 * 默认访问方法
	 * @throws Exception
	 */
	public void initPage() throws Exception{}
	
	@Override
	public final void init(ServletConfig config) throws ServletException {
		Enumeration<String> names = config.getInitParameterNames();
		while(names.hasMoreElements()){
			String name=names.nextElement();
			if(name.startsWith("Bean_")){
				//为servlet注入Bean对象
				String beanName=name.substring("Bean_".length());
				String beanClass=config.getInitParameter(name);
				
					try {
						if(beanClass==null||"".equals(beanClass.trim())) throw new Exception("未配置类名！-->"+beanName);
						
						Object bean = Class.forName(beanClass).newInstance();
						this.getClass().getField(beanName).set(this,bean);
					} catch (InstantiationException e) {
						try {
							throw new InstantiationException("无法实例化（"+beanClass+"）!");
						} catch (InstantiationException e1) {
							e1.printStackTrace();
						}
					} catch (ClassNotFoundException e) {
						try {
							throw new ClassNotFoundException("未找到类-->"+beanClass);
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					} catch (NoSuchFieldException e) {
						try {
							throw new NoSuchFieldException("未找到Bean声明字段（"+beanName+"）");
						} catch (NoSuchFieldException e1) {
							e1.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				
			}else{
				cfgParams.put(name,config.getInitParameter(name));
			}
		}
	}
	
	@Override
	public final void service(HttpServletRequest request, HttpServletResponse response){
		
		this.request=request;
		this.response=response;
		
		String encoding=null;
		try {
			encoding=cfgParams.get("encoding");
			if(encoding==null||"".equals(encoding.trim())) encoding="utf-8";
			request.setCharacterEncoding(encoding);
			response.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e2) {
			try {
				throw new UnsupportedEncodingException("不支持的字符集("+encoding+")");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		String action=null;
		try {
			//根据路由参数将请求转交到指定方法执行
			String routeParam=cfgParams.get("routeParam");
			action=this.get((routeParam==null||"".equals(routeParam))?"action":routeParam,"initPage");
			this.getClass().getMethod(action).invoke(this);
		} catch (IllegalAccessException e) {
			try {
				throw new IllegalAccessException("方法（"+action+"）拒绝访问!");
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			try {
				throw new NoSuchMethodException("未找到方法("+action+")!");
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 展示指定页面
	 * @param page
	 * @throws IOException 
	 * @throws ServletException 
	 */
	protected void show(String page){
		String pagePath=cfgParams.get("pagePath");
		try {
			request.getRequestDispatcher(((pagePath==null||"".equals(pagePath))?"/WEB-INF/pages/":pagePath)+page).forward(request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印指定字符串
	 * @param str
	 * @throws IOException
	 */
	protected void print(String str){
		try {
			response.getWriter().print(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取指定名称的请求参数
	 * @param name
	 * @param def
	 * @return
	 */
	protected String get(String name,String def){
		String value=request.getParameter(name);
		if(value!=null&&!"".equals(value.trim())){
			return value;
		}else{
			return def;
		}
	}
	
	/**
	 * 向页面输出指定参数
	 * @param name
	 * @param value
	 */
	protected void put(String name,Object value){
		request.setAttribute(name,value);
	}
	
}
