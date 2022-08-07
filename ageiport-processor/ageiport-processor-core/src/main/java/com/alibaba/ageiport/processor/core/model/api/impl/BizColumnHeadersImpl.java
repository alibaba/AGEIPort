package com.alibaba.ageiport.processor.core.model.api.impl;

import com.alibaba.ageiport.processor.core.model.api.BizColumnHeader;
import com.alibaba.ageiport.processor.core.model.api.BizColumnHeaders;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * BizColumnHeadersImpl
 *
 * @author lingyi
 */
@Getter
@Setter
public class BizColumnHeadersImpl implements BizColumnHeaders {

    List<BizColumnHeader> bizColumnHeaders;

}
