package com.tl.common.ext.utils;

import java.io.*;
import java.util.Calendar;

public class FileUtils {

    /**
     * 检查文件是否存在，存在返回true
     *
     * @param destFileName
     * @return
     */
    public static boolean checkFileIsExists(String destFileName) {
        File file = new File(destFileName);
        return file.exists();
    }

    /**
     * 复制文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > -1) {
                output.write(buf, 0, bytesRead);
            }
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 把输入流保存到指定文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void saveFile(InputStream source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = source;
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > -1) {
                output.write(buf, 0, bytesRead);
            }
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建文件
     */
    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建目录
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator))
            destDirName = destDirName + File.separator;
        return dir.mkdirs();
    }

    /**
     * 根据日期创建目录
     */
    public static String getDirByDate(String parentDir) {
        //生成日期文件夹目录
        Calendar instance = Calendar.getInstance();
        String year = instance.get(Calendar.YEAR) + "";
        String month = instance.get(Calendar.MONTH) + 1 + "";
        String day = instance.get(Calendar.DAY_OF_MONTH) + "";

        String path = year + File.separator + month + File.separator + day + File.separator;
        String finalPath = contactPath(parentDir, path);
        createDir(finalPath);
        return finalPath;
    }

    public static String getDateUrl(){
        Calendar instance = Calendar.getInstance();
        String year = instance.get(Calendar.YEAR) + "";
        String month = instance.get(Calendar.MONTH) + 1 + "";
        String day = instance.get(Calendar.DAY_OF_MONTH) + "";

        return year + "/" + month + "/" + day + "/";
    }


    public static String contactPath(String parentDir, String childDir) {
        String separator = File.separator;
        if (!parentDir.endsWith(separator)) {
            parentDir = parentDir + separator;
        }
        return parentDir + childDir;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     */
    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            return flag;
        } else {
            if (file.isFile()) {
                return deleteFile(sPath);
            } else {
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     */
    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

}
