package com.dace.textreader.bean;

import java.util.List;

public class ReaderLevelBean {
    /**
     * status : 200
     * msg : OK
     * data : {"articleList":[{"sequence":732,"id":10000735,"type":"科普","title":"地球也长个儿？","subContent":"经常听到同学们提出这种好奇的问题：我们生活在地球上，一天天地长大，地球是不是也在长大呢?过去，地球物理界多数人认为，地","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009252000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9306772246112398.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706753316356096","platformIndexId":"151651","appreciationNum":0,"level1":null,"articleAlbumSeq":1,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":739,"id":10000737,"type":"科普","title":"怎样证明地球自转？这里有个神奇的试验","subContent":"1851年的某一天，法国物理学家莱昂·傅科邀请各界名流来到法国巴黎先贤祠，他要在这里进行一次公开的科学实验。先贤祠建成于","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009253000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9307201669559290.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706757804261376","platformIndexId":"155060","appreciationNum":0,"level1":null,"articleAlbumSeq":2,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":749,"id":10000744,"type":"科普","title":"一星期是七天，你有没有问过为什么","subContent":"公元前6000~4000年，在美索不达米亚平原上，生活着一群苏美尔人。他们通过对月亮圆缺的观察，发现由半圆月至满月，时","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009256000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9307532106561536.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706764771000320","platformIndexId":"162635","appreciationNum":0,"level1":null,"articleAlbumSeq":3,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":752,"id":10000754,"type":"科普","title":"星中\u201c美人\u201d\u2014\u2014土星","subContent":"太阳系行星家庭中，土星算是一颗最美丽的星，因为它有一条又宽又亮的光环，就像是圆脑袋上戴一顶帽子，可爱极了。土星的光环是","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009257000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9308451842234948.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706771322503168","platformIndexId":"162675","appreciationNum":0,"level1":null,"articleAlbumSeq":4,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":761,"id":10000759,"type":"科普","title":"NASA命名新星座，哥斯拉、绿巨人都\u201c上天\u201d了","subContent":"美国宇航局命名了约20个新星座，以纪念天体观测卫星\u201c费米\u201d升空10周年，其中包括\u201c哥斯拉\u201d、\u201c绿巨人\u201d和\u201c小王子\u201d等有趣","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009258000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9305245086320258.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706775063822336","platformIndexId":"164447","appreciationNum":0,"level1":null,"articleAlbumSeq":5,"supply":"纷级阅读","isAlbum":1,"userImage":null}],"num":5}
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
         * articleList : [{"sequence":732,"id":10000735,"type":"科普","title":"地球也长个儿？","subContent":"经常听到同学们提出这种好奇的问题：我们生活在地球上，一天天地长大，地球是不是也在长大呢?过去，地球物理界多数人认为，地","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009252000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9306772246112398.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706753316356096","platformIndexId":"151651","appreciationNum":0,"level1":null,"articleAlbumSeq":1,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":739,"id":10000737,"type":"科普","title":"怎样证明地球自转？这里有个神奇的试验","subContent":"1851年的某一天，法国物理学家莱昂·傅科邀请各界名流来到法国巴黎先贤祠，他要在这里进行一次公开的科学实验。先贤祠建成于","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009253000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9307201669559290.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706757804261376","platformIndexId":"155060","appreciationNum":0,"level1":null,"articleAlbumSeq":2,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":749,"id":10000744,"type":"科普","title":"一星期是七天，你有没有问过为什么","subContent":"公元前6000~4000年，在美索不达米亚平原上，生活着一群苏美尔人。他们通过对月亮圆缺的观察，发现由半圆月至满月，时","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009256000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9307532106561536.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706764771000320","platformIndexId":"162635","appreciationNum":0,"level1":null,"articleAlbumSeq":3,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":752,"id":10000754,"type":"科普","title":"星中\u201c美人\u201d\u2014\u2014土星","subContent":"太阳系行星家庭中，土星算是一颗最美丽的星，因为它有一条又宽又亮的光环，就像是圆脑袋上戴一顶帽子，可爱极了。土星的光环是","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009257000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9308451842234948.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706771322503168","platformIndexId":"162675","appreciationNum":0,"level1":null,"articleAlbumSeq":4,"supply":"纷级阅读","isAlbum":1,"userImage":null},{"sequence":761,"id":10000759,"type":"科普","title":"NASA命名新星座，哥斯拉、绿巨人都\u201c上天\u201d了","subContent":"美国宇航局命名了约20个新星座，以纪念天体观测卫星\u201c费米\u201d升空10周年，其中包括\u201c哥斯拉\u201d、\u201c绿巨人\u201d和\u201c小王子\u201d等有趣","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"分级阅读世界","flag":0,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553009258000,"audio":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9305245086320258.jpg?imageView2/1/w/300/h/200","video":null,"authorId":null,"author":"佚名","score":null,"grade":2,"commentNum":0,"isMachine":1,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557706775063822336","platformIndexId":"164447","appreciationNum":0,"level1":null,"articleAlbumSeq":5,"supply":"纷级阅读","isAlbum":1,"userImage":null}]
         * num : 5
         */

