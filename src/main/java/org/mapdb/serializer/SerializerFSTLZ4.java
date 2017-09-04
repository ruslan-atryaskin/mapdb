package org.mapdb.serializer;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SerializerFSTLZ4<T> extends Serializer<T> {

	private static final int SIZE_OF_INT = 4;

	private FSTConfiguration conf;
    private LZ4Factory factory;
	private LZ4Compressor compressor;
	private LZ4FastDecompressor decompressor;

	private int sizeLimitBeforeCompression;

    public SerializerFSTLZ4(int sizeLimitBeforeCompression) {
        this.sizeLimitBeforeCompression = sizeLimitBeforeCompression;
        conf = FSTConfiguration.createDefaultConfiguration();
        factory = LZ4Factory.fastestInstance();
        compressor = factory.fastCompressor();
        decompressor = factory.fastDecompressor();
    }
    public SerializerFSTLZ4(int sizeLimitBeforeCompression, int compressionLevel) {
        this.sizeLimitBeforeCompression = sizeLimitBeforeCompression;
        conf = FSTConfiguration.createDefaultConfiguration();
        factory = LZ4Factory.fastestInstance();
        compressor = factory.highCompressor(compressionLevel);
        decompressor = factory.fastDecompressor();
    }

	public byte[] object2Bytes(T obj) {
		byte[] data = conf.asByteArray(obj);
		if (data.length < sizeLimitBeforeCompression) {
			return createNonCompressedByteArray(data);
		}
		final int decompressedLength = data.length;
		int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
		byte[] compressed = new byte[maxCompressedLength];
		int compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
		if (compressedLength + SIZE_OF_INT >= decompressedLength) {
			return createNonCompressedByteArray(data);
		}
		return createCompressedByteArray(decompressedLength, compressed, compressedLength);
	}

	private static byte[] createCompressedByteArray(final int decompressedLength, byte[] compressed, int compressedLength) {
		ByteBuffer buffer = ByteBuffer.allocate(compressedLength + SIZE_OF_INT);
		buffer.putInt(decompressedLength);
		buffer.put(Arrays.copyOf(compressed, compressedLength));
		return buffer.array();
	}

	private static byte[] createNonCompressedByteArray(byte[] data) {
		ByteBuffer buffer = ByteBuffer.allocate(data.length + SIZE_OF_INT);
		buffer.putInt(-data.length);
		buffer.put(data);
		return buffer.array();
	}

	@SuppressWarnings("unchecked")
	public T bytes2Object(byte[] objBytes) {
		ByteBuffer buffer = ByteBuffer.wrap(objBytes);
		int decompressedLength = buffer.getInt();
		if (decompressedLength < 0) {
			return (T) conf.asObject(Arrays.copyOfRange(objBytes, SIZE_OF_INT, SIZE_OF_INT - decompressedLength));
		}
		byte[] restored = new byte[decompressedLength];
		decompressor.decompress(Arrays.copyOfRange(objBytes, SIZE_OF_INT, SIZE_OF_INT + decompressedLength), 0, restored, 0, decompressedLength);
		return (T) conf.asObject(restored);
	}

	@Override
	public void serialize(DataOutput2 out, T value) throws IOException {
		byte[] objBytes = object2Bytes(value);
		out.writeInt(objBytes.length);
		if (objBytes.length > 0) {
			out.write(objBytes, 0, objBytes.length);
		}
	}

	@Override
	public T deserialize(DataInput2 input, int available) throws IOException {
		int len = input.readInt();
		if (len > 0) {
			byte[] objBytes = new byte[len];
			input.readFully(objBytes, 0, len);
			return bytes2Object(objBytes);
		}
		return null;
	}

}
