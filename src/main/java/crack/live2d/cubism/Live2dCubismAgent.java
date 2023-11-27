package crack.live2d.cubism;

import crack.live2d.cubism.tools.AsmCml;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

import static crack.live2d.cubism.LibPathsManager.Main_Jar_Path;

public class Live2dCubismAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        AsmCml.init(Main_Jar_Path);
        ClassFileTransformer transformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className.equals(AsmCml.Class_ClmStatusResult_Name)) {
                    return setClass(classfileBuffer);
                }
                return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            }
        };
        inst.addTransformer(transformer);

        // TransformerManager transformerManager = new TransformerManager(new BasicClassProvider());
        // transformerManager.addRawTransformer(AsmCml.Class_ClmStatusResult_Name, (manager, node) -> AsmCml.getCmlNode(node));
        // transformerManager.hookInstrumentation(inst);
    }

    private static byte[] setClass(byte[] buffer) {
        ClassReader reader = new ClassReader(buffer);
        ClassNode new_node = AsmCml.getCmlNode(reader);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        new_node.accept(writer);
        return writer.toByteArray();
    }

}
