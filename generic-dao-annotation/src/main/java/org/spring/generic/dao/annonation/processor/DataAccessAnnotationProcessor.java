package org.spring.generic.dao.annonation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 *
 * @author
 */
@Component
public class DataAccessAnnotationProcessor implements BeanPostProcessor {
    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.scanDataAccessAnnonation(bean, beanName); 
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void scanDataAccessAnnonation(Object bean, String beanName) {
        this.configureFieldInjection(bean);
    }

    private void configureFieldInjection(Object bean) {
        Class<?> managedBeanClass = bean.getClass();
        
        FieldCallback fieldCallback
                = new DataAccessFieldCallback(configurableBeanFactory, bean);
        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback); 
    }
    
}
