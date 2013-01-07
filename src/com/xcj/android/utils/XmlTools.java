package com.xcj.android.utils;

import java.io.File;
import java.io.InputStream;
import java.util.List;


/**
 * XML 文件处理接口
 * @author EwinLive
 *
 */
public abstract class XmlTools<T> {
	/**
	 * 创建一个XML文件
	 * 应该在最终创建文件前校验文件的合法性。
	 * @return
	 */
	public File ceateXml(String path, String fileName) throws Exception{
		return FileTools.createSDFile(path, fileName);
	};
	
	/**
	 * 解析XML 文件
	 * 应该在最终解析文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @return
	 */
	public abstract List<T> paseXml(String path, String fileName) throws Exception;
	
	/**
	 * 保存数据
	 * 应该在最终保存文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @param date
	 * @return
	 */
	public abstract boolean saveXml(String path, String fileName, List<T> date) throws Exception;
	
	/**
	 * 使用缓冲写入器保存数据
	 * 应该在最终保存文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public abstract boolean saveXmlBuffer(String path, String fileName, List<T> date) throws Exception;
	
	/**
	 * 保存数据
	 * 应该在最终保存文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @param is
	 * @return
	 */
	public File saveXml(String path, String fileName, InputStream is) throws Exception{
		return FileTools.writeToSD(path, fileName, is);
	};
	
	/**
	 * 保存数据
	 * 应该在最终保存文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @param bytes
	 * @return
	 */
	public File saveXml(String path, String fileName, byte[] bytes) throws Exception{
		return FileTools.writeToSD(path, fileName, bytes);
	};
	
	/**
	 * 删除XML 文件
	 * 应该在最终删除文件前校验文件的合法性。
	 * @param path
	 * @param fileName
	 * @return
	 */
	public boolean deleteXml(String path, String fileName) throws Exception{
		return FileTools.deleteFile(path, fileName);
	};
}
