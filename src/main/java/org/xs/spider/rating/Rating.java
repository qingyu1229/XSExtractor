package org.xs.spider.rating;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Rating
{
	public static int doRate(Element element){
		Map<String,Integer> map=new HashMap<String,Integer>();
		int s=0;
		
		Elements br_elements= element.getElementsByTag("br");
		int br_size= br_elements.size();
		
		Elements p_elements= element.getElementsByTag("p");
		int p_size= p_elements.size();
		
		String htmlString= element.html();
		
		map=GetCharsNum.getNum(htmlString);
		
		int chCharacter=map.get("chCharacter");
		int chPunctuationCharacter=map.get("chPunctuationCharacter");
		int otherCharacter=map.get("otherCharacter");
		
		s=br_size* Properties.BRSecore
				+p_size* Properties.PSecore
				+chCharacter* Properties.CNCHARSCORE
				+chPunctuationCharacter* Properties.CNPNCHARSCORE
				+otherCharacter/5;
		
		/*System.out.println("中文个数有--" + chCharacter);
		System.out.println("中文标点个数有--" + chPunctuationCharacter);
		System.out.println("其他字符个数有--" + otherCharacter);*/
		element.attr("secore", String.valueOf(s));
		
		return s;
	}
}
