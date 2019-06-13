package com.akoo.common.util.behaviorTree.ai.impl.action;

import com.akoo.common.util.behaviorTree.ai.common.EStatus;
import com.akoo.common.util.behaviorTree.ai.abs.BaseAction;
import com.akoo.common.util.behaviorTree.ai.ifs.IBehaviour;

public class ActionRunaway extends BaseAction {

  @Override
  public EStatus update() {
    System.out.println("ActionRunaway 跑路");
    return EStatus.Success;
  }

  @Override
  public void addChild(IBehaviour child) {
  }
}
