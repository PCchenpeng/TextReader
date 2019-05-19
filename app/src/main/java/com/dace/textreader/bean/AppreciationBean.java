package com.dace.textreader.bean;

import java.util.List;

public class AppreciationBean {

    /**
     * status : 200
     * msg : OK
     * data : {"appreciationList":[{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1283,"time":1557823472000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1282,"time":1557823471000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1281,"time":1557823470000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/touxiang/default3.png","start":2,"student_id":8429,"end":20,"id":1279,"time":1557823453000,"category":2,"essay_title":"爱情","content":"我是名字","username":"8854024261"}],"myself":{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/touxiang/default3.png","start":2,"student_id":8429,"end":20,"id":1279,"time":1557823453000,"category":2,"essay_title":"爱情","content":"我是名字","username":"8854024261"}}
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
         * appreciationList : [{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1283,"time":1557823472000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1282,"time":1557823471000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/default.png","start":2,"student_id":8427,"end":20,"id":1281,"time":1557823470000,"category":2,"essay_title":"爱情","content":"我是名字","username":"毛建定"},{"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/touxiang/default3.png","start":2,"student_id":8429,"end":20,"id":1279,"time":1557823453000,"category":2,"essay_title":"爱情","content":"我是名字","username":"8854024261"}]
         * myself : {"note":"不是说，我是我们的哥","essay_id":10000745,"userImg":"http://web.pythe.cn/image/touxiang/default3.png","start":2,"student_id":8429,"end":20,"id":1279,"time":1557823453000,"category":2,"essay_title":"爱情","content":"我是名字","username":"8854024261"}
         */

        private MyselfBean myself;
        private List<MyselfBean> appreciationList;

        public MyselfBean getMyself() {
            return myself;
        }

        public void setMyself(MyselfBean myself) {
            this.myself = myself;
        }

        public List<MyselfBean> getAppreciationList() {
            return appreciationList;
        }

        public void setAppreciationList(List<MyselfBean> appreciationList) {
            this.appreciationList = appreciationList;
        }

        public static class MyselfBean {
            /**
             * note : 不是说，我是我们的哥
             * essay_id : 10000745
             * userImg : http://web.pythe.cn/image/touxiang/default3.png
             * start : 2
             * student_id : 8429
             * end : 20
             * id : 1279
             * time : 1557823453000
             * category : 2
             * essay_title : 爱情
             * content : 我是名字
             * username : 8854024261
             */

            private String note;
            private int essay_id;
            private String userImg;
            private int start;
            private int student_id;
            private int end;
            private String id;
            private long time;
            private int category;
            private String essay_title;
            private String content;
            private String username;
            private boolean isSelected;
            private boolean isEditor;

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public int getEssay_id() {
                return essay_id;
            }

            public void setEssay_id(int essay_id) {
                this.essay_id = essay_id;
            }

            public String getUserImg() {
                return userImg;
            }

            public void setUserImg(String userImg) {
                this.userImg = userImg;
            }

            public int getStart() {
                return start;
            }

            public void setStart(int start) {
                this.start = start;
            }

            public int getStudent_id() {
                return student_id;
            }

            public void setStudent_id(int student_id) {
                this.student_id = student_id;
            }

            public int getEnd() {
                return end;
            }

            public void setEnd(int end) {
                this.end = end;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
            }

            public String getEssay_title() {
                return essay_title;
            }

            public void setEssay_title(String essay_title) {
                this.essay_title = essay_title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }

            public boolean isEditor() {
                return isEditor;
            }

            public void setEditor(boolean editor) {
                isEditor = editor;
            }
        }
    }
}
