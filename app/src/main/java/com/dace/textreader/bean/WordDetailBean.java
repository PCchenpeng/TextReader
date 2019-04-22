package com.dace.textreader.bean;

import java.util.List;

public class WordDetailBean {
    /**
     * status : 200
     * msg : OK
     * data : {"explain":[{"exam":"制服上的肩章是用来表示级别的标志。","desc":"显示某种意义。"},{"exam":"曹禺《雷雨》：\u201c我想，我很明白地对你表示过。这些日子我没有见你，我想你很明白。\u201d","desc":"用语言、行动显出某种思想、感情、态度。"},{"exam":"他脸上虽没有什么表示，心里可是乐滋滋的。","desc":"显出思想感情的言语、动作或神情。"}],"audio":[{"pinyin":"biǎo shì","url":"http://media.pythe.cn/word/audio/ci/7/9/0675979229512275.mp3"}],"word":"表示","url":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=表示"}
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
         * explain : [{"exam":"制服上的肩章是用来表示级别的标志。","desc":"显示某种意义。"},{"exam":"曹禺《雷雨》：\u201c我想，我很明白地对你表示过。这些日子我没有见你，我想你很明白。\u201d","desc":"用语言、行动显出某种思想、感情、态度。"},{"exam":"他脸上虽没有什么表示，心里可是乐滋滋的。","desc":"显出思想感情的言语、动作或神情。"}]
         * audio : [{"pinyin":"biǎo shì","url":"http://media.pythe.cn/word/audio/ci/7/9/0675979229512275.mp3"}]
         * word : 表示
         * url : https://check.pythe.cn/1readingModule/wordsExplain.html?wc=表示
         */

        private String word;
        private String url;
        private List<ExplainBean> explain;
        private List<AudioBean> audio;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<ExplainBean> getExplain() {
            return explain;
        }

        public void setExplain(List<ExplainBean> explain) {
            this.explain = explain;
        }

        public List<AudioBean> getAudio() {
            return audio;
        }

        public void setAudio(List<AudioBean> audio) {
            this.audio = audio;
        }

        public static class ExplainBean {
            /**
             * exam : 制服上的肩章是用来表示级别的标志。
             * desc : 显示某种意义。
             */

            private String exam;
            private String desc;

            public String getExam() {
                return exam;
            }

            public void setExam(String exam) {
                this.exam = exam;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }

        public static class AudioBean {
            /**
             * pinyin : biǎo shì
             * url : http://media.pythe.cn/word/audio/ci/7/9/0675979229512275.mp3
             */

            private String pinyin;
            private String url;

            public String getPinyin() {
                return pinyin;
            }

            public void setPinyin(String pinyin) {
                this.pinyin = pinyin;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
