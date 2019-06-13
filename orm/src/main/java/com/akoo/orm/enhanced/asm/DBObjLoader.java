package com.akoo.orm.enhanced.asm;

import com.akoo.orm.enhanced.asm.builder.DBObjFieldBuilder;
import com.akoo.orm.enhanced.asm.builder.impl.ext.DBVOBuilder;
import com.akoo.orm.enhanced.asm.builder.impl.ext.ListBuilder;
import com.akoo.orm.enhanced.asm.builder.impl.ext.MapBuilder;
import com.akoo.orm.enhanced.asm.builder.impl.ext.SetBuilder;
import com.akoo.orm.enhanced.Util;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DBObjLoader extends ClassVisitor implements Opcodes {
    private static Logger log = LoggerFactory.getLogger(DBObjLoader.class);
    private static DBObjWriter dw = new DBObjWriter();
    private DBObjInfo dbobjInfo;

    public DBObjLoader(String dbobjSuperClass, String voName) {
        super(ASM5);
        dbobjInfo = new DBObjInfo(voName + "$PO", dbobjSuperClass == null ? RWConstant.DBOBJ_INTERNAL_NAME : dbobjSuperClass, voName);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new VOFieldVisitor(dbobjInfo, new DBObjFieldInfo(name, desc, signature));
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (!Cache.containsDBObjClass(dbobjInfo.getMapVOName()))
            Cache.addDBObjClass(dbobjInfo.getMapVOName(), dw.writeClass(dbobjInfo));
    }

    public DBObjInfo getDbobjInfo() {
        return dbobjInfo;
    }

    public void setDbobjInfo(DBObjInfo dbobjInfo) {
        this.dbobjInfo = dbobjInfo;
    }

    private static class SaveAnnoVisitor extends AnnotationVisitor {

        private DBObjInfo dbobjInfo;

        private DBObjFieldInfo fieldInfo;

        public SaveAnnoVisitor(DBObjInfo dbobjInfo, DBObjFieldInfo fieldInfo) {
            super(ASM5);
            this.dbobjInfo = dbobjInfo;
            this.fieldInfo = fieldInfo;
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (RWConstant.SAVE_INDEX.equals(name)) {
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

    private static class VOFieldVisitor extends FieldVisitor {

        private DBObjInfo dbobjInfo;

        private DBObjFieldInfo fieldInfo;

        public VOFieldVisitor(DBObjInfo dbobjInfo, DBObjFieldInfo fieldInfo) {
            super(ASM5);
            this.dbobjInfo = dbobjInfo;
            this.fieldInfo = fieldInfo;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc.equals(RWConstant.SAVE_ANNO_DESC))
                return new SaveAnnoVisitor(dbobjInfo, fieldInfo);
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitEnd() {
            if (dbobjInfo.containsFieldInfo(fieldInfo)) {//处理需要存储的字段
                String desc = fieldInfo.getDesc();
                String sig = fieldInfo.getSig();
                DBObjFieldBuilder baseTrans = RWConstant.getBaseBuilder(desc);
                if (baseTrans != null) {//
                    fieldInfo.setBuilder(baseTrans);
                } else if (sig == null) {//非基础类型非泛型对象只支持DBVO
                    try {
                        Type t = Type.getType(desc);
                        Class<?> clazz = loadDBObjClass(t, desc);
                        fieldInfo.setBuilder(new DBVOBuilder(clazz, Cache.getMappingDBObjClass(t.getInternalName())));
                    } catch (ClassNotFoundException | IOException e) {
                        log.error("", e);
                    }
                } else {//仅支持List, Map, Set三类泛型集合, 内部元素支持基本类型以及DBVO
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
            if (Cache.containsDBObjClass(t.getInternalName()))//已经完成该DBVO对应DBObj加载的情况下直接略过
                return clazz;
            if (!Util.checkDBVO(clazz))
                throw new RuntimeException("not suport type:" + Type.getDescriptor(clazz));
            DBObjChainLoader l = new DBObjChainLoader();
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

            private DBObjFieldBuilder trans;
            private Class voclass = null;
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
                                lt.setDbvoClass(voclass);
                                lt.setDbobjClass(objclass);
                                trans = lt;
                                break;
                            case TYPE_SET:
                                SetBuilder st = new SetBuilder();
                                st.setDbvoClass(voclass);
                                st.setDbobjClass(objclass);
                                trans = st;
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                    case 2: {
                        Class kVOClass = voclass;
                        Class kObjClass = objclass;
                        loadVOAndObjClass(name);
                        Class vVOClass = voclass;
                        Class vObjClass = objclass;
                        MapBuilder mt = new MapBuilder();
                        mt.setKdbvoClass(kVOClass);
                        mt.setKdbobjClass(kObjClass);
                        mt.setVdbvoClass(vVOClass);
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
                        voclass = Class.forName(type.getClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    objclass = voclass;
                } else {
                    try {
                        voclass = loadDBObjClass(type, desc);
                    } catch (ClassNotFoundException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    objclass = Cache.getMappingDBObjClass(name);
                }
            }


            public DBObjFieldBuilder getTrans() {
                return trans;
            }


            public void setTrans(DBObjFieldBuilder trans) {
                this.trans = trans;
            }

        }


    }


}
