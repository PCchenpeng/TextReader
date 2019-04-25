package com.dace.textreader.bean;

import java.util.List;

public class TestSearchBean {
    /**
     * status : 200
     * msg : OK
     * data : {"randomId":3,"tipList":[{"id":2,"tip":"描写秋天的诗歌","time":1533535981000,"status":1},{"id":3,"tip":"春天的成语","time":1533535981000,"status":1},{"id":4,"tip":"我想知道三个金念什么","time":1533535981000,"status":1},{"id":5,"tip":"我想看看形容快乐的成语","time":1533535981000,"status":1},{"id":21,"tip":"搜索一下关于爱情的诗句","time":1533535981000,"status":1},{"id":22,"tip":"描写秋天的诗歌","time":1533535981000,"status":1}]}
     */

    private int status;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * randomId : 3
         * tipList : [{"id":2,"tip":"描写秋天的诗歌","time":1533535981000,"status":1},{"id":3,"tip":"春天的成语","time":1533535981000,"status":1},{"id":4,"tip":"我想知道三个金念什么","time":1533535981000,"status":1},{"id":5,"tip":"我想看看形容快乐的成语","time":1533535981000,"status":1},{"id":21,"tip":"搜索一下关于爱情的诗句","time":1533535981000,"status":1},{"id":22,"tip":"描写秋天的诗歌","time":1533535981000,"status":1}]
         */

        private int randomId;
        private List<TipListBean> tipList;

        public int getRandomId() {
            return randomId;
        }

        public void setRandomId(int randomId) {
            this.randomId = randomId;
        }

        public List<TipListBean> getTipList() {
            return tipList;
        }

        public void setTipList(List<TipListBean> tipList) {
            this.tipList = tipList;
        }

        public static class TipListBean {
            /**
             * id : 2
             * tip : 描写秋天的诗歌
             * time : 1533535981000
             * status : 1
             */

            private int id;
            private String tip;
            private long time;
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

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
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
}