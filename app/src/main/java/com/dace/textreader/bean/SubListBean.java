package com.dace.textreader.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SubListBean implements Parcelable {
    /**
     * dynasty : 唐代
     * image : http://web.pythe.cn/xd/dushi/images/6832860083148737.jpg?imageView2/1/w/375/h/210
     * flag : 0
     * author : 李白(唐代)
     * source : 为你读书
     * title : 弃我去者，昨日之日不可留
     * type : 5
     * content : 弃我去者，昨日之日不可留；乱我心者，今日之日多烦忧。长风万里送秋雁，对此可以酣高楼。蓬莱文章建安骨，中间小谢又清发
     * search_id : 557840483511042048
     * update_time : Wed Mar 20 08:18:55 CST 2019
     * _version_ : 1631796674351333400
     * source_image : http://web.pythe.cn/xd/logo/dushi.jpg
     * sort_num : 4
     * score_py : 624
     * id : 95bcb48a-65be-4c5c-90ca-a14ad41a1009
     * category : 美文
     * index_id : 10004319
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dynasty);
        dest.writeString(image);
        dest.writeInt(flag);
        dest.writeString(author);
        dest.writeString(source);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeString(search_id);
        dest.writeString(update_time);
        dest.writeLong(_version_);
        dest.writeString(source_image);
        dest.writeInt(sort_num);
        dest.writeInt(score_py);
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(index_id);
    }

    public static final Creator<SubListBean> CREATOR = new Creator<SubListBean>() {
        @Override
        public SubListBean createFromParcel(Parcel in) {
            SubListBean subListBean = new SubListBean();
            subListBean.dynasty = in.readString();
            subListBean.image = in.readString();
            subListBean.flag = in.readInt();
            subListBean.author = in.readString();
            subListBean.source = in.readString();
            subListBean.title = in.readString();
            subListBean.type = in.readInt();
            subListBean.content = in.readString();
            subListBean.search_id = in.readString();
            subListBean.update_time = in.readString();
            subListBean._version_ = in.readLong();
            subListBean.source_image = in.readString();
            subListBean.sort_num = in.readInt();
            subListBean.score_py = in.readInt();
            subListBean.id = in.readString();
            subListBean.category = in.readString();
            subListBean.index_id = in.readString();
            return new SubListBean();
        }

        @Override
        public SubListBean[] newArray(int size) {
            return new SubListBean[size];
        }
    };



    private String dynasty;
    private String image;
    private int flag;
    private String author;
    private String source;
    private String title;
    private int type;
    private String content;
    private String search_id;
    private String update_time;
    private long _version_;
    private String source_image;
    private int sort_num;
    private int score_py;
    private String id;
    private String category;
    private String index_id;

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSearch_id() {
        return search_id;
    }

    public void setSearch_id(String search_id) {
        this.search_id = search_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public long get_version_() {
        return _version_;
    }

    public void set_version_(long _version_) {
        this._version_ = _version_;
    }

    public String getSource_image() {
        return source_image;
    }

    public void setSource_image(String source_image) {
        this.source_image = source_image;
    }

    public int getSort_num() {
        return sort_num;
    }

    public void setSort_num(int sort_num) {
        this.sort_num = sort_num;
    }

    public int getScore_py() {
        return score_py;
    }

    public void setScore_py(int score_py) {
        this.score_py = score_py;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIndex_id() {
        return index_id;
    }

    public void setIndex_id(String index_id) {
        this.index_id = index_id;
    }
}