        private int num;
        private List<ArticleListBean> articleList;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<ArticleListBean> getArticleList() {
            return articleList;
        }

        public void setArticleList(List<ArticleListBean> articleList) {
            this.articleList = articleList;
        }

        public static class ArticleListBean {
            /**
             * sequence : 732
             * id : 10000735
             * type : 科普
             * title : 地球也长个儿？
             * subContent : 经常听到同学们提出这种好奇的问题：我们生活在地球上，一天天地长大，地球是不是也在长大呢?过去，地球物理界多数人认为，地
             * sourceImage : http://web.pythe.cn/xd/logo/12334555.jpg
             * source : 分级阅读世界
             * flag : 0
             * format : 2
             * shareNum : 0
             * likeNum : 0
             * collectNum : 0
             * status : 1
             * pv : 0
             * time : 1553009252000
             * audio : null
             * image : http://web.pythe.cn/xd/freding/essay/images/9306772246112398.jpg?imageView2/1/w/300/h/200
             * video : null
             * authorId : null
             * author : 佚名
             * score : null
             * grade : 2
             * commentNum : 0
             * isMachine : 1
             * isMap : 0
             * dynasty : null
             * gradeId : null
             * videoTime : null
             * audioTime : null
             * searchId : 557706753316356096
             * platformIndexId : 151651
             * appreciationNum : 0
             * level1 : null
             * articleAlbumSeq : 1
             * supply : 纷级阅读
             * isAlbum : 1
             * userImage : null
             */

            private String sequence;
            private String id;
            private String type;
            private String title;
            private String subContent;
            private String sourceImage;
            private String source;
            private int flag;
            private int format;
            private int shareNum;
            private int likeNum;
            private int collectNum;
            private int status;
            private int pv;
            private long time;
            private String audio;
            private String image;
            private String video;
            private Object authorId;
            private String author;
            private int score;
            private int grade;
            private int commentNum;
            private int isMachine;
            private int isMap;
            private Object dynasty;
            private Object gradeId;
            private Object videoTime;
            private Object audioTime;
            private String searchId;
            private String platformIndexId;
            private int appreciationNum;
            private Object level1;
            private int articleAlbumSeq;
            private String supply;
            private int isAlbum;
            private Object userImage;

            public String getSequence() {
                return sequence;
            }

            public void setSequence(String sequence) {
                this.sequence = sequence;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getSourceImage() {
                return sourceImage;
            }

            public void setSourceImage(String sourceImage) {
                this.sourceImage = sourceImage;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public int getFormat() {
                return format;
            }

            public void setFormat(int format) {
                this.format = format;
            }

            public int getShareNum() {
                return shareNum;
            }

            public void setShareNum(int shareNum) {
                this.shareNum = shareNum;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public void setLikeNum(int likeNum) {
                this.likeNum = likeNum;
            }

            public int getCollectNum() {
                return collectNum;
            }

            public void setCollectNum(int collectNum) {
                this.collectNum = collectNum;
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

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public String getAudio() {
                return audio;
            }

            public void setAudio(String audio) {
                this.audio = audio;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getVideo() {
                return video;
            }

            public void setVideo(String video) {
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

            public int getCommentNum() {
                return commentNum;
            }

            public void setCommentNum(int commentNum) {
                this.commentNum = commentNum;
            }

            public int getIsMachine() {
                return isMachine;
            }

            public void setIsMachine(int isMachine) {
                this.isMachine = isMachine;
            }

            public int getIsMap() {
                return isMap;
            }

            public void setIsMap(int isMap) {
                this.isMap = isMap;
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

            public String getSearchId() {
                return searchId;
            }

            public void setSearchId(String searchId) {
                this.searchId = searchId;
            }

            public String getPlatformIndexId() {
                return platformIndexId;
            }

            public void setPlatformIndexId(String platformIndexId) {
                this.platformIndexId = platformIndexId;
            }

            public int getAppreciationNum() {
                return appreciationNum;
            }

            public void setAppreciationNum(int appreciationNum) {
                this.appreciationNum = appreciationNum;
            }

            public Object getLevel1() {
                return level1;
            }

            public void setLevel1(Object level1) {
                this.level1 = level1;
            }

            public int getArticleAlbumSeq() {
                return articleAlbumSeq;
            }

            public void setArticleAlbumSeq(int articleAlbumSeq) {
                this.articleAlbumSeq = articleAlbumSeq;
            }

            public String getSupply() {
                return supply;
            }

            public void setSupply(String supply) {
                this.supply = supply;
            }

            public int getIsAlbum() {
                return isAlbum;
            }

            public void setIsAlbum(int isAlbum) {
                this.isAlbum = isAlbum;
            }

            public Object getUserImage() {
                return userImage;
            }

            public void setUserImage(Object userImage) {
                this.userImage = userImage;
            }
        }
    }
}
