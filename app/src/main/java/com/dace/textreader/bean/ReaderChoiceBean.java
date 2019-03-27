package com.dace.textreader.bean;

import java.util.List;

public class ReaderChoiceBean {
    /**
     * status : 200
     * msg : OK
     * data : {"num":5,"name":"精选推荐","essayList":[{"sequence":1,"id":10000002,"type":"故事","title":"小袋袋，当心","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":3,"time":1553003453000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3274969654999945.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3273931011477597.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682432124518400","platformIndexId":"05dbb430-7a2c-4be8-b478-82b1cfa1a6e2","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":2,"id":10000004,"type":"故事","title":"玛蒂娜和小猫","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003454000,"audio":"http://media.pythe.cn/xd/feixiang/audio/4224096864805734.mp3","image":"http://web.pythe.cn/xd/feixiang/images/4223403342919993.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682435488350208","platformIndexId":"2cab8587-88bb-480d-bf8f-4620d973117f","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":3,"id":10000003,"type":"故事","title":"米菲在动物园","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003453000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3277039608685868.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3276393694415170.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682432766246912","platformIndexId":"c956b1c9-634c-4921-877e-0e5eaab32a75","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":4,"id":10000005,"type":"故事","title":"艾薇的礼物","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003455000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3279130433415143.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3278135798968316.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682438172704768","platformIndexId":"b55b9cc2-f787-4e20-855a-c528685e2729","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":5,"id":10000006,"type":"故事","title":"大卫，不可以","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553003455000,"audio":"http://media.pythe.cn/xd/feixiang/audio/1017863874990144.mp3","image":"http://web.pythe.cn/xd/feixiang/images/1016658052352734.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682440081113088","platformIndexId":"1a3e797e-63c1-4463-8dc4-ebfbf0f2cae5","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null}]}
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
         * essayList : [{"sequence":1,"id":10000002,"type":"故事","title":"小袋袋，当心","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":3,"time":1553003453000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3274969654999945.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3273931011477597.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682432124518400","platformIndexId":"05dbb430-7a2c-4be8-b478-82b1cfa1a6e2","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":2,"id":10000004,"type":"故事","title":"玛蒂娜和小猫","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003454000,"audio":"http://media.pythe.cn/xd/feixiang/audio/4224096864805734.mp3","image":"http://web.pythe.cn/xd/feixiang/images/4223403342919993.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682435488350208","platformIndexId":"2cab8587-88bb-480d-bf8f-4620d973117f","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":3,"id":10000003,"type":"故事","title":"米菲在动物园","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003453000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3277039608685868.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3276393694415170.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":2,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682432766246912","platformIndexId":"c956b1c9-634c-4921-877e-0e5eaab32a75","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":4,"id":10000005,"type":"故事","title":"艾薇的礼物","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":1,"time":1553003455000,"audio":"http://media.pythe.cn/xd/feixiang/audio/3279130433415143.mp3","image":"http://web.pythe.cn/xd/feixiang/images/3278135798968316.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682438172704768","platformIndexId":"b55b9cc2-f787-4e20-855a-c528685e2729","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null},{"sequence":5,"id":10000006,"type":"故事","title":"大卫，不可以","subContent":null,"sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","source":"绘本世界","flag":1,"format":2,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":0,"time":1553003455000,"audio":"http://media.pythe.cn/xd/feixiang/audio/1017863874990144.mp3","image":"http://web.pythe.cn/xd/feixiang/images/1016658052352734.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":null,"grade":1,"commentNum":0,"isMachine":0,"isMap":0,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682440081113088","platformIndexId":"1a3e797e-63c1-4463-8dc4-ebfbf0f2cae5","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"userImage":null}]
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
             * sequence : 1
             * id : 10000002
             * type : 故事
             * title : 小袋袋，当心
             * subContent : null
             * sourceImage : http://web.pythe.cn/xd/logo/12334555.jpg
             * source : 绘本世界
             * flag : 1
             * format : 2
             * shareNum : 0
             * likeNum : 0
             * collectNum : 0
             * status : 1
             * pv : 3
             * time : 1553003453000
             * audio : http://media.pythe.cn/xd/feixiang/audio/3274969654999945.mp3
             * image : http://web.pythe.cn/xd/feixiang/images/3273931011477597.jpg?imageView2/1/w/750/h/420
             * video : null
             * authorId : null
             * author : null
             * score : null
             * grade : 1
             * commentNum : 0
             * isMachine : 0
             * isMap : 2
             * dynasty : null
             * gradeId : null
             * videoTime : null
             * audioTime : null
             * searchId : 557682432124518400
             * platformIndexId : 05dbb430-7a2c-4be8-b478-82b1cfa1a6e2
             * appreciationNum : 0
             * level1 : null
             * articleAlbumSeq : null
             * supply : 飞象绘本
             * isAlbum : 0
             * userImage : null
             */

            private int sequence;
            private int id;
            private String type;
            private String title;
            private Object subContent;
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
            private Object video;
            private Object authorId;
            private Object author;
            private Object score;
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
            private Object articleAlbumSeq;
            private String supply;
            private int isAlbum;
            private Object userImage;

            public int getSequence() {
                return sequence;
            }

            public void setSequence(int sequence) {
                this.sequence = sequence;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
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

            public Object getSubContent() {
                return subContent;
            }

            public void setSubContent(Object subContent) {
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

            public Object getAuthor() {
                return author;
            }

            public void setAuthor(Object author) {
                this.author = author;
            }

            public Object getScore() {
                return score;
            }

            public void setScore(Object score) {
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

            public Object getArticleAlbumSeq() {
                return articleAlbumSeq;
            }

            public void setArticleAlbumSeq(Object articleAlbumSeq) {
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
