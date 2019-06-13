package com.akoo.common.util.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GZipUtil {
	private ByteArrayOutputStream gzipos;
	private CustomGzipOutputStream gos;

	public GZipUtil() {
		this.gzipos = new ByteArrayOutputStream();
		try {
			this.gos = new CustomGzipOutputStream(this.gzipos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] gzip(byte[] bs) throws IOException {
		byte[] compressedByte = null;
		try {
			this.gos.write(bs);
			this.gos.finish();
			this.gzipos.flush();
			compressedByte = this.gzipos.toByteArray();
			this.gzipos.reset();
			this.gos.reset();
			this.gos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			GZipUtilPool.setIdle(this);
		}
		return compressedByte;
	}
}