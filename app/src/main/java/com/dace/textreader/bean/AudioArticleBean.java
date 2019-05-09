package com.dace.textreader.bean;

import java.util.List;

public class AudioArticleBean {

    /**
     * status : 200
     * msg : OK
     * data : {"collectOrNot":0,"album":null,"essay":{"sequence":167820808720,"id":10000004,"type":"故事","title":"玛蒂娜和小猫","subContent":"绘本奇幻世界能促进青少年儿童语言发展，艺术情操，阅读兴趣，提高审美，开阔视野和创造能力。","sourceImage":"http://web.pythe.cn/xd/logo/default6.jpg","source":"绘本世界","flag":1,"level":0,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":7,"time":1555925452000,"audio":"http://media.pythe.cn/xd/feixiang/audio/4224096864805734.mp3","image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":"佚名","score":200,"grade":1,"commentNum":0,"isMachine":0,"isMap":1,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682435488350208","platformIndexId":"2cab8587-88bb-480d-bf8f-4620d973117f","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"wordNum":6,"userImage":null,"content":null,"recommendation":null,"guidance":null,"machineAudio":null,"contentList":[{"page":1,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4218229955002505.jpg","second":4},{"page":2,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220501240239044.jpg","second":11},{"page":3,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220536871183771.jpg","second":20},{"page":4,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4221990459483210.jpg","second":29},{"page":5,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4222029410585873.jpg","second":37},{"page":6,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223223132151591.jpg","second":48},{"page":7,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223249718166002.jpg","second":53},{"page":8,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223328074939254.jpg","second":61},{"page":9,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223356148825139.jpg","second":76},{"page":10,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223396920891662.jpg","second":0}],"machineAudioList":null},"shareList":{"wx":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=wx","title":"玛蒂娜和小猫"},"weibo":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=weibo","title":"玛蒂娜和小猫"},"qq":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=qq","title":"玛蒂娜和小猫"}}}
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
         * collectOrNot : 0
         * album : null
         * essay : {"sequence":167820808720,"id":10000004,"type":"故事","title":"玛蒂娜和小猫","subContent":"绘本奇幻世界能促进青少年儿童语言发展，艺术情操，阅读兴趣，提高审美，开阔视野和创造能力。","sourceImage":"http://web.pythe.cn/xd/logo/default6.jpg","source":"绘本世界","flag":1,"level":0,"shareNum":0,"likeNum":0,"collectNum":0,"status":1,"pv":7,"time":1555925452000,"audio":"http://media.pythe.cn/xd/feixiang/audio/4224096864805734.mp3","image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/750/h/420","video":null,"authorId":null,"author":"佚名","score":200,"grade":1,"commentNum":0,"isMachine":0,"isMap":1,"dynasty":null,"gradeId":null,"videoTime":null,"audioTime":null,"searchId":"557682435488350208","platformIndexId":"2cab8587-88bb-480d-bf8f-4620d973117f","appreciationNum":0,"level1":null,"articleAlbumSeq":null,"supply":"飞象绘本","isAlbum":0,"wordNum":6,"userImage":null,"content":null,"recommendation":null,"guidance":null,"machineAudio":null,"contentList":[{"page":1,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4218229955002505.jpg","second":4},{"page":2,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220501240239044.jpg","second":11},{"page":3,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220536871183771.jpg","second":20},{"page":4,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4221990459483210.jpg","second":29},{"page":5,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4222029410585873.jpg","second":37},{"page":6,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223223132151591.jpg","second":48},{"page":7,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223249718166002.jpg","second":53},{"page":8,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223328074939254.jpg","second":61},{"page":9,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223356148825139.jpg","second":76},{"page":10,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223396920891662.jpg","second":0}],"machineAudioList":null}
         * shareList : {"wx":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=wx","title":"玛蒂娜和小猫"},"weibo":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=weibo","title":"玛蒂娜和小猫"},"qq":{"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=qq","title":"玛蒂娜和小猫"}}
         */

        private int collectOrNot;
        private Object album;
        private EssayBean essay;
        private ShareListBean shareList;

        public int getCollectOrNot() {
            return collectOrNot;
        }

        public void setCollectOrNot(int collectOrNot) {
            this.collectOrNot = collectOrNot;
        }

        public Object getAlbum() {
            return album;
        }

        public void setAlbum(Object album) {
            this.album = album;
        }

        public EssayBean getEssay() {
            return essay;
        }

        public void setEssay(EssayBean essay) {
            this.essay = essay;
        }

        public ShareListBean getShareList() {
            return shareList;
        }

        public void setShareList(ShareListBean shareList) {
            this.shareList = shareList;
        }

