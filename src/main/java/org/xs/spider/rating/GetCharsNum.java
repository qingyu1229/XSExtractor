package org.xs.spider.rating;

import java.util.HashMap;
import java.util.Map;

public class GetCharsNum
{

	public static Map<String, Integer> getNum(String str)
	{
		/** 中文文字字符 */
		int chCharacter = 0;
		/** 中文标点字符 */
		int chPunctuationCharacter = 0;
		/** 其他字符 */
		int otherCharacter = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		str.trim();
		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if (isChinese(chars[i]))
			{
				chCharacter++;
			} else if (isChinesePunctuation(chars[i]))
			{
				chPunctuationCharacter++;
			} else
			{
				otherCharacter++;
				//System.out.println(chars[i]);
			}
		}

		map.put("chCharacter", chCharacter);
		map.put("chPunctuationCharacter", chPunctuationCharacter);
		map.put("otherCharacter", otherCharacter);
		/*
		 * System.out.println("中文个数有--" + chCharacter);
		 * System.out.println("中文标点个数有--" + chPunctuationCharacter);
		 * System.out.println("其他字符个数有--" + otherCharacter);
		 */

		return map;
	}

	/***
	 * 判断字符是否为中文
	 * 
	 * @param ch
	 *            需要判断的字符
	 * @return 中文返回true，非中文返回false
	 */
	private static boolean isChinese(char ch)
	{
		// 获取此字符的UniCodeBlock
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		/*
		 * CJK_UNIFIED_IDEOGRAPHS // 中日韩统一表意文字 CJK_COMPATIBILITY_IDEOGRAPHS //
		 * 中日韩兼容字符 CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A // 中日韩统一表意文字扩充A
		 * GENERAL_PUNCTUATION // 一般标点符号, 判断中文的“号 CJK_SYMBOLS_AND_PUNCTUATION //
		 * 符号和标点, 判断中文的。号 HALFWIDTH_AND_FULLWIDTH_FORMS // 半角及全角字符, 判断中文的，号
		 */
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
		{
			return true;
		}
		return false;
	}

	/***
	 * 判断字符是否为中文标点
	 * 
	 * @param ch
	 *            需要判断的字符
	 * @return 中文返回true，非中文返回false
	 */
	private static boolean isChinesePunctuation(char ch)
	{
		// 获取此字符的UniCodeBlock
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		/*
		 * CJK_UNIFIED_IDEOGRAPHS // 中日韩统一表意文字 CJK_COMPATIBILITY_IDEOGRAPHS //
		 * 中日韩兼容字符 CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A // 中日韩统一表意文字扩充A
		 * GENERAL_PUNCTUATION // 一般标点符号, 判断中文的“号 CJK_SYMBOLS_AND_PUNCTUATION //
		 * 符号和标点, 判断中文的。号 HALFWIDTH_AND_FULLWIDTH_FORMS // 半角及全角字符, 判断中文的，号
		 */
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
		{
			return true;
		}
		return false;
	}
}
