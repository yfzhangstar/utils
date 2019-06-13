package com.akoo.common.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 适合于少量修改,大量随机访问的 有序列表
 * @author peterveron
 *
 * @param <T>
 */
public class RankedList<T extends Rankable> implements Iterable<T>{

	private Comparator<? super T> c;
	private ArrayList<T> list;

	
	public RankedList(Comparator<? super T> c){
		this.c = c;
		list = new ArrayList<>(10000);
	}
	
	
	public void add(T tgt){
		int ix = insert(tgt);
		if(ix>=0)
			refresh(ix+1);
	}
	
	
	public int insert(T tgt) {
		int ix = index(tgt);
		if(ix<0){
			ix = -1-ix;
			tgt.setRank(ix);
			list.add(ix, tgt);
			return ix;
		}else
			return -1;
	}

	public boolean contains(T tgt){
		return index(tgt)>=0;
	}

	public int index(T tgt) {
		return Collections.binarySearch(list, tgt, c);
	}


	public T delete(int ix){
		if(ix<0||ix>=size())
			return null;
		T remove = list.remove(ix);
		refresh(ix);
		return remove;
	}
	
	
	public void delete(T tgt){
		int ix = remove(tgt);
		if(ix >= 0)
			refresh(ix);
	}


	public int remove(T tgt) {
		int rank = tgt.getRank();
		if(rank<0||rank>=list.size())
			return -1;
		if(list.get(rank)!=tgt)
			return -1;
		list.remove(rank);
		return tgt.getRank();
	}
	
	public void refresh(int ix) {
		int size = list.size();
		for(int i = ix; i < size; i++){
			list.get(i).setRank(i);;
		}
	}
	
	public List<T> getRange(int start, int end){
		return list.subList(Math.max(start,0), Math.min(end, list.size()));
	}


	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}
	
	public int size(){
		return list.size();
	}
	
	
	public List<T> getList(){
		return list;
	}
	
	
	
	
}
