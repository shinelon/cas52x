package com.pay.credit.cloud.prom.config;

import java.util.Arrays;
import java.util.List;

import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机能概要:过滤掉一些不需要授权登录的URL
 */
public class SimpleUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SimpleUrlPatternMatcherStrategy.class);

    /**
     * 机能概要: 判断是否匹配这个字符串
     *
     * @param url
     *            用户请求的连接
     * @return true : 不拦截 false :必须得登录了
     */
    @Override
    public boolean matches(String url) {

        if (url.contains("/logout")) {
            return true;
        }

        List<String> list = Arrays.asList("/", "/index", "/favicon.ico");

        String name = url.substring(url.lastIndexOf("/"));
        if (name.indexOf("?") != -1) {
            name = name.substring(0, name.indexOf("?"));
        }

        logger.debug("name：" + name);
        boolean result = list.contains(name);
        if (!result) {
            logger.debug("拦截URL：" + url);
        }
        return result;
    }

    /**
     * 正则表达式的规则，这个地方可以是web传递过来的
     */
    @Override
    public void setPattern(String pattern) {

    }
}
