package com.akoo.common.util.behaviorTree.tree;


import com.akoo.common.util.behaviorTree.ai.common.EStatus;

/**
 * Behavior接口是所有行为树节点的核心，且我规定所有节点的构造和析构方法都必须是protected，以防止在栈上创建对象，
 * 所有的节点对象通过Create()静态方法在堆上创建，通过Release()方法销毁
 * 由于Behavior是个抽象接口，故没有提供Create()方法，本接口满足如下契约
 * 在Update方法被首次调用前，调用一次OnInitialize函数，负责初始化等操作
 * Update（）方法在行为树每次更新时调用且仅调用一次。
 * 当行为不再处于运行状态时，调用一次OnTerminate（），并根据返回状态不同执行不同的逻辑
 */
public abstract class Behavior2 {

  //创建对象请调用Create()释放对象请调用Release()
  protected Behavior2() {
    setStatus(EStatus.Invalid);
  }

  //释放对象所占资源
  public abstract void release();

  //包装函数，防止打破调用契约
  public abstract EStatus tick();

  public abstract void addChild(Behavior2 child);

  protected abstract EStatus update();

  protected void onInitialize() {
  }

  protected void onTerminate(EStatus Status) {
  }

  protected EStatus status;

  public void setStatus(EStatus status) {
    this.status = status;
  }

  public EStatus getStatus() {
    return status;
  }
};