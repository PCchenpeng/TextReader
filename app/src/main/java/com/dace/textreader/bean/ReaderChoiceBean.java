package com.dace.textreader.bean;

import java.util.List;

public class ReaderChoiceBean {

    /**
     * status : 200
     * msg : OK
     * data : {"num":5,"name":"精选推荐","essayList":[{"id":97,"title":"心迷宫","cover":"http://web.pythe.cn/xd/freding/topic/images/FnMhvhsLREpaE21vMWMyICGxs4Pi.jpg?imageView2/1/w/750/h/420","time":1553009395000,"status":1,"pv":10,"collectNum":0,"shareNum":0,"format":0,"startLevel":10,"stopLevel":16,"category":"科普","parentId":null,"sentenceNum":0,"searchId":"557707353827442688","albumSequence":815530093948430,"subIntroduction":"人的内心世界如迷宫般难以掌握，心理学的研究对象正是人类的心理现象及其影响下的精神功能和行为活动。随着社会的日益进步，人们","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"150","isMap":0,"introduction":null,"follow":null},{"id":98,"title":"当代思想锋芒","cover":"http://web.pythe.cn/xd/freding/topic/images/Fm7uofrpg8fLwzP1WhYmEfzXpkPV.jpg?imageView2/1/w/750/h/420","time":1553009396000,"status":1,"pv":0,"collectNum":0,"shareNum":0,"format":0,"startLevel":0,"stopLevel":16,"category":"散文","parentId":null,"sentenceNum":0,"searchId":"557707358860607488","albumSequence":815530093953968,"subIntroduction":"杂文是一种直接、迅速反映社会事变或动向的文艺性论文，特点是\u201c杂而有文\u201d，短小、锋利、隽永，富于文艺工作者色彩和诗的语言，","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"174","isMap":0,"introduction":null,"follow":null},{"id":99,"title":"匠心之中式建筑","cover":"http://web.pythe.cn/xd/freding/topic/images/Fksx3UPqiQXAqQj5ugAqE4aTA9vq.jpg?imageView2/1/w/750/h/420","time":1553009397000,"status":1,"pv":2,"collectNum":0,"shareNum":0,"format":0,"startLevel":9,"stopLevel":15,"category":"文化","parentId":null,"sentenceNum":0,"searchId":"557707362874556416","albumSequence":915530093969857,"subIntroduction":"中国历史悠久，地域广阔，从古至今，古人的智慧不断聚集，形成了现在中国建筑多样的局面。尤其是中国的木结构建筑，是世界文化宝","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"204","isMap":0,"introduction":null,"follow":null},{"id":100,"title":"现代名家散文","cover":"http://web.pythe.cn/xd/freding/topic/images/FhrTgCZVQq5nVdLz10dAtz7Wrec8.jpg?imageView2/1/w/750/h/420","time":1553009398000,"status":1,"pv":2,"collectNum":0,"shareNum":0,"format":0,"startLevel":6,"stopLevel":15,"category":"散文","parentId":null,"sentenceNum":0,"searchId":"557707365508579328","albumSequence":815530093976166,"subIntroduction":"古往今来，有多少精美的散文像珍珠般在熠熠生辉！本专栏从现代文学名家的散文名篇中精选出适合中小学生学习、阅读的经典之作，所","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"56","isMap":0,"introduction":null,"follow":null},{"id":101,"title":"战舰与潜艇","cover":"http://web.pythe.cn/xd/freding/topic/images/Fs9XS8JTyMl5et4WuU69P6CNGKnZ.jpg?imageView2/1/w/750/h/420","time":1553009402000,"status":1,"pv":1,"collectNum":0,"shareNum":0,"format":0,"startLevel":8,"stopLevel":14,"category":"军事","parentId":null,"sentenceNum":0,"searchId":"557707383372120064","albumSequence":215530094010662,"subIntroduction":"在大海上的权势，是一个国家真正崛起为大国的基础。本栏目将搜集相关文章，结合精心采集的图片资料，讲述各种战舰与潜艇的故事。","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"126","isMap":0,"introduction":null,"follow":null}]}
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
         * num : 5
         * name : 精选推荐
         * essayList : [{"id":97,"title":"心迷宫","cover":"http://web.pythe.cn/xd/freding/topic/images/FnMhvhsLREpaE21vMWMyICGxs4Pi.jpg?imageView2/1/w/750/h/420","time":1553009395000,"status":1,"pv":10,"collectNum":0,"shareNum":0,"format":0,"startLevel":10,"stopLevel":16,"category":"科普","parentId":null,"sentenceNum":0,"searchId":"557707353827442688","albumSequence":815530093948430,"subIntroduction":"人的内心世界如迷宫般难以掌握，心理学的研究对象正是人类的心理现象及其影响下的精神功能和行为活动。随着社会的日益进步，人们","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"150","isMap":0,"introduction":null,"follow":null},{"id":98,"title":"当代思想锋芒","cover":"http://web.pythe.cn/xd/freding/topic/images/Fm7uofrpg8fLwzP1WhYmEfzXpkPV.jpg?imageView2/1/w/750/h/420","time":1553009396000,"status":1,"pv":0,"collectNum":0,"shareNum":0,"format":0,"startLevel":0,"stopLevel":16,"category":"散文","parentId":null,"sentenceNum":0,"searchId":"557707358860607488","albumSequence":815530093953968,"subIntroduction":"杂文是一种直接、迅速反映社会事变或动向的文艺性论文，特点是\u201c杂而有文\u201d，短小、锋利、隽永，富于文艺工作者色彩和诗的语言，","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"174","isMap":0,"introduction":null,"follow":null},{"id":99,"title":"匠心之中式建筑","cover":"http://web.pythe.cn/xd/freding/topic/images/Fksx3UPqiQXAqQj5ugAqE4aTA9vq.jpg?imageView2/1/w/750/h/420","time":1553009397000,"status":1,"pv":2,"collectNum":0,"shareNum":0,"format":0,"startLevel":9,"stopLevel":15,"category":"文化","parentId":null,"sentenceNum":0,"searchId":"557707362874556416","albumSequence":915530093969857,"subIntroduction":"中国历史悠久，地域广阔，从古至今，古人的智慧不断聚集，形成了现在中国建筑多样的局面。尤其是中国的木结构建筑，是世界文化宝","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"204","isMap":0,"introduction":null,"follow":null},{"id":100,"title":"现代名家散文","cover":"http://web.pythe.cn/xd/freding/topic/images/FhrTgCZVQq5nVdLz10dAtz7Wrec8.jpg?imageView2/1/w/750/h/420","time":1553009398000,"status":1,"pv":2,"collectNum":0,"shareNum":0,"format":0,"startLevel":6,"stopLevel":15,"category":"散文","parentId":null,"sentenceNum":0,"searchId":"557707365508579328","albumSequence":815530093976166,"subIntroduction":"古往今来，有多少精美的散文像珍珠般在熠熠生辉！本专栏从现代文学名家的散文名篇中精选出适合中小学生学习、阅读的经典之作，所","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"56","isMap":0,"introduction":null,"follow":null},{"id":101,"title":"战舰与潜艇","cover":"http://web.pythe.cn/xd/freding/topic/images/Fs9XS8JTyMl5et4WuU69P6CNGKnZ.jpg?imageView2/1/w/750/h/420","time":1553009402000,"status":1,"pv":1,"collectNum":0,"shareNum":0,"format":0,"startLevel":8,"stopLevel":14,"category":"军事","parentId":null,"sentenceNum":0,"searchId":"557707383372120064","albumSequence":215530094010662,"subIntroduction":"在大海上的权势，是一个国家真正崛起为大国的基础。本栏目将搜集相关文章，结合精心采集的图片资料，讲述各种战舰与潜艇的故事。","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"126","isMap":0,"introduction":null,"follow":null}]
         */

