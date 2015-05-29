package org.xs.spider.Fetcher;

/**
 * Created by Administrator on 15-5-29.
 */
public interface ImgFetcher {

    /**
     * 图片下载器，依据图片Url下载图片，并存储在图片服务器上，然后返回相应的URL
     * @param imgUrl 需要下载的图片Url
     * @return 存储到本地后的图片URL
     */
    public String fetch(String imgUrl);
}
