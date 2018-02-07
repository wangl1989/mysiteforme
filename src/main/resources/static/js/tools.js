/**
 * Created by tnt on 2017/11/29.
 *
 */
//时间戳的处理
layui.define(["layer","laytpl"],function(exports){
    var laytpl = layui.laytpl;
    laytpl.toDateString = function(d, format){
        if(undefined === d || null == d || '' === d){
            return "";
        }
        var date = new Date(d || new Date())
            ,ymd = [
            this.digit(date.getFullYear(), 4)
            ,this.digit(date.getMonth() + 1)
            ,this.digit(date.getDate())
        ]
            ,hms = [
            this.digit(date.getHours())
            ,this.digit(date.getMinutes())
            ,this.digit(date.getSeconds())
        ];

        format = format || 'yyyy-MM-dd HH:mm:ss';

        return format.replace(/yyyy/g, ymd[0])
            .replace(/MM/g, ymd[1])
            .replace(/dd/g, ymd[2])
            .replace(/HH/g, hms[0])
            .replace(/mm/g, hms[1])
            .replace(/ss/g, hms[2]);
    };

    //数字前置补零
    laytpl.digit = function(num, length, end){
        var str = '';
        num = String(num);
        length = length || 2;
        for(var i = num.length; i < length; i++){
            str += '0';
        }
        return num < Math.pow(10, length) ? str + (num|0) : num;
    };

    /**
     * 计算多长时间之前
     * @param dateTimeStamp
     * @returns {*}
     */
    laytpl.timeago = function (dateTimeStamp) {
        if(undefined === dateTimeStamp || null == dateTimeStamp || '' === dateTimeStamp){
            return "";
        }
        var minute=1000*60,
        hour=minute*60,      //把分，时，天，周，半个月，一个月用毫秒表示
        day=hour*24,
        week=day*7,
        halfamonth=day*15,
        month=day*30,
        year=day*365,
        now=new Date().getTime(),   //获取当前时间毫秒
        diffValue=now-dateTimeStamp;//时间差
        if(diffValue<0){return "";}
        var  minC=diffValue/minute,  //计算时间差的分，时，天，周，月
        hourC=diffValue/hour,
        dayC=diffValue/day,
        weekC=diffValue/week,
        monthC=diffValue/month,
        yearC=diffValue/year,
        result;
        if(yearC>=1){
            result=" "+parseInt(minC)+"年前";
        }else if(monthC>=1){
            result=" "+parseInt(monthC)+"月前"
        }else if(weekC>=1){
            result=" "+parseInt(weekC)+"周前"
        }else if(dayC>=1){
            result=" "+parseInt(dayC)+"天前"
        }else if(hourC>=1){
            result=" "+parseInt(hourC)+"小时前"
        }else if(minC>=1){
            result=" "+parseInt(minC)+"分钟前"
        }else {
            result = "刚刚";
        }
        return result;
    }

    /**
     * 转义html字符
     * @param str
     * @returns {string | void}
     */
    laytpl.escape2Html = function (str) {
        var arrEntities={'lt':'<','gt':'>','nbsp':' ','amp':'&','quot':'"'};
        return str.replace(/&(lt|gt|nbsp|amp|quot);/ig,function(all,t){return arrEntities[t];});
    };

    /**
     * 空格转义成普通字符串
     * @param str
     * @returns {string | void}
     */
    laytpl.nbsp2Space = function (str) {
        var arrEntities = {'nbsp' : ' '};
        return str.replace(/&(nbsp);/ig, function(all, t){return arrEntities[t]})
    }
});