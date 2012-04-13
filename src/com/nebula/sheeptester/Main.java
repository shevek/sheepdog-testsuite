/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester;

import com.nebula.sheeptester.model.Vdi;
import com.nebula.sheeptester.model.Sheep;
import com.nebula.sheeptester.model.ComputeNode;
import com.nebula.sheeptester.model.Context;
import com.nebula.sheeptester.model.SheepOperation;
import com.nebula.sheeptester.model.SheepOperator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class Main {

    private static final Log LOG = LogFactory.getLog(Main.class);
    public static final String OPT_HELP = "help";
    public static final String OPT_HOST = "host";
    public static final String OPT_USER = "user";
    public static final String OPT_PASSWORD = "password";
    public static final String OPT_COLLIE = "collie";
    public static final String OPT_THREADS = "threads";
    public static final String OPT_VDIS = "vdis";
    public static final String OPT_READS = "reads";
    public static final String OPT_WRITES = "writes";
    public static final String OPT_LOOP = "loop";
    public static final int DFLT_THREADS = 10;
    public static final int DFLT_VDIS = 4;
    public static final int DFLT_READS = 100;
    public static final int DFLT_WRITES = 100;
    public static final int DFLT_LOOP = 1;

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
        options.addOption(OptionBuilder.hasArg().withDescription("Number of threads.").create(OPT_THREADS));
        options.addOption(OptionBuilder.hasArg().withDescription("Number of VDIs.").create(OPT_VDIS));
        options.addOption(OptionBuilder.hasArg().withDescription("Number of reads.").create(OPT_READS));
        options.addOption(OptionBuilder.hasArg().withDescription("Number of writes.").create(OPT_WRITES));
        options.addOption(OptionBuilder.hasArg().withDescription("Number of I/O loops.").create(OPT_LOOP));

        CommandLineParser cmdparser = new GnuParser();
        CommandLine cmdline = cmdparser.parse(options, args);

        if (cmdline.hasOption(OPT_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setLongOptPrefix("--");
            formatter.printHelp("sheeptester-app.jar --" + OPT_HOST + "=...", options);
            return;
        }

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

        List<Sheep> sheeps = new ArrayList<Sheep>();
        for (String host : hosts) {
            ComputeNode node = new ComputeNode(context, host, user, password);
            sheeps.addAll(node.getSheep());
        }

        LOG.info("Sheep are " + sheeps);
        // Sheep sheep = sheeps.get(0);


        int threads = getOptionInteger(cmdline, OPT_THREADS, DFLT_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {

            final List<Vdi> vdis = new ArrayList<Vdi>();
            int nvdis = getOptionInteger(cmdline, OPT_VDIS, DFLT_VDIS);
            for (int i = 0; i < nvdis; i++) {
                vdis.add(new Vdi());
            }

            deleteVdis(executor, sheeps, vdis); // Should fail, unless previous run was aborted.
            createVdis(executor, sheeps, vdis);

            int loop = getOptionInteger(cmdline, OPT_LOOP, 1);
            for (int l = 0; l < loop; l++) {
                LOG.info("Starting iteration " + l);

                int nwrites = getOptionInteger(cmdline, OPT_WRITES, DFLT_WRITES);
                int nreads = getOptionInteger(cmdline, OPT_READS, DFLT_READS);
                final CountDownLatch latch = new CountDownLatch(nwrites + nreads);

                for (int i = 0; i < nwrites; i++) {
                    Sheep sheep = getRandom(sheeps);
                    Vdi vdi = getRandom(vdis);
                    SheepOperation operation = new SheepOperation(SheepOperator.WRITE_VDI, sheep, vdi) {

                        @Override
                        protected void fini() {
                            latch.countDown();
                        }
                    };
                    executor.submit(operation);
                }

                for (int i = 0; i < nreads; i++) {
                    Sheep sheep = getRandom(sheeps);
                    Vdi vdi = getRandom(vdis);
                    SheepOperation operation = new SheepOperation(SheepOperator.READ_VDI, sheep, vdi) {

                        @Override
                        protected void fini() {
                            latch.countDown();
                        }
                    };
                    executor.submit(operation);
                }

                latch.await();
            }

            deleteVdis(executor, sheeps, vdis);

        } finally {
            executor.shutdown();
        }
    }

    private static int getOptionInteger(CommandLine cmdline, String option, int dflt) {
        String value = cmdline.getOptionValue(option);
        if (value == null)
            return dflt;
        return Integer.parseInt(value);
    }
    private static final Random RANDOM = new Random();

    private static <T> T getRandom(@Nonnull List<T> objects) {
        int idx = RANDOM.nextInt(objects.size());
        return objects.get(idx);
    }

    private static void createVdis(ExecutorService executor, List<Sheep> sheeps, List<Vdi> vdis) throws InterruptedException {
        LOG.info("Creating VDIs.");
        {
            final CountDownLatch latch = new CountDownLatch(vdis.size());
            for (Vdi vdi : vdis) {
                Sheep sheep = getRandom(sheeps);
                SheepOperation operation = new SheepOperation(SheepOperator.CREATE_VDI, sheep, vdi) {

                    @Override
                    protected void fini() {
                        latch.countDown();
                    }
                };
                executor.submit(operation);
            }
            latch.await();
        }
    }

    private static void deleteVdis(ExecutorService executor, List<Sheep> sheeps, List<Vdi> vdis) throws InterruptedException {
        LOG.info("Deleting VDIs.");
        {
            final CountDownLatch latch = new CountDownLatch(vdis.size());
            for (Vdi vdi : vdis) {
                Sheep sheep = getRandom(sheeps);
                SheepOperation operation = new SheepOperation(SheepOperator.DELETE_VDI, sheep, vdi) {

                    @Override
                    protected void fini() {
                        latch.countDown();
                    }
                };
                executor.submit(operation);
            }
            latch.await();
        }
    }
}
