package com.alibaba.ageiport.ext.arch.factory;


import com.alibaba.ageiport.ext.arch.ExtensionFactory;
import com.alibaba.ageiport.ext.arch.ExtensionLoader;
import com.alibaba.ageiport.ext.arch.SPI;

/**
 * SpiExtensionFactory
 *
 * @author lingyi
 */
public class SpiExtensionFactory implements ExtensionFactory {

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if (!loader.getSupportedExtensions().isEmpty()) {
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }

}
