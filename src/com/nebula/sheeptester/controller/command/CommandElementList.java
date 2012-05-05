/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierNickname;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author shevek
 */
@TypeQualifierNickname
@ElementListUnion({
    @ElementList(inline = true, type = EchoCommand.class, required = false),
    @ElementList(inline = true, type = ParallelCommand.class, required = false)
})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandElementList {
}
