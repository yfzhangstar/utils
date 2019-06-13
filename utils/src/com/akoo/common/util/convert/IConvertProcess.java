package com.akoo.common.util.convert;

/**
 * @author Aly on  2016-07-12.
 */
public interface IConvertProcess {

    public void toBytes(Object obj, DataHolder holder);

    public void fromBytes(DataHolder holder, Object obj) throws ClassNotFoundException;

}
