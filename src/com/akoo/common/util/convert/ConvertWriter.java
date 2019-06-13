package com.akoo.common.util.convert;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.*;

class ConvertWriter extends ClassLoader implements Opcodes {

    String Object_Name = Type.getInternalName(Object.class);

    String DATAHOLDER_PATH = "com/akoo/common/util/convert/DataHolder";
    String BUFFER = "com/akoo/common/util/convert/DBBuffer";
    String CONVERT_INTERFACE_PATH = Type.getInternalName(IConvertProcess.class);

    // 是否写文件
    public static boolean write2ClassFile = false;
    private ClassWriter cw;

    ConvertWriter() {

    }

    Class<? extends IConvertProcess> writeClass(DTObjInfo objInfo) {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, objInfo.getConvertName(), "", objInfo.getSuperName(), new String[]{CONVERT_INTERFACE_PATH});
        buildConstructor_1(objInfo.getConvertName(), objInfo.getSuperName());
        buildToBytesMethod(objInfo);
        buildFromBytesMethod(objInfo);

        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        Class<?> clazz = (Class<?>) defineClass( bytes,  ConvertWriter.class.getClassLoader());
//        System.out.println("class : " + clazz);
        if (write2ClassFile) {
            try (FileOutputStream fos = new FileOutputStream(clazz.getSimpleName() + ".class")) {
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (Class<? extends IConvertProcess>) clazz;
    }
    private void buildConstructor_1(String convertName, String superName) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L" + convertName + ";", null, l0, l2, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private static Class<?> defineClass(byte[] bytes, ClassLoader classloader) {
        return TheUnSafe.getUNSAFE().defineClass(null, bytes, 0, bytes.length, classloader, null);
    }

    private void buildToBytesMethod(DTObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toBytes", "(L"+Object_Name+";L"+DATAHOLDER_PATH+";)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.OBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "toBytes", "(L"+Object_Name+";L"+DATAHOLDER_PATH+";)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, objInfo.getName());
        mv.visitVarInsn(ASTORE, 3);

        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitMethodInsn(INVOKESTATIC, BUFFER, "allocate", "()L"+BUFFER+";", false);
        mv.visitVarInsn(ASTORE, 4);
        Label l2 = new Label();
        mv.visitLabel(l2);
        Set<Entry<Integer, DTObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DTObjFieldInfo> e : es) {
            DTObjFieldInfo fieldInfo = e.getValue();
            Label l3 = new Label();
            fieldInfo.getBuilder().buildToBytesHolderMContent(mv, fieldInfo.getName(), objInfo.getName());
            mv.visitLabel(l3);
        }
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "toBytes", "()[B", true);
        mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "putBytes", "([B)V", true);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

    private void buildFromBytesMethod(DTObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "fromBytes", "(L"+DATAHOLDER_PATH+";L"+Object_Name+";)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.OBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "fromBytes", "(L"+DATAHOLDER_PATH+";L"+Object_Name+";)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, objInfo.getName());
        mv.visitVarInsn(ASTORE, 3);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, DATAHOLDER_PATH, "getBytes", "()[B", true);
        mv.visitMethodInsn(INVOKESTATIC, BUFFER, "warp", "([B)L"+BUFFER+";", false);
        mv.visitVarInsn(ASTORE, 4);
        Label l2 = new Label();
        mv.visitLabel(l2);
        Set<Entry<Integer, DTObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DTObjFieldInfo> e : es) {
            DTObjFieldInfo fieldInfo = e.getValue();
            Label l3 = new Label();
            fieldInfo.getBuilder().buildFromBytesHolderMContent(mv, fieldInfo.getName(), objInfo.getName());
            mv.visitLabel(l3);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

}
