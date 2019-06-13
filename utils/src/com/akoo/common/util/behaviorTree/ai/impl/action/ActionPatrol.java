package com.akoo.common.util.behaviorTree.ai.impl.action;

import com.akoo.common.util.behaviorTree.ai.common.EStatus;
import com.akoo.common.util.behaviorTree.ai.abs.BaseAction;
import com.akoo.common.util.behaviorTree.ai.ifs.IBehaviour;

public class ActionPatrol extends BaseAction {

  @Override
  public EStatus update() {
    System.out.println("ActionPatrol 巡逻");


    return EStatus.Success;
  }

  @Override
  public void addChild(IBehaviour child) {
  }
}
