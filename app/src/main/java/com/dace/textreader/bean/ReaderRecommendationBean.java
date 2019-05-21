package com.dace.textreader.bean;

import java.util.List;

public class ReaderRecommendationBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"sentenceNum":0,"articleList":[{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000738,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"《欧叶妮·格朗台》第一节（1）","subContent":"献给玛丽亚您的肖像是本书最美的点缀；但愿您的芳名在这里是经过祝福的黄杨枝，虽不知摘自哪一棵树，但一定已被宗教圣化"},{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8334313777805950.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000751,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"《欧叶妮·格朗台》第一节（2）","subContent":"遇到好年景，他能算出箍桶匠们总共需要多少板材，计算之准确，误差不超过一两块板材。一天阳光能教他发财，一场恶雨能让他亏本。"}],"format":0,"albumId":3,"albumTitle":"《欧叶妮·格朗台》","albumCover":"http://web.pythe.cn/xd/freding/topic/images/FhbOXTK5SfzVnJpzVVT2dV6kfrfa.jpg?imageView2/1/w/375/h/210","startLevel":14,"endLevel":16},{"sentenceNum":0,"articleList":[{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9221908827116023.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000761,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"被告上法庭的美国911接线员","subContent":"在美国，遇到任何麻烦，都可以拨打911报警急救电话，电话那头的调度员会根据计算机屏幕上显示的地点，联系急救单位进行营救。"},{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/9224087692716884.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000764,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"丁鹏：对不起，我本科不是北大的","subContent":"考上北大研究生后，常被问\u201c你本科是北大的吗\u201d？我能猜到对方已经备好的夸赞词。但我不得不将对方的期待打个折扣，\u201c不好意思，"}],"format":0,"albumId":2,"albumTitle":"人间百态","albumCover":"https://check.pythe.cn:446/weike/images/2019/3/22/41553218413421284.jpeg?imageView2/1/w/375/h/210","startLevel":6,"endLevel":6},{"sentenceNum":0,"articleList":[{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8508029318530789.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000745,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"世界史上规模最大的内战 ：太平天国之上帝爱疯狂（一）","subContent":"洪秀全，原名洪仁坤，广东花县人。他爹是邻近诸村的保正，家里薄有田产，社会地位和经济条件在当地是好的。他有两个哥哥，三兄弟"},{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8508618096980445.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000750,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"世界史上规模最大的内战 ：太平天国之天齐与享乐（二）","subContent":"中国民族革命的洪流，起伏约200年，太平天国最终成在两广地区，它的发展是有其深层次原因的。英国在广东受了多年压迫，对于广"}],"format":0,"albumId":4,"albumTitle":"太平天国","albumCover":"https://check.pythe.cn:446/weike/images/2019/3/22/31553236315144505.jpeg?imageView2/1/w/375/h/210","startLevel":6,"endLevel":6},{"sentenceNum":0,"articleList":[{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8415068051251506.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000733,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"日本到底是什么？","subContent":"中日甲午战争中，中国被日本打败，签署《马关条约》，割让了台湾，并付出了两亿两白银的赔款。此前的战争都没给中国带来震撼。但"},{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8417087985522314.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000742,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"王维诗歌的世界影响","subContent":"唐代诗人王维是对世界影响最大的中国诗人之一。一、王维对亚洲的影响受王维影响，日本汉诗创作形成了\u201c禅诗\u201d风格，日本僧人"}],"format":0,"albumId":5,"albumTitle":"史海综评","albumCover":"http://web.pythe.cn/xd/freding/topic/images/FnGUDkv4Z287ihczY95ItxJcCbDi.jpg?imageView2/1/w/375/h/210","startLevel":4,"endLevel":16}]
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
         * sentenceNum : 0
         * articleList : [{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000738,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"《欧叶妮·格朗台》第一节（1）","subContent":"献给玛丽亚您的肖像是本书最美的点缀；但愿您的芳名在这里是经过祝福的黄杨枝，虽不知摘自哪一棵树，但一定已被宗教圣化"},{"score":null,"image":"http://web.pythe.cn/xd/freding/essay/images/8334313777805950.jpg?imageView2/1/w/750/h/420","flag":0,"articleId":10000751,"format":0,"source":"分级阅读世界","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"《欧叶妮·格朗台》第一节（2）","subContent":"遇到好年景，他能算出箍桶匠们总共需要多少板材，计算之准确，误差不超过一两块板材。一天阳光能教他发财，一场恶雨能让他亏本。"}]
         * format : 0
         * albumId : 3
         * albumTitle : 《欧叶妮·格朗台》
         * albumCover : http://web.pythe.cn/xd/freding/topic/images/FhbOXTK5SfzVnJpzVVT2dV6kfrfa.jpg?imageView2/1/w/375/h/210
         * startLevel : 14
         * endLevel : 16
         */

        private int sentenceNum;
        private int format;
        private String albumId;
        private String albumTitle;
        private String albumCover;
        private int startLevel;
        private int endLevel;
        private List<ArticleListBean> articleList;

        public int getSentenceNum() {
            return sentenceNum;
        }

        public void setSentenceNum(int sentenceNum) {
            this.sentenceNum = sentenceNum;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public String getAlbumTitle() {
            return albumTitle;
        }

        public void setAlbumTitle(String albumTitle) {
            this.albumTitle = albumTitle;
        }

        public String getAlbumCover() {
            return albumCover;
        }

        public void setAlbumCover(String albumCover) {
            this.albumCover = albumCover;
        }

        public int getStartLevel() {
            return startLevel;
        }

        public void setStartLevel(int startLevel) {
            this.startLevel = startLevel;
        }

        public int getEndLevel() {
            return endLevel;
        }

        public void setEndLevel(int endLevel) {
            this.endLevel = endLevel;
        }

        public List<ArticleListBean> getArticleList() {
            return articleList;
        }

        public void setArticleList(List<ArticleListBean> articleList) {
            this.articleList = articleList;
        }

        public static class ArticleListBean {
            /**
             * score : null
             * image : http://web.pythe.cn/xd/freding/essay/images/8334118396632161.jpg?imageView2/1/w/750/h/420
             * flag : 0
             * articleId : 10000738
             * format : 0
             * source : 分级阅读世界
             * sourceImage : http://web.pythe.cn/xd/logo/12334555.jpg
             * title : 《欧叶妮·格朗台》第一节（1）
             * subContent : 献给玛丽亚您的肖像是本书最美的点缀；但愿您的芳名在这里是经过祝福的黄杨枝，虽不知摘自哪一棵树，但一定已被宗教圣化
             */

            private int score;
            private String image;
            private int flag;
            private String articleId;
            private int format;
            private String source;
            private String sourceImage;
            private String title;
            private String subContent;
            private String fenlei;

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public String getArticleId() {
                return articleId;
            }

            public void setArticleId(String articleId) {
                this.articleId = articleId;
            }

            public int getFormat() {
                return format;
            }

            public void setFormat(int format) {
                this.format = format;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getFenlei() {
                return fenlei;
            }

            public void setFenlei(String fenlei) {
                this.fenlei = fenlei;
            }

            public String getSourceImage() {
                return sourceImage;
            }

            public void setSourceImage(String sourceImage) {
                this.sourceImage = sourceImage;
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
        }
    }
}
