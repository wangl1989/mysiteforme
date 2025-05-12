package com.mysiteforme.admin.security;


import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Safelist SAFELIST;
    
    static {
        // 基础配置
        SAFELIST = Safelist.basic()
            .addTags("div", "span", "h1", "h2", "h3", "h4", "h5", "h6",
                    "p", "strong", "em", "b", "i", "u", "s", "br",
                    "a", "ul", "ol", "li", "table", "thead", "tbody", "tr", "td", "th")
            .addAttributes(":all", "style", "class", "id", "target", "title")
            // 图片标签的特殊配置
            .addTags("img")
            .addAttributes("img", "src", "alt", "width", "height", "style", "class", "id")
            // 链接的特殊配置
            .addAttributes("a", "href", "target", "rel", "title")
            // 允许图片的 data URI
            .addProtocols("img", "src", "http", "https", "data");
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        if (value == null) {
            return null;
        }
        return Jsoup.clean(value, SAFELIST);
    }
}
