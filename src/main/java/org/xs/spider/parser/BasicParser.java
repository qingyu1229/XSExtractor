package org.xs.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xs.spider.rating.Rating;

import java.util.List;

/**
 * Created by Administrator on 15-5-27.
 */
public class BasicParser implements Parser {

    @Override
    public Document denoiseForDoc(Document document) {
        document.getElementsByTag("script").remove();
        document.getElementsByTag("style").remove();
        document.getElementsByTag("link").remove();
        document.getElementsByTag("img").remove();
        document.getElementsByTag("input").remove();
        document.getElementsByTag("object").remove();
        document.getElementsByTag("textarea").remove();
        document.getElementsByTag("a").attr("href", "javascript:void(0)").remove();
        document.getElementsByAttributeValue("display", "none").remove();
        document.getElementsByAttributeValueContaining("style", "display:none");
        document.getElementsByAttributeValueContaining("style", "overflow: hidden");
        return document;
    }

    @Override
    public Element excavateContent(Document document) {
        Element body = document.body();
        doScoreToElement(body);
        Element contentElement = getMaxScoreChild(body);
        return contentElement.parent();
    }

    @Override
    public void denioseForContentElement(Element contentElement) {

    }

    @Override
    public void downloadImg(Element contentElement) {

    }

    @Override
    public String removeNeedlessChars(String contentStr) {
        String stockCodes[] = new String[]{
                "<!--.[^-]*(?=-->)-->",
                "(?is)<!--.*?-->",
                "摘自\\w{2,5}网", "【[\\u4e00-\\u9fa5]{2,6}网】",
                "\\(([^\\(]*)?\\d{5,6}([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?简称([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?微博([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?基金吧([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?股吧([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?代码([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?记者([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?编辑([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?作者([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?点击([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?访问([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?www\\.([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?http://([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?来源([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?标题([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?微信([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?收盘价([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?客户端([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?交易所([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?行情([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?评论([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?声明([^\\(|\\)]*)?\\)",
                "\\(([^\\(]*)?版权([^\\(|\\)]*)?\\)",
        };
        for (String string : stockCodes) {
            contentStr = contentStr.replaceAll(string, "");
        }
        return contentStr;
    }

    @Override
    public String removeTails(String contentStr) {
        String[] tails = new String[]{"【免责声明", "【版权声明", "【重点推荐", "【延伸阅读",
                "【推荐阅读", "【相关阅读", "免责声明", "版权声明", "【更多详情", "【相关专题"};
        for (String string : tails) {
            int index = contentStr.indexOf(string);
            if (index > 0) {
                contentStr = contentStr.substring(0, index);
            }
        }
        return contentStr;
    }

    @Override
    public Element format(String contentStr) {
        Document doc= Jsoup.parse(contentStr);
        return doc.body();
    }

    @Override
    public String getContent(Document document) {
        return getContentEle(document).toString();
    }

    @Override
    public Element getContentEle(Document document) {
        denoiseForDoc(document);
        Element element = excavateContent(document);
        if (element == null) {
            return null;
        }
        denioseForContentElement(element);
        downloadImg(element);
        String contentStr1 = removeNeedlessChars(element.toString());
        String contentStr2 = removeTails(contentStr1);
        Element contentEle = format(contentStr2);
        return contentEle;
    }

    @Override
    public String getContentPath() {
        return null;
    }

    @Override
    public String getContentText(Document document) {
        return getContentEle(document).text();
    }

    private int doScoreToElement(Element element) {
        boolean hasText = element.hasText();
        if (!hasText) {//如果节点没有内容
            element.attr("score", "0");
            return 0;
        } else {
            Elements children = element.children();
            if (children.size() == 0) {//不含有子节点
                return Rating.doRate(element);
            } else {//含有子节点
                int accum = 0;
                for (Element child : children) {
                    accum += doScoreToElement(child);
                }
                element.attr("score", String.valueOf(accum));
                return accum;
            }
        }
    }


    public StringBuffer checkElementPath(Element element, StringBuffer strAccum) {
        Element maxScoreElement = getMaxScoreChild(element);
        if (maxScoreElement == null) {
            return strAccum;
        } else {
            String tagName = maxScoreElement.tagName();
            boolean hasIdAttr = maxScoreElement.hasAttr("id");//是否含有id属性
            boolean hashClassAttr = maxScoreElement.hasAttr("class");//是否含有class属性
            strAccum.append(tagName);
            if (hasIdAttr) {//优先考虑id属性
                strAccum.append("#" + maxScoreElement.attr("id"));
                return strAccum;
            } else if (hashClassAttr) {//如果包含class属性
                String classAttr = maxScoreElement.attr("class").trim().replace(" ", ".");
                strAccum.append("." + classAttr);
                if (element.getElementsByClass(classAttr).size() == 1) {
                    return strAccum;
                }
            }
            strAccum.append(">");
            return checkElementPath(maxScoreElement, strAccum);
        }
    }

    public Element getMaxScoreChild(Element element) {
        if (element.childNodeSize() == 0) {
            return null;
        }
        Elements children = element.children();
        Element maxScoreElement = null;
        int score = 0;
        for (Element e : children) {
            String strScore = e.attr("score");
            if (strScore == null) {
                continue;
            }
            if (Integer.valueOf(strScore) > score) {
                maxScoreElement = e;
                score = Integer.valueOf(strScore);
            }
        }
        return maxScoreElement;
    }

}
