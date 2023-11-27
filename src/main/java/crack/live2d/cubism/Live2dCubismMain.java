package crack.live2d.cubism;

import org.zeroturnaround.zip.ZipUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static crack.live2d.cubism.LibPathsManager.Main_Jar_Path;

public class Live2dCubismMain {

    static String Jar_Name;

    public static void main(String[] args) {
        Jar_Name = getJarName();
        crack();
    }

    private static void crack() {
        System.out.println("[Crack] 破解补丁安装中...");
        System.out.println("进度: 0/2");
        clearRSA();
        setCMD();
        System.out.println("[Crack] 破解完成!");
    }

    public static String getJarName() {
        String name = Live2dCubismMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] names = name.split("/");
        name = names[names.length - 1];
        return name;
    }

    private static void clearRSA() {
        System.out.println("[Crack] 开始清除签名文件");
        List<String> del_name = new ArrayList<>();
        ZipUtil.iterate(Main_Jar_Path.toFile(), zipEntry -> {
            String name = zipEntry.getName();
            if (name.startsWith("META-INF") && isRsa(name)) {
                del_name.add(name);
            }
        });
        if (!del_name.isEmpty()) ZipUtil.removeEntries(Main_Jar_Path.toFile(), del_name.toArray(new String[0]));
        System.out.println("[Crack] 清除完成");
        System.out.println("进度: 1/2");
    }

    private static void setCMD() {
        System.out.println("[Crack] 修改启动脚本中...");
        try {
            List<Path> paths = Files.list(Paths.get("./")).collect(Collectors.toList());
            for (Path path : paths) {
                String name = path.getFileName().toString();
                if (name.startsWith("CubismEditor") && name.endsWith(".bat")) {
                    writeCMD(path);
                }
            }
            System.out.println("[Crack] 修改完成");
            System.out.println("进度: 2/2");
        } catch (IOException e) {
            System.out.println("[Crack] 修改失败");
            throw new RuntimeException(e);
        }
    }

    private static void writeCMD(Path path) throws IOException {
        List<String> cmd_strings = Files.readAllLines(path);
        int key = -1;
        for (int i = 0; i < cmd_strings.size(); i++) {
            String cmd = cmd_strings.get(i);
            if (cmd.equals("%JAVA_EXE% ^")) {
                key = i;
                break;
            }
        }
        if (key > -1) cmd_strings.set(key, "%JAVA_EXE% -javaagent:" + Jar_Name + " ^");
        Files.write(path, cmd_strings);
    }

    private static boolean isRsa(String name) {
        String[] names = new String[]{".RSA", ".SF", "MF"};
        for (String n : names) {
            if (name.endsWith(n)) return true;
        }
        return false;
    }

}