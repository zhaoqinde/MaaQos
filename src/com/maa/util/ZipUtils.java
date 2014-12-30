package com.maa.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.protocol.HTTP;

public class ZipUtils {

	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte 
	 
    /**
     * 批量压缩文件
     * 
     * @param resFileList
     *            要压缩的文件列表
     * @param zipFile
     *            生成的压缩文件
     * @throws IOException
     *             当压缩过程出错时抛出
     */ 
    public static byte[] zipFiles(String resFilePath) throws IOException { 
 
    	File[]resFileList = new File(resFilePath).listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				return true;
			}
    		
    	});
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipout = null; 
        try { 
            zipout = new ZipOutputStream(baos); 
            for (File resFile : resFileList) { 
                zipFile(resFile, zipout, ""); 
            } 
           
        } finally { 
            if (zipout != null) 
                zipout.close(); 
        } 
        return baos.toByteArray();
    } 
   
    /**
     * 压缩文件
     * 
     * @param resFile
     *            需要压缩的文件
     * @param zipout
     *            压缩的目的文件
     * @param rootpath
     *            压缩的文件路径
     * @throws FileNotFoundException
     *             找不到文件时抛出
     * @throws IOException
     *             当压缩过程出错时抛出
     */ 
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException, IOException { 
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName(); 
        rootpath = new String(rootpath.getBytes("8859_1"), HTTP.UTF_8); 
        BufferedInputStream in = null; 
        try { 
            if (resFile.isDirectory()) { 
                File[] fileList = resFile.listFiles(); 
                for (File file : fileList) { 
                    zipFile(file, zipout, rootpath); 
                } 
            } else { 
                byte buffer[] = new byte[BUFF_SIZE]; 
                in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE); 
                zipout.putNextEntry(new ZipEntry(rootpath)); 
                int realLength; 
                while ((realLength = in.read(buffer)) != -1) { 
                    zipout.write(buffer, 0, realLength); 
                } 
                in.close(); 
                zipout.flush(); 
                zipout.closeEntry(); 
            } 
        } finally { 
            if (in != null) 
                in.close(); 
            // if (zipout != null); 
            // zipout.close(); 
        } 
    } 
}
