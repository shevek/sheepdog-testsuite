/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import com.nebula.sheeptester.controller.model.Vdi;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author shevek
 */
public class CollieParser {

    public static class Epoch extends ArrayList<InetAddress> {

        private int id;
        private long time;
    }

    public static enum Status {

        RUNNING, WAITING, UNKNOWN;

        private static Status forString(String text) {
            if (text.startsWith("running"))
                return RUNNING;
            throw new IllegalArgumentException("Unknown status " + text);
        }
    }

    public static class ClusterInfo {

        public Status status;
        public final List<Epoch> epochs = new ArrayList<Epoch>();
    }
    private static final String STATUS_PREFIX = "Cluster status: ";
    private static final String CREATED_PREFIX = "Cluster created at ";

    public ClusterInfo parseClusterInfo(byte[] data) throws IOException {
        InputStream is = new ByteArrayInputStream(data);
        Reader ir = new InputStreamReader(is, "US-ASCII");
        BufferedReader br = new BufferedReader(ir);

        ClusterInfo out = new ClusterInfo();
        String line;

        {
            String statusLine = br.readLine();
            if (!statusLine.startsWith(STATUS_PREFIX))
                throw new IllegalArgumentException("Expected " + STATUS_PREFIX + ", not " + statusLine);
            out.status = Status.forString(statusLine.substring(STATUS_PREFIX.length()));
        }

        line = br.readLine();
        if (!StringUtils.isBlank(line))
            throw new IllegalArgumentException("Expected blank, not " + line);

        {
            String createdLine = br.readLine();
            if (!createdLine.startsWith(CREATED_PREFIX))
                throw new IllegalArgumentException("Expected " + CREATED_PREFIX + ", not " + createdLine);
        }

        line = br.readLine();
        if (!StringUtils.isBlank(line))
            throw new IllegalArgumentException("Expected blank, not " + line);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Pattern pattern = Pattern.compile("\\s*(\\d+)\\s+\\[(.*)\\]");
        for (;;) {
            String epochLine = br.readLine();
            if (epochLine == null)
                break;

            Epoch epoch = new Epoch();

            ParsePosition pos = new ParsePosition(0);
            Date date = format.parse(epochLine, pos);
            if (date == null)
                throw new IllegalArgumentException("Failed to parse a date from " + epochLine);
            epoch.time = date.getTime();

            epochLine = epochLine.substring(pos.getIndex());

            Matcher m = pattern.matcher(epochLine);
            if (!m.matches())
                throw new IllegalArgumentException("Line did not match " + pattern + ": " + epochLine);

            epoch.id = Integer.parseInt(m.group(1));

            for (String addr : StringUtils.split(m.group(2), ", ")) {
                epoch.add(InetAddress.getByName(addr));
            }

            out.epochs.add(epoch);
        }

        return out;
    }

    public List<Vdi> parseVdiList(byte[] data) throws IOException {
        InputStream is = new ByteArrayInputStream(data);
        Reader ir = new InputStreamReader(is, "US-ASCII");
        BufferedReader br = new BufferedReader(ir);
        if (true)
            throw new UnsupportedOperationException("Not yet implemented.");

        List<Vdi> out = new ArrayList<Vdi>();
        return out;
    }
}
