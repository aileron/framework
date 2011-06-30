/**
 *
 */
package cc.aileron.workflow;

import static cc.aileron.wsgi.Wsgi.Method.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import cc.aileron.hier.UrlTreeContainer;
import cc.aileron.hier.UrlTreeContainerDelimiterDefault;
import cc.aileron.hier.UrlTreeContainerImpl;
import cc.aileron.wsgi.Wsgi.Method;

/**
 * @author aileron
 */
public class UrlTreeContainerTest
{
    /**
     * test
     */
    @Test
    public void test()
    {

        t.put("/test/a/", Method.GET, 1);
        t.put("/test/b", Method.GET, 2);

        Assert.assertEquals(1, t.get("/test/a", Method.GET, null, null));
        Assert.assertEquals(2, t.get("/test/b", Method.GET, null, null));

        t.put("/test/${id}", Method.GET, 3);
        t.put("/test/${id}/test", Method.GET, 4);

        t.put("/${id}/test", Method.GET, 5);

        t.put("/${id}/${dir}", Method.GET, 6);

        t.put("/search_${id}", Method.GET, 7);

        t.put("/line_${id}", Method.GET, 8);
        t.put("/area_${id}", Method.GET, 9);

        t.put("/${city}_${pref}/area_list", Method.GET, 10);
        t.put("/${city}_${pref}/line_list", Method.GET, 11);
        t.put("/${city}_${pref}/station_list", Method.GET, 12);

        t.put("/corp_${id}/list", Method.GET, 13);

        t.put("/${id}/detail", Method.GET, 14);
        t.put("/${id}/image_list", Method.GET, 15);
        t.put("/${id}/image_${imagenumber}", Method.GET, 16);

        t.put("/${city}_sitemap", Method.GET, 17);
        t.put("/${city}_sitemap2", Method.GET, 18);
        t.put("/${city}_sitemap3", Method.GET, 19);

        t.put("/${city}_sitemap/selection", Method.GET, 20);

        t.put("/${option}_${city}_${pref}/area_list", Method.GET, 21);
        t.put("/${option}_${city}_${pref}/line_list", Method.GET, 22);
        t.put("/${option}_${city}_${pref}/station_list", Method.GET, 23);

        final HashMap<String, Object> p = new HashMap<String, Object>();
        final Set<String> key = p.keySet();

        final HashMap<String, Object> up = new HashMap<String, Object>();

        Assert.assertEquals(3, t.get("/test/d", Method.GET, up, key));
        Assert.assertEquals(4, t.get("/test/c/test", Method.GET, up, key));
        Assert.assertEquals(0, t.get("/test/a/test", Method.GET, up, key));

        Assert.assertEquals(5, t.get("/bbb/test", Method.GET, up, key));
        Assert.assertEquals(6, t.get("/bbb/aaa", Method.GET, up, key));

        Assert.assertEquals(7, t.get("/search_tokyo", Method.GET, up, key));
        Assert.assertEquals(8, t.get("/line_tokyo", Method.GET, up, key));

        Assert.assertEquals(9, t.get("/area_tokyo", Method.GET, up, key));

        Assert.assertEquals(10,
                t.get("/shibuya_tokyo/area_list", Method.GET, up, key));

        Assert.assertEquals(11,
                t.get("/shibuya_tokyo/line_list", Method.GET, up, key));

        Assert.assertEquals(12,
                t.get("/shibuya_tokyo/station_list", Method.GET, up, key));

        Assert.assertEquals(13, t.get("/corp_10/list", Method.GET, up, key));

        final ArrayList<String> urls = new ArrayList<String>(t.all().keySet());
        Collections.sort(urls);
        for (final String e : urls)
        {
            System.out.println(e);
        }
    }

    /**
     * test
     */
    @Test
    public void test2()
    {
        t.put("/${a}_${b}_${c}/test", GET, 1);
        t.put("/${a}_${b}_${c}/test", GET, "edit", 2);
        t.put("/${a}_${b}_${c}/test", GET, "update", 3);
        t.put("/${a}_${b}_${c}/test", GET, "confirm", 4);

        as(1, "/a_b_c/test", GET, "");
        as(2, "/b_c_d/test", GET, "edit");
        as(3, "/e_f_g/test", GET, "update");
        as(4, "/h_i_j/test", GET, "confirm");

    }

    /**
     */
    @Test
    public void test3()
    {
        t.put("/", GET, 1);
        t.put("/${a}", GET, 2);
        t.put("/${a}_edit", GET, 3);

        as(1, "/", GET, "");
        as(2, "/test", GET, "");
        as(3, "/test_edit", GET, "");

        as(0, "/style.css", GET, "");

    }

    /**
     * test
     */
    @Test
    public void test4()
    {
        t.put("/", GET, 1);
        t.put("/${name}", GET, 2);

        as(1, "/", GET, "");
        as(2, "/aaaaaa", GET, "");

    }

    private void as(final int id, final String url, final Method method,
            final String key)
    {
        final HashMap<String, Object> p = new HashMap<String, Object>();
        if (!key.isEmpty())
        {
            p.put(key, null);
        }
        final Set<String> keySet = p.keySet();

        final HashMap<String, Object> up = new HashMap<String, Object>();
        Assert.assertEquals(id, t.get(url, method, up, keySet));
    }

    UrlTreeContainer t = new UrlTreeContainerImpl(new UrlTreeContainerDelimiterDefault());
}
