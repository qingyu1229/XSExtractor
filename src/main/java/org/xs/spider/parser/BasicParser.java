package org.xs.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xs.spider.Fetcher.BasicImgFetcher;
import org.xs.spider.Fetcher.ImgFetcher;
import org.xs.spider.rating.Rating;

import java.util.List;

/**
 * Created by Administrator on 15-5-27.
 * 默认的通用解析器
 */
public class BasicParser implements Parser {

    /**
     * 移除Document中的杂质
     *
     * @param document
     * @return
     */
    @Override
    public Document denoiseForDoc(Document document) {
        document.getElementsByTag("script").remove();
        document.getElementsByTag("style").remove();
        document.getElementsByTag("select").remove();
        document.getElementsByTag("link").remove();
        document.getElementsByTag("input").remove();
        document.getElementsByTag("object").remove();
        document.getElementsByTag("textarea").remove();
        document.getElementsByTag("a").attr("href", "javascript:void(0)").remove();
        document.getElementsByAttributeValue("display", "none").remove();
        document.getElementsByAttributeValueContaining("style", "display:none").remove();
        document.getElementsByAttributeValueContaining("style", "overflow: hidden").remove();
        return document;
    }

    /**
     * 定位正文部分的Element
     *
     * @param document
     * @return
     */
    @Override
    public Element excavateContent(Document document) {
        Element body = document.body();
        doScoreToElement(body);
        Element maxScoreElement = getMaxScoreChild(body);

        StringBuffer buffer=new StringBuffer();
        StringBuffer pathBuffer= checkPath(maxScoreElement,buffer,document);
        String path=pathBuffer.toString();

        if(path.contains(">p>")){
            path= path.split(">p>")[0];
        }

        if(path.endsWith(">")){
            path=path.substring(0, path.length()-1);
        }
        if(path.endsWith(">p")){
            path=path.substring(0, path.length()-2);
        }
        //System.out.println(path);
        Element contentElement=body.select(path).first();
        return contentElement;
    }

    /**
     * 从定位到的正文Element中进行再次去噪
     *
     * @param contentElement
     */
    @Override
    public void denioseForContentElement(Element contentElement) {

    }

    /**
     * 下载图片（由于需要相应的Fetcher，目前尚未实现）
     *
     * @param contentElement
     */
    @Override
    public void downloadImg(Element contentElement) {
        Elements imgElements = contentElement.getElementsByTag("img");
        if (imgElements == null) {
            return;
        }
        ImgFetcher fetcher = new BasicImgFetcher();
        for (Element element : imgElements) {
            String url = element.attr("src");
            String localImgUrl = fetcher.fetch(url);
            element.attr("src", localImgUrl);
        }
    }

    /**
     * 将正文的Element转换为String类型后，使用正则进行再次降噪，移除正文部分通常不需要的注释、来源网站、作者、版权声明等
     *
     * @param contentStr
     * @return
     */
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

    /**
     * 移除文章末尾的免责声明、推荐阅读等内容
     *
     * @param contentStr
     * @return
     */
    @Override
    public String removeTails(String contentStr) {
        String[] tails = new String[]{"【免责声明", "【版权声明", "【重点推荐", "【延伸阅读",
                "【推荐阅读", "【相关阅读", "免责声明", "版权声明", "【更多详情", "【相关专题","上一篇：","下一篇："};
        for (String string : tails) {
            int index = contentStr.indexOf(string);
            if (index > 0) {
                contentStr = contentStr.substring(0, index);
            }
        }
        return contentStr;
    }

    /**
     * 由于前两步的操作可能破坏Html标签
     * 这一步操作将对正文进行格式化为标准的Html片段
     *
     * @param contentStr
     * @return
     */
    @Override
    public Element format(String contentStr) {
        Document doc = Jsoup.parse(contentStr);
        return doc.body();
    }

    @Override
    public String getContent(Document document) {
        String contentStr=getContentEle(document).toString();
        contentStr= contentStr.replace("<body>","");
        contentStr= contentStr.replace("</body>","");
        return contentStr;
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
        Elements children = element.children();
        if (children.size() == 0) {//不含有子节点
            return Rating.doRate(element);
        } else {//含有子节点
            int accum = Rating.doOwnTextRate(element);
            for (Element child : children) {
                accum += doScoreToElement(child);
            }
            element.attr("score", String.valueOf(accum));
            return accum;
        }
    }

    private StringBuffer checkPath(Element element, StringBuffer accum,Document document) {
        if (element == null) {
            return accum;
        }
        if(element.parent()==null){
            return accum;
        }

        if (element.parent() != null) {
            Element parentElement = element.parent();
            String tagStr = parentElement.tagName();
            // 如果能够找到带有ID属性的父节点就停止查找
            if (parentElement.hasAttr("id")) {
                accum.insert(0, tagStr + "#" + parentElement.attr("id") + ">");
            } else if (parentElement.hasAttr("class")) {
                String classStr =parentElement.attr("class").trim().replace(" ", ".");
                //System.out.println("accum:"+accum);
                /**
                 * 不考虑P标签的class属性
                 */
                if("p".equals(tagStr)){
                    classStr="";
                }

                if(!"".equals(classStr)){
                    accum.insert(0, tagStr +"."+ classStr +   ">");
                }else{
                    accum.insert(0, tagStr + ">");
                }

                /**
                 * 判断class是否唯一
                 * 如果是P标签，则往上查找
                 */
                if ("p".equals(tagStr)||document.getElementsByClass(classStr).size() > 1) {
                    accum= checkPath(element.parent(),accum,document);
                }else{
                    return accum;
                }

            } else {
                accum.insert(0,tagStr+">");
                if(!"body".equals(tagStr)){
                    accum= checkPath(element.parent(),accum,document);
                }
            }
        }
        return accum;
    }

    public Element getMaxScoreChild(Element element) {
        if (element.childNodeSize() == 0) {
            return element;
        }
        Elements children = element.children();
        if (children == null || children.size() == 0) {
            return element;
        }
        //System.out.println(children.size());
        Element maxScoreElement = children.first();
        int score = 0;
        for (Element e : children) {
            //System.out.println(e.tagName());
            String strScore = e.attr("score");
            if (strScore == null) {
                continue;
            }
            if (Integer.valueOf(strScore) > score) {
                maxScoreElement = e;
                score = Integer.valueOf(strScore);
            }
        }
        return getMaxScoreChild(maxScoreElement);
    }

}
