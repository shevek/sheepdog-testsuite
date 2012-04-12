/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nebula.sheeptester.ValidationException;
import com.nebula.sheeptester.exec.TimedProcess;
import java.util.ArrayList;
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
public class ComputeNode {

    private static final Log LOG = LogFactory.getLog(ComputeNode.class);
    private final Context context;
    private final String host;
    private final String user;
    private final Session session;
    // private final Map<Integer, Sheep> sheep = new HashMap<Integer, Sheep>();

    public ComputeNode(Context context, String host, String user, String password) throws JSchException {
        this.context = context;
        this.host = host;
        this.user = user;

        this.session = context.getJsch().getSession(user, host);
        session.setUserInfo(new ConsoleUserInfo(password));
        session.setDaemonThread(true);
        session.connect();
    }

    @Nonnull
    public Context getContext() {
        return context;
    }

    @Nonnull
    public String getHost() {
        return host;
    }

    @Nonnull
    public String getUser() {
        return user;
    }

    @Nonnull
    public Session getSession() {
        return session;
    }

    @Nonnull
    public String getCollie() {
        return context.getCollie();
    }

    @Nonnull
    public List<Sheep> getSheep() throws ValidationException, InterruptedException {
        List<Sheep> sheeps = new ArrayList<Sheep>();

        // ByteArrayOutputStream output = new ByteArrayOutputStream();
        TimedProcess process = new TimedProcess(this, "sudo netstat -tnlp", 2000);
        process.start();
        process.await();
        String output = process.getOutput().toString();
        Pattern P_WORDS = Pattern.compile("\\s+");
        // tcp        0      0 0.0.0.0:8541            0.0.0.0:*               LISTEN      4252/skype      
        for (String line : StringUtils.split(output, "\n")) {
            if (!line.startsWith("tcp "))
                continue;
            String[] words = P_WORDS.split(line);
            if (words.length != 7)
                throw new ValidationException("Bad line from netstat (words=" + words.length + "): '" + line + "'");
            if (!"LISTEN".equals(words[5]))
                throw new ValidationException("Bad line from netstat (LISTEN): " + line);

            int idx;

            idx = words[6].indexOf('/');
            if (idx == -1)
                throw new ValidationException("Bad line from netstat (cmd-idx): " + line);
            String proc = words[6].substring(idx + 1);
            if (!"sheep".equals(proc))
                continue;
            int pid = Integer.parseInt(words[6].substring(0, idx));

            idx = words[3].lastIndexOf(':');
            if (idx == -1)
                throw new ValidationException("Bad line from netstat (port-idx): " + line);
            int port = Integer.parseInt(words[3].substring(idx + 1));

            Sheep sheep = new Sheep(this, port, pid);
            sheeps.add(sheep);
        }

        return sheeps;
    }

    @Override
    public String toString() {
        return "ComputeNode(" + getUser() + "@" + getHost() + ")";
    }
}