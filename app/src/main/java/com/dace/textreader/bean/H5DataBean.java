package com.dace.textreader.bean;

import java.util.List;

public class H5DataBean {

    /**
     * album : null
     * shareList : {"wx":{"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=wx","title":"七绝·为女民兵题照"},"weibo":{"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=weibo","title":"七绝·为女民兵题照"},"qq":{"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=qq","title":"七绝·为女民兵题照"}}
     * type : 国学
     * title : 七绝·为女民兵题照
     * subContent : 飒爽英姿五尺枪，曙光初照演兵场。中华儿女多奇志，不爱红装爱武装。
     * audio : null
     * image : http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/750/h/500
     * video : https://second-know.cdn.bcebos.com/media/mda-XQWOfxzUdcVRlrsL/861096758db6e309850da994c2d7fb73.mp4?auth_key=1557287199-945237-0-09d57ad5307f1a7d30a2ed44e52d3534&platform=hanyu
     * authorId : 15865
     * author : 毛泽东
     * score : 0
     * grade : 3
     * isMachine : 0
     * dynasty : 近现代
     * gradeId : null
     * videoTime : 00:47
     * audioTime : null
     * appreciationNum : 0
     * machineAudio : null
     * collectOrNot : 0
     * machineAudioList : [{"duration":null,"name":null,"audio":"http://media.pythe.cn/xd/bdshiwen/audio/932016056265028.mp3"}]
     */

    private Object album;
    private ShareListBean shareList;
    private String type;
    private String title;
    private String subContent;
    private Object audio;
    private String image;
    private String video;
    private int authorId;
    private String author;
    private int score;
    private int grade;
    private int isMachine;
    private String dynasty;
    private Object gradeId;
    private String videoTime;
    private Object audioTime;
    private int appreciationNum;
    private Object machineAudio;
    private int collectOrNot;
    private List<MachineAudioListBean> machineAudioList;

    public Object getAlbum() {
        return album;
    }

    public void setAlbum(Object album) {
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
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

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public Object getGradeId() {
        return gradeId;
    }

    public void setGradeId(Object gradeId) {
        this.gradeId = gradeId;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
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

    public List<MachineAudioListBean> getMachineAudioList() {
        return machineAudioList;
    }

    public void setMachineAudioList(List<MachineAudioListBean> machineAudioList) {
        this.machineAudioList = machineAudioList;
    }

    public static class ShareListBean {
        /**
         * wx : {"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=wx","title":"七绝·为女民兵题照"}
         * weibo : {"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=weibo","title":"七绝·为女民兵题照"}
         * qq : {"image":"http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=qq","title":"七绝·为女民兵题照"}
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
             * image : http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=wx
             * title : 七绝·为女民兵题照
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
             * image : http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=weibo
             * title : 七绝·为女民兵题照
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
             * image : http://web.pythe.cn/xd/bdshiwen/image/60910133083093.jpg?imageView2/1/w/200/h/200
             * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=8429&gradeId=122&flag=0&isShare=1&py=1&essayId=0B3A2B3G1B0B0H5H3&version=3.2.6&platForm=android&channel=qq
             * title : 七绝·为女民兵题照
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
         * duration : null
         * name : null
         * audio : http://media.pythe.cn/xd/bdshiwen/audio/932016056265028.mp3
         */

        private Object duration;
        private Object name;
        private String audio;

        public Object getDuration() {
            return duration;
        }

        public void setDuration(Object duration) {
            this.duration = duration;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
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