        private int num;
        private String name;
        private List<EssayListBean> essayList;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<EssayListBean> getEssayList() {
            return essayList;
        }

        public void setEssayList(List<EssayListBean> essayList) {
            this.essayList = essayList;
        }

        public static class EssayListBean {
            /**
             * id : 97
             * title : 心迷宫
             * cover : http://web.pythe.cn/xd/freding/topic/images/FnMhvhsLREpaE21vMWMyICGxs4Pi.jpg?imageView2/1/w/750/h/420
             * time : 1553009395000
             * status : 1
             * pv : 10
             * collectNum : 0
             * shareNum : 0
             * format : 0
             * startLevel : 10
             * stopLevel : 16
             * category : 科普
             * parentId : null
             * sentenceNum : 0
             * searchId : 557707353827442688
             * albumSequence : 815530093948430
             * subIntroduction : 人的内心世界如迷宫般难以掌握，心理学的研究对象正是人类的心理现象及其影响下的精神功能和行为活动。随着社会的日益进步，人们
             * authorId : null
             * author : null
             * supply : 纷级阅读
             * plaformIndexId : 150
             * isMap : 0
             * introduction : null
             * follow : null
             */

            private String id;
            private String title;
            private String cover;
            private long time;
            private int status;
            private int pv;
            private int collectNum;
            private int shareNum;
            private int format;
            private int startLevel;
            private int stopLevel;
            private String category;
            private Object parentId;
            private int sentenceNum;
            private String searchId;
            private long albumSequence;
            private String subIntroduction;
            private Object authorId;
            private Object author;
            private String supply;
            private String plaformIndexId;
            private int isMap;
            private Object introduction;
            private Object follow;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
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

