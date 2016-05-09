package io.cloudhuang;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class to enable cache on methods.
 *
 * @author cloudhuang
 */
@Aspect
public class MethodCacheInterceptor implements ApplicationContextAware {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    private Cache defaultCache;

    // the pointcut signature
    @Pointcut("execution(* *(..)) &&" + " @annotation(cachable) ")
    private void cachableMehtod(Cachable cachable) {
    }

    /**
     * do the caching method. First check the value exist in cache, if not execute the caching method and cache it.
     *
     * @param pjp
     * @param cachable
     * @return
     */
    @Around("cachableMehtod(cachable)")
    public Object doCaching(ProceedingJoinPoint pjp, Cachable cachable) throws Throwable {
        log.debug("processing method caching");
        String cacheName = cachable.value();
        String cacheKey = getCacheKey(pjp.getTarget().getClass().getName(), pjp.getSignature().getName(), pjp.getArgs());

        Element element = getElement(cacheName, cacheKey);
        Object obj;

        if (element == null) {
            obj = pjp.proceed();
            if (log.isDebugEnabled()) {
                log.debug("[doCaching] adding key: [" + cacheKey + "]\n" +
                        "Cache status: \n" + getCache(cacheName).toString());
            }
            putElement(cacheName, cacheKey, obj);
        } else {
            log.debug("Retrieve the value from cache");
            obj = element.getValue();
        }

        return obj;
    }

    /**
     * Construct caching key.
     * Algorithm: targetName/methodName\targuments elements
     *
     * @param targetName
     * @param methodName
     * @param arguments
     * @return
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append(targetName).append("/").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (Object argument : arguments) {
                sb.append("\t");
                sb.append(argument);
            }
        }

        return sb.toString();
    }

    private void putElement(final String cacheName, final String cacheKey, final Object obj) {
        Object cachingObj = obj;

        if (obj instanceof Map) {
            cachingObj = Collections
                    .unmodifiableMap((Map<Class<?>, Class<?>>) obj);
        } else if (obj instanceof List) {
            cachingObj = Collections.unmodifiableList((List<Class<?>>) obj);
        }

        Element element = new Element(cacheKey, (Serializable) cachingObj);
        Cache cache = getCache(cacheName);
        cache.put(element);
    }

    private Element getElement(String cacheName, String cacheKey) {
        Cache cache = getCache(cacheName);
        Element element;

        element = cache.get(cacheKey);

        return element;
    }

    private Cache getCache(String cacheName) {
        Cache cache = null;
        if (!StringUtils.hasText(cacheName)) {
            cache = defaultCache;
        } else if (applicationContext != null) {
            log.debug("[getCache]cache bean is set. Using " + cacheName);
            cache = (Cache) applicationContext.getBean(cacheName);
        } else {
            log.error("[getCache]applicationContext was not found");
        }
        return cache;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDefaultCache(Cache defaultCache) {
        this.defaultCache = defaultCache;
    }
}
