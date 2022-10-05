package annotation;


import java.lang.annotation.*;

/**
 * 标记负载均衡实现类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoadBalance {

    String value() default "Random";
}
