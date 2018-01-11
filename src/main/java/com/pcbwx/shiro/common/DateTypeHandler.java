package com.pcbwx.shiro.common;

/** 
 *  
 */  
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
  
/** 
 * @author 王海龙
 * java中的boolean和jdbc中的char之间转换;true-Y;false-N 
 */  
public class DateTypeHandler implements TypeHandler<Date> {  
  
    @Override  
    public Date getResult(ResultSet rs, String columnName) throws SQLException { 
    	String str = rs.getString(columnName); 
        return Date.valueOf(str);
    }  
  
    @Override 
    public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
    	String str = rs.getString(columnIndex);
         return Date.valueOf(str);
    }
  
    @Override  
    public Date getResult(CallableStatement cs, int columnIndex)  
            throws SQLException {  
    	String str = cs.getString(columnIndex);  
        return Date.valueOf(str);
    }  
    
    @Override  
    public void setParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());  
    }  
}  
