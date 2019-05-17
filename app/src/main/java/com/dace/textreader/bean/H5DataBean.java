package com.dace.textreader.bean;

import java.util.List;

public class H5DataBean {

    /**
     * album : {"id":1220,"title":"外星人邀你来做客","cover":"http://web.pythe.cn/xd/replace/2019/4/22/51555922754789312.jpeg","time":1555922758000,"status":1,"pv":1,"collectNum":0,"shareNum":0,"format":0,"startLevel":1,"stopLevel":6,"category":"故事","parentId":null,"sentenceNum":0,"searchId":"568866964961886208","albumSequence":2,"subIntroduction":"古今中外一直有关于外星人的遐想，但现今人类还无法实际探查是否有外星人存在，虽然一直以来，很多人声称自己见证外星人造访地球","authorId":null,"author":null,"supply":"考拉","plaformIndexId":"8","isMap":1,"introduction":null,"follow":null}
     * shareList : {"wx":{"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=wx","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"},"weibo":{"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=weibo","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"},"qq":{"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=qq","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"}}
     * type : 历史
     * title : 好紧张……第一次看到飞碟该怎么办
     * subContent : 如果你对天文学有过接触，就一定会知道，在太阳系的行星和卫星中，除了地球以外，其他星球均不可能有智能生命存在。宇宙中若有外
     * audio : null
     * image : http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/750/h/500
     * video : null
     * authorId : null
     * author : 佚名
     * score : 478
     * grade : 2
     * isMachine : 0
     * dynasty : null
     * gradeId : null
     * videoTime : null
     * audioTime : null
     * appreciationNum : 0
     * machineAudio : null
     * collectOrNot : 0
     * followOrNot : 0
     * machineAudioList : [{"duration":"08:41","name":"milf","audio":"http://media.pythe.cn/machine/audio/article/2/615553795964036.mp3"}]
     */

    private AlbumBean album;
    private ShareListBean shareList;
    private String type;
    private String title;
    private String subContent;
    private Object audio;
    private String image;
    private String video;
    private String authorId;
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
    private List<MachineAudioListBean> machineAudioList;

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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
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

    public List<MachineAudioListBean> getMachineAudioList() {
        return machineAudioList;
    }

    public void setMachineAudioList(List<MachineAudioListBean> machineAudioList) {
        this.machineAudioList = machineAudioList;
    }

    public static class AlbumBean {
        /**
         * id : 1220
         * title : 外星人邀你来做客
         * cover : http://web.pythe.cn/xd/replace/2019/4/22/51555922754789312.jpeg
         * time : 1555922758000
         * status : 1
         * pv : 1
         * collectNum : 0
         * shareNum : 0
         * format : 0
         * startLevel : 1
         * stopLevel : 6
         * category : 故事
         * parentId : null
         * sentenceNum : 0
         * searchId : 568866964961886208
         * albumSequence : 2
         * subIntroduction : 古今中外一直有关于外星人的遐想，但现今人类还无法实际探查是否有外星人存在，虽然一直以来，很多人声称自己见证外星人造访地球
         * authorId : null
         * author : null
         * supply : 考拉
         * plaformIndexId : 8
         * isMap : 1
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
        private String sentenceNum;
        private String searchId;
        private String albumSequence;
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

        public String getSentenceNum() {
            return sentenceNum;
        }

        public void setSentenceNum(String sentenceNum) {
            this.sentenceNum = sentenceNum;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public String getAlbumSequence() {
            return albumSequence;
        }

        public void setAlbumSequence(String albumSequence) {
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
         * wx : {"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=wx","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"}
         * weibo : {"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=weibo","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"}
         * qq : {"image":"http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=qq","title":"好紧张\u2026\u2026第一次看到飞碟该怎么办"}
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
             * image : http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=wx
             * title : 好紧张……第一次看到飞碟该怎么办
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
             * image : http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=weibo
             * title : 好紧张……第一次看到飞碟该怎么办
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
             * image : http://web.pythe.cn/xd/kl/images/7750289980061528.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=-1&gradeId=-1&flag=0&isShare=1&py=1&essayId=jB0A9D6E1F4E5G1F1&version=3.2.6&platForm=android&channel=qq
             * title : 好紧张……第一次看到飞碟该怎么办
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

    public static class MachineAudioListBean {
        /**
         * duration : 08:41
         * name : milf
         * audio : http://media.pythe.cn/machine/audio/article/2/615553795964036.mp3
         */

        private String duration;
        private String name;
        private String audio;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }
    }
}
