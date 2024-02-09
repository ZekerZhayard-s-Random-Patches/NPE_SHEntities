package io.github.zekerzhayard.npe_shentities.gradle;

import java.util.ArrayList;
import java.util.List;

public class RunConfigurationExtension {
    protected List<String> clientJvmArgs = new ArrayList<>();
    protected List<String> serverJvmArgs = new ArrayList<>();

    public void addClientJvmArgs(List<String> args) {
        this.clientJvmArgs.addAll(args);
    }

    public void addServerJvmArgs(List<String> args) {
        this.serverJvmArgs.addAll(args);
    }

    public List<String> getClientJvmArgs() {
        return this.clientJvmArgs;
    }

    public List<String> getServerJvmArgs() {
        return this.serverJvmArgs;
    }
}
