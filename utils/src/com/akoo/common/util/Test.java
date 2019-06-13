package com.akoo.common.util;

import com.akoo.common.util.convert.*;

public class Test {

    public class Process implements IConvertProcess {

        public void toBytes(Object var1, DataHolder var2) {
            Demo var3 = (Demo)var1;
            DataHolder buffer = DBBuffer.allocate();
            buffer.putInt(var3.getAaa());
            buffer.putLong(var3.getBbb());
//            buffer.putString(Util.classObject2String(var3.getDemo2()));
//            Util.fillObjectToHolder(var3.getDemo2(), buffer);
            var2.putBytes(buffer.toBytes());
        }

        public void fromBytes(DataHolder var1, Object var2) throws ClassNotFoundException {
            Demo var3 = (Demo)var2;
            DataHolder buffer = DBBuffer.warp(var1.getBytes());
            var3.setAaa(buffer.getInt());
            var3.setBbb(buffer.getLong());
//            Class type = Util.classString2Object(buffer.getString());
//            DemoParent var4 = (DemoParent)Util.getObjectFrmHolder(buffer, type);
//            var3.setDemo2(var4);
        }
    }

}
