package com.dace.textreader.view.edit;

import android.text.Editable;
import android.text.NoCopySpan;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.view.edit
 * Created by Administrator.
 * Created time 2018/9/10 0010 下午 5:53.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public interface TextWatcher extends NoCopySpan {

    void beforeTextChanged(CharSequence s, int start,
                           int count, int after);

    void onTextChanged(CharSequence s, int start, int before, int count);

    void afterTextChanged(Editable s);

}
