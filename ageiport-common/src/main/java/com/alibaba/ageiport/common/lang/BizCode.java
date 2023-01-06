package com.alibaba.ageiport.common.lang;

import java.io.Serializable;

import com.alibaba.ageiport.common.utils.StringUtils;

import lombok.Data;
import lombok.NonNull;

/**
 * Business Code
 *
 * @author lingyi
 **/
@Data
public class BizCode implements Serializable {

    @NonNull
    private String code;

    @NonNull
    private String messageTemplate;

    public BizCode(@NonNull String code, @NonNull String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String parseMessage(Object... params) {
        return StringUtils.format(messageTemplate, params);
    }

}
