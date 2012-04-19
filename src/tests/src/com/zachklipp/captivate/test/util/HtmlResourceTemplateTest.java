package com.zachklipp.captivate.test.util;

import com.zachklipp.captivate.util.HtmlResourceTemplate;

import junit.framework.TestCase;

public class HtmlResourceTemplateTest extends TestCase
{
  private static final String[] sValues = new String[]
  {
    "empty-placeholder", "empty placeholder contents",
    "placeholder-with-contents", "new contents",
  };
  
  private static final String sSourceDocument = join("\n", new String[] {
    "<html>",
    "<body>",
    "<div></div>",
    "<h1>",
    "<div id=\"" + sValues[0] + "\"></div>",
    "</h1>",
    "<div id=\"" + sValues[2] + "\"><b>Hello,</b> world!</div>",
    "</body>",
    "</html>",
  });
  
  private static final String[] sResultLines = new String[] {
    "<html>",
    "<body>",
    "<div></div>",
    "<h1>",
    "<div id=\"" + sValues[0] + "\">" + sValues[1] + "</div>",
    "</h1>",
    "<div id=\"" + sValues[2] + "\">" + sValues[3] + "</div>",
    "</body>",
    "</html>",
  };

  public void testRenderString()
  {
    HtmlResourceTemplate template = new HtmlResourceTemplate()
      .withValues(sValues);
    
    String result = template.render(sSourceDocument);
    String[] resultLines = result.split("\n+");
    
    assertEquals(sResultLines.length, resultLines.length);
    
    for (int i = 0; i < sResultLines.length; i++)
    {
      assertEquals(sResultLines[i], resultLines[i]);
    }
  }
  
  private static String join(String delimiter, String... items)
  {
    StringBuilder builder = new StringBuilder();
    
    for (String item : items)
      builder.append(item).append(delimiter);
    
    return builder.toString();
  }

}

