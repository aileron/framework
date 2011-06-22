/**
 * 
 */
package cc.aileron.template;

import java.io.PrintWriter;

import cc.aileron.pojo.PojoAccessor;

/**
 * @author aileron
 */
public interface Template
{
    /**
     * @param accessor
     * @param writer
     * @param context
     */
    void print(PojoAccessor<?> accessor, PrintWriter writer);
}
