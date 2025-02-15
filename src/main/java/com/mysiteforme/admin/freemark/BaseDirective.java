/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:56:23
 * @ Description: 基础指令类 已废弃 
 */

package com.mysiteforme.admin.freemark;

import freemarker.template.*;

@Deprecated
public class BaseDirective {

    public String getString(TemplateModel paramValue) throws TemplateModelException {
        if(paramValue == null){
            return "";
        }else{
            if(paramValue instanceof TemplateScalarModel){  //是字符串
                return ((TemplateScalarModel)paramValue).getAsString();
            }else{
                throw new TemplateModelException("String转换异常!");
            }
        }
    }

    public long getLong(TemplateModel paramValue) throws TemplateModelException{
        if(paramValue==null)
        {
            return 0;
        }
        else
        {
            if(paramValue instanceof TemplateScalarModel)  //是字符串
            {
                String value=((TemplateScalarModel)paramValue).getAsString();
                return Long.parseLong(value);
            }
            else if(paramValue instanceof TemplateNumberModel)  //数字
            {
                return ((TemplateNumberModel)paramValue).getAsNumber().longValue();
            }
            else
            {
                throw new TemplateModelException("Long转换异常!");
            }
        }
    }

    public	int getInt(TemplateModel paramValue) throws TemplateModelException
    {
        if(paramValue==null)
        {
            return 0;
        }
        else
        {
            if(paramValue instanceof TemplateScalarModel)  //是字符串
            {
                String value=((TemplateScalarModel)paramValue).getAsString();
                return Integer.parseInt(value);
            }
            else if(paramValue instanceof TemplateNumberModel)  //数字
            {
                return ((TemplateNumberModel)paramValue).getAsNumber().intValue();
            }
            else
            {
                throw new TemplateModelException("int转换异常!");
            }
        }
    }

    public  boolean getBoolean(TemplateModel paramValue) throws TemplateModelException
    {
        if(paramValue==null)
        {
            return false;
        }
        else
        {
            if(paramValue instanceof TemplateScalarModel)  //是字符串
            {
                String value=((TemplateScalarModel)paramValue).getAsString();
                return Boolean.parseBoolean(value);
            }
            else if(paramValue instanceof TemplateBooleanModel)  //boolean
            {
                return ((TemplateBooleanModel)paramValue).getAsBoolean();
            }
            else
            {
                throw new TemplateModelException("boolean转换异常!");
            }
        }
    }
}
