package date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
    public static  String FORMAT="yyyy-MM-dd HH:mm:ss";
	private static void formatDataTest() {
		/*
		 * 日期转期望格式的字符串
		 */
		//HH 和 hh 的差别：前者是24小时制，后者是12小时制。
		StringBuilder sb = new StringBuilder();
		sb.append("yyyy年MM月dd日 HH:mm:ss")
				.append(" 上下午标志 a")
				.append(" E")
				.append(" 一年中的第D天")
				.append(" 一月中的第F个星期")
				.append(" 一年中的第w个星期")
				.append(" 一月中的第W个星期")
				.append(" Z")
				.append(" z");
		SimpleDateFormat sdf = new SimpleDateFormat(sb.toString());
		String dateString = sdf.format(new Date());
		System.out.println(dateString);
		/*
		 * 字符串转日期
		 */
		Date date;
		try {
			date = sdf.parse(dateString);
			System.out.println(date);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void main(String[] args){
		formatDataTest();
	}
}
