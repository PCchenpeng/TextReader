package com.dace.textreader.bean;

import java.util.List;

public class CardDetailBean {
    /**
     * status : 200
     * msg : OK
     * data : {"next":{"cardId":9527,"type":1,"title":"高考冲刺卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":0.03,"price":0.02,"discountPrice":0.01,"period":365,"status":2,"tips":null},"pre":{"cardId":1688,"type":1,"title":"考研跳楼卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":30000,"price":20000,"discountPrice":10000,"period":365,"status":2,"tips":null},"functionRecord":[{"id":"560103946690101248","startTime":1553580787000,"stopTime":1585116787000,"times":3,"cardId":3,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103946765598720","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":4,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103946899816448","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":5,"studentId":2636,"superior":"155358078706069","status":0,"situation":null},{"id":"560103946979508224","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":6,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103947222777856","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":7,"studentId":2636,"superior":"155358078706069","status":1,"situation":null}],"functions":[{"id":"3","name":"名师作文微辅导 X 3","description":"提交作文批改，让资深老师从根本发现并解决写作问题","img":"http://file.pythe.cn/image/my_card_study_function01.png","status":1,"times":3,"period":365,"price":null,"discount":null,"superior":2333,"category":1,"teacher":null,"course":null,"outsourcing":"{\"type\":1}"},{"id":"4","name":"微课任意学","description":"专属微课栏目，扩展课外知识，提高作文水平","img":"http://file.pythe.cn/image/my_card_study_function02.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":2,"teacher":null,"course":"[8153924974617500,7153621733352318,9153621733390242]","outsourcing":null},{"id":"5","name":"在线直播课","description":"选择一堂精准的线上课程，针对性提高写作水平","img":"http://file.pythe.cn/image/my_card_study_function05.png","status":1,"times":1,"period":365,"price":null,"discount":null,"superior":2333,"category":3,"teacher":null,"course":null,"outsourcing":"[128,129,130,131,132,133]"},{"id":"6","name":"名师一对一答疑 X 3","description":"在线答疑，针对性问题针对解决","img":"http://file.pythe.cn/image/my_card_study_function04.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":5,"teacher":null,"course":null,"outsourcing":null},{"id":"7","name":"口才评测","description":"每天口才练习表达，帮助自己梳理语言组织逻辑","img":"http://file.pythe.cn/image/my_card_study_function03.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":4,"teacher":null,"course":null,"outsourcing":null}],"cardRecord":{"id":155358078706069,"saleId":154849328107371,"discountPrice":null,"price":null,"startTime":1553580787000,"stopTime":1585116787000,"type":null,"studentId":2636,"cardId":2333,"cardCode":"025737","actived":1,"orderId":560103946576855040,"status":1,"discount":null,"title":null,"image":null,"situation":null},"price":0.02,"discountPrice":0.01,"discount":true,"actived":true,"card":{"cardId":2333,"type":1,"title":"派知学习卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":0.03,"price":0.02,"discountPrice":0.01,"period":365,"status":2,"tips":"{\"pageDisplay\":\"学霸之路\",\"displayBanner\":\"专享福利\",\"bannerGuide\":\"了解科学的学习规划>>\",\"guideUrl\":\"https://check.pythe.cn/1vipStudyCards/scientificallyStudy.html?share=0\",\"activeNotice\":\"送你一张学习卡，开启学霸训练之旅\",\"buttonDisplay\":\"马上去激活\",\"activeImg\":\"http://test.pythe.cn/image/popup_bg.png\",\"entranceImg\":\"http://test.pythe.cn/image/my_card_entrance.png\",\"validImage\":\"/image/my_wallet_studycard_valid.png\",\"invalidImage\":\"/image/my_wallet_studycard_null.png\"}","description":"组合卡包加把劲骑士","function":null}}
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
         * next : {"cardId":9527,"type":1,"title":"高考冲刺卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":0.03,"price":0.02,"discountPrice":0.01,"period":365,"status":2,"tips":null}
         * pre : {"cardId":1688,"type":1,"title":"考研跳楼卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":30000,"price":20000,"discountPrice":10000,"period":365,"status":2,"tips":null}
         * functionRecord : [{"id":"560103946690101248","startTime":1553580787000,"stopTime":1585116787000,"times":3,"cardId":3,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103946765598720","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":4,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103946899816448","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":5,"studentId":2636,"superior":"155358078706069","status":0,"situation":null},{"id":"560103946979508224","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":6,"studentId":2636,"superior":"155358078706069","status":1,"situation":null},{"id":"560103947222777856","startTime":1553580787000,"stopTime":1585116787000,"times":null,"cardId":7,"studentId":2636,"superior":"155358078706069","status":1,"situation":null}]
         * functions : [{"id":"3","name":"名师作文微辅导 X 3","description":"提交作文批改，让资深老师从根本发现并解决写作问题","img":"http://file.pythe.cn/image/my_card_study_function01.png","status":1,"times":3,"period":365,"price":null,"discount":null,"superior":2333,"category":1,"teacher":null,"course":null,"outsourcing":"{\"type\":1}"},{"id":"4","name":"微课任意学","description":"专属微课栏目，扩展课外知识，提高作文水平","img":"http://file.pythe.cn/image/my_card_study_function02.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":2,"teacher":null,"course":"[8153924974617500,7153621733352318,9153621733390242]","outsourcing":null},{"id":"5","name":"在线直播课","description":"选择一堂精准的线上课程，针对性提高写作水平","img":"http://file.pythe.cn/image/my_card_study_function05.png","status":1,"times":1,"period":365,"price":null,"discount":null,"superior":2333,"category":3,"teacher":null,"course":null,"outsourcing":"[128,129,130,131,132,133]"},{"id":"6","name":"名师一对一答疑 X 3","description":"在线答疑，针对性问题针对解决","img":"http://file.pythe.cn/image/my_card_study_function04.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":5,"teacher":null,"course":null,"outsourcing":null},{"id":"7","name":"口才评测","description":"每天口才练习表达，帮助自己梳理语言组织逻辑","img":"http://file.pythe.cn/image/my_card_study_function03.png","status":1,"times":null,"period":365,"price":null,"discount":null,"superior":2333,"category":4,"teacher":null,"course":null,"outsourcing":null}]
         * cardRecord : {"id":155358078706069,"saleId":154849328107371,"discountPrice":null,"price":null,"startTime":1553580787000,"stopTime":1585116787000,"type":null,"studentId":2636,"cardId":2333,"cardCode":"025737","actived":1,"orderId":560103946576855040,"status":1,"discount":null,"title":null,"image":null,"situation":null}
         * price : 0.02
         * discountPrice : 0.01
         * discount : true
         * actived : true
         * card : {"cardId":2333,"type":1,"title":"派知学习卡","img":"http://test.pythe.cn/image/my_card_study.png","postPrice":0.03,"price":0.02,"discountPrice":0.01,"period":365,"status":2,"tips":"{\"pageDisplay\":\"学霸之路\",\"displayBanner\":\"专享福利\",\"bannerGuide\":\"了解科学的学习规划>>\",\"guideUrl\":\"https://check.pythe.cn/1vipStudyCards/scientificallyStudy.html?share=0\",\"activeNotice\":\"送你一张学习卡，开启学霸训练之旅\",\"buttonDisplay\":\"马上去激活\",\"activeImg\":\"http://test.pythe.cn/image/popup_bg.png\",\"entranceImg\":\"http://test.pythe.cn/image/my_card_entrance.png\",\"validImage\":\"/image/my_wallet_studycard_valid.png\",\"invalidImage\":\"/image/my_wallet_studycard_null.png\"}","description":"组合卡包加把劲骑士","function":null}
         */

