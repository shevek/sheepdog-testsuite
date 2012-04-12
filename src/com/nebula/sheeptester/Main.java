/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester;

import com.nebula.sheeptester.model.Vdi;
import com.nebula.sheeptester.model.Sheep;
import com.nebula.sheeptester.model.ComputeNode;
import com.jcraft.jsch.JSch;
import com.nebula.sheeptester.model.Context;
import com.nebula.sheeptester.model.SheepOperation;
import com.nebula.sheeptester.model.SheepOperator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 *
 * @author shevek
 */
public class Main {

    public static final String OPT_HELP = "help";
    public static final String OPT_HOST = "host";
    public static final String OPT_USER = "user";
    public static final String OPT_PASSWORD = "password";
    public static final String OPT_COLLIE = "collie";
    private List<ComputeNode> nodes;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("Display help.").create(OPT_HELP));
        options.addOption(OptionBuilder.hasArgs().withDescription("Host or IP of a target system.").create(OPT_HOST));
        options.addOption(OptionBuilder.hasArg().withDescription("Username for ssh.").create(OPT_USER));
        options.addOption(OptionBuilder.hasArg().withDescription("Password for ssh.").create(OPT_PASSWORD));
        options.addOption(OptionBuilder.hasArg().withDescription("Path to collie command.").create(OPT_COLLIE));

        CommandLineParser cmdparser = new GnuParser();
        CommandLine cmdline = cmdparser.parse(options, args);

        String user = cmdline.getOptionValue(OPT_USER, "maestro");
        String password = cmdline.getOptionValue(OPT_PASSWORD, "maestro");

        String[] hosts = cmdline.getOptionValues(OPT_HOST);
        if (hosts == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setLongOptPrefix("--");
            formatter.printHelp("sheeptester-app.jar --" + OPT_HOST + "=...", options);
            return;
        }

        Context context = new Context(cmdline);

        Sheep sheep = null;
        for (String host : hosts) {
            ComputeNode node = new ComputeNode(context, host, user, password);
            List<Sheep> sheeps = node.getSheep();
            System.out.println(sheeps);
            sheep = sheeps.get(0);
        }
        // Sheep sheep = sheeps.get(0);
        Vdi vdi = new Vdi("shevek", 1048576);

        {
            SheepOperation operation = new SheepOperation(SheepOperator.CREATE_VDI, sheep, vdi);
            operation.run();
        }
        {
            SheepOperation operation = new SheepOperation(SheepOperator.READ_VDI, sheep, vdi);
            operation.run();
        }
        {
            SheepOperation operation = new SheepOperation(SheepOperator.WRITE_VDI, sheep, vdi);
            operation.run();
        }
        {
            SheepOperation operation = new SheepOperation(SheepOperator.DELETE_VDI, sheep, vdi);
            operation.run();
        }
    }
}
