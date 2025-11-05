package base;

public class TypeConv {
	public static byte[] stringToByteArr(String str){
		return 	str.getBytes();
	}
	public static String byteArrToString(byte[] bytes){
		return new String(bytes);
	}

	public static  void main(String[] args){
		String helloWorld="HelloWorld";
		String test =byteArrToString(stringToByteArr(helloWorld));
		System.out.println(test);
		if(helloWorld.equals(byteArrToString(stringToByteArr(helloWorld)))){
			System.out.println("equal");
		}else{
			System.out.println("notEqual");
		}
	}
}
