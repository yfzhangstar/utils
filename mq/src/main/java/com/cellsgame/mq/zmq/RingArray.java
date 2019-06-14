package com.cellsgame.mq.zmq;

public class RingArray<T> {   
    Object[] a; //对象数组，队列最多存储a.length-1个对象   
    int front;  //队首下标   
    int rear;   //队尾下标  
    private int size;
    
    public RingArray(){   
        this(10); //调用其它构造方法   
    }   
    
    
    public RingArray(int size){   
        a = new Object[size];
        front = 0;   
        rear = 0;
        setSize(size);
    }
    
    
    
    /**  
     * 将一个对象追加到队列尾部  队列满时队尾覆盖队头, 队头前进一位
     * @param obj 对象  
     * @return
     */  
    public void remove(T obj){
    	if(obj == null)
    		return;
    	for (int i = 0; i < a.length; i++) {
    		if(a[i] == null)
    			continue;
    		if(a[i].equals(obj))
    			a[i] = null;
		}
    } 
    
    /**  
     * 将一个对象追加到队列尾部  队列满时队尾覆盖队头, 队头前进一位
     * @param obj 对象  
     * @return
     */  
    public void reenter(T obj){
    	a[rear]  = obj;
    	rear = (rear+1)%a.length;
    	if(rear == front){
    		front = (front+1)%a.length;   
    	}
    } 
    
    
    /**  
     * 队头的第一个对象出队  
     * @return 出队的对象，队列空时返回null  
     */  
    public T next(){   
        if(rear==front)
        	rear = (rear+1)%a.length;  
        T obj = (T) a[front];   
        front = (front+1)%a.length;
        return obj;   
    }


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}   
   
  
} 