package org.xs.spider.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by Administrator on 15-5-27.
 */
public interface Parser {

    /**
     * 对Document进行降噪
     * @param document
     * @return
     */
    public Document denoiseForDoc(Document document);

    /**
     * 定位到文章正文部分
     * @param document
     * @return
     */
    public Element excavateContent(Document document);

    /**
     * 对正文部分降噪
     * @param contentElement
     */
    public void denioseForContentElement(Element contentElement);

    /**
     * 下载图片
     * @param contentElement
     */
    public void downloadImg(Element contentElement);

    /**
     * 按照正则移除正文中多余的部分
     * @param regStrs
     * @return
     */
    public String removeNeedlessChars(List<String> regStrs,String contentStr);

    /**
     * 移除尾部杂质
     * @param contentStr
     * @param tailStr
     * @return
     */
    public String removeTails(String contentStr,String tailStr);

    /**
     * 将contentStr重新格式化为Element
     * @param contentStr
     * @return
     */
    public Element format(String contentStr);

    /**
     * 获取正文
     * @param document
     * @return
     */
    public String getContent(Document document);


}
