package com.dace.textreader.bean;

import java.util.List;

public class ReaderTabAlbumItemBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"cover":"http://web.pythe.cn/xd/freding/topic/images/FnGUDkv4Z287ihczY95ItxJcCbDi.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749470179328","format":0,"albumId":5,"title":"史海综评","subIntroduction":"现在看到的历史，只是过去的沧海一粟。历史的真相究竟是什么？如何看待今日呈现的历史？古往今来的历史学家、哲学家们是如何的品","category":null,"follow":1},{"cover":"http://web.pythe.cn/xd/freding/topic/images/Fn_RIMRlm6OdgYP5lH1kvy-SfWSz.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749235298304","format":0,"albumId":2,"title":"人间百态","subIntroduction":"本专栏讲述的是你我身边人之事。你定能常常见到扫地大叔杵着扫把在休息，煎饼果子奶奶抹去额前的汗水露出笑容，外卖小哥递给你餐","category":null,"follow":0},{"cover":"http://web.pythe.cn/xd/freding/topic/images/FhbOXTK5SfzVnJpzVVT2dV6kfrfa.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749210132480","format":0,"albumId":3,"title":"《欧叶妮·格朗台》","subIntroduction":"《欧叶妮·格朗台》是法国批判现实主义小说家巴尔扎克创作的长篇小说，收录于《人间喜剧》。小说叙述了一个金钱毁灭人性和造成家","category":null,"follow":0},{"cover":"http://web.pythe.cn/xd/freding/topic/images/FmojX-BDugbJdQUupW1AaNARIG33.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749277241344","format":0,"albumId":4,"title":"太平天国","subIntroduction":"兴起于19世纪中叶的太平天国革命，是晚清时期几乎颠覆了清王朝的大事件。从1850年广西桂平的金田村起义，到1864年它的","category":null,"follow":0},{"cover":"http://web.pythe.cn/xd/freding/topic/images/Fj8sIZnz73dE0aGo8Zi2g8Imtvlr.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749507928064","format":0,"albumId":6,"title":"民生百态","subIntroduction":"随着国家的强大，中国的社会也发生了许多变化，作为社会中的一个个体，我们应关心身边发生事情，培养自己对社会的责任感。本专","category":null,"follow":0},{"cover":"http://web.pythe.cn/xd/freding/topic/images/FowsxAfGqYZCMarnubD_w4PDZ6PW.jpg?imageView2/1/w/750/h/420","sentenceNum":null,"searchId":"557706749520510976","format":0,"albumId":7,"title":"白衣天使","subIntroduction":"杏林春满、妙手回春、救死扶伤、德医双馨\u2026\u2026这些词语都是用来赞美以心为灯，守护生命的白衣使者。虽然他们中的很多人未被世人所","category":null,"follow":0}]
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
         * cover : http://web.pythe.cn/xd/freding/topic/images/FnGUDkv4Z287ihczY95ItxJcCbDi.jpg?imageView2/1/w/750/h/420
         * sentenceNum : null
         * searchId : 557706749470179328
         * format : 0
         * albumId : 5
         * title : 史海综评
         * subIntroduction : 现在看到的历史，只是过去的沧海一粟。历史的真相究竟是什么？如何看待今日呈现的历史？古往今来的历史学家、哲学家们是如何的品
         * category : null
         * follow : 1
         */

        private String cover;
        private Object sentenceNum;
        private String searchId;
        private int format;
        private int albumId;
        private String title;
        private String subIntroduction;
        private Object category;
        private int follow;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public Object getSentenceNum() {
            return sentenceNum;
        }

        public void setSentenceNum(Object sentenceNum) {
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

        public Object getCategory() {
            return category;
        }

        public void setCategory(Object category) {
            this.category = category;
        }

        public int getFollow() {
            return follow;
        }

        public void setFollow(int follow) {
            this.follow = follow;
        }
    }
}
