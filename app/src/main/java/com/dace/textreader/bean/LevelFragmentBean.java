package com.dace.textreader.bean;

import org.litepal.crud.LitePalSupport;

import java.util.List;


public class LevelFragmentBean extends LitePalSupport {


    /**
     * status : 200
     * msg : OK
     * data : [{"id":8,"grade":-1,"gradename":"全部","status":2},{"id":9,"grade":1,"gradename":"200~400","status":2},{"id":10,"grade":2,"gradename":"400~600","status":2},{"id":11,"grade":3,"gradename":"600~800","status":2},{"id":12,"grade":4,"gradename":"800~1000","status":2},{"id":13,"grade":5,"gradename":"1000~1200","status":2},{"id":15,"grade":6,"gradename":"1200~1400","status":2}]
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
         * id : 8
         * grade : -1
         * gradename : 全部
         * status : 2
         */

        private int id;
        private int grade;
        private String gradename;
        private int status;
        private boolean isSelected;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getGradename() {
            return gradename;
        }

        public void setGradename(String gradename) {
            this.gradename = gradename;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
