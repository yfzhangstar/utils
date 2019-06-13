package com.akoo.common.util.behaviorTree.ai.impl.condition;

import com.akoo.common.util.behaviorTree.ai.common.EStatus;
import com.akoo.common.util.behaviorTree.ai.abs.BaseCondition;
import com.akoo.common.util.behaviorTree.ai.ifs.IBehaviour;

public class ConditionIsHealthLow extends BaseCondition {

  @Override
  public EStatus update() {
    int random = getRandom();
    if (random < 70) {
      System.out.println("Health is low");
      return !isNegation() ? EStatus.Success : EStatus.Failure;
    } else {
      System.out.println("Health is Not low");
      return !isNegation() ? EStatus.Failure : EStatus.Success;
    }

  }

  @Override
  public void addChild(IBehaviour child) {
  }
}
