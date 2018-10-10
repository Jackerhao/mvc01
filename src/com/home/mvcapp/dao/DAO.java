package com.home.mvcapp.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;

import com.home.mvcapp.db.JdbcUtils;

/**
 * ��װ�˻�����CRUD�ķ���,�Թ�����̳�ʹ��
 * ��ǰDAOֱ���ڷ����л�ȡ���ݿ�����.
 * ����DAO��ȡDBUtils�������
 * @author Administrator
 *
 * @param <T>:��ǰDAO�����ʵ�����������ʲô
 */
public class DAO <T>{
	
	private QueryRunner queryRunner =new QueryRunner();
	
     private Class<T> clazz;
     
     public DAO() {
    	 Type superClass = getClass().getGenericSuperclass();
    	 if(superClass instanceof ParameterizedType) {
    		ParameterizedType parameterizedType=(ParameterizedType) superClass;
    		
    		Type [] typeArgs=parameterizedType.getActualTypeArguments();
    		if(typeArgs != null && typeArgs.length>0) {
    		if(typeArgs[0] instanceof Class) {
    			clazz = (Class<T>)typeArgs[0];
    		}
    	 }
    	 }
     }
     
     
     /**
      * ����ĳһ���ֶε�ֵ.���緵��ĳһ����¼��customerName,�򷵻����ݱ����ж�������¼��
      * @param sql
      * @param args
      * @return
      */
     public <E> E getForValue(String sql,Object ...args) {
    	 Connection connection = null;
 		try {
 			connection=JdbcUtils.getConnection();
 			return (E) queryRunner.query(connection, sql, new ScalarHandler(),args);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}finally {
 			JdbcUtils.releaseConnection(connection);
 		}
    	 return null;
     }
     
     /**
      * ����T����Ӧ��List
      * @param sql
      * @param args
      * @return
      */
     public List<T> getForList(String sql,Object ...args){
    	 Connection connection = null;
 		try {
 			connection=JdbcUtils.getConnection();
 			return queryRunner.query(connection, sql, new BeanListHandler<>
 			(clazz),args);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}finally {
 			JdbcUtils.releaseConnection(connection);
 		}
    	 return null;
     }
	
	/**
	 * ���ض�Ӧ��T��һ��ʵ�����������ʲô
	 * @param sql
	 * @param args
	 * @return
	 */
	public T get(String sql,Object ...args) {

		Connection connection = null;
		try {
			connection=JdbcUtils.getConnection();
			return queryRunner.query(connection, sql,new BeanHandler<>
			(clazz),args);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtils.releaseConnection(connection);
		}
		return null;
	}
	/**
	 * �÷�����װ��INSERT DELETE UPDATE����.
	 * @param sql:SQL���
	 * @param args:���SQL����ռλ��.
	 */
	public void update(String sql,Object ...args) {
		Connection connection = null;
		
		try {
			connection=JdbcUtils.getConnection();
			queryRunner.update(connection,sql,args);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtils.releaseConnection(connection);
		}
	}
	public void save(String sql,Object ...args) {
		
	}
	
}
