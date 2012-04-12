/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.jcraft.jsch.JSch;
import com.nebula.sheeptester.Main;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author shevek
 */
public class Context {

    private final JSch jsch = new JSch();
    private final String collie;
    private Set<Vdi> vdis = new HashSet<Vdi>();

    public Context(CommandLine cmdline) {
        collie = cmdline.getOptionValue(Main.OPT_COLLIE, "collie");
    }

    @Nonnull
    public JSch getJsch() {
        return jsch;
    }

    @Nonnull
    public String getCollie() {
        return collie;
    }

    public void addVdi(@Nonnull Vdi vdi) {
        vdis.add(vdi);
    }

    public void removeVdi(@Nonnull Vdi vdi) {
        vdis.remove(vdi);
    }
}
