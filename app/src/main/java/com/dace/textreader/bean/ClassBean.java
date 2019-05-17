package com.dace.textreader.bean;

import java.util.List;

public class ClassBean {

    /**
     * status : 200
     * msg : OK
     * data : [{"title":"春夏秋冬","authorId":null,"articleAlbumSeq":1,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.1.jpg?imageView2/1/w/300/h/200","id":10346045,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"姓氏歌","authorId":null,"articleAlbumSeq":2,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.2.jpg?imageView2/1/w/300/h/200","id":10346044,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"小青蛙","authorId":null,"articleAlbumSeq":3,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.3.jpg?imageView2/1/w/300/h/200","id":10346043,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"猜字谜","authorId":null,"articleAlbumSeq":4,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.4.jpg?imageView2/1/w/300/h/200","id":10346042,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"动物儿歌","authorId":null,"articleAlbumSeq":5,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.5.jpg?imageView2/1/w/300/h/200","id":10346041,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"古对今","authorId":null,"articleAlbumSeq":6,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.6.jpg?imageView2/1/w/300/h/200","id":10346040,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"操场上","authorId":null,"articleAlbumSeq":7,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.7.jpg?imageView2/1/w/300/h/200","id":10346039,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"人之初","authorId":null,"articleAlbumSeq":8,"author":null,"image":"http://web.pythe.cn/image/kewen/112/0.8.jpg?imageView2/1/w/300/h/200","id":10346038,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"吃水不忘挖井人","authorId":null,"articleAlbumSeq":9,"author":null,"image":"http://web.pythe.cn/image/kewen/112/1.jpg?imageView2/1/w/300/h/200","id":10346037,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"我多想去看看","authorId":null,"articleAlbumSeq":10,"author":"王宝柱","image":"http://web.pythe.cn/image/kewen/112/2.jpg?imageView2/1/w/300/h/200","id":10346036,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"一个接一个","authorId":null,"articleAlbumSeq":11,"author":"金子美铃","image":"http://web.pythe.cn/image/kewen/112/3.jpg?imageView2/1/w/300/h/200","id":10346035,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"四个太阳","authorId":null,"articleAlbumSeq":12,"author":"夏辇生","image":"http://web.pythe.cn/image/kewen/112/4.jpg?imageView2/1/w/300/h/200","id":10346034,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"小公鸡和小鸭子","authorId":null,"articleAlbumSeq":13,"author":null,"image":"http://web.pythe.cn/image/kewen/112/5.jpg?imageView2/1/w/300/h/200","id":10346033,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"树和喜鹊","authorId":null,"articleAlbumSeq":14,"author":"金波","image":"http://web.pythe.cn/image/kewen/112/6.jpg?imageView2/1/w/300/h/200","id":10346032,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"怎么都快乐","authorId":null,"articleAlbumSeq":15,"author":"任溶溶","image":"http://web.pythe.cn/image/kewen/112/7.jpg?imageView2/1/w/300/h/200","id":10346031,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"夜色","authorId":null,"articleAlbumSeq":16,"author":"柯岩","image":"http://web.pythe.cn/image/kewen/112/9.jpg?imageView2/1/w/300/h/200","id":10346030,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"端午粽","authorId":null,"articleAlbumSeq":17,"author":"屠再华","image":"http://web.pythe.cn/image/kewen/112/10.jpg?imageView2/1/w/300/h/200","id":10346029,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"彩虹","authorId":null,"articleAlbumSeq":18,"author":"韦其麟","image":"http://web.pythe.cn/image/kewen/112/11.jpg?imageView2/1/w/300/h/200","id":10346028,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"古诗二首（池上）","authorId":2478,"articleAlbumSeq":19,"author":"白居易","image":"http://web.pythe.cn/image/kewen/112/12.1.jpg?imageView2/1/w/300/h/200","id":10346027,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"古诗二首（小池）","authorId":5081,"articleAlbumSeq":20,"author":"杨万里","image":"http://web.pythe.cn/image/kewen/112/12.2.jpg?imageView2/1/w/300/h/200","id":10346026,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"荷叶圆圆","authorId":null,"articleAlbumSeq":21,"author":"胡木仁","image":"http://web.pythe.cn/image/kewen/112/13.jpg?imageView2/1/w/300/h/200","id":10346025,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"要下雨了","authorId":null,"articleAlbumSeq":22,"author":"罗亚","image":"http://web.pythe.cn/image/kewen/112/14.jpg?imageView2/1/w/300/h/200","id":10346024,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"文具的家","authorId":null,"articleAlbumSeq":23,"author":"圣野","image":"http://web.pythe.cn/image/kewen/112/15.jpg?imageView2/1/w/300/h/200","id":10346023,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"一分钟","authorId":null,"articleAlbumSeq":24,"author":"鲁兵","image":"http://web.pythe.cn/image/kewen/112/16.jpg?imageView2/1/w/300/h/200","id":10346022,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"动物王国开大会","authorId":null,"articleAlbumSeq":25,"author":"嵇鸿","image":"http://web.pythe.cn/image/kewen/112/17.jpg?imageView2/1/w/300/h/200","id":10346021,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"小猴子下山","authorId":null,"articleAlbumSeq":26,"author":null,"image":"http://web.pythe.cn/image/kewen/112/18.jpg?imageView2/1/w/300/h/200","id":10346020,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"棉花姑娘","authorId":null,"articleAlbumSeq":27,"author":null,"image":"http://web.pythe.cn/image/kewen/112/19.jpg?imageView2/1/w/300/h/200","id":10346019,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"咕咚","authorId":null,"articleAlbumSeq":28,"author":null,"image":"http://web.pythe.cn/image/kewen/112/20.jpg?imageView2/1/w/300/h/200","id":10346018,"flag":0,"score":null,"subContent":null,"sequence":null},{"title":"小壁虎借尾巴","authorId":null,"articleAlbumSeq":29,"author":"林颂英","image":"http://web.pythe.cn/image/kewen/112/21.jpg?imageView2/1/w/300/h/200","id":10346017,"flag":0,"score":null,"subContent":null,"sequence":null}]
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
         * title : 春夏秋冬
         * authorId : null
         * articleAlbumSeq : 1
         * author : null
         * image : http://web.pythe.cn/image/kewen/112/0.1.jpg?imageView2/1/w/300/h/200
         * id : 10346045
         * flag : 0
         * score : null
         * subContent : null
         * sequence : null
         */

        private String title;
        private Object authorId;
        private int articleAlbumSeq;
        private Object author;
        private String image;
        private int id;
        private int flag;
        private Object score;
        private Object subContent;
        private Object sequence;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Object authorId) {
            this.authorId = authorId;
        }

        public int getArticleAlbumSeq() {
            return articleAlbumSeq;
        }

        public void setArticleAlbumSeq(int articleAlbumSeq) {
            this.articleAlbumSeq = articleAlbumSeq;
        }

        public Object getAuthor() {
            return author;
        }

        public void setAuthor(Object author) {
            this.author = author;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public Object getScore() {
            return score;
        }

        public void setScore(Object score) {
            this.score = score;
        }

        public Object getSubContent() {
            return subContent;
        }

        public void setSubContent(Object subContent) {
            this.subContent = subContent;
        }

        public Object getSequence() {
            return sequence;
        }

        public void setSequence(Object sequence) {
            this.sequence = sequence;
        }
    }
}
