package com.akoo.common.util.behaviorTree.tree;
/************************************************************************/
/* 组合结点                                                             */

import java.util.ArrayList;

/************************************************************************/
public interface ICompositeNode extends IBehaviourTreeNode {
  void addNode(IBehaviourTreeNode node);

  void removeNode(IBehaviourTreeNode node);

  boolean hasNode(IBehaviourTreeNode node);

  void addCondition(IConditionNode node);

  void removeCondition(IConditionNode node);

  boolean hasCondition(IConditionNode node);

  ArrayList getNodeList();

  ArrayList getConditionList();
}