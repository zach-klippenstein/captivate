package com.zachklipp.captivate.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;

/*
 * Allows the use of HTML resources as templates, by setting the
 * text content of elements by ID.
 */
public class HtmlResourceTemplate
{
  private Map<String, String> mValues;
  
  public HtmlResourceTemplate withValues(String... values)
  {
    if (values.length % 2 != 0)
    {
      throw new IllegalArgumentException("All element IDs must have a value");
    }
    
    Map<String, String> valueMap = new HashMap<String, String>();
    
    for (int i = 0; i < values.length; i += 2)
    {
      valueMap.put(values[i], values[i+1]);
    }
    
    return withValues(valueMap);
  }
  
  public HtmlResourceTemplate withValues(Map<String, String> values)
  {
    mValues = values;
    return this;
  }
  
  public String render(Context context, int resId)
  {
    return render(context.getResources().openRawResource(resId));
  }
  
  public String render(InputStream input)
  {
    return render(readStream(input));
  }
  
  // TODO this whole chain should return InputStreams if possible
  public String render(String html)
  {
    Document document = parseDocument(new ByteArrayInputStream(html.getBytes()));
    
    if (document != null)
    {
      render(document);
      html = convertDocumentToString(document);
    }
    
    return html;
  }
  
  private void render(Document document)
  {
    assert(mValues != null);

    for (String elementId : mValues.keySet())
    {
      setTextContent(document, elementId, mValues.get(elementId));
    }
  }
  
  private static void setTextContent(Document document, String elementId, String content)
  {
    assert(document != null);
    
    Element element = document.getElementById(elementId);
    
    if (element != null)
    {
      element.setTextContent(content);
    }
  }
  
  private String readStream(InputStream input)
  {
    char[] buffer;
    
    try
    {
      buffer = new char[input.available()];
      new InputStreamReader(input).read(buffer);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Error reading template source", e);
    }
    
    return new String(buffer);
  }
  
  private Document parseDocument(InputStream source)
  {
    try
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      return builder.parse(source);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Error parsing template source", e);
    }
  }
  
  private static String convertDocumentToString(Document document)
  {
    TransformerFactory transFactory = TransformerFactory.newInstance();
    StringWriter buffer = new StringWriter();
    Transformer transformer;
    
    try
    {
      transformer = transFactory.newTransformer();
      transformer.transform(new DOMSource(document), new StreamResult(buffer));
    }
    catch (TransformerException e)
    {
      throw new RuntimeException(e);
    }
    
    return buffer.toString();
  }
}
