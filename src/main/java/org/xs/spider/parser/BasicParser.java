package org.xs.spider.parser;

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
        Element body=document.body();
        doScoreToElement(body);


        return null;
    }

    @Override
    public void denioseForContentElement(Element contentElement) {

    }

    @Override
    public void downloadImg(Element contentElement) {

    }

    @Override
    public String removeNeedlessChars(List<String> regStrs) {
        return null;
    }

    @Override
    public String removeTails(String contentStr, String tailStr) {
        return null;
    }

    @Override
    public Element format(String contentStr) {
        return null;
    }

    @Override
    public String getContent(Document document) {
        return null;
    }

    private  int doScoreToElement(Element element){
        boolean hasText=element.hasText();
        if(!hasText){//如果节点没有内容
            element.attr("score", "0");
            return 0;
        }else{
            Elements children=element.children();
            if(children.size()==0){//不含有子节点
                return Rating.doRate(element);
            }else{//含有子节点
                int accum=0;
                for(Element child:children){
                    accum+=doScoreToElement(child);
                }
                element.attr("score", String.valueOf(accum));
                return accum;
            }
        }
    }


    public  StringBuffer checkContentPath(Element element,StringBuffer StrAccum){
        Element maxScoreElement=getMaxScoreChild(element);
        if(maxScoreElement==null){
            return StrAccum;
        }else{
            String tagName=maxScoreElement.tagName();
            boolean hasIdAttr=maxScoreElement.hasAttr("id");//是否含有id属性
            boolean hashClassAttr=maxScoreElement.hasAttr("class");//是否含有class属性
            StrAccum.append(tagName);
            if(hasIdAttr){//优先考虑id属性
                StrAccum.append("#"+maxScoreElement.attr("id"));
                return StrAccum;
            }else if(hashClassAttr){//如果包含class属性
                String classAttr=maxScoreElement.attr("class").trim().replace(" ", ".");
                StrAccum.append("."+classAttr);
                if(element.getElementsByClass(classAttr).size()==1){
                    return StrAccum;
                }
            }
            StrAccum.append(">");
            return checkContentPath(maxScoreElement,StrAccum);
        }
    }

    public  Element getMaxScoreChild(Element element){
        if(element.childNodeSize()==0){
            return null;
        }
        Elements children=element.children();
        Element maxScoreElement=null;
        int score=0;
        for(Element e:children){
            String strScore=e.attr("score");
            if(strScore==null){
                continue;
            }

            if(Integer.valueOf(strScore)>score){
                maxScoreElement=e;
                score=Integer.valueOf(strScore);
            }
        }
        return maxScoreElement;
    }
    public String removeStockCode(String content) {
        String stockCodes[] = new String[] {
                "<!--.[^-]*(?=-->)-->",
                "(?is)<!--.*?-->",
                "\\(\\d{6},\\)",
                "\\(\\d{1,6}[\\.|,|，|；|;|-][ |(&nbsp;)]{0,10}\\w{2}\\)",
                "\\(\\d{1,6}\\.\\w{2,4}[，|,|;|；][\\u4e00-\\u9fa5]{2,10}\\)",
                "\\(\\d{1,6}\\.\\w{2,4}/\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4};\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4}；\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4}、\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4},\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4}，\\d{1,6}\\.\\w{2,4}\\)",
                "\\(\\d{1,6}\\.\\w{2,4},-{0,1}\\d{1,2}\\.\\d{1,4},-{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{2,4}, -{0,1}\\d{1,2}\\.\\d{1,4}, -{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{2,4},(&nbsp;){0,10}-{0,1}\\d{1,2}\\.\\d{1,4},(&nbsp;){0,10}-{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{2,4}，-{0,1}\\d{1,2}\\.\\d{1,4}，-{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{2,4}， -{0,1}\\d{1,2}\\.\\d{1,4}， -{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{2,4}，(&nbsp;){0,10}-{0,1}\\d{1,2}\\.\\d{1,4}，(&nbsp;){0,10}-{0,1}\\d{1,2}\\.\\d{1,2}%\\)",
                "\\(\\d{1,6}\\.\\w{1,4},-{0,1}\\d{1,6}\\.\\w{2,4},-{0,1}\\d{1,2}\\.\\d{1,2}%,实时行情\\)",
                "\\(\\d{1,6}\\.\\w{1,4}，-{0,1}\\d{1,6}\\.\\w{2,4}，-{0,1}\\d{1,2}\\.\\d{1,2}%，实时行情\\)",
                // "\\d{1,6}[\\.|,|，|；|;|-]{0,1}[ |(&nbsp;)]{0,10}\\w{2,4}[\\.|,|，|；|;]{0,1}[ |(&nbsp;)]{0,10}",
                "\\[-{0,1}\\d{1,4}\\.\\d{1,2}% 资金 研报\\]",
                "\\[-{0,1}\\d{1,4}\\.\\d{1,2}%[(&nbsp;)| |	| ]{0,10}资金 研报\\]",

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
            content = content.replaceAll(string, "");
        }
        return content;
    }

    public String removeNoNeedChars(String content) {

        String[] spechars = new String[] { "【免责声明", "【版权声明", "【重点推荐", "【延伸阅读",
                "【推荐阅读", "【相关阅读", "免责声明", "版权声明", "【更多详情", "【相关专题" };

        for (String string : spechars) {
            int index = content.indexOf(string);
            if (index > 0) {
                content = content.substring(0, index);
            }
        }
        return content;
    }

    public String replaceNoNeedChars(String content) {
        String spechars[] = new String[] { "我有话说", "欢迎发表评论", "发表评论", "收藏本文",
                "微博推荐", "(行情研报)", "[微博]", "(微博)", "(财苑)", "返回列表", "加入收藏",
                "打印本页", "打印本稿", "新闻订阅", "分享到", "【打印】", "网站论坛", "字体：大 中 小",
                "字体:大 中 小", "字体：", "字体:", "推荐朋友", "关闭窗口", "关闭", "慧聪资讯手机客户端下载",
                "(行情，问诊)", "(行情,问诊)", "(博客,微博)", "(微信)", "(市值重估)",
                "[最新消息 价格 户型 点评]", "[简介 最新动态]", "[简介最新动态]", "[最新消息价格户型点评]",
                "(楼盘)", "(点击查看最新人物消息)", "( 详情 图库 团购 点评 ) ",
                "(CNFIN.COM/XINHUA08.COM)--", "(CNFIN.COM / XINHUA08.COM)--",
                "(CNFIN.COM&nbsp;/&nbsp;XINHUA08.COM)--", "(看跌期权)", "(放心保)",
                "(查询信托产品)", "(楼盘资料)", "(<strong>点击进入徐工网上购机页面</strong>)",
                "点击浏览全文", "返回首页", "更多精彩，请登录“人民微博”参与互动", "《》：", "(滚动资讯)", "(完)",
                "(中国电子商务研究中心讯)", "()" };

        for (String string : spechars) {
            content = content.replace(string, "");
        }
        return content;
    }

    public void removeAdvertiseText(Element maxTextElement) {
        Elements elements = maxTextElement.select("strong");
        for (Element element : elements) {
            String advertiseTexts[] = new String[] { "查看", "||", "相关评论",
                    "相关专题", "重点推荐", "延伸阅读", "推荐阅读", "相关报道", "商业专栏", "证券要闻",
                    "行业动态", "公司新闻", "相关新闻", "往期回顾", "今日消息", "机构策略", "原标题",
                    "相关阅读", "更多详情", "免责声明", "版权声明", "相关链接", "机构研究", "个股点评",
                    "行业新闻", "公司动态" };
            for (String string : advertiseTexts) {
               // removeNoNeedTextElement(element, string);
            }
        }

    }

}
