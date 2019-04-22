package com.dace.textreader.bean;

import java.util.List;

public class ReaderTabSelectItemBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"score":0,"image":"http://web.pythe.cn/xd/bdshiwen/image/75064140609546.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558187848679948288","dynasty":"先秦","format":2,"albumId":10043448,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"采薇","type":"国学","subContent":"采薇采薇，薇亦作止。曰归曰归，岁亦莫止。靡室靡家，玁狁之故。不遑启居，玁狁之故。采薇采薇，薇亦柔止。曰归曰归，心亦忧止。"},{"score":0,"image":"http://web.pythe.cn/xd/bdshiwen/image/755375248337950.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558187842908585984","dynasty":"先秦","format":2,"albumId":10043441,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"九歌·湘夫人","type":"国学","subContent":"帝子降兮北渚，目眇眇兮愁予。袅袅兮秋风，洞庭波兮木叶下。登白薠兮骋望，与佳期兮夕张。鸟何萃兮苹中，罾何为兮木上。沅有芷兮"},{"score":0,"image":"http://web.pythe.cn/xd/bdshiwen/image/123856861011565.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558187762008850432","dynasty":"先秦","format":2,"albumId":10043345,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"蒹葭","type":"国学","subContent":"蒹葭苍苍，白露为霜。所谓伊人，在水一方。溯洄从之，道阻且长。溯游从之，宛在水中央。蒹葭萋萋，白露未晞。所谓伊人，在水之湄"},{"score":0,"image":"http://web.pythe.cn/xd/bdshiwen/image/668376044644023.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558187752059961344","dynasty":"先秦","format":2,"albumId":10043334,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"关雎","type":"国学","subContent":"关关雎鸠，在河之洲。窈窕淑女，君子好逑。参差荇菜，左右流之。窈窕淑女，寤寐求之。求之不得，寤寐思服。悠哉悠哉，辗转反侧。"},{"score":0,"image":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558087541505523712","dynasty":null,"format":2,"albumId":10032979,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"南山经","type":"国学","subContent":"南山经之首曰鹊山。其首曰招摇之山，临于西海之上。多桂多金玉。有草焉，其状如韭而青华，其名曰祝馀，食之不饥。有木焉，其状如"},{"score":0,"image":"http://web.pythe.cn/article_default.jpg?imageView2/1/w/750/h/420","flag":0,"searchId":"558087543187439616","dynasty":null,"format":2,"albumId":10032980,"source":"国学学堂","sourceImage":"http://web.pythe.cn/xd/logo/12334555.jpg","title":"西山经","type":"国学","subContent":"西山经华山之首，曰钱来之山，其上多松，其下多洗石。有兽焉，其状如羊而马尾，名曰羬羊，其脂可以已腊。西四十五里，曰松果之"}]
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
         * score : 0
         * image : http://web.pythe.cn/xd/bdshiwen/image/75064140609546.jpg?imageView2/1/w/750/h/420
         * flag : 0
         * searchId : 558187848679948288
         * dynasty : 先秦
         * format : 2
         * albumId : 10043448
         * source : 国学学堂
         * sourceImage : http://web.pythe.cn/xd/logo/12334555.jpg
         * title : 采薇
         * type : 国学
         * subContent : 采薇采薇，薇亦作止。曰归曰归，岁亦莫止。靡室靡家，玁狁之故。不遑启居，玁狁之故。采薇采薇，薇亦柔止。曰归曰归，心亦忧止。
         */

        private int score;
        private String image;
        private int flag;
        private String searchId;
        private String dynasty;
        private int format;
        private int albumId;
        private String source;
        private String sourceImage;
        private String title;
        private String type;
        private String subContent;

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

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public String getDynasty() {
            return dynasty;
        }

        public void setDynasty(String dynasty) {
            this.dynasty = dynasty;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public int getAlbumId() {
            return albumId;
        }

        public void setAlbumId(int albumId) {
            this.albumId = albumId;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubContent() {
            return subContent;
        }

        public void setSubContent(String subContent) {
            this.subContent = subContent;
        }
    }
}
