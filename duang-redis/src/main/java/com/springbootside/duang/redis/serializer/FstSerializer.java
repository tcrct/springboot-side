package com.springbootside.duang.redis.serializer;

import com.springbootside.duang.redis.core.CacheException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * FstSerializer.
 */
public class FstSerializer implements ISerializer {

	private static final Logger LOG = LoggerFactory.getLogger(FstSerializer.class);
	
	public static final ISerializer me = new FstSerializer();
	
	public byte[] keyToBytes(String key) {
		return SafeEncoder.encode(key);
	}

	public String keyFromBytes(byte[] bytes) {
		return SafeEncoder.encode(bytes);
	}

	public byte[] fieldToBytes(Object field) {
		return valueToBytes(field);
	}

    public Object fieldFromBytes(byte[] bytes) {
    	return valueFromBytes(bytes);
    }

	public byte[] valueToBytes(Object value) {
		FSTObjectOutput fstOut = null;
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			fstOut = new FSTObjectOutput(bytesOut);
			fstOut.writeObject(value);
			fstOut.flush();
			return bytesOut.toByteArray();
		}
		catch (Exception e) {
			throw new CacheException("Redis将值序列化成Byte时出错: " + e.getMessage(), e);
		}
		finally {
			if(fstOut != null)
				try {fstOut.close();} catch (IOException e) {LOG.error(e.getMessage(), e);}
		}
	}
	
	public Object valueFromBytes(byte[] bytes) {
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		
		FSTObjectInput fstInput = null;
		try {
			fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return fstInput.readObject();
		}
		catch (Exception e) {
			throw new CacheException("Redis将Byte反序列化时出错: " + e.getMessage(), e);
		}
		finally {
			if(fstInput != null)
				try {fstInput.close();} catch (IOException e) {LOG.error(e.getMessage(), e);}
		}
	}
}



