package ut.edu.udayton.udri.stash;

import org.junit.Test;
import edu.udayton.udri.stash.MyPluginComponent;
import edu.udayton.udri.stash.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}