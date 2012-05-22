/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import com.nebula.sheeptester.controller.model.ClusterInfo;
import com.nebula.sheeptester.controller.model.ClusterEpoch;
import com.nebula.sheeptester.controller.model.ClusterStatus;
import com.nebula.sheeptester.controller.model.SheepAddress;
import com.nebula.sheeptester.controller.model.Vdi;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private static final String STATUS_PREFIX = "Cluster status: ";
    private static final String CREATED_PREFIX = "Cluster created at ";
    private static final String EPOCH_PREFIX = "Epoch Time ";

    public static ClusterInfo parseClusterInfo(byte[] data) throws IOException {
        InputStream is = new ByteArrayInputStream(data);
        Reader ir = new InputStreamReader(is, "US-ASCII");
        BufferedReader br = new BufferedReader(ir);

        ClusterInfo out = new ClusterInfo();
        String line;

        {
            String statusLine = br.readLine();
            if (!statusLine.startsWith(STATUS_PREFIX))
                throw new IllegalArgumentException("Expected " + STATUS_PREFIX + ", not " + statusLine);
            out.status = ClusterStatus.forString(statusLine.substring(STATUS_PREFIX.length()));
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

        line = br.readLine();
        if (!StringUtils.startsWith(line, EPOCH_PREFIX))
            throw new IllegalArgumentException("Expected " + EPOCH_PREFIX + ", not " + line);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Pattern pattern = Pattern.compile("\\s*(\\d+)\\s+\\[(.*)\\]");
        for (;;) {
            String epochLine = br.readLine();
            if (epochLine == null)
                break;

            ClusterEpoch epoch = new ClusterEpoch();

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

            for (String saddr : StringUtils.split(m.group(2), ", ")) {
                int idx = saddr.indexOf(':');
                if (idx < 0)
                    throw new IllegalStateException("Cannot parse an InetSocketAddress from " + saddr + ": Didn't find a colon between address and port.");
                String addr = saddr.substring(0, idx);
                int port = Integer.parseInt(saddr.substring(idx + 1));
                epoch.add(new SheepAddress(addr, port));
            }

            out.epochs.add(epoch);
        }

        return out;
    }

    public static List<Vdi> parseVdiList(byte[] data) throws IOException {
        InputStream is = new ByteArrayInputStream(data);
        Reader ir = new InputStreamReader(is, "US-ASCII");
        BufferedReader br = new BufferedReader(ir);
        if (true)
            throw new UnsupportedOperationException("Not yet implemented.");

        List<Vdi> out = new ArrayList<Vdi>();
        return out;
    }
}
