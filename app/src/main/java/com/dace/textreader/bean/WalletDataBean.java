package com.dace.textreader.bean;

import java.util.List;

public class WalletDataBean {
    /**
     * status : 200
     * msg : OK
     * data : {"wallet":{"couponNum":0,"amount":0,"cardNum":0},"list":[{"cardId":9527,"type":null,"title":"高考冲刺卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.01,"discountPrice":0.01,"period":365,"status":null,"seq":null},{"cardId":2333,"type":null,"title":"派知学习卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":365,"status":null,"seq":null},{"cardId":1688,"type":null,"title":"考研跳楼卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":365,"status":null,"seq":null}],"card":{"cardId":20190404,"type":null,"title":"高考作文冲刺卡","img":"http://web.pythe.cn/image/my_card_examination_composition.jpg?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":90,"status":null,"seq":null}}
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
         * wallet : {"couponNum":0,"amount":0,"cardNum":0}
         * list : [{"cardId":9527,"type":null,"title":"高考冲刺卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.01,"discountPrice":0.01,"period":365,"status":null,"seq":null},{"cardId":2333,"type":null,"title":"派知学习卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":365,"status":null,"seq":null},{"cardId":1688,"type":null,"title":"考研跳楼卡","img":"http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":365,"status":null,"seq":null}]
         * card : {"cardId":20190404,"type":null,"title":"高考作文冲刺卡","img":"http://web.pythe.cn/image/my_card_examination_composition.jpg?imageView2/1/w/750/h/420","postPrice":null,"price":0.02,"discountPrice":0.01,"period":90,"status":null,"seq":null}
         */

        private WalletBean wallet;
        private CardBean card;
        private List<ListBean> list;

        public WalletBean getWallet() {
            return wallet;
        }

        public void setWallet(WalletBean wallet) {
            this.wallet = wallet;
        }

        public CardBean getCard() {
            return card;
        }

        public void setCard(CardBean card) {
            this.card = card;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class WalletBean {
            /**
             * couponNum : 0
             * amount : 0
             * cardNum : 0
             */

            private int couponNum;
            private float amount;
            private int cardNum;

            public int getCouponNum() {
                return couponNum;
            }

            public void setCouponNum(int couponNum) {
                this.couponNum = couponNum;
            }

            public float getAmount() {
                return amount;
            }

            public void setAmount(float amount) {
                this.amount = amount;
            }

            public int getCardNum() {
                return cardNum;
            }

            public void setCardNum(int cardNum) {
                this.cardNum = cardNum;
            }
        }

        public static class CardBean {
            /**
             * cardId : 20190404
             * type : null
             * title : 高考作文冲刺卡
             * img : http://web.pythe.cn/image/my_card_examination_composition.jpg?imageView2/1/w/750/h/420
             * postPrice : null
             * price : 0.02
             * discountPrice : 0.01
             * period : 90
             * status : null
             * seq : null
             */

            private String cardId;
            private Object type;
            private String title;
            private String img;
            private Object postPrice;
            private double price;
            private double discountPrice;
            private int period;
            private Object status;
            private Object seq;

            public String  getCardId() {
                return cardId;
            }

            public void setCardId(String cardId) {
                this.cardId = cardId;
            }

            public Object getType() {
                return type;
            }

            public void setType(Object type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public Object getPostPrice() {
                return postPrice;
            }

            public void setPostPrice(Object postPrice) {
                this.postPrice = postPrice;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getDiscountPrice() {
                return discountPrice;
            }

            public void setDiscountPrice(double discountPrice) {
                this.discountPrice = discountPrice;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            public Object getStatus() {
                return status;
            }

            public void setStatus(Object status) {
                this.status = status;
            }

            public Object getSeq() {
                return seq;
            }

            public void setSeq(Object seq) {
                this.seq = seq;
            }
        }

        public static class ListBean {
            /**
             * cardId : 9527
             * type : null
             * title : 高考冲刺卡
             * img : http://file.pythe.cn/image/my_card_study.png?imageView2/1/w/750/h/420
             * postPrice : null
             * price : 0.01
             * discountPrice : 0.01
             * period : 365
             * status : null
             * seq : null
             */

            private String cardId;
            private Object type;
            private String title;
            private String img;
            private Object postPrice;
            private double price;
            private double discountPrice;
            private int period;
            private Object status;
            private Object seq;

            public String  getCardId() {
                return cardId;
            }

            public void setCardId(String cardId) {
                this.cardId = cardId;
            }

            public Object getType() {
                return type;
            }

            public void setType(Object type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public Object getPostPrice() {
                return postPrice;
            }

            public void setPostPrice(Object postPrice) {
                this.postPrice = postPrice;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getDiscountPrice() {
                return discountPrice;
            }

            public void setDiscountPrice(double discountPrice) {
                this.discountPrice = discountPrice;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            public Object getStatus() {
                return status;
            }

            public void setStatus(Object status) {
                this.status = status;
            }

            public Object getSeq() {
                return seq;
            }

            public void setSeq(Object seq) {
                this.seq = seq;
            }
        }
    }
}
