package com.xcj.android.sqlite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.xcj.android.utils.BeanTools;

import android.content.Context;
import android.database.Cursor;

/**
 * 实现了EntityDao接口,其他实体DAO只要继承它即可拥有所有强大功能。
 * @author EwinLive
 *
 * @param <T>
 * @param <PK>
 */
public abstract class SimpleDao<T, PK extends Serializable> implements EntityDao<T, PK> {
	/**
	 * 实体的类型
	 */
	protected Class<T> entityClass;
	
	/**
	 * 表名
	 */
	protected String tableName;
	
	/**
	 * 数据库管理器
	 */
	protected DatabaseHelper dbHelper;
	
	/**
	 * 保存实体所要执行的SQL语句
	 * 只在创建对象时初始化。
	 */
	protected String saveSql;
	
	/**
	 * 更新实体所要执行的SQL语句
	 * 只在创建对象时初始化。
	 */
	protected String updateSql;
	
	/**
	 * 字段在数据表中所对应的列的索引
	 * 只在创建对象时初始化。
	 */
	protected int[] fieldPostion;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public String getSaveSql() {
		return saveSql;
	}

	public void setSaveSql(String saveSql) {
		this.saveSql = saveSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}
	
	/**
	 * 专属构造器
	 * 可通过子类的范型定义取得对象类型Class.
	 * @param tableName 实体对应的表名
	 * @param context 设备上下文，通常是一个Activity对象
	 */
	@SuppressWarnings("unchecked")
	public SimpleDao(String tableName, Context context) {
		this.entityClass = (Class<T>) BeanTools.getGenericClass(getClass());
		this.tableName = tableName;
		this.dbHelper = getDatabaseHelper(context);
		this.saveSql = initSaveSql();
		this.updateSql = initUpdateSql();
		this.fieldPostion = initFieldPostion();
	}

	@Override
	public void save(T entity) throws Exception {
		dbHelper.getReadableDatabase().execSQL(saveSql, getSaveValue(entity));
	}

	
	@SuppressWarnings("unused")
	@Override
	public void remove(PK... ids) {
		if(ids.length > 0){
			StringBuffer sb = new StringBuffer();
			for(PK id : ids){
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			dbHelper.getReadableDatabase().execSQL("delete from "+ tableName +" where _id in(" + sb + ")", (Object[]) ids);
		}
	}

	@Override
	public void upDate(T entity) throws Exception {
		dbHelper.getReadableDatabase().execSQL(updateSql, getUpdateValue(entity));
	}

	@Override
	public T find(PK id) {
		Cursor cursor = dbHelper.getReadableDatabase()
									.rawQuery("select * from " + tableName + " where _id=?", new String[]{String.valueOf(id)});
		cursor.moveToNext();
		return getEntityFromCursor(cursor);
	}

	@Override
	public List<T> getScroolData(Integer startResult, Integer maxResult){
		List<T> list = new ArrayList<T>(0);
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + tableName + " limit ?, ?", 
				new String[]{String.valueOf(startResult), String.valueOf(maxResult)});
		while(cursor.moveToNext()){
			list.add(getEntityFromCursor(cursor));
		}
		return list;
	}

