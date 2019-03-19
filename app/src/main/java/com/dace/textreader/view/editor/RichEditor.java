package com.dace.textreader.view.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.MyToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.example.test.myapplication
 * Created by Administrator.
 * Created time 2018/11/4 0004 下午 3:59.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class RichEditor extends ScrollView {

    private static final int EDIT_PADDING = 10; // edittext常规padding是10dp

    private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnClickListener deleteListener; // 图片右上角红叉按钮监听器
    private OnClickListener changeListener;
    private OnClickListener imageClickListener;
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    private TextWatcher textWatcher;
    private EditText lastFocusEdit; // 最近被聚焦的EditText
    private int editNormalPadding = 0;
    private Context mContext;

    private InputFilter inputFilter;

    private int words_num = 0;  //文本字数

    private int edit_count = 0;  //输入框数量

    private boolean noImageOperate = false;

    public void setNoImageOperate() {
        noImageOperate = true;
    }

    public void setNoEditor() {
        editorCancel();
    }

    private void editorCancel() {
        for (int i = 0; i < allLayout.getChildCount(); i++) {
            View view = allLayout.getChildAt(i);
            if (view instanceof EditText) {
                view.setFocusable(false);
                view.setFocusableInTouchMode(false);
                hideKeyBoard();
            }
        }
    }

    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        // 1. 初始化allLayout
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        //设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
        allLayout.setPadding(50, 15, 50, 15);
        addView(allLayout, layoutParams);

        // 2. 初始化键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                } else if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return insertEditText();
                }
                return false;
            }
        };

        // 3. 图片叉掉处理
        deleteListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout indexView = (RelativeLayout) v.getParent();
                int index = allLayout.indexOfChild(indexView);
                if (onDeleteClick != null) {
                    onDeleteClick.onImageDelete(index);
                }
            }
        };
        changeListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout indexView = (RelativeLayout) v.getParent();
                int index = allLayout.indexOfChild(indexView);
                if (onChangeClick != null) {
                    onChangeClick.onImageChange(index);
                }
            }
        };
        imageClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = allLayout.indexOfChild(v);
                if (onChangeClick != null) {
                    onChangeClick.onImageChange(index);
                }
            }
        };

        focusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lastFocusEdit = (EditText) v;
                }
            }
        };

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //s发生改变前的字符
                //start发生改变前的字符数
                //before将要被删除的字符数
                //count新增的字符数

                words_num = words_num + count;

                words_num = words_num - before;

                if (words_num < 0) {
                    words_num = 0;
                }

                if (onTextChangeListen != null) {
                    onTextChangeListen.onTextChange(words_num);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        inputFilter = new InputFilter() {
            Pattern pattern = Pattern.compile(
                    "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                Matcher matcher = pattern.matcher(source);
                if (!matcher.find()) {
                    return null;
                } else {
                    MyToastUtil.showToast(mContext, "写作不能输入表情喔~");
                    return "";
                }
            }
        };

        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final EditText firstEdit = createEditText("请输入正文", dip2px(context, EDIT_PADDING));
        allLayout.addView(firstEdit, firstEditParam);
        lastFocusEdit = firstEdit;
    }

    public interface OnTouchListen {
        void onTouch();
    }

    private OnTouchListen onTouchListen;

    public void setOnTouchListen(OnTouchListen onTouchListen) {
        this.onTouchListen = onTouchListen;
    }

    public interface OnImageDeleteClick {
        void onImageDelete(int index);
    }

    private OnImageDeleteClick onDeleteClick;

    public void setOnImageDeleteClick(OnImageDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public interface OnImageChangeClick {
        void onImageChange(int index);
    }

    private OnImageChangeClick onChangeClick;

    public void setOnImageChangeClick(OnImageChangeClick onChangeClick) {
        this.onChangeClick = onChangeClick;
    }

    public interface OnTextChangeListen {
        void onTextChange(int size);
    }

    private OnTextChangeListen onTextChangeListen;

    public void setOnTextChangeListen(OnTextChangeListen onTextChangeListen) {
        this.onTextChangeListen = onTextChangeListen;
    }

    public EditText getLastFocusEdit() {
        return lastFocusEdit;
    }

    public int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        int textLength = editTxt.getText().toString().trim().length();
        // 只有在光标已经顶到文本输入框的最前方
        if (startSelection == 0) {
            int editIndex = allLayout.indexOfChild(editTxt);
            View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null == preView) {
                if (allLayout.getChildCount() > 1 && textLength == 0) {
                    allLayout.removeView(editTxt);
                    edit_count = edit_count - 1;
                }
            } else {
                if (preView instanceof RelativeLayout && textLength == 0) {
                    //如果输入框的前一个视图是图片，并且输入框不是最后一个，并且EditText内容是否为空
                    if (edit_count > 1 && editIndex < allLayout.getChildCount() - 1) {
                        allLayout.removeView(editTxt);
                        edit_count = edit_count - 1;
                    }
                } else if (preView instanceof EditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView;
                    String str2 = preEdit.getText().toString();

                    words_num = words_num - str1.length();

                    allLayout.removeView(editTxt);
                    edit_count = edit_count - 1;

                    // 文本合并
                    preEdit.setText(str2 + str1);
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     */
    private void onImageCloseClick(int disappearingIndex) {
        View preView = allLayout.getChildAt(disappearingIndex - 1);
        View nextView = allLayout.getChildAt(disappearingIndex + 1);
        if (preView != null && nextView != null) {
            if (preView instanceof EditText && nextView instanceof EditText) {
                EditText edit1 = (EditText) preView;
                String str1 = edit1.getText().toString();
                EditText edit2 = (EditText) nextView;
                String str2 = edit2.getText().toString();
                // 文本合并
                String str = str1 + str2;
                words_num = words_num - str2.length();
                allLayout.removeView(edit2);
                edit_count = edit_count - 1;
                edit1.setText(str);
                edit1.requestFocus();
                edit1.setSelection(str1.length());
                lastFocusEdit = edit1;
            }
        }

        allLayout.removeViewAt(disappearingIndex);
    }

    /**
     * 输入文字
     *
     * @param text
     */
    public void appendText(String text) {
        int index = lastFocusEdit.getSelectionStart();
        Editable editable = lastFocusEdit.getEditableText();
        if (index < 0 || index >= lastFocusEdit.getText().toString().length()) {
            editable.append(text);
        } else {
            editable.insert(index, text);
        }
    }

    /**
     * 输入符号
     *
     * @param text
     */
    public void appendSymbolText(String text) {
        int index = lastFocusEdit.getSelectionStart();
        Editable editable = lastFocusEdit.getEditableText();
        if (index < 0 || index >= lastFocusEdit.getText().toString().length()) {
            editable.append(text);
        } else {
            editable.insert(index, text);
        }
        if (text.equals("“”") || text.equals("‘’") || text.equals("《》") || text.equals("（）")) {
            lastFocusEdit.setSelection(index + 1);
        }
    }

    /**
     * 设置图片资源
     *
     * @param index 图片布局所在的位置
     * @param path  图片资源路径
     */
    public void setImageResource(int index, String path) {
        View view = allLayout.getChildAt(index);
        if (view != null) {
            if (view instanceof RelativeLayout) {
                final DataImageView imageView = view.findViewById(R.id.edit_imageView);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.image_write_cover_bg)
                        .error(R.drawable.image_write_cover_bg)
                        .centerCrop()
                        .transform(new GlideRoundImage(mContext, 4));
                Glide.with(mContext)
                        .asBitmap()
                        .load(path)
                        .apply(options)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                imageView.setImageResource(R.drawable.image_write_cover_bg);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(resource);
                            }
                        });
                imageView.setAbsolutePath(path);
            }
        }
    }

    /**
     * 生成文本输入框
     */
    public EditText createEditText(String hint, int paddingTop) {
        edit_count = edit_count + 1;
        final EditText editText = (EditText) inflater.inflate(R.layout.rich_edittext, null);
        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
        if (!hint.equals("")) {
            editText.setText("\u3000\u3000");
        }
        editText.setSelection(editText.getText().toString().length());
        editText.setFilters(new InputFilter[]{inputFilter});
        editText.setOnFocusChangeListener(focusListener);
        editText.addTextChangedListener(textWatcher);
        editText.setOnClickListener(onTouchListener);
        return editText;
    }

    private OnClickListener onTouchListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onTouchListen != null) {
                onTouchListen.onTouch();
            }
        }
    };

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.edit_imageview, null);
        layout.setTag(viewTagIndex++);

        final ImageView delete = layout.findViewById(R.id.iv_delete);
        final ImageView change = layout.findViewById(R.id.iv_change);

        if (noImageOperate) {
            delete.setVisibility(GONE);
            change.setVisibility(GONE);
        } else {
            delete.setTag(layout.getTag());
            delete.setOnClickListener(deleteListener);

            change.setTag(layout.getTag());
            change.setOnClickListener(changeListener);
            layout.setOnClickListener(imageClickListener);
        }

        return layout;
    }

    /**
     * 插入输入框
     */
    private boolean insertEditText() {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex);
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);

        if (lastEditStr.length() == 0 || editStr1.length() == 0) {
            return false;
        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(editStr1);
            String editStr2 = lastEditStr.substring(cursorIndex);
            if (editStr2.length() == 0) {
                editStr2 = "";
            }
            //插入文字
            addEditTextAtIndex(lastEditIndex + 1, editStr2);
            return true;
        }
    }

    /**
     * 插入一张图片
     */
    public void insertImage(String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex);
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);

        if (lastEditStr.length() == 0 || lastEditStr.equals("\u3000")
                || lastEditStr.equals("\u3000\u3000") || editStr1.length() == 0) {
            addImageViewAtIndex(lastEditIndex, imagePath);
        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(editStr1);
            String editStr2 = lastEditStr.substring(cursorIndex);
            if (editStr2.length() == 0) {
                editStr2 = "";
            }

            //插入文字
            addEditTextAtIndex(lastEditIndex + 1, editStr2);

            //插入图片
            addImageViewAtIndex(lastEditIndex + 1, imagePath);

        }
        hideKeyBoard();
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
        }
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    public void addEditTextAtIndex(final int index, CharSequence editStr) {
        EditText editText2 = createEditText("", EDIT_PADDING);
        editText2.append("\u3000\u3000");
        editText2.append(editStr);

        editText2.setSelection(2);
        editText2.requestFocus();

        allLayout.addView(editText2, index);
    }

    /**
     * 清除特定位置的ImageView
     */
    public void removeImageViewAtIndex(int index) {
        onImageCloseClick(index);
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index, String imagePath) {
        final RelativeLayout imageLayout = createImageLayout();
        imageLayout.setTag(R.id.richEditor, "image");
        final DataImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_write_cover_bg)
                .error(R.drawable.image_write_cover_bg)
                .transform(new GlideRoundImage(mContext, 8));
        Glide.with(mContext)
                .asBitmap()
                .load(imagePath)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        imageView.setImageResource(R.drawable.image_write_cover_bg);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                });
        imageView.setAbsolutePath(imagePath);//保留这句，后面保存数据会用

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = DensityUtil.dip2px(mContext, 15);
        lp.topMargin = DensityUtil.dip2px(mContext, 20);
        imageView.setLayoutParams(lp);
        allLayout.addView(imageLayout, index);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public void clearData(Context context) {
        allLayout.removeAllViews();
        edit_count = 0;
        words_num = 0;
        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final EditText firstEdit = createEditText("请输入正文", dip2px(context, EDIT_PADDING));
        allLayout.addView(firstEdit, firstEditParam);
        lastFocusEdit = firstEdit;
    }

    /**
     * 获取内容
     *
     * @return
     */
    public String getContent() {
        JSONArray array = new JSONArray();
        try {
            int num = allLayout.getChildCount();
            for (int index = 0; index < num; index++) {
                View itemView = allLayout.getChildAt(index);
                if (itemView instanceof EditText) {
                    EditText item = (EditText) itemView;
                    String trim = item.getText().toString().trim();
                    if (TextUtils.isEmpty(trim) || trim.equals("\u3000")
                            || trim.equals("\u3000\u3000")) {
                        continue;
                    }

                    String text = item.getText().toString();
                    text = text.replace("\u3000\u3000", "");
                    JSONObject object = new JSONObject();
                    object.put("type", "text");
                    object.put("content", text);
                    array.put(object);

                } else if (itemView instanceof RelativeLayout) {
                    if ("image".equals(itemView.getTag(R.id.richEditor))) {
                        DataImageView item = itemView.findViewById(R.id.edit_imageView);

                        JSONObject object = new JSONObject();
                        object.put("type", "image");
                        object.put("content", item.getAbsolutePath());
                        array.put(object);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (array.length() == 0) {
            return "";
        } else {
            return array.toString();
        }
    }

    //是否需要创建新的EditText
    private boolean isNeedNewEdit = false;

    /**
     * 设置数据源
     *
     * @param content
     */
    public void setContent(String content) {
        try {
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String type = object.getString("type");
                String s = object.getString("content");
                if (type.equals("image")) {
                    insertImage(s);
                } else {
                    if (isNeedNewEdit) {
                        insertEditText();
                    } else {
                        isNeedNewEdit = true;
                    }
                    appendText(s);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            appendText(content);
        }
        isNeedNewEdit = false;
    }

}
