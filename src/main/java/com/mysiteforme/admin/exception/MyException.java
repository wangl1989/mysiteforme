package com.mysiteforme.admin.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangl on 2018/1/24.
 * todo:
 */
@Setter
@Getter
public class MyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String msg;
    private int code;

    public MyException() {
        this.code = 500;
    }

    public MyException(String msg,Throwable cause) {
        super(msg,cause);
        this.msg = msg;
    }

    public MyException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public MyException(String msg, int code,Throwable cause) {
        super(msg,cause);
        this.msg = msg;
        this.code = code;
    }

    public MyException(String msg) {
        this.code = 500;
        this.msg = msg;
    }

}
