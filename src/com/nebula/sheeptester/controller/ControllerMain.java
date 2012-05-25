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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.PersistenceException;
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
    public static final String OPT_TARGET = "target";
    public static final String OPT_VERBOSE = "verbose";
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
        options.addOption(OptionBuilder.withDescription("Operate as if on the target host (Do not use).").create(OPT_TARGET));
        options.addOption(OptionBuilder.withDescription("Operate verbosely.").create(OPT_VERBOSE));

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
            } catch (PersistenceException e) {
                LOG.error("Failed to load configuration file: " + e.getMessage());
                return;
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        ControllerContext context = new ControllerContext(configuration, cmdline);

        context.init();

        Map<String, String> results = new TreeMap<String, String>();
        try {
            List<String> tests = getTests(cmdline);
            if (tests != null) {
                GlobCompiler compiler = new GlobCompiler();
                PatternMatcher matcher = new Perl5Matcher();
                for (String testGlob : tests) {
                    Pattern pattern = compiler.compile(testGlob);
                    boolean found = false;
                    for (TestConfiguration config : configuration.getTests()) {
                        String testId = config.getId();
                        if (isTest(config, matcher, pattern)) {
                            found = true;
                            try {
                                results.put(testId, "Started...");
                                config.run(context);
                                results.put(testId, "OK");
                            } catch (ControllerAssertionException e) {
                                LOG.error("Test failed: " + e.getMessage());
                                results.put(testId, e.getMessage());
                            }
                        }
                    }
                    if (!found)
                        throw new NullPointerException("No such test " + testGlob);
                }
            } else {
                for (TestConfiguration config : configuration.getTests()) {
                    String testId = config.getId();
                    if (config.isAuto()) {
                        try {
                            results.put(testId, "Started...");
                            config.run(context);
                            results.put(testId, "OK");
                        } catch (ControllerAssertionException e) {
                            LOG.error("Test failed: " + e.getMessage());
                            results.put(testId, e.getMessage());
                        }
                    }
                }
            }

            LOG.info("");
            LOG.info("");
            LOG.info("=== Test Results ===");
            for (Map.Entry<String, String> e : results.entrySet()) {
                String value = e.getValue();
                int idx = value.indexOf('\n');
                if (idx > 0)
                    value = value.substring(0, idx);
                LOG.info(e.getKey() + ": " + value);
            }

        } catch (ControllerException e) {
            LOG.error("Failed.", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted!", e);
        } finally {
            context.fini();
        }
    }

    private static List<String> getTests(CommandLine cmdline) {
        String[] tests = cmdline.getOptionValues(OPT_TEST);
        if (tests == null)
            return null;
        List<String> out = new ArrayList<String>();
        for (String test : tests)
            out.addAll(Arrays.asList(StringUtils.split(test, ", ")));
        return out;
    }

    private static boolean isTest(TestConfiguration config, PatternMatcher matcher, Pattern pattern) {
        if (matcher.matches(config.getId(), pattern))
            return true;
        for (String groupId : config.getGroups())
            if (matcher.matches(groupId, pattern))
                return true;
        return false;
    }
}