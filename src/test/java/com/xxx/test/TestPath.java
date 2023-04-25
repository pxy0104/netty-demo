package com.xxx.test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 2023/4/24
 **/

public class TestPath {
    public static void main(String[] args) {
        Path path = Paths.get("words.txt");
        System.out.println(path.toAbsolutePath());
    }
}
