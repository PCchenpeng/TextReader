package com.dace.textreader.bean;

import java.util.List;

public class HotSearchBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"word_count":75,"word":"李白"},{"word_count":41,"word":"绘本"},{"word_count":34,"word":"英雄"},{"word_count":34,"word":"自有"},{"word_count":11,"word":"故事"},{"word_count":9,"word":"讲"},{"word_count":8,"word":"哦哦哦"},{"word_count":6,"word":"上海的"},{"word_count":5,"word":"你"},{"word_count":5,"word":"街道"},{"word_count":5,"word":"下雪"},{"word_count":4,"word":"喂喂"},{"word_count":4,"word":"夏天"},{"word_count":4,"word":"给"},{"word_count":4,"word":"北京"},{"word_count":3,"word":"古堡"},{"word_count":3,"word":"雨水"},{"word_count":2,"word":"理想"},{"word_count":2,"word":"引人"},{"word_count":2,"word":"哦哦哦哦"},{"word_count":2,"word":"李白。"},{"word_count":2,"word":"诗歌"},{"word_count":2,"word":"你说啥"},{"word_count":2,"word":"哦哦"}]
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
         * word_count : 75
         * word : 李白
         */

        private int word_count;
        private String word;

        public int getWord_count() {
            return word_count;
        }

        public void setWord_count(int word_count) {
            this.word_count = word_count;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
