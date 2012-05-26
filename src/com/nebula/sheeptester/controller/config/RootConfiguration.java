/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.config;

import java.util.Collections;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.apache.commons.collections15.ListUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "configuration")
public class RootConfiguration {

    @Attribute(required = false)
    private int threads = 20;
    @ElementList(inline = true, entry = "host", required = false)
    private List<HostConfiguration> hosts;
    @ElementList(inline = true, entry = "sheep", required = false)
    private List<SheepConfiguration> sheeps;
    @ElementList(inline = true, entry = "test", required = false)
    private List<TestConfiguration> tests;

    private static <T> List<T> add(List<T> a, List<T> b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return ListUtils.union(a, b);
    }

    public void addConfiguration(RootConfiguration other) {
        hosts = add(hosts, other.hosts);
        sheeps = add(sheeps, other.sheeps);
        tests = add(tests, other.tests);
    }

    @Nonnegative
    public int getThreads() {
        return threads;
    }

    @Nonnull
    public List<? extends HostConfiguration> getHosts() {
        if (hosts == null)
            return Collections.emptyList();
        return hosts;
    }

    @Nonnull
    public List<? extends SheepConfiguration> getSheeps() {
        if (sheeps == null)
            return Collections.emptyList();
        return sheeps;
    }

    @Nonnull
    public List<? extends TestConfiguration> getTests() {
        if (tests == null)
            return Collections.emptyList();
        return tests;
    }

    @CheckForNull
    public TestConfiguration getTest(@Nonnull String id) {
        for (TestConfiguration test : getTests())
            if (id.equals(test.getId()))
                return test;
        return null;
    }

    @Override
    public String toString() {
        return "Hosts: " + getHosts() + "\nSheep: " + getSheeps() + "\nTests: " + getTests() + "\n";
    }
}
