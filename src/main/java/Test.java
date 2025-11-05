import com.google.common.hash.Hashing;
import org.apache.commons.codec.Charsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Test {

	public static void main(String[] args ){
		List<String> values=new ArrayList<>();
		String version="1.0.0";
		String uid ="10";
		              //TIDALdcP
		String wbappid="TIDALdcP";
		String ticket="boWpwxFs91Q4RNaix8gtlwqt0roRkwpDQ9abDZYYfFUkqGGzo9caz9we5dRgWq3P";
		//             c8417f740a5e268fd2112aa037581a0f
		String  nonce="c8417f740a5e268fd2112aa037581a0f";
		String sign="4DA63DE7359D2336CF992C518F1E79282018FB2A";
		String orderStr="1.0.010TIDALdcPboWpwxFs91Q4RNaix8gtlwqt0roRkwpDQ9abDZYYfFUkqGGzo9caz9we5dRgWq3Pc8417f740a5e268fd2112aa037581a0f";

		//测试case
		wbappid="IDAXXXXX";
		uid="userID19959248596551";
		nonce="kHoSxvLZGxSoFsjxlbzEoUzh5PAnTU7T";
		ticket="XO99Qfxlti9iTVgHAjwvJdAZKN3nMuUhrsPdPlPVKlcyS50N6tlLnfuFBPIucaMS";
		sign="D7606F1741DDCF90757DA924EDCF152A200AC7F0";
		//
		values.add(version);
		values.add(uid);
		values.add(wbappid);
//		values.add(ticket);
		values.add(nonce);
		String javaRet=sign(values,ticket);
		System.out.println("javaRet:"+javaRet);
		if(javaRet.equals(sign)){
			System.out.println("sign 相等");
		}else{
			System.out.println("sign 不相等");
		}
	}

	public static String sign(List<String> values, String ticket) {
		if (values == null) {
			throw new NullPointerException("values is null");
		}
		values.removeAll(Collections.singleton(null));// remove null
		values.add(ticket);
		java.util.Collections.sort(values);
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		System.out.println("sb:"+sb.toString());
		return Hashing.sha1().hashString(sb, Charsets.UTF_8).toString().toUpperCase();
	}
}
