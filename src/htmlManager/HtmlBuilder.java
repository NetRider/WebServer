package htmlManager;

import java.io.StringWriter;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class HtmlBuilder {
	
	private VelocityEngine ve;
	private Template template;
	private VelocityContext context;
	
	public HtmlBuilder()
	{
		ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		
		context = new VelocityContext();
	}
	
	public void setTemplate(String templateName)
	{
		template = ve.getTemplate("/templates/" + templateName);
	}
	
	public void setData(String key, Object data)
	{
		context.put(key, data);     
	}
	
	public String getHtmlPageString()
	{
		StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
	}
}
