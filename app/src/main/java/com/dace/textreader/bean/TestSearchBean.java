package com.dace.textreader.bean;

import java.util.List;

public class TestSearchBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"id":2,"tip":"描写秋天的诗歌","time":null,"status":1},{"id":3,"tip":"春天的成语","time":null,"status":1},{"id":4,"tip":"我想知道三个金念什么","time":null,"status":1},{"id":5,"tip":"我想看看形容快乐的成语","time":null,"status":1},{"id":6,"tip":"鬼字组词","time":null,"status":1}]
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
         * id : 2
         * tip : 描写秋天的诗歌
         * time : null
         * status : 1
         */

        private int id;
        private String tip;
        private Object time;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public Object getTime() {
            return time;
        }

        public void setTime(Object time) {
            this.time = time;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
