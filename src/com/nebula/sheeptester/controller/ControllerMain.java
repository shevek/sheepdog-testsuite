/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller;

import com.nebula.sheeptester.controller.config.RootConfiguration;
import com.nebula.sheeptester.controller.config.TestConfiguration;
import com.nebula.sheeptester.target.TargetMain;
import java.io.File;
import java.io.InputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author shevek
 */
public class ControllerMain {

    private static final Log LOG = LogFactory.getLog(ControllerMain.class);
    public static final String OPT_HELP = "help";
    public static final String OPT_CONFIG = "config";
    public static final String OPT_JAR = "jar";
    public static final String OPT_COLLIE = "collie";
    public static final String OPT_SHEEP = "sheep";
    public static final String OPT_TEST = "test";
    public static final String OPT_THREADS = "threads";
    public static final String OPT_TARGET = "target";
    public static final int DFLT_THREADS = 20;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("Display help.").create(OPT_HELP));
        options.addOption(OptionBuilder.hasArg().withDescription("Name of a configuration file.").create(OPT_CONFIG));
        options.addOption(OptionBuilder.hasArg().withDescription("Tests to execute.").create(OPT_TEST));
        options.addOption(OptionBuilder.hasArg().withDescription("Path to sheeptester JAR.").create(OPT_JAR));
        options.addOption(OptionBuilder.hasArg().withDescription("Path to sheep binary.").create(OPT_SHEEP));
        options.addOption(OptionBuilder.hasArg().withDescription("Path to collie binary.").create(OPT_COLLIE));
        options.addOption(OptionBuilder.hasArg().withDescription("Number of threads.").create(OPT_THREADS));
        options.addOption(OptionBuilder.withDescription("Operate as if on the target host (Do not use).").create(OPT_TARGET));

        CommandLineParser cmdparser = new GnuParser();
        CommandLine cmdline = cmdparser.parse(options, args);

        if (cmdline.hasOption(OPT_TARGET)) {
            TargetMain.main(ArrayUtils.EMPTY_STRING_ARRAY);
            return;
        }

        if (cmdline.hasOption(OPT_HELP) || !cmdline.hasOption(OPT_CONFIG)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setLongOptPrefix("--");
            formatter.printHelp("sheeptester-app.jar --" + OPT_CONFIG + "=<config-file>", options);
            return;
        }

        RootConfiguration configuration;
        {
            String path = cmdline.getOptionValue(OPT_CONFIG);
            InputStream in = FileUtils.openInputStream(new File(path));
            try {
                Serializer serializer = new Persister();
                configuration = serializer.read(RootConfiguration.class, in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        ControllerContext context = new ControllerContext(configuration, cmdline);

        context.init();

        try {
            String[] tests = cmdline.getOptionValues(OPT_TEST);
            if (tests != null) {
                for (String testlist : tests) {
                    for (String test : StringUtils.split(testlist, ',')) {
                        TestConfiguration config = configuration.getTest(test);
                        if (config == null)
                            throw new NullPointerException("No such test " + test);
                        config.run(context);
                    }
                }
            } else {
                for (TestConfiguration config : configuration.getTests()) {
                    config.run(context);
                }
            }

        } finally {
            context.fini();
        }
    }
}