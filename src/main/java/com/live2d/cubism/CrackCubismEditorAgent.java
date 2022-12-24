package com.live2d.cubism;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CrackCubismEditorAgent {

    private static final Logger logger = CECLogger.getLogger("");


    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        log("CubismEditorCrack Open!!!");

        inst.addTransformer(new CubismEditor_Transformer(), true);

    }


    public static void log(Object obj) {
        logger.log(Level.INFO, obj + "\n");
    }

    public static void log_ClassInject(String className) {
        log("Class " + "\"" + className + "\"" + " Inject...");
    }

    public static byte[] setClassByte(String className, Method_Date... date) {
        byte[] bytes = new byte[0];

        String className_JS = className.replace("/", ".");

        try {

            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(className_JS);

            for (Method_Date d : date) {
                CtMethod method = ctClass.getMethod(d.methodName, d.methodDesc);
                method.setBody(d.body);
            }

            ctClass.detach();

            bytes = ctClass.toBytecode();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static byte[] setNoClassByte(String className, Method_Date... date) {
        byte[] bytes = new byte[0];

        String className_JS = className.replace("/", ".");

        try {

            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(className_JS);

            for (Method_Date d : date) {
                CtMethod method = ctClass.getDeclaredMethod(d.methodName);
                method.setBody(d.body);
            }

            ctClass.detach();

            bytes = ctClass.toBytecode();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static class CubismEditor_Transformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            switch (className) {
                case "com/live2d/c/m":
                    return setClassByte(className,
                            Method_Date.getMethod_Date(
                                    "b", "()Lcom/live2d/c/l;",
                                    "{" +
                                            "this.a = com.live2d.c.l.d;" +
                                            "return this.a;" +
                                            "}"));
                case "com/live2d/cubism/setting/Q":
                    return setClassByte(className,
                            Method_Date.getMethod_Date(
                                    "f", "()Ljava/lang/String;",
                                    "{return \"[ 压根不要钱的Pro版 来自某无名兔子 ]\";}"));

            }
            return classfileBuffer;

        }
    }

    public static class Method_Date {

        public final String methodName;
        public final String methodDesc;
        public final String body;

        private Method_Date(String methodName, String methodDesc, String body) {
            this.methodName = methodName;
            this.methodDesc = methodDesc;
            this.body = body;
        }

        public static Method_Date getMethod_Date(String methodName, String methodDesc, String body) {
            return new Method_Date(methodName, methodDesc, body);
        }

        public static Method_Date getMethod_Date(String methodName, String body) {
            return new Method_Date(methodName, "", body);
        }

    }


}
