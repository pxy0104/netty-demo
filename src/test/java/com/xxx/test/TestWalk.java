package com.xxx.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 2023/4/25
 **/

public class TestWalk {
    public static void main(String[] args) throws IOException {
        String source = "D:\\nacos";
        String target = "D:\\nacos_bak";

        Files.walk(Paths.get(source)).forEach(path -> {
            String targetName = path.toString().replace(source,target);
            if (Files.isDirectory(path)) {
                //如果是目录
                try {
                    Files.createDirectory(Paths.get(targetName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Files.isRegularFile(path)) {
                //是普通文件
                try {
                    Files.copy(path,Paths.get(targetName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
