/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:41:52
 * @ Description: 文件压缩工具类
 */

package com.mysiteforme.admin.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class ZipUtil {

    /**
     * 文件压缩方法
     */
    public static void zipFloder(String sourceFileName, String zipFileName) throws IOException {
        ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFileName)));
        BufferedOutputStream bos = new BufferedOutputStream(out);
        File sourceFile = new File(sourceFileName);
        //调用函数
        compress(out,bos,sourceFile,"");
        bos.close();
        out.close();
    }

    public static void compress(ZipOutputStream out,BufferedOutputStream bos,File sourceFile,String base) throws IOException {
        //如果路径为目录（文件夹）
        if(sourceFile.isDirectory()){
            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();
            //如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            if (flist != null && flist.length == 0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                out.putNextEntry(new ZipEntry(base + "/"));
            }
        }else{//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            out.putNextEntry( new ZipEntry(base) );
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int tag;
            //将源文件写入到zip文件中
            byte[] buffer = new byte[1024];
            while ((tag = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, tag);
            }
            bis.close();
            fos.close();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
