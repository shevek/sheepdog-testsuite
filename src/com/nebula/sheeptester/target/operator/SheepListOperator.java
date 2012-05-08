/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepListOperator extends AbstractOperator {

    private static final Log LOG = LogFactory.getLog(SheepListOperator.class);

    public static class SheepListResponse extends AbstractResponse {

        List<SheepProcess> sheeps;

        public SheepListResponse(Operator operator, List<SheepProcess> sheeps) {
            super(operator);
            this.sheeps = sheeps;
        }

        @Nonnull
        public List<SheepProcess> getSheeps() {
            if (sheeps == null)
                return Collections.emptyList();
            return sheeps;
        }

        @Override
        protected void toStringBuilderArgs(StringBuilder buf) {
            super.toStringBuilderArgs(buf);
            buf.append(" sheeps=").append(getSheeps());
        }
    }

    public static class SheepProcess {

        public int port;
        public int pid;

        public SheepProcess(int port, int pid) {
            this.port = port;
            this.pid = pid;
        }

        public SheepProcess() {
            this(-1, -1);
        }

        @Override
        public String toString() {
            return "Sheep(pid=" + pid + ",port=" + port + ")";
        }
    }

    @Override
    public SheepListResponse run(TargetContext context) throws Exception {
        List<SheepProcess> sheeps = new ArrayList<SheepProcess>();

        // ByteArrayOutputStream output = new ByteArrayOutputStream();
        TimedProcess process = new TimedProcess(context, 2000, "sudo", "netstat", "-tnlp");
        process.execute();
        String output = process.getOutput().toString();
        Pattern P_WORDS = Pattern.compile("\\s+");
        // tcp        0      0 0.0.0.0:8541            0.0.0.0:*               LISTEN      4252/skype      
        for (String line : StringUtils.split(output, "\n")) {
            if (!line.startsWith("tcp "))
                continue;
            String[] words = P_WORDS.split(line);
            if (words.length != 7)
                throw new TargetException("Bad line from netstat (words=" + words.length + "): '" + line + "'");
            if (!"LISTEN".equals(words[5]))
                throw new TargetException("Bad line from netstat (LISTEN): " + line);

            int idx;

            idx = words[6].indexOf('/');
            if (idx == -1) {
                LOG.warn("Bad line from netstat (cmd-idx): " + line);
                continue;
            }
            String proc = words[6].substring(idx + 1);
            if (!"sheep".equals(proc))
                continue;
            int pid = Integer.parseInt(words[6].substring(0, idx));

            idx = words[3].lastIndexOf(':');
            if (idx == -1)
                throw new TargetException("Bad line from netstat (port-idx): " + line);
            int port = Integer.parseInt(words[3].substring(idx + 1));

            SheepProcess sheep = new SheepProcess(port, pid);
            sheeps.add(sheep);
        }

        return new SheepListResponse(this, sheeps);
    }
}
