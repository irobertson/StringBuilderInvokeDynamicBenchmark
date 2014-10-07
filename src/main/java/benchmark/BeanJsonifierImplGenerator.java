package benchmark;

import static org.objectweb.asm.Opcodes.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class BeanJsonifierImplGenerator {

  public static byte[] dump(int constStringAppendCount, boolean useInvokeDynamic) throws Exception {
    ClassWriter cw = new ClassWriter(0);

    cw.visit(52, ACC_PUBLIC,
      "benchmark/BeanJsonifierImpl",
      null,
      "java/lang/Object", new String[] { "benchmark/BeanJsonifier" });

    visitConstructor(cw);
    visitMarshal(cw, constStringAppendCount, useInvokeDynamic);
    cw.visitEnd();

    return cw.toByteArray();
  }

  private static void visitMarshal(ClassWriter cw, int constStringAppendCount, boolean useInvokeDynamic) {
    MethodVisitor mv;
    mv = cw.visitMethod(ACC_PUBLIC, "marshal", methodDesc(String.class, Bean.class), null, null);
    mv.visitCode();

    // Construct a new String Builder
    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

    // call StringBuilder.append("x") for constStringAppendCount times.
    for (int i = 0; i < constStringAppendCount; i++) {
      mv.visitLdcInsn("x");
      mv.visitMethodInsn(
        INVOKEVIRTUAL, "java/lang/StringBuilder", "append", methodDesc(StringBuilder.class, String.class), false);
    }

    // now load the field s, either by invokeDynamic or a getfield op.
    mv.visitVarInsn(ALOAD, 1);
    if (useInvokeDynamic) {
      mv.visitInvokeDynamicInsn(
        "access_prop2",
        methodDesc(String.class, Bean.class),
        new Handle(
          H_INVOKESTATIC,
          "benchmark/Bootstrap",
          "getField",
          methodDesc(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class)));
    }
    else {
      mv.visitFieldInsn(GETFIELD, "benchmark/Bean", "s", "Ljava/lang/String;");
    }

    // append value, call toString and return.
    mv.visitMethodInsn(
      INVOKEVIRTUAL, "java/lang/StringBuilder", "append", methodDesc(StringBuilder.class, String.class), false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", methodDesc(String.class), false);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private static void visitConstructor(ClassWriter cw) {
    MethodVisitor mv;
    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", methodDesc(void.class), false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  private static String methodDesc(Class<?> returnType, Class<?> ... paramTypes){
    return MethodType.methodType(returnType, paramTypes).toMethodDescriptorString();
  }
}
