package com.mysiteforme.admin.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXSS(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            String[] cleanValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanValues[i] = cleanXSS(values[i]);
            }
            return cleanValues;
        }
        return null;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // 使用 commons-text 进行 HTML 转义
        value = StringEscapeUtils.escapeHtml4(value);

        // 过滤特定的 XSS 关键字
        value = value.replaceAll("(?i)<script>", "")
                .replaceAll("(?i)</script>", "")
                .replaceAll("(?i)<javascript>", "")
                .replaceAll("(?i)</javascript>", "")
                .replaceAll("(?i)javascript:", "")
                .replaceAll("(?i)vbscript:", "")
                .replaceAll("(?i)onload=", "")
                .replaceAll("(?i)onerror=", "");

        return value;
    }
}

