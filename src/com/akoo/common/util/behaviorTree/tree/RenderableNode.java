package com.akoo.common.util.behaviorTree.tree;

import java.awt.Rectangle;

public class RenderableNode {
  public RenderableNode parent;
  public IBehaviourTreeNode targetNode;
  public Rectangle posRect = new Rectangle();
  public String name;
  public int layer;
  public RunStatus staus;

  @Override
  public String toString() {
    return name + "\n" + staus.toString();
  }

  public void Render() {
    boolean running = staus == RunStatus.Running;
    Rectangle rect = posRect;
    rect.y -= (posRect.height / 2);

//    var oldColor = GUI.color;
//    if (running) {
//      GUI.color = Color.green;
//    }
//    GUI.Box(rect, ToString());
//    GUI.color = oldColor;
//
//    if (parent == null && targetNode != null && targetNode.parent != null) {
//      parent = targetNode.parent.renderNode;
//    }
//    if (parent != null) {
//      Vector2 parentPos = new Vector2();
//      parentPos.x = parent.posRect.x + parent.posRect.width;
//      parentPos.y = parent.posRect.y;
//      GUIHelper
//              .DrawLine(new Vector2(rect.x, rect.y + rect.height / 2), parentPos, running ? Color.green : Color.yellow);
//    }

  }
} 