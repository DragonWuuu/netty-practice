package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

@Slf4j
public class ScannerFile {
    public static void main(String[] args) {
        // 扫描typora的文件，看哪个文件里有“伫倚”二字

//        C:\Users\dragon\Documents\typora
        // 基础目录
        Path base = Paths.get("C:", "Users", "dragon", "Documents", "typora");
        log.info(base.toString());
        // 获取所有文件路径
        try {
            Iterator<Path> iterator = Files.walk(base).iterator();
            while (iterator.hasNext()) {
                Path next = iterator.next();
                if (next.toFile().isDirectory()) {
                    continue;
                }
                // 检查文件类型
                if (!next.toString().endsWith(".md")) {
                    continue;
                }
                log.info("file:{}", next);
                String content = Files.readString(next, StandardCharsets.UTF_8);
                if (content.contains("伫倚")) {
                    log.info("找到匹配内容的文件: {}", next);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
