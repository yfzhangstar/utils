package com.akoo.conc.util;

public class Util {
	
	/**
	 * 返回 大于一个整数的绝对值的最小的2的n次幂
	 * @param input
	 * @return
	 */
	public static int getMinPowerOf2BiggerThan(int input) {
		input = Math.abs(input);
		input |= input >> 1;
		input |= input >> 2;
		input |= input >> 4;
		input |= input >> 8;
		input |= input >> 16;
		input += 1;
		if (input < 0)
			input >>= 1;
		return input;
	}
	
	
	public static int getMinPowerOf2EqualsOrBiggerThan(int input) {
		int temp = Math.abs(input);
		if(0 == (temp-1&temp))
			return temp;
		return getMinPowerOf2BiggerThan(input);
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(getMinPowerOf2EqualsOrBiggerThan(777));
	}
}
