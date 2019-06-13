package com.akoo.common.util.behaviorTree;

import com.akoo.common.util.behaviorTree.ai.BehaviorTree;
import com.akoo.common.util.behaviorTree.ai.BehaviorTreeBuilder;
import com.akoo.common.util.behaviorTree.ai.impl.action.ActionAttack;
import com.akoo.common.util.behaviorTree.ai.impl.action.ActionPatrol;
import com.akoo.common.util.behaviorTree.ai.impl.action.ActionRunaway;
import com.akoo.common.util.behaviorTree.ai.impl.composite.SelectorImpl;
import com.akoo.common.util.behaviorTree.ai.impl.composite.SequenceImpl;
import com.akoo.common.util.behaviorTree.ai.impl.condition.ConditionIsHealthLow;
import com.akoo.common.util.behaviorTree.ai.impl.condition.ConditionIsSeeEnemy;

public class Main {
  public static void main(String[] args) {
    BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
    BehaviorTree behaviorTree =
          builder.addBehaviour(new SelectorImpl())
            .addBehaviour(new SequenceImpl())
              .addBehaviour(new ConditionIsSeeEnemy())
                .back()
              .addBehaviour(new SelectorImpl())
                .addBehaviour(new SequenceImpl())
                  .addBehaviour(new ConditionIsHealthLow())
                    .back()
                  .addBehaviour(new ActionRunaway())
                    .back()
                  .back()
                .addBehaviour(new ActionAttack())
                  .back()
                .back()
              .back()
            .addBehaviour(new ActionPatrol())
        .end();

    //模拟执行行为树
    for (int i = 0; i < 10; ++i){
      behaviorTree.tick();
      System.out.println("--------------" + i + "------------");
    }

    System.out.println("pause ");
  }
}
