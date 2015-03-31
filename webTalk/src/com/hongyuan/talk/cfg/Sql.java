package com.hongyuan.talk.cfg;

public enum Sql {
	//提取用户信息SQL语句
	GET_USERINFO("select id,user_name,password from user where user_name=:userName and password=md5(:password)"),
	
	//保存用户信息SQL语句
	SAVE_USER("insert into user(user_name,password) values(:userName,md5(:password))"),
	
	//提取第三方用户信息SQL语句
	GET_THIRD_USERINFO("select u.id,u.user_name from user u inner join third_user t on u.id=t.user_id where t.open_id=:openId"),
	
	//保存第三方登录SQL语句
	SAVE_THIRD_USER("insert into third_user(user_id,type,open_id) values(:userId,:type,:openId)");
	
	private final String value;
	private Sql(String value){
		this.value=value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