        public static class EssayBean {
            /**
             * sequence : 167820808720
             * id : 10000004
             * type : 故事
             * title : 玛蒂娜和小猫
             * subContent : 绘本奇幻世界能促进青少年儿童语言发展，艺术情操，阅读兴趣，提高审美，开阔视野和创造能力。
             * sourceImage : http://web.pythe.cn/xd/logo/default6.jpg
             * source : 绘本世界
             * flag : 1
             * level : 0
             * shareNum : 0
             * likeNum : 0
             * collectNum : 0
             * status : 1
             * pv : 7
             * time : 1555925452000
             * audio : http://media.pythe.cn/xd/feixiang/audio/4224096864805734.mp3
             * image : http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/750/h/420
             * video : null
             * authorId : null
             * author : 佚名
             * score : 200
             * grade : 1
             * commentNum : 0
             * isMachine : 0
             * isMap : 1
             * dynasty : null
             * gradeId : null
             * videoTime : null
             * audioTime : null
             * searchId : 557682435488350208
             * platformIndexId : 2cab8587-88bb-480d-bf8f-4620d973117f
             * appreciationNum : 0
             * level1 : null
             * articleAlbumSeq : null
             * supply : 飞象绘本
             * isAlbum : 0
             * wordNum : 6
             * userImage : null
             * content : null
             * recommendation : null
             * guidance : null
             * machineAudio : null
             * contentList : [{"page":1,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4218229955002505.jpg","second":4},{"page":2,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220501240239044.jpg","second":11},{"page":3,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4220536871183771.jpg","second":20},{"page":4,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4221990459483210.jpg","second":29},{"page":5,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4222029410585873.jpg","second":37},{"page":6,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223223132151591.jpg","second":48},{"page":7,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223249718166002.jpg","second":53},{"page":8,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223328074939254.jpg","second":61},{"page":9,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223356148825139.jpg","second":76},{"page":10,"subContent":"","pic":"http://web.pythe.cn/xd/feixiang/images/4223396920891662.jpg","second":0}]
             * machineAudioList : null
             */

            private long sequence;
            private int id;
            private String type;
            private String title;
            private String subContent;
            private String sourceImage;
            private String source;
            private int flag;
            private int level;
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
            private Object articleAlbumSeq;
            private String supply;
            private int isAlbum;
            private int wordNum;
            private Object userImage;
            private Object content;
            private Object recommendation;
            private Object guidance;
            private Object machineAudio;
            private Object machineAudioList;
            private List<ContentListBean> contentList;

            public long getSequence() {
                return sequence;
            }

            public void setSequence(long sequence) {
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

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
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

            public int getWordNum() {
                return wordNum;
            }

            public void setWordNum(int wordNum) {
                this.wordNum = wordNum;
            }

            public Object getUserImage() {
                return userImage;
            }

            public void setUserImage(Object userImage) {
                this.userImage = userImage;
            }

            public Object getContent() {
                return content;
            }

            public void setContent(Object content) {
                this.content = content;
            }

            public Object getRecommendation() {
                return recommendation;
            }

            public void setRecommendation(Object recommendation) {
                this.recommendation = recommendation;
            }

            public Object getGuidance() {
                return guidance;
            }

            public void setGuidance(Object guidance) {
                this.guidance = guidance;
            }

            public Object getMachineAudio() {
                return machineAudio;
            }

            public void setMachineAudio(Object machineAudio) {
                this.machineAudio = machineAudio;
            }

            public Object getMachineAudioList() {
                return machineAudioList;
            }

            public void setMachineAudioList(Object machineAudioList) {
                this.machineAudioList = machineAudioList;
            }

            public List<ContentListBean> getContentList() {
                return contentList;
            }

            public void setContentList(List<ContentListBean> contentList) {
                this.contentList = contentList;
            }

            public static class ContentListBean {
                /**
                 * page : 1
                 * subContent :
                 * pic : http://web.pythe.cn/xd/feixiang/images/4218229955002505.jpg
                 * second : 4
                 */

                private int page;
                private String subContent;
                private String pic;
                private int second;

                public int getPage() {
                    return page;
                }

                public void setPage(int page) {
                    this.page = page;
                }

                public String getSubContent() {
                    return subContent;
                }

                public void setSubContent(String subContent) {
                    this.subContent = subContent;
                }

                public String getPic() {
                    return pic;
                }

                public void setPic(String pic) {
                    this.pic = pic;
                }

                public int getSecond() {
                    return second;
                }

                public void setSecond(int second) {
                    this.second = second;
                }
            }
        }

        public static class ShareListBean {
            /**
             * wx : {"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=wx","title":"玛蒂娜和小猫"}
             * weibo : {"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=weibo","title":"玛蒂娜和小猫"}
             * qq : {"image":"http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200","link":"https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=qq","title":"玛蒂娜和小猫"}
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
                 * image : http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200
                 * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=wx
                 * title : 玛蒂娜和小猫
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
                 * image : http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200
                 * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=weibo
                 * title : 玛蒂娜和小猫
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
                 * image : http://web.pythe.cn/xd/replace/2019/4/22/81555925449129609.jpeg?imageView2/1/w/200/h/200
                 * link : https://check.pythe.cn/1readingModule/essayShare.html?studentId=7826&gradeId=-1&flag=1&isShare=1&py=100&essayId=qB5A1A8A4A2A2A2E1&version=2.1.2&platForm=ios&channel=qq
                 * title : 玛蒂娜和小猫
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
}
