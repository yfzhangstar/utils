package com.akoo.common.util.convert;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Stack;
import java.util.function.Consumer;

public class DTObjChainLoader extends ClassVisitor implements Opcodes {
    private static Logger log = LoggerFactory.getLogger(DTObjChainLoader.class);
    private String supername;
    private boolean root;
    private Stack<String> loadChain = new Stack<>();
    private Consumer<DTObjInfo> listener;

    public DTObjChainLoader(Consumer<DTObjInfo> listener) {
        super(ASM5);
        this.listener = listener;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        root = superName.equals(RWConstant.OBJ_INTERNAL_NAME);
        supername = superName;
        loadChain.push(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (root) {
            String dbobjSuperClass = null;
            while (!loadChain.isEmpty()) {
                String clsName = loadChain.pop();//使用栈从根部
                try {
                    ClassReader cr = new ClassReader(Type.getObjectType(clsName).getClassName());
                    DTObjClassVisitor unit = new DTObjClassVisitor(clsName, dbobjSuperClass, listener);
                    cr.accept(unit, 0);
                    dbobjSuperClass = unit.getDbobjInfo().getConvertName();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        } else {//当该vo的继承自DBVO的子类 而不是直接继承自DBVO时, 直接对父类进行访问, 直到其直接继承自DBVO, 再开始加载
            try {
                ClassReader cr = new ClassReader(Type.getObjectType(supername).getClassName());
                cr.accept(this, 0);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }


}
