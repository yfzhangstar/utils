package com.akoo.common.util.csv;

/**
 * @author Aly on  2016-07-14.
 *         <p>
 *         WARN  所有配置的Set方法都应该设置成private 防止被修改 或者错误修改
 */
public class BaseCfg {
    private int id;

    @Override
    public String toString() {
        return this.getClass().getName() + " [id=" + id + "]";
    }

    public int getId() {
        return id;
    }

    // 设置成private 防止修改
    private void setId(int id) {
        this.id = id;
    }
}
