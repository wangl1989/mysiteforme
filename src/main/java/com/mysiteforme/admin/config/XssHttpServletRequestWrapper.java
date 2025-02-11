package com.mysiteforme.admin.config;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    // 允许的HTML标签和属性列表
    private static final List<String> ALLOWED_TAGS = Arrays.asList(
            "p", "div", "span", "h1", "h2", "h3", "h4", "h5", "h6",
            "strong", "em", "b", "i", "u", "s", "br",
            "a", "ul", "ol", "li", "table", "thead", "tbody", "tr", "td", "th"
    );

    // 基本属性
    private static final List<String> ALLOWED_ATTRIBUTES = Arrays.asList(
            "style", "class", "id", "target", "title"
    );

    // 特殊标签的特殊属性
    private static final Map<String, List<String>> SPECIAL_TAGS = new HashMap<>();
    static {
        // img标签的特殊属性
        SPECIAL_TAGS.put("img", Arrays.asList("src", "alt", "width", "height", "style", "class", "id"));
        // a标签的特殊属性
        SPECIAL_TAGS.put("a", Arrays.asList("href", "target", "rel", "title"));
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
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    private String unescapeHtml(String value) {
        if (value == null) {
            return null;
        }

        String result = value;
        String temp = StringEscapeUtils.unescapeHtml4(result);

        // 只要解码后的结果与解码前不同，就说明还可以继续解码
        while (!temp.equals(result)) {
            result = temp;
            temp = StringEscapeUtils.unescapeHtml4(result);
        }

        return result;
    }

    private String cleanXSS(String value) {
        if (value == null) {
            return null;
        }

        // 首先检查是否已经被转义过
        String unescapedValue = unescapeHtml(value);

        // 保存所有的HTML标签
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(unescapedValue);
        int index = 0;

        // 临时替换所有HTML标签为占位符
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String tag = matcher.group();
            tags.add(tag);
            matcher.appendReplacement(sb, "##TAG" + index + "##");
            index++;
        }
        matcher.appendTail(sb);

        // 对非HTML标签的文本进行HTML转义
        String result = StringEscapeUtils.escapeHtml4(sb.toString());

        // 还原所有的HTML标签
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            // 清理标签
            tag = cleanTag(tag);
            result = result.replace("##TAG" + i + "##", tag);
        }

        return result;
    }

    // 添加更多危险标签的检测
    private static final List<String> DANGEROUS_TAGS = Arrays.asList(
            "script", "javascript", "vbscript", "expression",
            "iframe", "frame", "object", "embed", "applet"
    );

    // 在cleanTag方法中添加统一检查
    private boolean isDangerousTag(String tag) {
        String lowerTag = tag.toLowerCase();
        return DANGEROUS_TAGS.stream()
                .anyMatch(dangerous -> lowerTag.matches("(?i)</?" + dangerous + ".*?>"));
    }

    private String cleanTag(String tag) {
        // 先判断是否是script等危险标签
        if (isDangerousTag(tag)) {
            return StringEscapeUtils.escapeHtml4(tag);
        }

        // 基本的XSS过滤
        String result = tag
                .replaceAll("(?i)on\\w+\\s*=\\s*\".*?\"", "")
                .replaceAll("(?i)on\\w+\\s*=\\s*'.*?'", "")
                .replaceAll("(?i)javascript:", "")
                .replaceAll("(?i)vbscript:", "")
                .replaceAll("(?i)data:", "");

        // 如果是img标签，特殊处理
        if (tag.toLowerCase().startsWith("<img")) {
            return processImgTags(result);
        }

        // 处理允许的标签
        for (String allowedTag : ALLOWED_TAGS) {
            if (tag.toLowerCase().startsWith("<" + allowedTag)) {
                return "<" + allowedTag + cleanAttributes(allowedTag, result.substring(allowedTag.length() + 1)) + ">";
            } else if (tag.toLowerCase().startsWith("</" + allowedTag)) {
                return "</" + allowedTag + ">";
            }
        }

        // 不在白名单中的标签全部转义
        return StringEscapeUtils.escapeHtml4(tag);
    }



    private String processImgTags(String input) {
        Pattern pattern = Pattern.compile("(?i)<img\\s+([^>]*?)/?>");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String attributes = matcher.group(1);
            String cleanedAttributes = cleanAttributes("img", attributes);
            matcher.appendReplacement(sb, "<img " + cleanedAttributes + ">");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private String cleanAttributes(String tag, String attributes) {
        if (attributes == null || attributes.trim().isEmpty()) {
            return "";
        }

        StringBuilder cleanedAttributes = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*[\"'](.*?)[\"']");
        Matcher matcher = pattern.matcher(attributes);

        while (matcher.find()) {
            String attributeName = matcher.group(1).toLowerCase();
            String attributeValue = matcher.group(2);

            // 检查是否是特殊标签的特殊属性
            List<String> allowedAttrs = SPECIAL_TAGS.getOrDefault(tag, ALLOWED_ATTRIBUTES);

            if (allowedAttrs.contains(attributeName)) {
                // 对src属性进行特殊处理
                if ("src".equals(attributeName)) {
                    // 允许http、https和相对路径
                    if (attributeValue.matches("^(https?:|/|\\./|../|data:image/).*")) {
                        cleanedAttributes.append(" ")
                                .append(attributeName)
                                .append("=\"")
                                .append(attributeValue)
                                .append("\"");
                    }
                } else {
                    // 其他属性的基本清理
                    attributeValue = attributeValue
                            .replaceAll("(?i)javascript:", "")
                            .replaceAll("(?i)vbscript:", "")
                            .replaceAll("[\"']", "");

                    cleanedAttributes.append(" ")
                            .append(attributeName)
                            .append("=\"")
                            .append(attributeValue)
                            .append("\"");
                }
            }
        }

        return cleanedAttributes.toString();
    }
}



