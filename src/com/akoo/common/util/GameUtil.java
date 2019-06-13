package com.akoo.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@SuppressWarnings({"unchecked", "rawtypes"})
public class GameUtil{

    public static final Random r = new Random();
    private final static char[] codedigits = {
            '1', '2', '3', '4', '5',
            '6', '7', '8', '9',
            'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',

    };
    private static final char[] KEY_WORDS = "!\"#$%&'()*+,-./:;<=>?@[\\]^`{|}~".toCharArray();
    private static BlockingQueue<List> cacheList = new LinkedBlockingQueue<>(2000);
    private static BlockingQueue<List> cacheLinkedList = new LinkedBlockingQueue<>(2000);
    private static BlockingQueue<Map> cacheMap = new LinkedBlockingQueue<>(2000);

    static {
        Thread listCreateT = new Thread() {
            @Override
            public void run() {
                while(true){
					try {
						cacheList.put(new ArrayList<>());
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        };
        listCreateT.setName("listCreateThread");
        listCreateT.setDaemon(true);//设置为守护线
        listCreateT.start();
        
        Thread linkedListCreateT = new Thread() {
            @Override
            public void run() {
                while(true){
					try {
						cacheLinkedList.put(new LinkedList<>());
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        };
        linkedListCreateT.setName("linkedListCreateThread");
        linkedListCreateT.setDaemon(true);//设置为守护线
        linkedListCreateT.start();
        

		Thread mapCreateT = new Thread(){
			@Override
			public void run() {
				while(true){
					try {
						cacheMap.put(new HashMap());
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        };
        mapCreateT.setName("mapCreateThread");
        mapCreateT.setDaemon(true);
        mapCreateT.start();
    }

    /**
     * 创建并发操作安全的Map
     * 注意此处安全代表该Map的各方法内部安全
     */
    public static <T, K> Map<T, K> createMap(int capacity) {
        return new ConcurrentHashMap<>(capacity);
    }

	public static <T,K> Map<T,K> createMap(){
        return new ConcurrentHashMap<>();
    }

    public static List toList(Map m) {
        List ret = new ArrayList();
        ret.addAll(m.values());
        return ret;
    }

	public static <E> Set<E> createSet(){
		return new HashSet();
	}

    public static <T, K> Map<T, K> createSimpleMap() {
        Map m = cacheMap.poll();
        if (m == null)
            m = new HashMap<T, K>();
        return m;
    }

	/**
	 * 获取大于输入数的 小的 2的N次幂
	 */
	public static int getLeastPowerOf2BiggerThan(int input){
		input = Math.abs(input);
		input |= input>>1;
			input |= input>>2;
			input |= input>>4;
			input |= input>>8;
			input |= input>>16;
			input +=1;
			if(input<0)
				input>>=1;
			return input;
    }

    public static <T> List<T> createList() {
        List a = cacheList.poll();
        if (a == null)
            a = new ArrayList<T>();
        return a;
    }
    
    public static <T> List<T> createLinkedList() {
        List a = cacheLinkedList.poll();
        if (a == null)
            a = new LinkedList<T>();
        return a;
    }

	/**
	 * 交叉填充制定序列
	 */
	public static <T> void  crossFillSequence(List<T> first,List<T> second,List<T> sequence){
		int asize = first.size();
		int dsize = second.size();
		int size = Math.max(asize,dsize);
		for(int i = 0 ; i < size ; i++){
			if(i<asize)
				sequence.add(first.get(i));
			if(i<dsize)
				sequence.add(second.get(i));
        }
    }

    public static int[] getNumAround(int tgt, int delta, int floor, int ceiling) {
        int len = 0;
        int sm;
        int bg;
        boolean smOk;
        boolean bgOk;
        if (smOk = (sm = tgt - delta) > floor)
            len++;
        if (bgOk = (bg = tgt + delta) < ceiling)
            len++;
        int[] ret = new int[len];
        int ix = 0;
        if (smOk)
            ret[ix++] = sm;
        if (bgOk)
            ret[ix] = bg;
        return ret;
    }

	public static String readExInfo(String exInfo,String infoSign){
		if(exInfo==null)
			return "";
		if(!exInfo.matches(".*,"+infoSign+"=.*"))
			return null;
		return exInfo.replaceAll(".*,"+infoSign+"=", "").replaceAll(",.*", "");
    }

	public static boolean haveKeywords(String s) {
        for (char c : KEY_WORDS) {
            int p = s.indexOf(c);
            if (p >= 0)
                return true;
        }
        return false;
    }

	public static int setIntFlag(int src,int index){
		return src|(1<<index);
	}

	public static int resetIntFlag(int src,int index){
		return src&~(1<<index);
	}

	public static boolean checkIntFlag(int src,int index){
		return (src&(1<<index))!= 0;
	}


	public static byte[] zip(byte[] b){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(b);
			gos.finish();
			baos.flush();
			
			byte[] ret = baos.toByteArray();
			baos.close();
			gos.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

	public static byte[] unzip(byte[] b){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			GZIPInputStream gis = new GZIPInputStream(bais);
			do {
				byte[] buff = new byte[1024];
				int len = gis.read(buff);
				if(len <= 0)
					break;
				baos.write(buff, 0, len);
			} while (true);
			baos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
        }
    }

    public static String writeExInfo(String str, String infoSign, Object value) {
        if (str == null)
            str = "";
        return str.startsWith(",") ? str : "," + str + infoSign +
                "=" +
                value +
                ",";
    }

    public static boolean notSameDate(Date d1, Date d2) {
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c.setTimeInMillis(Math.max(0, d1.getTime() - 4 * 3600 * 1000));
        c2.setTimeInMillis(Math.max(0, d2.getTime() - 4 * 3600 * 1000));
        return c.get(Calendar.YEAR) != (c2.get(Calendar.YEAR)) || c.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR);
    }

    public static List<byte[]> splitByteArray(byte[] src, int splitSize) {
        int pos = 0;
        int len = src.length;
        List<byte[]> tempList = GameUtil.createList();
        if (len > splitSize) {
            for (int i = 0; i < len; i += splitSize) {
                byte[] block = new byte[splitSize];
                if((pos+splitSize)<len){
					System.arraycopy(src, pos, block, 0, splitSize);
					tempList.add(block);
                    pos += splitSize;
                }

            }
        }
        if (pos < src.length) {
            int last = len - pos;
            if (last > 0) {
                byte[] block  = new byte[last];
				System.arraycopy(src, pos, block,0, last);
				tempList.add(block);
			}
		}
		return tempList;
	}
	
	public static String getCode(int num){
		char[] code = new char[num];
		for(int i = 0;i<num;i++){
			code[i] = codedigits[r.nextInt(codedigits.length)];
		}
		return new String(code);
	}
	
	public static <T,K> Map<T,K> getOrCreateMapInMap(Map parent, Object sign){
		Map tgtMap = (Map) parent.get(sign);
		if(tgtMap != null)
			return tgtMap;
		else
			parent.put(sign, tgtMap = createSimpleMap());
		return tgtMap;
	}
	
	public static <T> List<T> getOrCreateListInMap(Map parent, Object sign){
		List tgtList = (List) parent.get(sign);
		if(tgtList != null)
			return tgtList;
		else
			parent.put(sign, tgtList = createList());
		return tgtList;
	}
	
}
