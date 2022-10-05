package annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标记客户端动态代理的类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RpcReference {

    /**
     * 超时时间 seconds
     * @return
     */
    long timeout() default 5;

    String loadBalance() default "Random";

}
