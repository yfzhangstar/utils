package com.akoo.common.util.collection;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Trie结点类
 */
public class TrieNode<T> {
    public boolean terminable; // 是不是单词结尾（即叶子节点）
    public int count; // 单词出现频数
    public Map<Character, TrieNode<T>> children = null;

    Set<T> value;

    TrieNode() {
        terminable = false;
        count = 0;
        children = new TreeMap<>();
    }
}
