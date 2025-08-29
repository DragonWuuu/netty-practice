package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class Main {
    public static void main(String[] args) {
        // 从一个文件copy到另一个文件，现在有两个文件：resource.txt、target.txt
        try {
            FileChannel resourceChannel = new FileInputStream("./nio/src/main/resources/resource.txt").getChannel();
            FileChannel targetChannel = new FileOutputStream("./nio/src/main/resources/target.txt").getChannel();
            resourceChannel.transferTo(0, resourceChannel.size(), targetChannel);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}