	@Override
	public Long getCount() {
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select count(*) from " + tableName, 
				null);
		if(cursor.moveToNext())
			return cursor.getLong(0);
		return 0l;
	}
	
	/**
	 * 初始化保存实体所需的SQL语句
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String initSaveSql(){	
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		StringBuffer bufferName = new StringBuffer();
		StringBuffer bufferExpr = new StringBuffer();
		
		for(String tmp : fieldName){
			bufferName.append(tmp).append(',');
			bufferExpr.append("?,");
		}
	
		//去除id字段及其属性值
		bufferName.delete(bufferName.indexOf("_id"), bufferName.indexOf("_id")+4);
		bufferExpr.delete(0, 2);

		//去除多余的分隔符
		bufferName.deleteCharAt(bufferName.length()-1);
		bufferExpr.deleteCharAt(bufferExpr.length()-1);
		
		String sql = "insert into "
			+ tableName
			+ "(" + bufferName.toString() + ") values(" + bufferExpr.toString() + ")";
		
		return sql;
	}
	
	/**
	 * 初始化更新实体所需的SQL语句
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String initUpdateSql(){	
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update "+ tableName + " set ");
		for(String tmp : fieldName){
			sqlBuffer.append(tmp).append("=?, ");
		}
	
		//去除id字段及其属性值
		sqlBuffer.delete(sqlBuffer.indexOf(" _id=?"), sqlBuffer.indexOf("_id") + 5);
		sqlBuffer.deleteCharAt(sqlBuffer.length()-2);
		sqlBuffer.append("where _id =?");
		
		return sqlBuffer.toString();
	}
	
	/**
	 * 获取保存实体所需的值
	 * @param entity
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("rawtypes")
	protected Object[] getSaveValue(T entity) throws IllegalAccessException, NoSuchFieldException{
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Object[] values;
		
		int length = fieldName.length;
		values = new Object[length-1];
		int j=0;
		for(int i=0; i<length; i++){
			if("_id".equals(fieldName[i].toString())){
				continue;//跳过ID字段
			}
			values[j++] = BeanTools.getPrivateProperty(entity, fieldName[i]);
		}
		return values;
	}
	
	/**
	 * 获取更新实体所需的值
	 * @param entity
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("rawtypes")
	protected Object[] getUpdateValue(T entity) throws Exception{
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Object[] values;
		
		int length = fieldName.length;
		values = new Object[length-1];
		int j=0;
		int id=0;

		for(int i=0; i<length; i++){
			if("_id".equals(fieldName[i].toString())){
				id = (Integer) BeanTools.getPrivateProperty(entity, fieldName[i]);
				continue;//跳过ID字段
			}
			values[j++] = BeanTools.getPrivateProperty(entity, fieldName[i]);
		}
		
		
		Object[] values2 = new Object[length];
		System.arraycopy(values, 0, values2, 0, values.length);
		values2[length-1] = id;
		
		return values2;
	}
	
	/**
	 * 初始化字段在数据表中 对应的索引
	 * @param cursor
	 */
	@SuppressWarnings("rawtypes")
	protected int[] initFieldPostion(){
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		int length = fieldName.length;
		int[] postion = new int[length];
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + tableName + " limit ?, ?", new String[]{"0", "2"});
		for(int i =0; i<length; i++){
			postion[i] = cursor.getColumnIndex(fieldName[i]);
		}
		
		return postion;
	}
	
	/**
	 * 从游标中获取实体
	 * @param cursor 游标
	 * @return T 实体对象
	 */
	@SuppressWarnings("rawtypes")
	public T getEntityFromCursor(Cursor cursor){
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Class<?>[] fieldType = (Class<?>[]) data.get("fieldType");
		int length = fieldName.length;
		
		T entity = null;
		String db_data;
		String fieldTypeName;
		try {
			entity = entityClass.newInstance();
			for(int i=0;i<length;i++){
				fieldTypeName = fieldType[i].getSimpleName();
				db_data = cursor.getString(fieldPostion[i]);
				if(null != db_data){
					if("String".equals(fieldTypeName)){
						BeanTools.setFieldValue(entity, fieldName[i], db_data);
					}else if("int".equals(fieldTypeName)){
						BeanTools.setFieldValue(entity, fieldName[i], Integer.parseInt(db_data));
					}else if("long".equals(fieldTypeName)){
						BeanTools.setFieldValue(entity, fieldName[i], Long.getLong(db_data));
					}
					else if("float".equals(fieldTypeName)){
						BeanTools.setFieldValue(entity, fieldName[i],Float.parseFloat(db_data));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return entity;
	}
	
	protected abstract DatabaseHelper getDatabaseHelper(Context context);
}
