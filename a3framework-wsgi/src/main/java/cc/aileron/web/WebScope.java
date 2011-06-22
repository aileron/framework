/**
 * 
 */
package cc.aileron.web;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * インスタンス、ライフサイクル
 */
public interface WebScope
{
    /**
     * Request
     */
    @Scope
    @Retention(RetentionPolicy.RUNTIME)
    @interface Request
    {
    }

    /**
     * Session
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Scope
    @interface Session
    {
    }
}
