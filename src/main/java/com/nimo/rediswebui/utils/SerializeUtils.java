package com.nimo.rediswebui.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.lang.Nullable;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SerializeUtils {
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {

		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
//		ByteArrayInputStream bais = null;
//		try {
//			// 反序列化
//			bais = new ByteArrayInputStream(bytes);
//			ObjectInputStream ois = new ObjectInputStream(bais);
//			return ois.readObject();
//		} catch (StreamCorruptedException xe) {
//			xe.printStackTrace();
//			return new String(bytes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return new String(bytes);
	}

	private final Converter<Object, byte[]> serializer =new SerializingConverter();
	private final Converter<byte[], Object> deserializer=new DeserializingConverter();


	public static Object deserialize(@Nullable byte[] bytes) {
		try {
			return new DeserializingConverter().convert(bytes);
		} catch (Exception e) {
			try {
				log.info("普通 {}",String.valueOf(bytes));
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		return String.valueOf(bytes);
	}

	public static byte[]  serialize(String key) {
		try {
			return new SerializingConverter().convert(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	public static String byteToHex(byte[] bytes){
		String strHex = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < bytes.length; n++) {
			strHex = Integer.toHexString(bytes[n] & 0xFF);
			sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
		}
		return sb.toString().trim();
	}

	public static String str2Hex(byte[] bytes) throws UnsupportedEncodingException {
		String hexRaw = String.format("%x", new BigInteger(1, bytes));
		char[] hexRawArr = hexRaw.toCharArray();
		StringBuilder hexFmtStr = new StringBuilder();
		final String SEP = "\\x";
		for (int i = 0; i < hexRawArr.length; i++) {
			hexFmtStr.append(SEP).append(hexRawArr[i]).append(hexRawArr[++i]);
		}
		return hexFmtStr.toString();
	}

	public static String hex2Str(String str) throws UnsupportedEncodingException {
		String strArr[] = str.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
		byte[] byteArr = new byte[strArr.length - 1];
		for (int i = 1; i < strArr.length; i++) {
			Integer hexInt = Integer.decode("0" + strArr[i]);
			System.out.println(hexInt);
			byteArr[i - 1] = hexInt.byteValue();
		}

		return new String(byteArr, "UTF-8");
	}



	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF"; //16进制能用到的所有字符 0-15
		char[] hexs = hexStr.toCharArray();//toCharArray() 方法将字符串转换为字符数组。
		int length = (hexStr.length() / 2);//1个byte数值 -> 两个16进制字符
		byte[] bytes = new byte[length];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			int position = i * 2;//两个16进制字符 -> 1个byte数值
			n = str.indexOf(hexs[position]) * 16;
			n += str.indexOf(hexs[position + 1]);
			// 保持二进制补码的一致性 因为byte类型字符是8bit的  而int为32bit 会自动补齐高位1  所以与上0xFF之后可以保持高位一致性
			//当byte要转化为int的时候，高的24位必然会补1，这样，其二进制补码其实已经不一致了，&0xff可以将高的24位置为0，低8位保持原样，这样做的目的就是为了保证二进制数据的一致性。
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}


	public static String test(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			int byteAscii1 = (int) buf[i];
//			System.out.println(byteAscii1);
			System.out.println(Integer.toHexString(buf[i] & 0xFF));
			System.out.println(Integer.toUnsignedString(buf[i] & 0xFF));
			char ch1 = (char)byteAscii1;
			sb.append(ch1);
		}
		return sb.toString();
	}


	public static void main(String[] args) throws UnsupportedEncodingException {
		String key="sscc";
		byte[] s=SerializeUtils.serialize(key);


		System.out.println(SerializeUtils.test(s));

	}


}
