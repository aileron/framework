/**
 * 
 */
package cc.aileron.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * リクエストパラメータや、URLパラメータを受けとる為のアノテーション
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface WebParam
{
    /**
     * @return パラメータ名
     */
    String[] value() default {};
}
