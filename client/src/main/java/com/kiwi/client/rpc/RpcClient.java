package com.kiwi.client.rpc;

import annotation.RpcReference;
import com.kiwi.client.invoke.InvokeProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;



@Slf4j
@Component
public class RpcClient implements ApplicationContextAware {


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcReference.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                Field[] fields = serviceBean.getClass().getDeclaredFields();
                try {
                    for (Field field : fields) {
                        RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                        if (rpcReference != null) {
                            long timeout = rpcReference.timeout();
                            String loadBalance = rpcReference.loadBalance();
                            field.setAccessible(true);
                            field.set(serviceBean, getProxyInstance(field.getType(), timeout, loadBalance));
                        }
                    }
                } catch (Exception e) {
                    log.error("error : ", e);
                }
            }
        }
    }


    private static <T> T getProxyInstance(Class<T> serviceClass, long timeout, String loadBalance) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = {serviceClass};

        return (T) Proxy.newProxyInstance(
                loader,
                interfaces,
                new InvokeProxy(timeout, loadBalance)
        );
    }


}
