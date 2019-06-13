package com.akoo.common.util.behaviorTree.tree;

public interface IBehaviourTreeNode {

  IBehaviourTreeNode Clone();

  public RunStatus getStatus() ;

  public void setStatus(RunStatus status);

  public String getNodeName() ;

  public void setNodeName(String nodeName);

  public RenderableNode getRenderNode();

  public void setRenderNode(RenderableNode renderNode);

  public IBehaviourTreeNode getParent();

  public void setParent(IBehaviourTreeNode parent);
}