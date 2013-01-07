package com.xcj.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * 文件操作助手
 * @author EwinLive
 *
 */
public class FileTools {
	public FileTools() {
		super();
	}
	
	/**
	 * 创建SD卡文件
	 * @param path 以separator(如："/")开头，系统会自动查询SD卡的盘符。
	 * 			        例如："/test",则通常可在系统中创建"/sdcard/test"真实目录。
	 * @param fileName 文件名
	 * @return 如果文件已存在，则返回该文件；否则将返回新建的文件。
	 * @throws IOException
	 */
	public static File createSDFile(String path, String fileName) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + path + File.separator + fileName);
		if(!file.exists()){
			createSDFolder(path);
			file.createNewFile();
		}
			
		return file;
	}
	
	/**
	 * 创建SD卡目录
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public static File createSDFolder(String path) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File dir = new File(sdPath + path);
		dir.mkdirs();
		return dir;
	}
	
	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean isFileExit(String fileName) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + fileName);
		return file.exists();
	}
	
	/**
	 * 判断文件是否存在
	 * @param path
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static boolean isFileExit(String path, String fileName) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + path + File.separator + fileName);
		return file.exists();
	}
	
	/**
	 * 根据完整文件名获取文件
	 * @param fileName
	 * @return 如果文件不存在返回null
	 * @throws IOException
	 */
	public static File getExitFile(String fileName) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + fileName);
		if(file.exists())
			return file;
		else
			return null;
	}
	
	/**
	 * 根据目录和文件名获取文件
	 * @param path
	 * @param fileName
	 * @return 若文件不存在，则返回null
	 * @throws IOException
	 */
	public static File getExitFile(String path, String fileName) throws IOException{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + path + File.separator + fileName);
		if(file.exists())
			return file;
		else
			return null;
	}
	
	/**
	 * 删除文件 
	 * @param path
	 * @param fileName
	 * @return 若文件不存在，返回false
	 */
	public static boolean deleteFile(String path, String fileName){
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + path + File.separator + fileName);
		if(file.exists()){
			file.delete();
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 删除文件 
	 * @param fileName
	 * @return 若文件不存在，返回false
	 */
	public static boolean deleteFile(String fileName){
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(sdPath + fileName);
		if(file.exists()){
			file.delete();
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 删除指定路径的文件夹
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteFolder(String path) throws Exception{
		String sdPath = Environment.getExternalStorageDirectory().toString();
		File folder = new File(sdPath + path);
		if (folder.exists()) {
			if (!folder.isDirectory())
				return false;// 非有效文件夹路径，则返回。
			clearFolder(path); // 清空文件夹
			folder.delete(); // 删除空文件夹
			return true;
		} else {
			return false;// 文件夹不存在，则返回。
		}
	}
	
	/**
	 * 清空指定路径的文件夹
	 * @since 1.0
	 * @param path 文件夹路径
	 * @return
	 * @throws Exception
	 */
	public static boolean clearFolder(String path) throws Exception {
		String sdPath = Environment.getExternalStorageDirectory().toString();
		path = sdPath + path;
		if (!path.endsWith(File.separator))
			path = path + File.separator;
		File folder = new File(path);
		if (folder.exists()) {
			if (!folder.isDirectory())
				return false;// 非有效文件夹路径，则返回。
			String[] list = folder.list();
			int length = list.length;
			File tmp = null;
			for (int i = 0; i < length; i++) {
				tmp = new File(path + list[i]);
				if (tmp.isFile())
					tmp.delete();
				if (tmp.isDirectory()) {
					clearFolder(path + list[i]);
					tmp.delete();
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 复制文件夹
	 * @since 1.0
	 * @param oldPath 源地址
	 * @param newPath 目标地址
	 * @return
	 * @throws Exception
	 */
	public static boolean copyFolder(String oldPath, String newPath)
			throws Exception {
		if (!oldPath.endsWith(File.separator))
			oldPath = oldPath + File.separator;
		if (!newPath.endsWith(File.separator))
			newPath = newPath + File.separator;

		File oldFolder = new File(oldPath);
		if (oldFolder.exists()) {
			if (!oldFolder.isDirectory())
				return false;// 非有效文件夹路径，则返回。
			(new File(newPath)).mkdirs(); // 如果新文件夹不存在 则创建该文件夹

			String[] list = oldFolder.list();
			int length = list.length;
			File tmp = null;
			for (int i = 0; i < length; i++) {
				tmp = new File(oldPath + list[i]);
				if (tmp.isFile()) {
					FileInputStream input = new FileInputStream(tmp);
					FileOutputStream output = new FileOutputStream(newPath
							+ (tmp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				} else if (tmp.isDirectory()) {
					copyFolder(oldPath + list[i], newPath + list[i]);
				}
			}
			return true;
		} else {
			return false;// 旧文件夹不存在，则返回。
		}
	}

	/**
	 * 重命名文件或文件夹
	 * @since 1.0
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean reNameFile(String oldName, String newName) {
		File file = new File(oldName);
		if (file.exists()) {
			file.renameTo(new File(newName));
			return true;
		}else
			return false;

	}
	
	/**
	 * 将字节数组写入到SD卡文件，如果文件不存在将自动创建该文件。
	 * @param path
	 * @param fileName
	 * @param bytes
	 * @return 操作失败则返回null
	 */
	public static File writeToSD(String path, String fileName, byte[] bytes){
		String sdPath = Environment.getExternalStorageDirectory().toString();
		OutputStream os = null;
		File file = new File(sdPath + path + File.separator + fileName);
		try {
			if(!file.exists())
				createSDFile(path, fileName);
				
			os = new FileOutputStream(file);
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if(os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * 把输入流的文件写入SD卡
	 * @param path
	 * @param fileName
	 * @param is
	 * @return 操作失败则返回null
	 */
	public static File writeToSD(String path, String fileName, InputStream is){
		String sdPath = Environment.getExternalStorageDirectory().toString();
		OutputStream os = null;
		File file = new File(sdPath + path + File.separator + fileName);
		try {
			if(!file.exists())
				createSDFile(path, fileName);
			
			os = new FileOutputStream(file);
			byte[] buffer = new byte[4*1024]; 
			
			while(is.read(buffer) != -1){
				os.write(buffer);
			}
			
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(os != null)
					os.close();
				if(is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
