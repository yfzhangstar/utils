package com.akoo.common.util.convert;

import com.akoo.common.util.convert.builder.DTObjFieldBuilder;
import com.akoo.common.util.convert.builder.impl.ext.ListBuilder;
import com.akoo.common.util.convert.builder.impl.ext.MapBuilder;
import com.akoo.common.util.convert.builder.impl.ext.ObjectBuilder;
import com.akoo.common.util.convert.builder.impl.ext.SetBuilder;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.io.IOException;
import java.util.function.Consumer;

public class DTObjClassVisitor extends ClassVisitor implements Opcodes {
    private DTObjInfo dbobjInfo;
    private Consumer<DTObjInfo> listener;

    public DTObjClassVisitor(String clzName, String superClzName, Consumer<DTObjInfo> listener) {
        super(ASM5);
        dbobjInfo = new DTObjInfo(clzName, superClzName == null ? RWConstant.OBJ_INTERNAL_NAME : superClzName);
        this.listener = listener;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (RWConstant.ACONVERT_ANNOTATION_NAME.equals(desc)) {
            try {
                String str = dbobjInfo.getName().replace("/",".");
                Class clz = Class.forName(str);
                Util.StrMappingClz.put(clz.getSimpleName(), clz);
                Util.ClzMappingStr.put(clz, clz.getSimpleName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            listener.accept(dbobjInfo);
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new DTOFieldVisitor(dbobjInfo, new DTObjFieldInfo(name, desc, signature), listener);
    }



    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    public DTObjInfo getDbobjInfo() {
        return dbobjInfo;
    }

    public void setDbobjInfo(DTObjInfo dbobjInfo) {
        this.dbobjInfo = dbobjInfo;
    }

    private static class SaveAnnoVisitor extends AnnotationVisitor {

        private DTObjInfo dbobjInfo;

        private DTObjFieldInfo fieldInfo;

        public SaveAnnoVisitor(DTObjInfo dbobjInfo, DTObjFieldInfo fieldInfo) {
            super(ASM5);
            this.dbobjInfo = dbobjInfo;
            this.fieldInfo = fieldInfo;
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (RWConstant.INDEX_IX.equals(name)) {
                Integer ix = (Integer) value;
                if (ix < 0) {
                    throw new RuntimeException("field ix error, cannt be minus. [" + dbobjInfo.getName() + "." + fieldInfo.getName() + "]");
                }
                if (dbobjInfo.getFieldsInfos().containsKey(ix)) {
                    throw new RuntimeException("field ix error, duplicated. [" + dbobjInfo.getName() + "." + fieldInfo.getName() + ", ix:" + ix + "]");
                }
                fieldInfo.setIx(ix);
                dbobjInfo.putFieldInfo(ix, fieldInfo);
            }
        }


    }

    private static class DTOFieldVisitor extends FieldVisitor {

        private DTObjInfo dbobjInfo;

        private DTObjFieldInfo fieldInfo;

        private Consumer<DTObjInfo> listener;

        public DTOFieldVisitor(DTObjInfo dbobjInfo, DTObjFieldInfo fieldInfo, Consumer<DTObjInfo> listener) {
            super(ASM5);
            this.dbobjInfo = dbobjInfo;
            this.fieldInfo = fieldInfo;
            this.listener = listener;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.equals(RWConstant.INDEX_ANNO_DESC))
                return new SaveAnnoVisitor(dbobjInfo, fieldInfo);
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitEnd() {
            if (dbobjInfo.containsFieldInfo(fieldInfo)) {//处理需要存储的字段
                String desc = fieldInfo.getDesc();
                String sig = fieldInfo.getSig();
                DTObjFieldBuilder baseTrans = RWConstant.getBaseBuilder(desc);
                if (baseTrans != null) {//
                    fieldInfo.setBuilder(baseTrans);
                }
                else if (sig == null) {//非基础类型非泛型对象只支持DBVO
                    try {
                        Type t = Type.getType(desc);
                        Class<?> clazz = loadDBObjClass(t, desc);
                        fieldInfo.setBuilder(new ObjectBuilder(clazz));
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
                else {//仅支持List, Map, Set三类泛型集合, 内部元素支持基本类型以及DBVO
                    SignatureReader sigR = new SignatureReader(sig);
                    VOFieldSigVisitor sigv = new VOFieldSigVisitor();
                    sigR.accept(sigv);
                    fieldInfo.setBuilder(sigv.getTrans());
                }
                super.visitEnd();
            }
        }

        private Class<?> loadDBObjClass(Type t, String desc) throws ClassNotFoundException, IOException {
            String className = t.getClassName();
            Class<?> clazz = Class.forName(className);
            if (ConvertManager.getProcessMap().containsKey(clazz))
                return clazz;
            DTObjChainLoader l = new DTObjChainLoader(listener);
            ClassReader cr = new ClassReader(clazz.getName());
            cr.accept(l, 0);
            return clazz;
        }

        private class VOFieldSigVisitor extends SignatureVisitor {
            private static final int TYPE_LIST = 0;
            private static final int TYPE_MAP = 1;
            private static final int TYPE_SET = 2;

            private int transType = -1;
            private int visitCount;

            private DTObjFieldBuilder trans;
            private Class objclass = null;

            public VOFieldSigVisitor() {
                super(ASM5);
            }


            @Override
            public void visitClassType(String name) {
                switch (visitCount) {
                case 0: {
                    Type type = Type.getObjectType(name);
                    String className = type.getClassName();

                    switch (className) {
                        case "java.util.List":
                            this.transType = TYPE_LIST;
                            break;
                        case "java.util.Set":
                            this.transType = TYPE_SET;
                            break;
                        case "java.util.Map":
                            this.transType = TYPE_MAP;
                            break;
                        default:
                            throw new RuntimeException("not support type:" + name);
                    }
                    break;
                }
                case 1: {
                    loadVOAndObjClass(name);
                    switch (transType) {
                        case TYPE_LIST:
                            ListBuilder lt = new ListBuilder();
                            lt.setDbobjClass(objclass);
                            trans = lt;
                            break;
                        case TYPE_SET:
                            SetBuilder st = new SetBuilder();
                            st.setDbobjClass(objclass);
                            trans = st;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case 2: {
                    Class kObjClass = objclass;
                    loadVOAndObjClass(name);
                    Class vObjClass = objclass;
                    MapBuilder mt = new MapBuilder();
                    mt.setKdbobjClass(kObjClass);
                    mt.setVdbobjClass(vObjClass);
                    trans = mt;
                    break;
                }
                default:
                    break;
            }
                super.visitClassType(name);
            visitCount++;
        }


        private void loadVOAndObjClass(String name) {
            Type type = Type.getObjectType(name);
            String desc = type.getDescriptor();
            if (RWConstant.containsBaseBuilder(desc)) {
                try {
                    objclass = Class.forName(type.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    objclass = loadDBObjClass(type, desc);
                } catch (ClassNotFoundException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


            public DTObjFieldBuilder getTrans() {
                return trans;
            }


            public void setTrans(DTObjFieldBuilder trans) {
                this.trans = trans;
            }

        }


    }


}
