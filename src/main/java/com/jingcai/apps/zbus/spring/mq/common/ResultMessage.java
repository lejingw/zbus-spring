package com.jingcai.apps.zbus.spring.mq.common;

/**
 * Created by lejing on 15/2/3.
 */
public interface ResultMessage {
    String getCode();

    String getMessage();

    class Impl implements ResultMessage {
        private String code;
        private String message;

        public Impl() {
        }

        public Impl(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object obj) {
            if(null == obj || !(obj instanceof Impl)){
                return false;
            }
            Impl obj2 = (Impl)obj;
            if(null != code && code.equals(obj2.getCode()) && null != message && message.equals(obj2.getMessage())){
                return true;
            }
            return false;
        }
    }
}
