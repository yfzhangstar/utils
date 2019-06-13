package com.akoo.common.util.collection;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.akoo.common.util.StringUtil;


/**
 * Tries数据结构（字典树） 这里只是最基本的实现，可判断某个单词是否出现过，单词出现频数等，根据需要可做其它扩展 <br>
 * 单词不区分大小写 ，全半角
 */
public class Trie<T> {

    private TrieNode<T> root;
    private boolean ignoreCase;                     // 忽略大小写
    private boolean ignoreWidth;                    // 忽略全半角
    private CharFilter wordCharFilter;              // 字符过滤器

    public Trie() {
        this(true, true, null);
    }

    public Trie(CharFilter wordCharFilter) {
        this(true, true, wordCharFilter);
    }

    public Trie(boolean ignoreCase, boolean ignoreWidth, CharFilter wordCharFilter) {
        this.ignoreCase = ignoreCase;
        this.ignoreWidth = ignoreWidth;
        this.root = new TrieNode<>();
        this.wordCharFilter = wordCharFilter;
    }

    public TrieNode<T> getRoot() {
        return root;
    }

    /**
     * 插入字符串 // val 不会替换
     * <p>
     * 当前单词中包含的所有数据
     */
    public void put(String word, T val) {
        TrieNode<T> node = root;
        word = word.trim();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (null != wordCharFilter && wordCharFilter.isFilter(ch)) {
                continue;
            }
            char chg = chgChar(ch);
            if (! (node.children.containsKey(chg))) {
                node.children.put(chg, new TrieNode<>());
            }
            node = node.children.get(chg);
        }
        node.terminable = true;
        node.count++;
        if (null != val) {
            if (null == node.value)
                node.value = new HashSet<>();
            node.value.add(val);
        }
    }

    /**
     * 转换大小写 转换全角为半角
     */
    private char chgChar(char c) {
        char chg = c;
        if (ignoreCase)
            chg = Character.toLowerCase(c);
        if (ignoreWidth)
            chg = StringUtil.toDBC(chg);
        return chg;
    }

    /**
     * 查找某个字符串 是否包含Key
     */
    public boolean anyKeyIn(String word) {
        for (int i = 0; i < word.length(); i++) {
            boolean have = anyKeyIn(word, i);
            if (have) return true;
        }
        return false;
    }

    /**
     * 查找某个字符串 是否包含Key
     */
    private boolean anyKeyIn(String word, int start) {
        TrieNode<T> node = root;
        for (int i = start; i < word.length(); i++) {
            char chg = chgChar(word.charAt(i));
            node = node.children.get(chg);
            if (null == node) {
                return false;
            }
        }
        return node.terminable; // 即便该字符串在Trie路径中，也不能说明该单词已存在，因为它有可能是某个子串
    }

    /**
     * 查找某个字符串
     */
    public boolean containsKey(String word) {
        TrieNode<T> node = root;
        for (int i = 0; i < word.length(); i++) {
            char chg = chgChar(word.charAt(i));
            if (! (node.children.containsKey(chg))) {
                return false;
            } else {
                node = node.children.get(chg);
            }
        }
        return node.terminable; // 即便该字符串在Trie路径中，也不能说明该单词已存在，因为它有可能是某个子串
    }

    /**
     * @param limit 结果返回最大数量
     */
    public List<T> keyStartWith(String word, int limit, Filter<T> filter) {
        TrieNode<T> node = root;
        List<T> child = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (null != wordCharFilter && wordCharFilter.isFilter(ch)) {
                continue;
            }
            char chg = chgChar(ch);
            if (! (node.children.containsKey(chg))) {
                return child;
            } else {
                node = node.children.get(chg);
            }
        }

        getNoteValues(node, child, limit, filter);
        return child; // 即便该字符串在Trie路径中，也不能说明该单词已存在，因为它有可能是某个子串
    }

    private void getNoteValues(TrieNode<T> node, List<T> child, int limit, Filter<T> filter) {
        if (null != node.value) {
            int vsize = node.value.size();
            if (vsize > 0) {
                int csize = child.size();
                int del = limit - csize;
                Iterator<T> it = node.value.iterator();

                for (int i = 0, max = node.value.size(); i < del && i < max; i++) {
                    T v = it.next();
                    if (null == filter || ! filter.isFilter(v)) {
                        child.add(v);
                    }
                }
            }
        }
        if (null != node.children) {
            for (TrieNode<T> cnode : node.children.values()) {
                getNoteValues(cnode, child, limit, filter);
                if (child.size() >= limit) {
                    return;
                }
            }
        }
    }

    public List<T> remove(String word) {
        return remove(word, null);
    }

    /**
     * 删除某个字符串（必须是个单词，而不是前缀）
     *
     * @param val 如果指定Value 只会删除指定value的值
     *            如果没有指定 那么默认删除该单词下所有的值
     */
    public List<T> remove(String word, T val) {
        List<T> values = new ArrayList<>();
        if (! containsKey(word)) {
            return values;
        }
        TrieNode<T> node = root;
        deleteStr(node, word, 0, word.length(), values, val);
        return values;
    }

    public boolean deleteStr(TrieNode<T> node, String word, int charAt, int limit, List<T> values, T val) {
        if (wordCharFilter != null) {
            for (int i = charAt; i < limit; i++) {
                if (! wordCharFilter.isFilter(word.charAt(i))) {
                    charAt = i;
                    break;
                }
            }
        }
        if (charAt >= word.length()) {
            values.addAll(node.value);
            if (null == val) {
                node.value.clear();
            } else
                node.value.remove(val);
            if (node.value.size() == 0)
                node.terminable = false; // 不要忘了这里信息的更新
            return node.children.isEmpty();
        }

        char firstChar = chgChar(word.charAt(charAt));
        TrieNode<T> child = node.children.get(firstChar);
        if (null != child) {
            if (deleteStr(child, word, charAt + 1, limit, values, val)) {
                node.children.remove(firstChar);
                if (node.children.isEmpty() && ! node.terminable) { // 注意第二个条件，假如有"abcd"与"abc",删除abcd时，要判断中间路径上是不是另一个子串的结束
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 以字典序输出Tire中所有出现的单词及频数
     */
    public void traverse() {
        StringBuffer word = new StringBuffer("");
        TrieNode<T> node = root;
        traverseTrie(node, word);
    }

    public void traverseTrie(TrieNode<T> node, StringBuffer word) {
        if (node.terminable) {
            System.out.println(word + "------" + node.count);
            if (node.children.isEmpty())
                return;
        }
        for (Entry<Character, TrieNode<T>> en : node.children.entrySet()) {
            char key = en.getKey();
            traverseTrie(node.children.get(key), word.append(key));
            word.deleteCharAt(word.length() - 1);
        }
    }
}
