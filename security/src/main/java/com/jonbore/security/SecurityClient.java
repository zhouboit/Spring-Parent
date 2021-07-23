package com.jonbore.security;

import com.unistrong.hadoop.HDFSClient;
import com.unistrong.hadoop.IOVFile;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/7/23
 */
public class SecurityClient {
    public static void main(String[] args) {
        try {
            //--ls hdfs://192.168.81.135:9000/test007/cindy007/jonbore/out/jar
            //--get hdfs://192.168.81.135:9000/test007/cindy007/jonbore/out/export/20210616152804177_mes_sys_app_copy_1623918068455.csv
            //--put /tmp/20210616152804177_mes_sys_app_copy_1623918068455.csv --dest hdfs://192.168.81.135:9000/test007/cindy007/jonbore/out/jar
            Option option = Option.fromArgs(args);
            String master = option.has("master") ? option.get("master") : "hdfs://192.168.81.135:9000";
            String user = option.has("user") ? option.get("user") : "hdfs";
            HDFSClient client = new HDFSClient(master, null, user);
            if (option.has("get")) {
                String file = option.get("get");
                String downloadDir = option.has("to") ? option.get("to") : "/tmp/" + System.currentTimeMillis() + "/" + file.substring(file.lastIndexOf("/") + 1);
                File target = new File(downloadDir);
                if (!target.getParentFile().exists()) {
                    target.getParentFile().mkdirs();
                }
                client.downloadHadoopFileToLocal(file, downloadDir);
                System.exit(0);
            }
            if (option.has("put")) {
                String file = option.get("put");
                String targetDir = option.get("dest");
                File source = new File(file);
                if (!source.exists()) {
                    throw new RuntimeException(String.format("指定上传文件不存在%s", file));
                }
                String targetFile = targetDir.concat("/").concat(source.getName());
                client.saveFile(new FileInputStream(source), targetFile);
                System.exit(0);
            }
            if (option.has("ls")) {
                list(client, option.get("ls"));
                System.exit(0);
            }

            System.out.printf("未实现的操作符,option:%s%n", Arrays.toString(args));
        } catch (Exception e) {
            System.out.printf("Hadoop客户端操作异常,Exception:%s%n", ExceptionUtils.getStackTrace(e));
            System.exit(127);
        } finally {
            System.out.println("执行完毕");
        }
    }

    private static void list(HDFSClient client, String path) throws IOException {
        List<IOVFile> list = client.listFile(path);
        list.forEach(iovFile -> {
            if (iovFile.isDirectory()) {
                System.out.printf("目录:%s%n", iovFile.getPath());
                try {
                    list(client, path.endsWith("/") ? path.concat(iovFile.getName()) : path.concat("/").concat(iovFile.getName()));
                } catch (IOException e) {
                    System.out.printf("Hadoop客户端操作异常,Exception:%s%n", ExceptionUtils.getStackTrace(e));
                }
            } else {
                System.out.printf("文件:%s,Size:%s,修改时间:%s%n", iovFile.getPath(), iovFile.getLength(), iovFile.getModificationTime());
            }
        });
    }
}
