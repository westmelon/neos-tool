package com.tl.common.ext.exception;



/**
 * Created by Mamf on 2017/6/21.
 */
public class TlBusinessException extends RuntimeException{

    private static final long serialVersionUID = 2332608236621015980L;

    private final int code;

    private final String msg;

    private final String subMsg;

    public TlBusinessException(int code , String msg) {
        super(msg);
        this.code = code ;
        this.msg = msg;
        this.subMsg = "";
    }

    public TlBusinessException(int code , String msg, String subMsg) {
        super(msg + " " + subMsg);
        this.code = code ;
        this.msg = msg;
        this.subMsg = subMsg;
    }

    public TlBusinessException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;
        this.subMsg = "";
    }



    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

    public String getSubMsg() {
        return subMsg;
    }
}
