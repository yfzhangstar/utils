package com.akoo.common.util.gzip;

import java.util.ArrayList;

public class GZipUtilPool {
	private static final ArrayList<GZipUtil> idleUtil = new ArrayList<GZipUtil>();

	private static final ArrayList<GZipUtil> busyUtil = new ArrayList<GZipUtil>();

	static {
		for (int i = 0; i < 10; i++) {
			GZipUtil sp = new GZipUtil();
			idleUtil.add(sp);
		}
	}

	public static synchronized GZipUtil getUtil() {
		if (idleUtil.size() < 2) {
			for (int i = 0; i < 100; i++) {
				idleUtil.add(new GZipUtil());
			}

		}

		GZipUtil util = (GZipUtil) idleUtil.remove(idleUtil.size() - 1);
		if (util == null) {
			util = new GZipUtil();
		}

		busyUtil.add(util);

		return util;
	}

	public static synchronized void setIdle(GZipUtil util) {
		busyUtil.remove(util);
		idleUtil.add(util);
	}

	public int getIdlesSize() {
		return idleUtil.size();
	}

	public int getBusysSize() {
		return busyUtil.size();
	}

	public static void printStatus() {
		System.out.println("idleUtil:" + idleUtil.size() + " : busyUtil:"
				+ busyUtil.size());
	}
}