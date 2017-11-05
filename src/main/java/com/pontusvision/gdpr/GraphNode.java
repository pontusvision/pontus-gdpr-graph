package com.pontusvision.gdpr;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;

public class GraphNode
{
  static final String METADATA = "Metadata.";

  Long id;
  String label;
  String shape;
  String title;



  public GraphNode()
  {
    this.shape = "box";

  }

  public GraphNode(Vertex v)
  {
    this();
    this.id = (Long) v.id();
    String vLabel = v.label();

    int vLabelSize = vLabel.length() + 1; // +1 for the dot.

//    this.label = vLabel;
    StringBuilder sb = new StringBuilder(vLabel).append("\n");

    this.title = "<h1> title </h1>";
    Iterator<VertexProperty<Object>> properties = v.properties();
    while (properties.hasNext())
    {
      VertexProperty<Object> prop = properties.next();
      String label = prop.label().toString();
      if (label.startsWith(vLabel))
      {
        sb.append(label.substring(vLabelSize)).append("\t:");

      }
      else if (label.startsWith(METADATA))
      {
        sb.append(label.substring(METADATA.length())).append(":");

      }
      else
      {
        sb.append(label).append(":");
      }

      sb.append(prop.value().toString()).append("\n");

    }
    this.label = sb.toString();
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }


  public String getShape()
  {
    return shape;
  }

  public void setShape(String shape)
  {
    this.shape = shape;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof GraphNode))
      return false;

    GraphNode graphNode = (GraphNode) o;

    return id.equals(graphNode.id);
  }

  @Override public int hashCode()
  {
    return id.hashCode();
  }
}
