package com.dace.textreader.bean;

import java.util.List;

public class CollectArticleBean {

    /**
     * status : 200
     * msg : OK
     * data : [{"sequence":null,"id":10043399,"type":"国学","title":"两小儿辩日 / 两小儿辩斗","subContent":"孔子东游，见两小儿辩斗，问其故。一儿曰：\u201c我以日始出时去人近，而日中时远也。\u201d一儿以日初出远，而日中时近也。一儿曰：\u201c日","sourceImage":null,"source":null,"flag":0,"level":null,"shareNum":null,"likeNum":null,"collectNum":null,"status":null,"pv":6,"time":null,"audio":null,"image":"http://web.pythe.cn/xd/temp/bk/51555468767713767.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":0,"grade":3,"commentNum":null,"isMachine":null,"isMap":null,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":null,"platformIndexId":null,"appreciationNum":null,"level1":null,"articleAlbumSeq":null,"supply":null,"isAlbum":null,"wordNum":null,"userImage":null},{"sequence":null,"id":10043390,"type":"国学","title":"桃夭","subContent":"桃之夭夭，灼灼其华。之子于归，宜其室家。桃之夭夭，有蕡其实。之子于归，宜其家室。桃之夭夭，其叶蓁蓁。之子于归，宜其家人。","sourceImage":null,"source":null,"flag":0,"level":null,"shareNum":null,"likeNum":null,"collectNum":null,"status":null,"pv":2,"time":null,"audio":null,"image":"http://web.pythe.cn/xd/bk/a8014c086e061d955e24c0a271f40ad163d9cace.jpg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":null,"score":0,"grade":3,"commentNum":null,"isMachine":null,"isMap":null,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":null,"platformIndexId":null,"appreciationNum":null,"level1":null,"articleAlbumSeq":null,"supply":null,"isAlbum":null,"wordNum":null,"userImage":null}]
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
         * sequence : null
         * id : 10043399
         * type : 国学
         * title : 两小儿辩日 / 两小儿辩斗
         * subContent : 孔子东游，见两小儿辩斗，问其故。一儿曰：“我以日始出时去人近，而日中时远也。”一儿以日初出远，而日中时近也。一儿曰：“日
         * sourceImage : null
         * source : null
         * flag : 0
         * level : null
         * shareNum : null
         * likeNum : null
         * collectNum : null
         * status : null
         * pv : 6
         * time : null
         * audio : null
         * image : http://web.pythe.cn/xd/temp/bk/51555468767713767.jpg?imageView2/1/w/750/h/420
         * video : null
         * authorId : null
         * author : null
         * score : 0
         * grade : 3
         * commentNum : null
         * isMachine : null
         * isMap : null
         * dynasty : null
         * gradeId : null
         * videoTime : null
         * audioTime : null
         * searchId : null
         * platformIndexId : null
         * appreciationNum : null
         * level1 : null
         * articleAlbumSeq : null
         * supply : null
         * isAlbum : null
         * wordNum : null
         * userImage : null
         */

        private Object sequence;
        private int id;
        private String type;
        private String title;
        private String subContent;
        private Object sourceImage;
        private Object source;
        private int flag;
        private Object level;
        private Object shareNum;
        private int likeNum;
        private Object collectNum;
        private Object status;
        private int pv;
        private Object time;
        private Object audio;
        private String image;
        private Object video;
        private Object authorId;
        private Object author;
        private int score;
        private int grade;
        private Object commentNum;
        private Object isMachine;
        private Object isMap;
        private Object dynasty;
        private Object gradeId;
        private Object videoTime;
        private Object audioTime;
        private Object searchId;
        private Object platformIndexId;
        private Object appreciationNum;
        private Object level1;
        private Object articleAlbumSeq;
        private Object supply;
        private Object isAlbum;
        private Object wordNum;
        private Object userImage;

        private boolean isEditor;  //是否处于编辑状态
        private boolean isSelected;  //是否是选中状态

        public Object getSequence() {
            return sequence;
        }

        public void setSequence(Object sequence) {
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

        public String getSubContent() {
            return subContent;
        }

        public void setSubContent(String subContent) {
            this.subContent = subContent;
        }

        public Object getSourceImage() {
            return sourceImage;
        }

        public void setSourceImage(Object sourceImage) {
            this.sourceImage = sourceImage;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public Object getLevel() {
            return level;
        }

        public void setLevel(Object level) {
            this.level = level;
        }

        public Object getShareNum() {
            return shareNum;
        }

        public void setShareNum(Object shareNum) {
            this.shareNum = shareNum;
        }

        public int getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(int likeNum) {
            this.likeNum = likeNum;
        }

        public Object getCollectNum() {
            return collectNum;
        }

        public void setCollectNum(Object collectNum) {
            this.collectNum = collectNum;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public int getPv() {
            return pv;
        }

        public void setPv(int pv) {
            this.pv = pv;
        }

        public Object getTime() {
            return time;
        }

        public void setTime(Object time) {
            this.time = time;
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

        public Object getAuthor() {
            return author;
        }

        public void setAuthor(Object author) {
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

        public Object getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(Object commentNum) {
            this.commentNum = commentNum;
        }

        public Object getIsMachine() {
            return isMachine;
        }

        public void setIsMachine(Object isMachine) {
            this.isMachine = isMachine;
        }

        public Object getIsMap() {
            return isMap;
        }

        public void setIsMap(Object isMap) {
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

        public Object getSearchId() {
            return searchId;
        }

        public void setSearchId(Object searchId) {
            this.searchId = searchId;
        }

        public Object getPlatformIndexId() {
            return platformIndexId;
        }

        public void setPlatformIndexId(Object platformIndexId) {
            this.platformIndexId = platformIndexId;
        }

        public Object getAppreciationNum() {
            return appreciationNum;
        }

        public void setAppreciationNum(Object appreciationNum) {
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

        public Object getSupply() {
            return supply;
        }

        public void setSupply(Object supply) {
            this.supply = supply;
        }

        public Object getIsAlbum() {
            return isAlbum;
        }

        public void setIsAlbum(Object isAlbum) {
            this.isAlbum = isAlbum;
        }

        public Object getWordNum() {
            return wordNum;
        }

        public void setWordNum(Object wordNum) {
            this.wordNum = wordNum;
        }

        public Object getUserImage() {
            return userImage;
        }

        public void setUserImage(Object userImage) {
            this.userImage = userImage;
        }

        public boolean isEditor() {
            return isEditor;
        }

        public void setEditor(boolean editor) {
            isEditor = editor;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
