package com.alibaba.ageiport.common.utils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;

/**
 * {@link BeanFactory} Utilities class
 *
 * @author lingyi
 */
public abstract class BeanFactoryUtils {

    /**
     * Get optional Bean
     *
     * @param beanFactory {@link ListableBeanFactory}
     * @param beanName    the name of Bean
     * @param beanType    the {@link Class type} of Bean
     * @param <T>         the {@link Class type} of Bean
     * @return A bean if present , or <code>null</code>
     */
    public static <T> T getOptionalBean(ListableBeanFactory beanFactory, String beanName, Class<T> beanType) {

        if (StringUtils.isBlank(beanName)) {
            return null;
        }

        String[] beanNames = ArrayUtils.of(beanName);

        List<T> beans = getBeans(beanFactory, beanNames, beanType);

        return CollectionUtils.isEmpty(beans) ? null : beans.get(0);
    }


    /**
     * Gets name-matched Beans from {@link ListableBeanFactory BeanFactory}
     *
     * @param beanFactory {@link ListableBeanFactory BeanFactory}
     * @param beanNames   the names of Bean
     * @param beanType    the {@link Class type} of Bean
     * @param <T>         the {@link Class type} of Bean
     * @return the read-only and non-null {@link List} of Bean names
     */
    public static <T> List<T> getBeans(ListableBeanFactory beanFactory, String[] beanNames, Class<T> beanType) {

        if (ArrayUtils.isEmpty(beanNames)) {
            return Collections.emptyList();
        }

        String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, beanType);

        List<T> beans = new ArrayList<T>(beanNames.length);

        for (String beanName : beanNames) {
            if (ArrayUtils.contains(allBeanNames, beanName)) {
                beans.add(beanFactory.getBean(beanName, beanType));
            }
        }

        return Collections.unmodifiableList(beans);
    }
}
