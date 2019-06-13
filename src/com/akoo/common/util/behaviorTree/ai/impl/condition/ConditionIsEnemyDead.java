package com.akoo.common.util.behaviorTree.ai.impl.condition;

import com.akoo.common.util.behaviorTree.ai.common.EStatus;
import com.akoo.common.util.behaviorTree.ai.abs.BaseCondition;
import com.akoo.common.util.behaviorTree.ai.ifs.IBehaviour;

public class ConditionIsEnemyDead extends BaseCondition {

  @Override
  public EStatus update() {
    int random = getRandom();
    if (random < 60) {
      System.out.println("Enemy Is Dead");
      return !isNegation() ? EStatus.Success : EStatus.Failure;
    } else {
      System.out.println("Enemy is Not Dead");
      return !isNegation() ? EStatus.Failure : EStatus.Success;
    }

  }

  @Override
  public void addChild(IBehaviour child) {
  }
}
