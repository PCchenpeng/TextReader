package com.dace.textreader.bean;

import java.util.List;

public class HtmlLinkBean {


    /**
     * status : 200
     * msg : OK
     * data : [{"name":"courseList","url":"https://check.pythe.cn/microClasshtml5/microClass.html"},{"name":"courseBoughtList","url":"https://check.pythe.cn/microClasshtml5/boughtPytheMicroClass.html"},{"name":"articleDetail","url":"https://check.pythe.cn/1readingModule/pyReadDetail0.html"},{"name":"articleTranslate","url":"https://check.pythe.cn/1readingModule/essayTranslate.html"},{"name":"noteDetail","url":"https://check.pythe.cn/update/share/pytheNoteShare.html"},{"name":"preSentence","url":"https://check.pythe.cn/update/share/preSentenceShare.html"}]
     */

    private int status;
    private String msg;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : courseList
         * url : https://check.pythe.cn/microClasshtml5/microClass.html
         */

        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
