package com.live2d.cubism;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CrackCubismEditorMain {

    private static final Logger logger = CECLogger.getLogger("");

    public static void main(String[] strings) throws IOException {
        log("正在安装补丁中...");
        clearRSA();
        setCMD();
        log("补丁安装完成!");
    }

    public static void clearRSA() throws IOException {
        File zip = Paths.get("app", "lib", "Live2D_Cubism.jar").toFile();

        List<String> name_list = new ArrayList<>();

        ZipUtil.iterate(zip, (in, zipEntry) -> {
            String name = zipEntry.getName();
            if (name.startsWith("META-INF/") && (name.endsWith(".RSA") || name.endsWith(".SF"))) {
                name_list.add(name);
            }
        });

        for (String name : name_list) {
            ZipUtil.removeEntry(zip, name);
        }
    }


    public static void setCMD() throws IOException {
        Path path_1 = Paths.get("CubismEditor4.bat");
        Path path_2 = Paths.get("CubismEditor4_d3d.bat");

        setCMD(path_1);
        setCMD(path_2);
    }

    private static void setCMD(Path path) throws IOException {
        List<String> lines_read = Files.readAllLines(path);
        List<String> lines_write = new ArrayList<>();

        for (String line : lines_read) {
            if (line.equals("%JAVA_EXE% ^")) {
                line = "%JAVA_EXE% -javaagent:Live2D_Cubism_Crack-4.2.02.jar -noverify ^";
            }
            lines_write.add(line);
        }

        Files.write(path, lines_write);
    }


    public static void log(Object obj) {
        logger.log(Level.INFO, obj + "\n");
    }
}
