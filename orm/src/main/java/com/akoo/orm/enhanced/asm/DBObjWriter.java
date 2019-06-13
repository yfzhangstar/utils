package com.akoo.orm.enhanced.asm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import com.akoo.orm.DBObj;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class DBObjWriter extends ClassLoader implements Opcodes {

    // 是否写文件
    public static boolean write2ClassFile = false;
    private ClassWriter cw;

    DBObjWriter() {

    }

    Class<? extends DBObj> writeClass(DBObjInfo objInfo) {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String objName = objInfo.getName();
        String superName = objInfo.getSuperName();
//        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, objName, "Lcom/akoo/orm/DBObj<L" + objName + ";>;", superName, null);
//        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, objName, "Lcom/akoo/orm/DBObj;", superName, null);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, objName, "", superName, null);

        buildFields(objInfo);

        buildConstructor_1(objName, superName);
        buildConstructor_2(objName, superName);
        buildConstructor_3(objName, superName);
        buildInitMethod(objName, objInfo);
        buildWriteBizDataMethod(objInfo);
        buildReadBizDataMethod(objInfo);
        buildReadVOMethod(objInfo);
        buildWriteVOMethod(objInfo);
        buildCloneMethod(objInfo);
        cw.visitEnd();
        byte[] byteArray = cw.toByteArray();
        @SuppressWarnings("unchecked")
        Class<? extends DBObj> clazz = (Class<? extends DBObj>) defineClass(null, byteArray, 0, byteArray.length);
        if (write2ClassFile) {
            try (FileOutputStream fos = new FileOutputStream(clazz.getSimpleName() + ".class")) {
                fos.write(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }


    /**
     * 创建字段
     */
    private void buildFields(DBObjInfo objInfo) {
        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            fieldInfo.getBuilder().buildDBObjField(cw, fieldInfo.getName());
        }
    }

    private void buildInitMethod(String objName, DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "init", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "init", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            mv.visitLabel(l2);
            fieldInfo.getBuilder().buildInitMContent(mv, fieldInfo.getName(), objInfo.getName());
        }
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLocalVariable("this", "L" + objName + ";", null, l0, l3, 0);
        mv.visitMaxs(0, 1);
        mv.visitEnd();
    }

    private void buildConstructor_1(String objname, String superName) {
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
        mv.visitLocalVariable("this", "L" + objname + ";", null, l0, l2, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void buildConstructor_2(String objName, String superName) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([B)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "([B)V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L" + objName + ";", null, l0, l2, 0);
        mv.visitLocalVariable("orm", "[B", null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void buildConstructor_3(String objName, String superName) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/akoo/orm/DataHolder;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(Lcom/akoo/orm/DataHolder;)V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L" + objName + ";", null, l0, l2, 0);
        mv.visitLocalVariable("buffer", "Lcom/akoo/orm/DataHolder;", null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }


    private void buildWriteBizDataMethod(DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeBizData", "(Lcom/akoo/orm/DataHolder;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "writeBizData", "(Lcom/akoo/orm/DataHolder;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            fieldInfo.getBuilder().buildWriteHolderMContent(mv, fieldInfo.getName(), objInfo.getName());
            mv.visitLabel(l2);
        }
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + objInfo.getName() + ";", null, l0, l1, 0);
        mv.visitLocalVariable("dbBuffer", "Lcom/akoo/orm/DataHolder;", null, l0, l1, 1);
        mv.visitMaxs(0, 2);
        mv.visitEnd();
    }

    private void buildReadBizDataMethod(DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readBizData", "(Lcom/akoo/orm/DataHolder;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "readBizData", "(Lcom/akoo/orm/DataHolder;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            fieldInfo.getBuilder().buildReadHolderMContent(mv, fieldInfo.getName(), objInfo.getName());
            mv.visitLabel(l2);
        }
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + objInfo.getName() + ";", null, l0, l1, 0);
        mv.visitLocalVariable("dbBuffer", "Lcom/akoo/orm/DataHolder;", null, l0, l1, 1);
        mv.visitMaxs(0, 2);
        mv.visitEnd();
    }

    private void buildCloneMethod(DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "cloneBizData", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "cloneBizData", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            fieldInfo.getBuilder().buildCloneMContent(mv, fieldInfo.getName(), objInfo.getName());
            mv.visitLabel(l2);
        }

        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + objInfo.getName() + ";", null, l0, l1, 0);
        mv.visitMaxs(0, 1);
        mv.visitEnd();
    }

    private void buildWriteVOMethod(DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeVO", "(Lcom/akoo/orm/DBVO;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "writeVO", "(Lcom/akoo/orm/DBVO;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, objInfo.getMapVOName());
        mv.visitVarInsn(ASTORE, 2);
        Label l4 = new Label();
        mv.visitLabel(l4);

        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            fieldInfo.getBuilder().buildWriteVOMContent(mv, fieldInfo.getName(), objInfo.getName(), objInfo.getMapVOName(), fieldInfo.isWrapper());
            mv.visitLabel(l2);
        }

        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + objInfo.getName() + ";", null, l0, l1, 0);
        mv.visitLocalVariable("dbvo", "Lcom/akoo/orm/DBVO;", null, l0, l1, 1);
        mv.visitLocalVariable("dvo", "L" + objInfo.getMapVOName() + ";", null, l4, l1, 2);
        mv.visitMaxs(0, 2);
        mv.visitEnd();
    }


    private void buildReadVOMethod(DBObjInfo objInfo) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readVO", "(Lcom/akoo/orm/DBVO;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        if (!objInfo.getSuperName().equals(RWConstant.DBOBJ_INTERNAL_NAME)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, objInfo.getSuperName(), "readVO", "(Lcom/akoo/orm/DBVO;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, objInfo.getMapVOName());
        mv.visitVarInsn(ASTORE, 2);
        Label l4 = new Label();
        mv.visitLabel(l4);

        Set<Entry<Integer, DBObjFieldInfo>> es = objInfo.getFieldsInfos().entrySet();
        for (Entry<Integer, DBObjFieldInfo> e : es) {
            DBObjFieldInfo fieldInfo = e.getValue();
            Label l2 = new Label();
            fieldInfo.getBuilder().buildReadVOMContent(mv, fieldInfo.getName(), objInfo.getName(), objInfo.getMapVOName(), fieldInfo.isWrapper());
            mv.visitLabel(l2);
        }

        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + objInfo.getName() + ";", null, l0, l1, 0);
        mv.visitLocalVariable("dbvo", "Lcom/akoo/orm/DBVO;", null, l0, l1, 1);
        mv.visitLocalVariable("dvo", "L" + objInfo.getMapVOName() + ";", null, l4, l1, 2);
        mv.visitMaxs(0, 2);
        mv.visitEnd();
    }
}
