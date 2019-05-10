package com.dace.textreader.bean;

import java.util.List;

public class SentenceListBean {

    /**
     * status : 200
     * msg : OK
     * data : [{"id":8722,"source":"太平御览·兵部·卷二","searchId":"558206163225804800","bookId":1132,"articleId":10053899,"status":null,"content":"凡军欲其众也，心欲其一也，三军一心则令可使无敌矣。","annotation":"凡是军队都希望人多、思想一致；全军一心，就可令行而无敌于天下。"},{"id":8723,"source":"太平御览·礼仪部·卷二十四","searchId":"558206544064413696","bookId":1132,"articleId":10054173,"status":null,"content":"上取象於天，下取法於地，中取则於人，人之所以群居，和壹之理尽矣。","annotation":"礼的制定，上取法于天，下取法于地，中间取法于人，人们共同居住、和谐统一的道理全在这里了。"},{"id":8724,"source":"太平御览·文部·卷六","searchId":"558206604772769792","bookId":1132,"articleId":10054218,"status":null,"content":"聪明睿智，守之以愚","annotation":"自己聪明智慧，要保持愚笨的样子"},{"id":8725,"source":"太平御览·学部·卷一","searchId":"558206627820470272","bookId":1132,"articleId":10054235,"status":null,"content":"不积跬步，无以致千里；不积小流，无以成江海。","annotation":"不积累一步半步，就没有办法到达千里的地方；不积累小河流，就没有办法汇成江海。"},{"id":8726,"source":"太平御览·治道部·卷一","searchId":"558206645373632512","bookId":1132,"articleId":10054248,"status":null,"content":"君者，源也；水者，流也。源清则流清，源浊则流浊。","annotation":"君主是百姓的本源，本源清澈，支流就清澈，本源污浊，支流就污浊。"},{"id":8727,"source":"太平御览·火部·卷三","searchId":"558206977436680192","bookId":1132,"articleId":10054498,"status":null,"content":"少而学者如日出之阳，壮而学者如日中之光，老而学者如秉烛之明。","annotation":"少年好学如同初升太阳那么鲜亮；壮年好学如同中午的阳光光芒四射；老年好学如同燃烛照明。"}]
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
         * id : 8722
         * source : 太平御览·兵部·卷二
         * searchId : 558206163225804800
         * bookId : 1132
         * articleId : 10053899
         * status : null
         * content : 凡军欲其众也，心欲其一也，三军一心则令可使无敌矣。
         * annotation : 凡是军队都希望人多、思想一致；全军一心，就可令行而无敌于天下。
         */

        private int id;
        private String source;
        private String searchId;
        private int bookId;
        private long articleId;
        private Object status;
        private String content;
        private String annotation;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public long getArticleId() {
            return articleId;
        }

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAnnotation() {
            return annotation;
        }

        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }
    }
}
