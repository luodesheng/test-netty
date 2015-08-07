package lds.gw.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lds.gw.exception.Parse8583Exception;
import lds.gw.model.Field8583;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description 解析8583
 * @author lds
 * @date 20150307
 *
 */
public class Parse8583 {

	private static final Logger LOG = LoggerFactory.getLogger(Parse8583.class);
	private static final List<Field8583> FIELDS = new ArrayList<Field8583>();
	private static String encoding;


	/**
	 * 初始化8583配置
	 */
	static {
		String basepath = Parse8583.class.getClassLoader().getResource("def8583").getPath();
		String path = basepath + File.separator + "CUPZ_8583.xml";
		LOG.debug("8583配置文件路径[{}]", path);

		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new File(path));
			Element def8583 = document.getRootElement();
			encoding = def8583.attributeValue("encoding");
			encoding = null == encoding ? "UTF-8" : encoding;
			LOG.info("8583报文字符集[{}]" ,encoding);
			@SuppressWarnings("unchecked")
			Iterator<Element> iterator = def8583.elementIterator("field");
			String attr = null;
			int i = 0;
			while (iterator.hasNext()) {
				Element element = iterator.next();
				Field8583 field = new Field8583();
				field.setId(element.attributeValue("id"));
				field.setName(element.attributeValue("name"));
				field.setType(element.attributeValue("type"));
				field.setSourceType(element.attributeValue("sourceType"));
				attr = element.attributeValue("sourceLength");
				field.setSourceLength(null != attr ? Integer.parseInt(attr) : 0);
				attr = element.attributeValue("length");
				field.setLength(null != attr ? Integer.parseInt(attr) : 0);
				field.setAlign(element.attributeValue("align"));
				field.setSourceLengthType(element.attributeValue("sourceLengthType"));
				field.setConvert(element.attributeValue("convert"));
				FIELDS.add(field);
				LOG.info("第{}域定义：{}", i++, field);
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
			throw new Parse8583Exception("8583配置文件解析出错：[{}]", path);
		}
	}

	public static List<Field8583> getFields() {
		return FIELDS;
	}

	/**
	 * @description 生成mac加密数据 8个字节分一组，两两异或，异或结果用于下一组异或，不足8字节，补0x00
	 * @param bytes
	 * @return
	 */
	public static byte[] macValue(byte[] bytes) {
		byte[] temp;
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			temp = null;
			boolean flg = true;
			while (flg) {
				byte[] tmp = new byte[8];
				int count = stream.read(tmp);
				if (count < 8) {
					flg = false;
					for (int i = 0; i < (8 - count); i++)
						tmp[count + i] = 0x00;
				}
				if (null != temp)
					for (int i = 0; i < 8; i++)
						temp[i] = (byte) (tmp[i] ^ temp[i]);
				else
					temp = tmp;
			}
			stream.close();
			return temp;
		} catch (IOException e) {
			throw new Parse8583Exception(e.getMessage());
		}
	}

	/**
	 * @description map对象转为8583字节数组
	 * @param map
	 * @return
	 */
	public static byte[] encode(Map<String, Object> map) {
		StringBuilder bitMapBuilder = new StringBuilder();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Field8583 field = null;

		try {
			// 第0域 消息类型处理
			field = FIELDS.get(0);
			byte[] field0Bytes = encodeValue(field, map.get(field.getName()));

			for (int i = 2; i < FIELDS.size(); i++) {
				field = FIELDS.get(i);
				Object value = map.get(field.getName());
				if (null != value) {
					bitMapBuilder.append("1");
					out.write(encodeValue(field, value));
				} else
					bitMapBuilder.append("0");
			}
			
			byte[] bitBytes = new byte[16];
			if (bitMapBuilder.length() > 64)
				bitMapBuilder.insert(0, "1");// 128域
			else {
				bitMapBuilder.insert(0, "0");
				bitBytes = new byte[8];
			}

			// LOG.info(formatBitMap(bitMapBuilder.toString()));
			LOG.info("位图：[{}]", bitMapBuilder);

			int j = 0;
			for (int i = 0; i < bitMapBuilder.length(); i += 8)
				bitBytes[j++] = ByteUtil.binaryToByte(bitMapBuilder.substring(i, i + 8));
			LOG.info("位图：[{}]", ByteUtil.bytesToHex(bitBytes));

			out.flush();
			byte[] result = out.toByteArray();
			
			out.reset();
			out.write(field0Bytes);
			out.write(bitBytes);
			out.write(result);

			return out.toByteArray();

		} catch (Parse8583Exception e){
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			if (null != field) {
				LOG.error("解析第[{}]域异常", field.getId());
				throw new Parse8583Exception("解析第[" + field.getId() + "]域异常", e);
			}
			throw new Parse8583Exception("8583报文解析出错", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * @description 8583域值转为Byte[]
	 * @param field
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static byte[] encodeValue(Field8583 field, Object value) throws IOException {
		byte[] bytes = null;
		if ("const".equals(field.getType())) {
			if ("byte".equals(field.getSourceType())) {
				if ("hex".equals(field.getConvert()))
					bytes = ByteUtil.hexToBytes((String) value);
				else
					bytes = (byte[]) value;
				if (bytes.length != field.getSourceLength())
					throw new Parse8583Exception("解析第[" + field.getId() + "]域，实际长度" + bytes.length + ",sourceLength=" + field.getSourceLength());
			} else {
				String strValue = ((String) value);
				if (0 < field.getLength() && strValue.length() != field.getLength()) {
					throw new Parse8583Exception("解析第[" + field.getId() + "]域，实际长度" + strValue.length() + ",length=" + field.getLength());
				}
				if ("asciibcd".equals(field.getSourceType())) {
					if ("hex".equals(field.getConvert()))
						bytes = ByteUtil.asciiToBcd(new String(ByteUtil.hexToBytes(strValue), encoding));
					else{
						if(null != field.getAlign()){
							StringBuffer s = new StringBuffer();
							int l = field.getSourceLength() * 2 - strValue.length();
							if("left".equals(field.getAlign())){
								s.append(strValue);
								for(int i = 0; i < l; i++)
									s.append("0");
							} else if("right".equals(field.getAlign())) {
								for(int i = 0; i < l; i++)
									s.append("0");
								s.append(strValue);
							}
							strValue = s.toString();
						}
						bytes = ByteUtil.asciiToBcd(strValue);
					}
				} else if ("ascii".equals(field.getSourceType())) {
					if ("hex".equals(field.getConvert()))
						bytes = ByteUtil.hexToBytes(strValue);
					else
						bytes = strValue.getBytes(encoding);
				}
				if (0 == field.getLength() && bytes.length != field.getSourceLength())
					throw new Parse8583Exception("解析第[" + field.getId() + "]域，实际长度" + bytes.length + ",sourceLength=" + field.getSourceLength());
			}

		} else if (field.getType().startsWith("var")) {
			ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
			String strValue = (String) value;
			String len;
			if (strValue.length() > field.getLength())
				throw new Parse8583Exception("解析第[" + field.getId() + "]域，实际长度" + strValue.length() + ",length=" + field.getLength());
			if ("hex".equals(field.getConvert())) {
				bytes = ByteUtil.hexToBytes(strValue);
				len = String.valueOf(bytes.length);
			} else if ("asciibcd".equals(field.getSourceType())) {
				bytes = ByteUtil.asciiToBcd(strValue);
				len = String.valueOf(strValue.getBytes(encoding).length);
			} else {
				bytes = strValue.getBytes(encoding);
				len = String.valueOf(bytes.length);
			}
			
			tmpOut.reset();
			
			if("var2".equals(field.getType()))
				len = String.valueOf(100 + Integer.parseInt(len)).substring(1);
			else if("var3".equals(field.getType()))
				len = String.valueOf(1000 + Integer.parseInt(len)).substring(1);
			
			if ("bcd".equals(field.getSourceLengthType()))
				tmpOut.write(ByteUtil.decimalToBcd(len));
			else if("ascii".equals(field.getSourceLengthType()))
				tmpOut.write(len.getBytes());
			else
				tmpOut.write(ByteUtil.intToBytes(bytes.length));
			
			tmpOut.write(bytes);

			tmpOut.flush();
			tmpOut.close();
			bytes = tmpOut.toByteArray();
		}

		LOG.info("enocde域[{}],{}=[{}][{}][{}]", field.getId(), field.getName(), bytes.length, value, ByteUtil.bytesToHex(bytes));

		return bytes;
	}

	/**
	 * @description 8583字节数组转为map对象
	 * @param bytes 8583报文字节数组
	 * @return
	 */
	public static Map<String, Object> decode(byte[] bytes) {
		Map<String, Object> map = new HashMap<String, Object>();
		InputStream stream = new ByteArrayInputStream(bytes);
		Field8583 field = null;
		
		try {
			field = FIELDS.get(0);

			// 消息类型
			map.put(field.getName(), decodeValue(field, stream));
			LOG.info("消息类型：[{}]", map.get(field.getName()));

			// 位图处理
			byte[] tempBytes = new byte[8];
			stream.read(tempBytes);
			String bitMapStr = ByteUtil.bytesToBinary(tempBytes);
			String bitMapHex = ByteUtil.bytesToHex(tempBytes);
			if (bitMapStr.startsWith("1")) {
				tempBytes = new byte[8];
				stream.read(tempBytes);
				bitMapStr += ByteUtil.bytesToBinary(tempBytes);
				bitMapHex += ByteUtil.bytesToHex(tempBytes);
			}
			LOG.info("位图：[{}]", bitMapHex);
			LOG.info("位图：[{}]", bitMapStr);
			field = FIELDS.get(1);
			map.put(field.getName(), bitMapHex);

			// 其它域解析
			char[] bitChars = bitMapStr.toCharArray();
			for (int i = 1; i < bitChars.length; i++) {
				if ('1' == bitChars[i]) {
					field = FIELDS.get(i + 1);
					map.put(field.getName(), decodeValue(field, stream));
				}
			}

			return map;
		} catch (Parse8583Exception e){
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			if (null != field) {
				LOG.error("解析第[{}]域异常", field.getId());
				throw new Parse8583Exception("解析第[" + field.getId() + "]域异常", e);
			}
			throw new Parse8583Exception("8583报文解析出错", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * @description 从字节流读取解析出域值
	 * @param field 域定义属性
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static Object decodeValue(Field8583 field, InputStream stream) throws IOException {
		Object value = null;
		byte[] bytes = null;
		int len = 0, rlen = 0;
		if (field.getType().startsWith("var")) {
			if ("var3".equals(field.getType())) {
				if ("bcd".equals(field.getSourceLengthType()))
					bytes = new byte[2];
				else
					bytes = new byte[3];
			} else if ("var2".equals(field.getType())) {
				if ("bcd".equals(field.getSourceLengthType()))
					bytes = new byte[1];
				else
					bytes = new byte[2];
			} else
				throw new Parse8583Exception("解析第[" + field.getId() + "]域，不存在的type=" + field.getType());

			stream.read(bytes);
			if ("bcd".equals(field.getSourceLengthType()))
				len = Integer.parseInt(ByteUtil.bcdToDecimal‎(bytes));
			else if("ascii".equals(field.getSourceLengthType()))
				len = Integer.parseInt(new String(bytes));
			else
				len = ByteUtil.bytesToInt(bytes);
			if ("asciibcd".equals(field.getSourceType()))
				bytes = new byte[len / 2 + (0 != len % 2 ? 1 : 0)];
			else
				bytes = new byte[len];
		} else if ("const".equals(field.getType())) {
			bytes = new byte[field.getSourceLength()];
		} else
			throw new Parse8583Exception("解析第[" + field.getId() + "]域，不存在的type=" + field.getType());

		rlen = stream.read(bytes);

		if ("ascii".equals(field.getSourceType())) {
			if ("hex".equals(field.getConvert()))
				value = ByteUtil.bytesToHex(bytes);
			else
				value = substring(new String(bytes, encoding), field.getAlign(), 0 < len ? len : field.getLength());
		} else if ("byte".equals(field.getSourceType())) {
			if ("hex".equals(field.getConvert()))
				value = ByteUtil.bytesToHex(bytes);
			else
				value = bytes;
		} else if ("asciibcd".equals(field.getSourceType())) {
			String ascii = substring(ByteUtil.bcdToAscii(bytes), field.getAlign(), 0 < len ? len : field.getLength());
			if ("hex".equals(field.getConvert()))
				value = ByteUtil.bytesToHex(ascii.getBytes(encoding));
			else
				value = ascii;
		}
		LOG.info("decode域[{}],{}=[{}][{}][{}]", field.getId(), field.getName(), rlen, value, ByteUtil.bytesToHex(bytes));

		return value;

	}

	/**
	 * @description 解析含补位的字符串域值
	 * @param source
	 * @param align
	 * @param length
	 * @return
	 */
	private static String substring(String source, String align, int length) {
		if (0 < length && null != align) {
			if ("left".equals(align))
				return source.substring(0, length);
			if ("right".equals(align))
				return source.substring(source.length() - length);
		}
		return source;
	}
}