        private NextBean next;
        private PreBean pre;
        private CardRecordBean cardRecord;
        private double price;
        private double discountPrice;
        private boolean discount;
        private boolean actived;
        private CardBean card;
        private List<FunctionRecordBean> functionRecord;
        private List<FunctionsBean> functions;

        public NextBean getNext() {
            return next;
        }

        public void setNext(NextBean next) {
            this.next = next;
        }

        public PreBean getPre() {
            return pre;
        }

        public void setPre(PreBean pre) {
            this.pre = pre;
        }

        public CardRecordBean getCardRecord() {
            return cardRecord;
        }

        public void setCardRecord(CardRecordBean cardRecord) {
            this.cardRecord = cardRecord;
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

        public boolean isDiscount() {
            return discount;
        }

        public void setDiscount(boolean discount) {
            this.discount = discount;
        }

        public boolean isActived() {
            return actived;
        }

        public void setActived(boolean actived) {
            this.actived = actived;
        }

        public CardBean getCard() {
            return card;
        }

        public void setCard(CardBean card) {
            this.card = card;
        }

        public List<FunctionRecordBean> getFunctionRecord() {
            return functionRecord;
        }

        public void setFunctionRecord(List<FunctionRecordBean> functionRecord) {
            this.functionRecord = functionRecord;
        }

        public List<FunctionsBean> getFunctions() {
            return functions;
        }

        public void setFunctions(List<FunctionsBean> functions) {
            this.functions = functions;
        }

        public static class NextBean {
            /**
             * cardId : 9527
             * type : 1
             * title : 高考冲刺卡
             * img : http://test.pythe.cn/image/my_card_study.png
             * postPrice : 0.03
             * price : 0.02
             * discountPrice : 0.01
             * period : 365
             * status : 2
             * tips : null
             */

            private long cardId;
            private int type;
            private String title;
            private String img;
            private double postPrice;
            private double price;
            private double discountPrice;
            private int period;
            private int status;
            private Object tips;

            public long getCardId() {
                return cardId;
            }

            public void setCardId(long cardId) {
                this.cardId = cardId;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
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

            public double getPostPrice() {
                return postPrice;
            }

            public void setPostPrice(double postPrice) {
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Object getTips() {
                return tips;
            }

            public void setTips(Object tips) {
                this.tips = tips;
            }
        }

        public static class PreBean {
            /**
             * cardId : 1688
             * type : 1
             * title : 考研跳楼卡
             * img : http://test.pythe.cn/image/my_card_study.png
             * postPrice : 30000
             * price : 20000
             * discountPrice : 10000
             * period : 365
             * status : 2
             * tips : null
             */

            private long cardId;
            private int type;
            private String title;
            private String img;
            private double postPrice;
            private double price;
            private double discountPrice;
            private int period;
            private int status;
            private Object tips;

            public long getCardId() {
                return cardId;
            }

            public void setCardId(long cardId) {
                this.cardId = cardId;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
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

            public double getPostPrice() {
                return postPrice;
            }

            public void setPostPrice(double postPrice) {
                this.postPrice = postPrice;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public double getDiscountPrice() {
                return discountPrice;
            }

            public void setDiscountPrice(int discountPrice) {
                this.discountPrice = discountPrice;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Object getTips() {
                return tips;
            }

            public void setTips(Object tips) {
                this.tips = tips;
            }
        }

        public static class CardRecordBean {
            /**
             * id : 155358078706069
             * saleId : 154849328107371
             * discountPrice : null
             * price : null
             * startTime : 1553580787000
             * stopTime : 1585116787000
             * type : null
             * studentId : 2636
             * cardId : 2333
             * cardCode : 025737
             * actived : 1
             * orderId : 560103946576855040
             * status : 1
             * discount : null
             * title : null
             * image : null
             * situation : null
             */

            private long id;
            private long saleId;
            private Object discountPrice;
            private Object price;
            private long startTime;
            private long stopTime;
            private Object type;
            private int studentId;
            private long cardId;
            private String cardCode;
            private int actived;
            private long orderId;
            private int status;
            private Object discount;
            private Object title;
            private Object image;
            private Object situation;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getSaleId() {
                return saleId;
            }

            public void setSaleId(long saleId) {
                this.saleId = saleId;
            }

            public Object getDiscountPrice() {
                return discountPrice;
            }

            public void setDiscountPrice(Object discountPrice) {
                this.discountPrice = discountPrice;
            }

            public Object getPrice() {
                return price;
            }

            public void setPrice(Object price) {
                this.price = price;
            }

            public long getStartTime() {
                return startTime;
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

            public long getStopTime() {
                return stopTime;
            }

            public void setStopTime(long stopTime) {
                this.stopTime = stopTime;
            }

            public Object getType() {
                return type;
            }

            public void setType(Object type) {
                this.type = type;
            }

            public int getStudentId() {
                return studentId;
            }

            public void setStudentId(int studentId) {
                this.studentId = studentId;
            }

            public long getCardId() {
                return cardId;
            }

            public void setCardId(long cardId) {
                this.cardId = cardId;
            }

            public String getCardCode() {
                return cardCode;
            }

            public void setCardCode(String cardCode) {
                this.cardCode = cardCode;
            }

            public int getActived() {
                return actived;
            }

            public void setActived(int actived) {
                this.actived = actived;
            }

            public long getOrderId() {
                return orderId;
            }

            public void setOrderId(long orderId) {
                this.orderId = orderId;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Object getDiscount() {
                return discount;
            }

            public void setDiscount(Object discount) {
                this.discount = discount;
            }

            public Object getTitle() {
                return title;
            }

            public void setTitle(Object title) {
                this.title = title;
            }

            public Object getImage() {
                return image;
            }

            public void setImage(Object image) {
                this.image = image;
            }

            public Object getSituation() {
                return situation;
            }

            public void setSituation(Object situation) {
                this.situation = situation;
            }
        }

        public static class CardBean {
            /**
             * cardId : 2333
             * type : 1
             * title : 派知学习卡
             * img : http://test.pythe.cn/image/my_card_study.png
             * postPrice : 0.03
             * price : 0.02
             * discountPrice : 0.01
             * period : 365
             * status : 2
             * tips : {"pageDisplay":"学霸之路","displayBanner":"专享福利","bannerGuide":"了解科学的学习规划>>","guideUrl":"https://check.pythe.cn/1vipStudyCards/scientificallyStudy.html?share=0","activeNotice":"送你一张学习卡，开启学霸训练之旅","buttonDisplay":"马上去激活","activeImg":"http://test.pythe.cn/image/popup_bg.png","entranceImg":"http://test.pythe.cn/image/my_card_entrance.png","validImage":"/image/my_wallet_studycard_valid.png","invalidImage":"/image/my_wallet_studycard_null.png"}
             * description : 组合卡包加把劲骑士
             * function : null
             */

            private long cardId;
            private int type;
            private String title;
            private String img;
            private double postPrice;
            private double price;
            private double discountPrice;
            private int period;
            private int status;
            private String tips;
            private String description;
            private Object function;

            public long getCardId() {
                return cardId;
            }

            public void setCardId(long cardId) {
                this.cardId = cardId;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
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

            public double getPostPrice() {
                return postPrice;
            }

            public void setPostPrice(double postPrice) {
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getTips() {
                return tips;
            }

            public void setTips(String tips) {
                this.tips = tips;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public Object getFunction() {
                return function;
            }

            public void setFunction(Object function) {
                this.function = function;
            }
        }

        public static class FunctionRecordBean {
            /**
             * id : 560103946690101248
             * startTime : 1553580787000
             * stopTime : 1585116787000
             * times : 3
             * cardId : 3
             * studentId : 2636
             * superior : 155358078706069
             * status : 1
             * situation : null
             */

            private String id;
            private long startTime;
            private long stopTime;
            private int times;
            private long cardId;
            private int studentId;
            private String superior;
            private int status;
            private Object situation;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getStartTime() {
                return startTime;
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

            public long getStopTime() {
                return stopTime;
            }

            public void setStopTime(long stopTime) {
                this.stopTime = stopTime;
            }

            public int getTimes() {
                return times;
            }

            public void setTimes(int times) {
                this.times = times;
            }

            public long getCardId() {
                return cardId;
            }

            public void setCardId(long cardId) {
                this.cardId = cardId;
            }

            public int getStudentId() {
                return studentId;
            }

            public void setStudentId(int studentId) {
                this.studentId = studentId;
            }

            public String getSuperior() {
                return superior;
            }

            public void setSuperior(String superior) {
                this.superior = superior;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Object getSituation() {
                return situation;
            }

            public void setSituation(Object situation) {
                this.situation = situation;
            }
        }

        public static class FunctionsBean {
            /**
             * id : 3
             * name : 名师作文微辅导 X 3
             * description : 提交作文批改，让资深老师从根本发现并解决写作问题
             * img : http://file.pythe.cn/image/my_card_study_function01.png
             * status : 1
             * times : 3
             * period : 365
             * price : null
             * discount : null
             * superior : 2333
             * category : 1
             * teacher : null
             * course : null
             * outsourcing : {"type":1}
             */

            private String id;
            private String name;
            private String description;
            private String img;
            private int status;
            private int times;
            private int period;
            private Object price;
            private Object discount;
            private int superior;
            private int category;
            private Object teacher;
            private Object course;
            private String outsourcing;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getTimes() {
                return times;
            }

            public void setTimes(int times) {
                this.times = times;
            }

            public int getPeriod() {
                return period;
            }

            public void setPeriod(int period) {
                this.period = period;
            }

            public Object getPrice() {
                return price;
            }

            public void setPrice(Object price) {
                this.price = price;
            }

            public Object getDiscount() {
                return discount;
            }

            public void setDiscount(Object discount) {
                this.discount = discount;
            }

            public int getSuperior() {
                return superior;
            }

            public void setSuperior(int superior) {
                this.superior = superior;
            }

            public int getCategory() {
                return category;
            }

            public void setCategory(int category) {
                this.category = category;
            }

            public Object getTeacher() {
                return teacher;
            }

            public void setTeacher(Object teacher) {
                this.teacher = teacher;
            }

            public Object getCourse() {
                return course;
            }

            public void setCourse(Object course) {
                this.course = course;
            }

            public String getOutsourcing() {
                return outsourcing;
            }

            public void setOutsourcing(String outsourcing) {
                this.outsourcing = outsourcing;
            }
        }
    }
}
