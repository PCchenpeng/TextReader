package com.dace.textreader.bean;

import java.util.List;

public class SearchResultBean {
    /**
     * status : 200
     * msg : OK
     * data : {"ret_array":[{"subList":[{"dynasty":"唐代","image":"http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210","flag":0,"author":"李白(唐代)","source":"为你读书","title":"弃我去者，昨日之日不可留","type":5,"content":"弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发","search_id":"557840483511042048","update_time":"Wed Mar 20 08:18:55 CST 2019","_version_":1631796674351333400,"source_image":"http://web.pythe.cn/xd/logo/dushi.jpg","sort_num":4,"score_py":624,"id":"95bcb48a-65be-4c5c-90ca-a14ad41a1009","category":"美文","index_id":"10004319"}],"type":3},{"subList":[{"image":"http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210","flag":0,"author":"李白(唐代)","source":"为你读书","title":"弃我去者，昨日之日不可留","type":5,"content":"弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发","search_id":"557840483511042048","update_time":"Wed Mar 20 08:18:55 CST 2019","_version_":1631796674351333400,"source_image":"http://web.pythe.cn/xd/logo/dushi.jpg","sort_num":4,"score_py":624,"id":"95bcb48a-65be-4c5c-90ca-a14ad41a1009","category":"美文","index_id":"10004319"}],"type":5}],"ret_type":"author"}
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
         * ret_array : [{"subList":[{"dynasty":"唐代","image":"http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210","flag":0,"author":"李白(唐代)","source":"为你读书","title":"弃我去者，昨日之日不可留","type":5,"content":"弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发","search_id":"557840483511042048","update_time":"Wed Mar 20 08:18:55 CST 2019","_version_":1631796674351333400,"source_image":"http://web.pythe.cn/xd/logo/dushi.jpg","sort_num":4,"score_py":624,"id":"95bcb48a-65be-4c5c-90ca-a14ad41a1009","category":"美文","index_id":"10004319"}],"type":3},{"subList":[{"image":"http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210","flag":0,"author":"李白(唐代)","source":"为你读书","title":"弃我去者，昨日之日不可留","type":5,"content":"弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发","search_id":"557840483511042048","update_time":"Wed Mar 20 08:18:55 CST 2019","_version_":1631796674351333400,"source_image":"http://web.pythe.cn/xd/logo/dushi.jpg","sort_num":4,"score_py":624,"id":"95bcb48a-65be-4c5c-90ca-a14ad41a1009","category":"美文","index_id":"10004319"}],"type":5}]
         * ret_type : author
         */

        private String ret_type;
        private List<RetArrayBean> ret_array;

        public String getRet_type() {
            return ret_type;
        }

        public void setRet_type(String ret_type) {
            this.ret_type = ret_type;
        }

        public List<RetArrayBean> getRet_array() {
            return ret_array;
        }

        public void setRet_array(List<RetArrayBean> ret_array) {
            this.ret_array = ret_array;
        }

        public static class RetArrayBean {
            /**
             * subList : [{"dynasty":"唐代","image":"http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210","flag":0,"author":"李白(唐代)","source":"为你读书","title":"弃我去者，昨日之日不可留","type":5,"content":"弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发","search_id":"557840483511042048","update_time":"Wed Mar 20 08:18:55 CST 2019","_version_":1631796674351333400,"source_image":"http://web.pythe.cn/xd/logo/dushi.jpg","sort_num":4,"score_py":624,"id":"95bcb48a-65be-4c5c-90ca-a14ad41a1009","category":"美文","index_id":"10004319"}]
             * type : 3
             */

            private int type;
            private List<SubListBean> subList;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public List<SubListBean> getSubList() {
                return subList;
            }

            public void setSubList(List<SubListBean> subList) {
                this.subList = subList;
            }
        }
    }
}
