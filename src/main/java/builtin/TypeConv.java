package builtin;

import java.io.UnsupportedEncodingException;

public class TypeConv {
	public static void ByteConv() {
		//byte[] to string
		byte[] bytes = new byte[100];
		for (byte i = 0; i < 100; i++) {
			bytes[i]=i;
		}
		String byteStr= null;
		try {
			byteStr = new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(byteStr);
	}

	public static void main(String[] args) {
		//静态属性
		TypeConv.ByteConv();
		//
	}
}
