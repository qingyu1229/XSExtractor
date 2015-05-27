package org.xs.spider.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SmartNextPageFecther {


	public Map<Integer, String> getNextPageUrl(Document doc, String baseurl) {
		Document document = doc.clone();
		Map<Integer, String> map = new HashMap<Integer, String>();

		Elements a_elements = document.getElementsByTag("a");
		for (Element e : a_elements) {
			String uu = e.attr("href");
			uu = UrlTools.getFullUrl(baseurl, uu);
			if (uu == null || uu.trim().isEmpty()) {
				continue;
			}
			String a_text = e.text();
			// 是否是下一页的
			boolean bl = checkText(a_text);

			if (bl) {
				int cu = checkUrl(baseurl, uu);
				if (cu != -1) {
					map.put(cu, uu);
				}
			}
		}

		return map;
	}

	public boolean checkText(String text) {
		String[] texts = { "首页", "第一页", "下一页", "末页", "最后一页", "尾页" };
		for (int i = 0; i < texts.length; i++) {
			if (texts[i].equals(text)) {
				return true;
			}
		}
		if (text.matches("\\d{1,2}")) {
			return true;
		}
		return false;
	}

	public int checkUrl(String url1, String url2) {
		int l1 = url1.length();
		int l2 = url2.length();
		if (l1 == 0 || l2 == 0) {
			return -1;
		}

		String longStr = l1 > l2 ? url1 : url2;
		String shortStr = l1 < l2 ? url1 : url2;
		int j = 0;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < longStr.length() - 1; i++) {
			if (longStr.charAt(i) != shortStr.charAt(j)) {
				sb.append(longStr.charAt(i));
			} else {
				j++;
				if (j == shortStr.length()) {
					break;
				}
			}
		}
		if (sb.length() == 0) {
			return -1;
		}
		String variances = sb.toString();
		String numStr = variances.replaceAll("_", "").replaceAll("=", "")
				.replaceAll("index", "").replaceAll("page", "")
				.replaceAll("p", "").replaceAll("-", "");
		if (numStr.matches("\\d{1,2}")) {
			return Integer.valueOf(numStr);
		} else {
			return -1;
		}
	}
}
