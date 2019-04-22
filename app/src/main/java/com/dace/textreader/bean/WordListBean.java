package com.dace.textreader.bean;

import java.util.List;

public class WordListBean {
    /**
     * status : 200
     * msg : OK
     * data : {"mix":[{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=五","word":"五"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=六世纪","word":"六世纪"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=的","word":"的"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=民歌","word":"民歌"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=我","word":"我"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=到","word":"到"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=德国","word":"德国"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=去","word":"去"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=以前","word":"以前"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=跟","word":"跟"}],"base":[{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=五","word":"五"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=六","word":"六"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=世","word":"世"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=纪","word":"纪"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=的","word":"的"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=民","word":"民"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=歌","word":"歌"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=我","word":"我"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=到","word":"到"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=德","word":"德"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=国","word":"国"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=去","word":"去"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=以","word":"以"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=前","word":"前"},{"source":"https://check.pythe.cn/1readingModule/wordsExplain.html?wc=跟","word":"跟"}]}
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
        private List<MixBean> mix;
        private List<BaseBean> base;

        public List<MixBean> getMix() {
            return mix;
        }

        public void setMix(List<MixBean> mix) {
            this.mix = mix;
        }

        public List<BaseBean> getBase() {
            return base;
        }

        public void setBase(List<BaseBean> base) {
            this.base = base;
        }

        public static class MixBean {
            /**
             * source : https://check.pythe.cn/1readingModule/wordsExplain.html?wc=五
             * word : 五
             */

            private String source;
            private String word;

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }

        public static class BaseBean {
            /**
             * source : https://check.pythe.cn/1readingModule/wordsExplain.html?wc=五
             * word : 五
             */

            private String source;
            private String word;

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }
    }
}
