package com.dace.textreader.bean;

public class H5DataBean {
    /**
     * album : {"id":3,"title":"《欧叶妮·格朗台》","cover":"http://web.pythe.cn/xd/freding/topic/images/FhbOXTK5SfzVnJpzVVT2dV6kfrfa.jpg","time":1553009251000,"status":1,"pv":80,"collectNum":0,"shareNum":1,"format":0,"startLevel":14,"stopLevel":16,"category":"名著","parentId":null,"sentenceNum":0,"searchId":"557706749210132480","albumSequence":815530092506405,"subIntroduction":"《欧叶妮·格朗台》是法国批判现实主义小说家巴尔扎克创作的长篇小说，收录于《人间喜剧》。小说叙述了一个金钱毁灭人性和造成家","authorId":null,"author":null,"supply":"纷级阅读","plaformIndexId":"46","isMap":0,"introduction":null,"follow":null}
     * shareList : {"wx":{"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=wx","title":"《欧叶妮·格朗台》第一节（1）"},"weibo":{"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=weibo","title":"《欧叶妮·格朗台》第一节（1）"},"qq":{"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=qq","title":"《欧叶妮·格朗台》第一节（1）"}}
     * type : 美文
     * title : 《欧叶妮·格朗台》第一节（1）
     * subContent : 献给玛丽亚您的肖像是本书最美的点缀；但愿您的芳名在这里是经过祝福的黄杨枝，虽不知摘自哪一棵树，但一定已被宗教圣化
     * audio : null
     * image : http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422
     * video : null
     * authorId : null
     * author : 佚名
     * score : 1029
     * grade : 5
     * isMachine : 1
     * dynasty : null
     * gradeId : null
     * videoTime : null
     * audioTime : null
     * appreciationNum : 2
     * machineAudio : null
     * collectOrNot : 1
     * followOrNot : 0
     */

    private AlbumBean album;
    private ShareListBean shareList;
    private String type;
    private String title;
    private String subContent;
    private Object audio;
    private String image;
    private Object video;
    private Object authorId;
    private String author;
    private int score;
    private int grade;
    private int isMachine;
    private Object dynasty;
    private Object gradeId;
    private Object videoTime;
    private Object audioTime;
    private int appreciationNum;
    private Object machineAudio;
    private int collectOrNot;
    private int followOrNot;

    public AlbumBean getAlbum() {
        return album;
    }

    public void setAlbum(AlbumBean album) {
        this.album = album;
    }

    public ShareListBean getShareList() {
        return shareList;
    }

    public void setShareList(ShareListBean shareList) {
        this.shareList = shareList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public Object getAudio() {
        return audio;
    }

    public void setAudio(Object audio) {
        this.audio = audio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getVideo() {
        return video;
    }

    public void setVideo(Object video) {
        this.video = video;
    }

    public Object getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Object authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getIsMachine() {
        return isMachine;
    }

    public void setIsMachine(int isMachine) {
        this.isMachine = isMachine;
    }

    public Object getDynasty() {
        return dynasty;
    }

    public void setDynasty(Object dynasty) {
        this.dynasty = dynasty;
    }

    public Object getGradeId() {
        return gradeId;
    }

    public void setGradeId(Object gradeId) {
        this.gradeId = gradeId;
    }

    public Object getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(Object videoTime) {
        this.videoTime = videoTime;
    }

    public Object getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(Object audioTime) {
        this.audioTime = audioTime;
    }

    public int getAppreciationNum() {
        return appreciationNum;
    }

    public void setAppreciationNum(int appreciationNum) {
        this.appreciationNum = appreciationNum;
    }

    public Object getMachineAudio() {
        return machineAudio;
    }

    public void setMachineAudio(Object machineAudio) {
        this.machineAudio = machineAudio;
    }

    public int getCollectOrNot() {
        return collectOrNot;
    }

    public void setCollectOrNot(int collectOrNot) {
        this.collectOrNot = collectOrNot;
    }

    public int getFollowOrNot() {
        return followOrNot;
    }

    public void setFollowOrNot(int followOrNot) {
        this.followOrNot = followOrNot;
    }

    public static class AlbumBean {
        /**
         * id : 3
         * title : 《欧叶妮·格朗台》
         * cover : http://web.pythe.cn/xd/freding/topic/images/FhbOXTK5SfzVnJpzVVT2dV6kfrfa.jpg
         * time : 1553009251000
         * status : 1
         * pv : 80
         * collectNum : 0
         * shareNum : 1
         * format : 0
         * startLevel : 14
         * stopLevel : 16
         * category : 名著
         * parentId : null
         * sentenceNum : 0
         * searchId : 557706749210132480
         * albumSequence : 815530092506405
         * subIntroduction : 《欧叶妮·格朗台》是法国批判现实主义小说家巴尔扎克创作的长篇小说，收录于《人间喜剧》。小说叙述了一个金钱毁灭人性和造成家
         * authorId : null
         * author : null
         * supply : 纷级阅读
         * plaformIndexId : 46
         * isMap : 0
         * introduction : null
         * follow : null
         */

        private int id;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

    public static class ShareListBean {
        /**
         * wx : {"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=wx","title":"《欧叶妮·格朗台》第一节（1）"}
         * weibo : {"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=weibo","title":"《欧叶妮·格朗台》第一节（1）"}
         * qq : {"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=qq","title":"《欧叶妮·格朗台》第一节（1）"}
         */

        private WxBean wx;
        private WeiboBean weibo;
        private QqBean qq;

        public WxBean getWx() {
            return wx;
        }

        public void setWx(WxBean wx) {
            this.wx = wx;
        }

        public WeiboBean getWeibo() {
            return weibo;
        }

        public void setWeibo(WeiboBean weibo) {
            this.weibo = weibo;
        }

        public QqBean getQq() {
            return qq;
        }

        public void setQq(QqBean qq) {
            this.qq = qq;
        }

        public static class WxBean {
            /**
             * image : http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=wx
             * title : 《欧叶妮·格朗台》第一节（1）
             */

            private String image;
            private String link;
            private String title;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class WeiboBean {
            /**
             * image : http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=weibo
             * title : 《欧叶妮·格朗台》第一节（1）
             */

            private String image;
            private String link;
            private String title;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class QqBean {
            /**
             * image : http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/422
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8428&gradeId=151&flag=0&isShare=0&py=1&essayId=10000738&version=3.2.6&platForm=android&channel=qq
             * title : 《欧叶妮·格朗台》第一节（1）
             */

            private String image;
            private String link;
            private String title;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
