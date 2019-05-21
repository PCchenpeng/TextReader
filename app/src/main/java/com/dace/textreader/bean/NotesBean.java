package com.dace.textreader.bean;

    /**
     * status : 200
     * msg : OK
     * data : [{"studentId":8429,"note":"面临理解那么居民理工科你咯","start":38,"end":50,"id":1444,"time":1558264278000,"category":0,"essayId":10004322,"essayTitle":"在我所爱过的一切事物之中","shareList":{"wx":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=wx","title":"在我所爱过的一切事物之中"},"weibo":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=weibo","title":"在我所爱过的一切事物之中"},"qq":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=qq","title":"在我所爱过的一切事物之中"}},"content":"只剩下一片蓝天和几颗寒星。"},{"studentId":8429,"note":"面临理解那么居民理工科你咯","start":51,"end":58,"id":1445,"time":1558264278000,"category":0,"essayId":10004322,"essayTitle":"在我所爱过的一切事物之中","shareList":{"wx":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1445&channel=wx","title":"在我所爱过的一切事物之中"},"weibo":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1445&channel=weibo","title":"在我所爱过的一切事物之中"},"qq":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1445&channel=qq","title":"在我所爱过的一切事物之中"}},"content":"风在林中的树木间"},{"studentId":8429,"note":"面临理解那么居民理工科你咯","start":27,"end":37,"id":1443,"time":1558264277000,"category":0,"essayId":10004322,"essayTitle":"在我所爱过的一切事物之中","shareList":{"wx":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1443&channel=wx","title":"在我所爱过的一切事物之中"},"weibo":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1443&channel=weibo","title":"在我所爱过的一切事物之中"},"qq":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1443&channel=qq","title":"在我所爱过的一切事物之中"}},"content":"我所爱过的一切事物之中"}]
     */


    public  class NotesBean {
        /**
         * studentId : 8429
         * note : 面临理解那么居民理工科你咯
         * start : 38
         * end : 50
         * id : 1444
         * time : 1558264278000
         * category : 0
         * essayId : 10004322
         * essayTitle : 在我所爱过的一切事物之中
         * shareList : {"wx":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=wx","title":"在我所爱过的一切事物之中"},"weibo":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=weibo","title":"在我所爱过的一切事物之中"},"qq":{"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=qq","title":"在我所爱过的一切事物之中"}}
         * content : 只剩下一片蓝天和几颗寒星。
         */

        private int studentId;
        private String note;
        private int start;
        private int end;
        private String id;
        private String time;
        private int category;
        private long essayId;
        private String essayTitle;
        private ShareListBean shareList;
        private String content;

        public boolean isEditor() {
            return isEditor;
        }

        public void setEditor(boolean editor) {
            isEditor = editor;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        private boolean isEditor;  //是否处于编辑状态
        private boolean isSelected;

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public long getEssayId() {
            return essayId;
        }

        public void setEssayId(long essayId) {
            this.essayId = essayId;
        }

        public String getEssayTitle() {
            return essayTitle;
        }

        public void setEssayTitle(String essayTitle) {
            this.essayTitle = essayTitle;
        }

        public ShareListBean getShareList() {
            return shareList;
        }

        public void setShareList(ShareListBean shareList) {
            this.shareList = shareList;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public static class ShareListBean {
            /**
             * wx : {"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=wx","title":"在我所爱过的一切事物之中"}
             * weibo : {"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=weibo","title":"在我所爱过的一切事物之中"}
             * qq : {"image":"http://web.pythe.cn/public/logo/300/icon.png","link":"https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=qq","title":"在我所爱过的一切事物之中"}
             */

            private WxBean wx;
            private WeiboBean weibo;
            private QqBean qq;

            public WxBean getWx() {
                return wx;
            }

            public void setWx(WxBean wx) {
                this.wx = wx;
            }

            public WeiboBean getWeibo() {
                return weibo;
            }

            public void setWeibo(WeiboBean weibo) {
                this.weibo = weibo;
            }

            public QqBean getQq() {
                return qq;
            }

            public void setQq(QqBean qq) {
                this.qq = qq;
            }

            public static class WxBean {
                /**
                 * image : http://web.pythe.cn/public/logo/300/icon.png
                 * link : https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=wx
                 * title : 在我所爱过的一切事物之中
                 */

                private String image;
                private String link;
                private String title;

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }

            public static class WeiboBean {
                /**
                 * image : http://web.pythe.cn/public/logo/300/icon.png
                 * link : https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=weibo
                 * title : 在我所爱过的一切事物之中
                 */

                private String image;
                private String link;
                private String title;

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }

            public static class QqBean {
                /**
                 * image : http://web.pythe.cn/public/logo/300/icon.png
                 * link : https://check.pythe.cn/update/share/pytheNoteShare.html?noteId=1444&channel=qq
                 * title : 在我所爱过的一切事物之中
                 */

                private String image;
                private String link;
                private String title;

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