            public int getPv() {
                return pv;
            }

            public void setPv(int pv) {
                this.pv = pv;
            }

            public int getCollectNum() {
                return collectNum;
            }

            public void setCollectNum(int collectNum) {
                this.collectNum = collectNum;
            }

            public int getShareNum() {
                return shareNum;
            }

            public void setShareNum(int shareNum) {
                this.shareNum = shareNum;
            }

            public int getFormat() {
                return format;
            }

            public void setFormat(int format) {
                this.format = format;
            }

            public int getStartLevel() {
                return startLevel;
            }

            public void setStartLevel(int startLevel) {
                this.startLevel = startLevel;
            }

            public int getStopLevel() {
                return stopLevel;
            }

            public void setStopLevel(int stopLevel) {
                this.stopLevel = stopLevel;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public Object getParentId() {
                return parentId;
            }

            public void setParentId(Object parentId) {
                this.parentId = parentId;
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

            public long getAlbumSequence() {
                return albumSequence;
            }

            public void setAlbumSequence(long albumSequence) {
                this.albumSequence = albumSequence;
            }

            public String getSubIntroduction() {
                return subIntroduction;
            }

            public void setSubIntroduction(String subIntroduction) {
                this.subIntroduction = subIntroduction;
            }

            public Object getAuthorId() {
                return authorId;
            }

            public void setAuthorId(Object authorId) {
                this.authorId = authorId;
            }

            public Object getAuthor() {
                return author;
            }

            public void setAuthor(Object author) {
                this.author = author;
            }

            public String getSupply() {
                return supply;
            }

            public void setSupply(String supply) {
                this.supply = supply;
            }

            public String getPlaformIndexId() {
                return plaformIndexId;
            }

            public void setPlaformIndexId(String plaformIndexId) {
                this.plaformIndexId = plaformIndexId;
            }

            public int getIsMap() {
                return isMap;
            }

            public void setIsMap(int isMap) {
                this.isMap = isMap;
            }

            public Object getIntroduction() {
                return introduction;
            }

            public void setIntroduction(Object introduction) {
                this.introduction = introduction;
            }

            public Object getFollow() {
                return follow;
            }

            public void setFollow(Object follow) {
                this.follow = follow;
            }
        }
    }
}
