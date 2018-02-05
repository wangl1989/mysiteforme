package com.mysiteforme.admin.util;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author:tnt
 * @Description:${TODO}
 * @Date: Create in 9:50 2018/1/10.
 */
public class ZipUtil {

    //推荐创建不可变静态类成员变量
    private static final Log log = LogFactory.get();

    /**
     * 文件压缩方法
     * @param sourceFileName
     * @param zipFileName
     */
    public static void zipFloder(String sourceFileName, String zipFileName) throws IOException {
        ZipOutputStream out = new ZipOutputStream( new FileOutputStream(zipFileName));
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
            if(flist.length==0){//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                out.putNextEntry(  new ZipEntry(base+"/") );
            }else{
            //如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for(int i=0;i<flist.length;i++){
                    compress(out,bos,flist[i],(StringUtils.isBlank(base))?flist[i].getName():base+"/"+flist[i].getName());
                }
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
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
