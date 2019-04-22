package com.dace.textreader.bean;

import java.util.List;

public class ReaderTabBean {
    /**
     * status : 200
     * msg : OK
     * data : [{"id":1,"type":"国学","content":"国学经典及蒙书，有的富有文采，有的语句精炼，有的生动形象，有的包罗宏富。学习经典可以丰富孩子的想象力，提高阅读、写作能力，锻炼记忆力，增强认识能力，扩大知识面，为孩子的一生打下深厚的文化基础。","title":"国学","image":"http://web.pythe.cn/album/category/read_top_bg_literature.png","audio":null,"status":1},{"id":2,"type":"故事","content":"学习历史能培养人的浩然正气,启迪人的智慧,陶冶人的情操,开拓人的视野，积累知识。","title":"故事","image":"http://web.pythe.cn/album/category/read_top_bg_story.png","audio":null,"status":1},{"id":3,"type":"美文","content":"阅读美文可以陶冶思想情操，给人以深沉的思维空间。加以思考，对孩子的人格塑造有很大的好处。孩子通过这些不朽的文学作品而认识、感悟到的世界，对真善美、假恶丑的认识和理解。","title":"美文","image":"http://web.pythe.cn/album/category/read_top_bg_beautiful.png","audio":null,"status":1},{"id":4,"type":"科普","content":"学习科普知识，最深层次激发孩子的联想，培养孩子的思维能力。","title":"科普","image":"http://web.pythe.cn/album/category/read_top_bg_science.png","audio":null,"status":1}]
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
         * id : 1
         * type : 国学
         * content : 国学经典及蒙书，有的富有文采，有的语句精炼，有的生动形象，有的包罗宏富。学习经典可以丰富孩子的想象力，提高阅读、写作能力，锻炼记忆力，增强认识能力，扩大知识面，为孩子的一生打下深厚的文化基础。
         * title : 国学
         * image : http://web.pythe.cn/album/category/read_top_bg_literature.png
         * audio : null
         * status : 1
         */

        private int id;
        private String type;
        private String content;
        private String title;
        private String image;
        private Object audio;
        private int status;

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getAudio() {
            return audio;
        }

        public void setAudio(Object audio) {
            this.audio = audio;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
