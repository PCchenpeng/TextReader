package com.dace.textreader.bean;

public class ContactBean {
    /**
     * status : 200
     * msg : OK
     * data : {"id":1,"region":"CN","content":"{\"phone\": \"020-37208895\",\"mail\": \"service@pythe.cn\",\"officialAccount\": \"派知\",\"QQ\": \"2055652309\",\"address\": \"广州市天河区科韵北路创锦产业园二楼H7\"}","info":{"QQ":"2055652309","address":"广州市天河区科韵北路创锦产业园二楼H7","mail":"service@pythe.cn","phone":"020-37208895","officialAccount":"派知"}}
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
         * id : 1
         * region : CN
         * content : {"phone": "020-37208895","mail": "service@pythe.cn","officialAccount": "派知","QQ": "2055652309","address": "广州市天河区科韵北路创锦产业园二楼H7"}
         * info : {"QQ":"2055652309","address":"广州市天河区科韵北路创锦产业园二楼H7","mail":"service@pythe.cn","phone":"020-37208895","officialAccount":"派知"}
         */

        private int id;
        private String region;
        private String content;
        private InfoBean info;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * QQ : 2055652309
             * address : 广州市天河区科韵北路创锦产业园二楼H7
             * mail : service@pythe.cn
             * phone : 020-37208895
             * officialAccount : 派知
             */

            private String QQ;
            private String address;
            private String mail;
            private String phone;
            private String officialAccount;

            public String getQQ() {
                return QQ;
            }

            public void setQQ(String QQ) {
                this.QQ = QQ;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getMail() {
                return mail;
            }

            public void setMail(String mail) {
                this.mail = mail;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getOfficialAccount() {
                return officialAccount;
            }

            public void setOfficialAccount(String officialAccount) {
                this.officialAccount = officialAccount;
            }
        }
    }
}
