/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.transform.Transform;

/**
 *
 * @author shevek
 */
public class OffsetTransform implements Transform<Long> {

    @Override
    public Long read(String value) throws Exception {
        if (StringUtils.isBlank(value))
            return -1L;
        return Long.parseLong(value);
    }

    @Override
    public String write(Long value) throws Exception {
        if (value < 0)
            return null;
        return String.valueOf(value);
    }
}