package com.jonbore.web.client;

import com.google.common.collect.Lists;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * 文件操作帮助类
 */
public class FileUtil {

    /**
     * 创建文件
     *
     * @param pathStr  路径
     * @param fileName 文件名字
     * @return File实例
     */
    public static File createFile(String pathStr, String fileName) throws Exception {
        File path = new File(pathStr);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(pathStr + "/" + fileName);
        if (file.exists()) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                file = new File(pathStr + "/" + fileName.substring(0, dotIndex) + System.currentTimeMillis()
                        + fileName.substring(dotIndex, fileName.length()));
            } else {
                file = new File(pathStr + "/" + fileName + System.currentTimeMillis());
            }
        }
        return file;
    }

    /**
     * 创建目录
     * 支持创建多级目录
     *
     * @param folderPath
     */
    public static void newFolder(String folderPath) throws Exception {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 删除文件夹
     *
     * @return boolean
     */
    public static void delFolder(String folderPath) throws Exception {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹

        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) throws Exception {
        boolean flag = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                flag = true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            throw e;
        }
        return flag;

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static boolean copyFolder(String oldPath, String newPath) throws Exception {
        boolean flag = false;
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
            flag = true;
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
            throw e;
        }
        return flag;
    }


    /**
     * 复制整个文件夹内容
     *
     * @param oldPath   String 原文件路径 如：c:/fqf
     * @param newPath   String 复制后路径 如：f:/fqf/ff
     * @param filterDir String    过滤目录名称
     * @return boolean
     */
    public static boolean copyFolder(String oldPath, String newPath, String filterDir) throws Exception {
        boolean flag = false;
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    if (!file[i].equals(filterDir)) {
                        copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i], filterDir);
                    }
                }
            }
            flag = true;
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
            throw e;
        }
        return flag;
    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static boolean moveFile(String oldPath, String newPath) throws Exception {
        boolean flag = copyFile(oldPath, newPath);
        if (flag) {
            delFile(oldPath);
        }
        return flag;

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFolder(String oldPath, String newPath) throws Exception {
        copyFolder(oldPath, newPath, null);
        delFolder(oldPath);

    }


    public static void delFile(File file) throws Exception {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (File f : files) {
                    delFile(f);
                }
                file.delete();
            }
        }
    }

    public static void delFile(String path) throws Exception {
        delFile(new File(path));
    }

    /**
     * copy 文件
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }


    /**
     * @return
     * @throws Exception
     */
    public static File getFile(String fileName, String propertyName) throws Exception {
        String filePath = null;
        if (propertyName != null && !"".equals(propertyName)) {
            filePath = System.getProperty(propertyName);
        }
        File file = null;

        if (filePath == null || "".equals(filePath)) {
            URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
            if (url == null) {
                throw new FileNotFoundException(fileName + " not found!");
            }
            file = new File(url.getPath());
        } else {
            filePath = filePath.endsWith("/") ? filePath.concat(fileName)
                    : filePath.concat("/").concat(fileName);
            file = new File(filePath);
        }
        return file;
    }


    public static String getFilePath(String fileName, String propertyName) throws Exception {
        String filePath = null;
        if (propertyName != null && !"".equals(propertyName)) {
            filePath = System.getProperty(propertyName);
        }

        if (filePath == null || "".equals(filePath)) {

            URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
            if (url == null) {
                throw new FileNotFoundException(fileName + " not found!");
            }
            filePath = url.getPath();
        } else {
            filePath = filePath.endsWith("/") ? filePath.concat(fileName)
                    : filePath.concat("/").concat(fileName);
        }
        return filePath;
    }

    public static String getFileDir(String fileName, String propertyName) throws Exception {
        String filePath = null;
        if (propertyName != null && !"".equals(propertyName)) {
            filePath = System.getProperty(propertyName);
        }

        if (filePath == null || "".equals(filePath)) {

            URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
            if (url == null) {
                throw new FileNotFoundException(fileName + " not found!");
            }
            filePath = url.getPath();
            filePath = filePath.replace(fileName, "");
        } else {
            filePath = filePath.endsWith("/") ? filePath.concat(fileName)
                    : filePath.concat("/").concat(fileName);
        }
        return filePath;
    }

    public static String read(File file, String charset) throws Exception {
        final byte[] content = read(file);
        return content == null ? "" : new String(content);
    }

    public static byte[] read(File file) {
        if (!(file.exists() && file.isFile())) {
            throw new IllegalArgumentException("The remote not exist or not a remote");
        }
        FileInputStream fis = null;
        byte[] content = null;
        try {
            fis = new FileInputStream(file);
            content = new byte[fis.available()];
            fis.read(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fis = null;
            }
        }
        return content;
    }


    /**
     * 将saveProperties保存为文件
     *
     * @param filePath
     * @param parameterName
     * @param parameterValue
     */
    public static void saveProperties(String filePath, String parameterName, String parameterValue) throws Exception {
        Properties prop = new Properties();
        try {
            InputStream fis = new FileInputStream(filePath);
            prop.load(fis);
            OutputStream fos = new FileOutputStream(filePath);
            prop.setProperty(parameterName, parameterValue);
            prop.store(fos, "Update '" + parameterName + "' value");
            fis.close();
        } catch (IOException e) {
            System.err.println("Visit " + filePath + " for updating " + parameterName + " value error");
        }

    }


    /**
     * 删除单个文件
     *
     * @param filePath 文件目录路径
     * @param fileName 文件名称
     */
    public static void deleteFile(String filePath, String fileName) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().equals(fileName)) {
                        files[i].delete();
                        return;
                    }
                }
            }
        }
    }

    /**
     * 流转byte数组
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 数据文件，返回文件内容
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString + "\n";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    /**
     * 数据文件，返回文件内容
     *
     * @param path
     * @return
     */
    public static List<String> readFile2LineList(String path) {
        BufferedReader reader = null;
        List<String> list = Lists.newArrayList();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 将给定字符写入目标文件
     *
     * @param content
     * @param file
     * @return
     */
    public static boolean write(String content, File file) throws Exception {
        boolean flag = false;
        file.getParentFile().mkdirs();
        InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        try (OutputStream os = new FileOutputStream(file)) {
            byte[] buf = new byte[2048];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
            os.close();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return flag;
    }


}
