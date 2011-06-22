i="""
package cc.aileron.webclient.html.entity;

/**
 * @author aileron
 */
public interface %(classname)s extends HtmlElement
{
}
"""

for line in open('classname.dat'):
	classname = line.rstrip()
	classnameraw = classname.replace('Html','HTML')
	open(classname+'.java', 'w').write(i % locals())
