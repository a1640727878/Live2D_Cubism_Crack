package crack.live2d.cubism.tools;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.zeroturnaround.zip.ZipUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsmCml {

    public static String Class_ClmStatusResult_Name;
    public static String Class_ClmStatus_Name;
    public static String Field_Clm_Name;

    static List<FieldNode> Clm_Feld_List;

    public static void init(Path jar_path) {

        ZipUtil.iterate(jar_path.toFile(), (in, zipEntry) -> {
            String name = zipEntry.getName();
            if (name.endsWith(".class")) {
                readClass(in);
            }
        });
        setFieldClmName();
    }

    private static void readClass(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        List<AnnotationNode> annotations = node.visibleAnnotations;
        if (annotations == null) return;
        for (AnnotationNode annotation : annotations) {
            if (annotation.desc.contains("Lkotlin/Metadata;")) {
                List<Object> objs = annotation.values;
                Optional<String> optional_name = getName(objs);
                if (optional_name.isPresent()) {
                    String name = optional_name.get();
                    if (name.contentEquals("com/live2d/rlm/ClmStatusResult")) {
                        Class_ClmStatusResult_Name = node.name;
                        System.out.println(Class_ClmStatusResult_Name);
                        Clm_Feld_List = new ArrayList<>(node.fields);
                    } else if (name.contentEquals("com/live2d/rlm/ClmStatus")) Class_ClmStatus_Name = node.name;
                }
            }
        }
    }

    private static void setFieldClmName() {
        for (FieldNode field : Clm_Feld_List) {
            String desc = field.desc;
            if (desc.contentEquals(getClmStatusDesc())) {
                Field_Clm_Name = field.name;
                return;
            }
        }
    }

    private static void setGetClmStatus(MethodVisitor visitor) {
        visitor.visitCode();
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, Class_ClmStatus_Name, "values", "()[" + getClmStatusDesc(), false);
        visitor.visitInsn(Opcodes.ICONST_2);
        visitor.visitInsn(Opcodes.AALOAD);
        visitor.visitFieldInsn(Opcodes.PUTFIELD, Class_ClmStatusResult_Name, Field_Clm_Name, getClmStatusDesc());
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, Class_ClmStatusResult_Name, Field_Clm_Name, getClmStatusDesc());
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(3, 1);
        visitor.visitEnd();
    }

    public static ClassNode getCmlNode(ClassReader reader) {
        ClassNode new_node = new ClassNode(Opcodes.ASM9) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if (descriptor.contentEquals(getClmStatusDesc())) {
                    return super.visitField(Opcodes.ACC_PRIVATE, name, descriptor, signature, value);
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (descriptor.contentEquals(getClmStatusMethodDesc())) {
                    setGetClmStatus(mv);
                    return mv;
                }
                return mv;
            }
        };
        reader.accept(new_node,0);
        return new_node;
    }


    public static String getClmStatusDesc() {
        return "L" + Class_ClmStatus_Name + ";";
    }

    public static String getClmStatusMethodDesc() {
        return "()" + getClmStatusDesc();
    }

    private static int[] getArrayInt(List<Integer> integerList) {
        int[] ints = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            ints[i] = integerList.get(i);
        }
        return ints;
    }

    private static String[] getArrayString(List<String> strings) {
        return strings.toArray(new String[0]);
    }


    private static Optional<String> getName(List<Object> objs) {
        Object[] os = objs.toArray();
        int k = -1;
        String[] d2 = new String[0];
        for (int i = 0; i < os.length; i++) {
            switch (os[i].toString()) {
                case "k": {
                    k = (int) os[i + 1];
                    continue;
                }
                case "d2": {
                    d2 = getArrayString((List<String>) os[i + 1]);
                }
            }
        }
        if (d2.length < 1) return Optional.empty();
        String name = d2[0];
        name = name.substring(1, name.length() - 1);
        if (k == 1) return Optional.of(name);
        else return Optional.empty();
    }

}
