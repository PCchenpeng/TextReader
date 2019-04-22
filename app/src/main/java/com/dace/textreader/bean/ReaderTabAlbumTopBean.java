package com.dace.textreader.bean;

import java.util.List;

public class ReaderTabAlbumTopBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558103654436044800","format":1,"albumId":1024,"title":"九章算术","subIntroduction":"它是中国古代第一部数学专著，是《算经十书》中最重要的一种，成于公元一世纪左右。该书内容十分丰富，系统总结了战国、秦、汉时"},{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558103673532710912","format":1,"albumId":1025,"title":"滴天髓阐微","subIntroduction":"相传为宋人京图撰，至清代道光年间由任铁樵注释，增注由袁树珊撰辑。"},{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558103755128700928","format":1,"albumId":1026,"title":"焦氏易林","subIntroduction":"《焦氏易林》又名《易林》，十六卷，西汉焦延寿撰。《四库全书》将之列于\u201c子部术数类\u201d。易林源自于《周易》，每一卦各变为六十"},{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558103763802521600","format":1,"albumId":1027,"title":"荡寇志","subIntroduction":"《荡寇志》是中国清代长篇小说名，作者俞万春（1794\u20141849），此书草创于道光六年（1826），写成于道光二十七年（1"},{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558103978559275008","format":1,"albumId":1028,"title":"狄公案","subIntroduction":"《狄公案》又名《武则天四大奇案》《狄梁公全传》，清末长篇公案小说，作者名已佚，共六卷六十四回。前三十回，写狄仁杰任昌平县"},{"cover":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","sentenceNum":0,"searchId":"558104062877368320","format":1,"albumId":1029,"title":"东游记","subIntroduction":"《东游记》，又名《上洞八仙传》、《八仙出处东游记》，共二卷五十六回。作者为明代吴元泰。内容为八仙的神话传说，记叙铁拐李、"}]
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
         * cover : http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420
         * sentenceNum : 0
         * searchId : 558103654436044800
         * format : 1
         * albumId : 1024
         * title : 九章算术
         * subIntroduction : 它是中国古代第一部数学专著，是《算经十书》中最重要的一种，成于公元一世纪左右。该书内容十分丰富，系统总结了战国、秦、汉时
         */

        private String cover;
        private int sentenceNum;
        private String searchId;
        private int format;
        private int albumId;
        private String title;
        private String subIntroduction;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public int getSentenceNum() {
            return sentenceNum;
        }

        public void setSentenceNum(int sentenceNum) {
            this.sentenceNum = sentenceNum;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public int getAlbumId() {
            return albumId;
        }

        public void setAlbumId(int albumId) {
            this.albumId = albumId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubIntroduction() {
            return subIntroduction;
        }

        public void setSubIntroduction(String subIntroduction) {
            this.subIntroduction = subIntroduction;
        }
    }
}
