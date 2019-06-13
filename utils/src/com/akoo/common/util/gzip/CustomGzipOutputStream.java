package com.akoo.common.util.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class CustomGzipOutputStream extends CustomDeflaterOutputStream {
	protected CRC32 crc = new CRC32();
	//private static final int GZIP_MAGIC = 35615;
	//private static final int TRAILER_SIZE = 8;
	private static final byte[] header = { 31, -117, 8 };

	public CustomGzipOutputStream(OutputStream out, int size)
			throws IOException {
		super(out, new Deflater(-1, true), size);
		this.usesDefaultDeflater = true;
		writeHeader();
		this.crc.reset();
	}

	public CustomGzipOutputStream(OutputStream out) throws IOException {
		this(out, 512);
	}

	public synchronized void write(byte[] buf, int off, int len)
			throws IOException {
		super.write(buf, off, len);
		this.crc.update(buf, off, len);
	}

	public void finish() throws IOException {
		if (!this.def.finished()) {
			this.def.finish();
			while (!this.def.finished()) {
				int len = this.def.deflate(this.buf, 0, this.buf.length);
				if ((this.def.finished()) && (len <= this.buf.length - 8)) {
					writeTrailer(this.buf, len);
					len += 8;
					this.out.write(this.buf, 0, len);
					return;
				}
				if (len > 0) {
					this.out.write(this.buf, 0, len);
				}
			}

			byte[] trailer = new byte[8];
			writeTrailer(trailer, 0);
			this.out.write(trailer);
		}
	}

	private void writeHeader() throws IOException {
		this.out.write(header);
	}

	private void writeTrailer(byte[] buf, int offset) throws IOException {
		writeInt((int) this.crc.getValue(), buf, offset);
		writeInt(this.def.getTotalIn(), buf, offset + 4);
	}

	private void writeInt(int i, byte[] buf, int offset) throws IOException {
		writeShort(i & 0xFFFF, buf, offset);
		writeShort(i >> 16 & 0xFFFF, buf, offset + 2);
	}

	private void writeShort(int s, byte[] buf, int offset) throws IOException {
		buf[offset] = (byte) (s & 0xFF);
		buf[(offset + 1)] = (byte) (s >> 8 & 0xFF);
	}

	public void reset() throws IOException {
		this.def.reset();
		writeHeader();
		this.crc.reset();
	}
}