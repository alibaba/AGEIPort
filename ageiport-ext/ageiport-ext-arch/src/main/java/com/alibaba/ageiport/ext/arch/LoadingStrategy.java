package com.alibaba.ageiport.ext.arch;

public interface LoadingStrategy {
    String directory();

    default boolean preferExtensionClassLoader() {
        return false;
    }

    default String[] excludedPackages() {
        return null;
    }
}
