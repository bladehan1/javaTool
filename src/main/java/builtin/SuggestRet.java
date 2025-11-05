package builtin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

////{
//        //query: "菜菜",
//        //result: [
//        //{
//        //word: "菜菜sayi",
//        //rank: 99908962,
//        //extends: {
//        //    "type":100,
//        //    "detail":{
//        //        "word":"菜菜sayi",
//        //        "id":104784769,
//        //        "v":true
//        //    }
//        //}
//        //}
//        //]
//        //}
@Accessors(chain = true)
@Data
public class SuggestRet {
	public static final int TYPE_USER = 100;
	String query;
	List<SuggestTip> result;

	@Accessors(chain = true)
	@Data
	public static class SuggestTip {
		public static final int TYPE_USER = 100;
		String word;
		float rank;
		@JSONField(name = "extends")
		TipExtends tipExtends;
	}

	@Accessors(chain = true)
	@Data
	public static class TipExtends {
		int type;
		Detail detail;
	}

	@Accessors(chain = true)
	@Data
	public static class Detail {
		int id;
	}
//TODO 编译时，注解包生效
	public static void main(String[] args) {
		SuggestRet.Detail detail = new SuggestRet.Detail().setId(1);
		SuggestRet.TipExtends tipExtends = new SuggestRet.TipExtends()
				.setType(SuggestRet.TYPE_USER)
				.setDetail(detail);
		SuggestRet.SuggestTip suggestTip = new SuggestRet.SuggestTip();
		suggestTip.setRank(1.0f);
		suggestTip.setWord("nickname");
		suggestTip.setTipExtends(tipExtends);
		List<SuggestRet.SuggestTip> suggestTipList = new ArrayList<>();
		suggestTipList.add(suggestTip);
		SuggestRet sr = new SuggestRet().setQuery("nickname");

		sr.setResult(suggestTipList);

		System.out.println(JSON.toJSON(sr));
//        sr.setResult();
	}
}


