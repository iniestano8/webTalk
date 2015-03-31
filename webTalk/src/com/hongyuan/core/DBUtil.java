package com.hongyuan.core;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
 
public class DBUtil {
	
    private static DataSource dataSource = null;
    static{
    	/**
    	 * 初始化数据源，不同的数据库获取数据源的方式不同，可参考相应数据库的说明文档。
    	 */
    	MysqlDataSource mds=new MysqlDataSource();
        mds.setURL("jdbc:mysql://123.57.144.35:3306/test");
        mds.setUser("root");
        mds.setPassword("9ce9023d80");
        mds.setCharacterEncoding("utf8");
        dataSource=mds;
    }
    
    /**
     * 获取数据库连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
 
    /**
     * 关闭数据库连接资源
     * @param conn	
     * @param s
     * @param rs
     * @throws SQLException
     */
    public static void close(Connection conn, Statement s, ResultSet rs){
        try {
			if (rs != null) rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        try {
			if (s != null) s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        try {
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 执行数据库查询语句
     * @param sql		查询sql,匿名参数用？表示，命名参数使用“：参数名”表示
     * @param params	查询参数
     * @return
     * @throws SQLException
     */
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> select(Object sql,Object... params) throws SQLException{
		Object result=DBUtil.executeSql(sql,params);
		if(result==null){
			return null;
		}else{
			return (List<Map<String,Object>>)result;
		}
    }
    
	/**
	 * 执行插入
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static int insert(Object sql,Object... params) throws SQLException{
		return DBUtil.update(sql, params);
	}
	
	/**
	 * 执行数据库记录变更语句（增，删，改）
	 * @param sql		查询sql,匿名参数用？表示，命名参数使用“：参数名”表示
	 * @param params	查询参数
	 * @return
	 * @throws SQLException
	 */
    public static int update(Object sql,Object... params) throws SQLException{
    	Object result=DBUtil.executeSql(sql,params);
    	if(result==null){
    		return 0;
    	}else{
    		return (Integer)result;
    	}
    }
    
    /**
     * 执行删除
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public static int delete(Object sql,Object... params) throws SQLException{
    	return DBUtil.update(sql, params);
    }
    
    /**
   	 * 通用Sql执行方法
   	 * @param sql		查询sql,匿名参数用？表示，命名参数使用“：参数名”表示
   	 * @param params	命名参数
   	 * @return
   	 * @throws SQLException
   	 */
    public static Object executeSql(Object sql, Object... params) throws SQLException {

    	if(sql==null||"".equals(sql.toString().trim())) throw new SQLException("sql语句为空！");
    	
    	//获取sql语句
    	String sqlStr=sql.toString().trim();
    	
    	//处理命名参数
    	if(params!=null&&params.length==1&&params[0] instanceof Map){
    		List<Object> pList=new ArrayList<Object>();
    		Map<String,Object> pMap=(Map<String, Object>)params[0];
        	Matcher pMatcher = Pattern.compile(":(\\w+)").matcher(sqlStr);
        	while(pMatcher.find()){
        		String pName=pMatcher.group(1);
        		pList.add(pMap.get(pName));
        	}
        	
        	sqlStr=pMatcher.replaceAll("?");
        	params=pList.toArray();
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sqlStr);
             
            if (null != params) {
                //初始化查询参数
            	for(int i=0;i<params.length;i++){
            		Object param = params[i];
            		if(param!=null){
            			ps.setObject(i+1,param);
            		}else{
            			ps.setNull(i+1,Types.NULL);
            		}
            		
            	}
            }
            
            //处理结果集
            boolean isResultSet = ps.execute();
            List<Object> result = new ArrayList<Object>();
            do {
                if (isResultSet) {
                	List<Map<String,Object>> tableData=new ArrayList<Map<String,Object>>();
                	ResultSet resultSet=ps.getResultSet();
                	while(resultSet.next()){
                		Map<String,Object> rowData=new HashMap<String,Object>();
                		for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                			rowData.put(resultSet.getMetaData().getColumnName(i),resultSet.getObject(i));
                		}
                		tableData.add(rowData);
                	}
                    result.add(tableData);
                } else {
                    result.add(new Integer(ps.getUpdateCount()));
                }
            } while ((isResultSet = ps.getMoreResults()) == true || ps.getUpdateCount() != -1);
 
            //处理返回结果
            if (result.size() == 0) {
                return null;
            } else if (result.size() == 1) {
                return result.get(0);
            } else {
                return result;
            }
        } catch (SQLException e) {
            throw new SQLException("无效sql!-->"+sql);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }
}