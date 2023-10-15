package me.howandev.nexus.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {
    public static Set<File> listFileTree(File dir) {
        Set<File> fileTree = new HashSet<>();
        if (dir == null)
            return fileTree;

        File[] fileList = dir.listFiles();
        if (fileList == null)
            return fileTree;

        for (File entry : fileList) {
            if (entry.isFile()) fileTree.add(entry);
            else fileTree.addAll(listFileTree(entry));
        }

        return fileTree;
    }
}
