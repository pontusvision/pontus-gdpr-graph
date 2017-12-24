package com.pontusvision.gdpr;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.net.URISyntaxException;
import java.util.Iterator;

public class GraphNode
{
  static final String METADATA = "Metadata.";
  static final Escaper percentEscaper = new PercentEscaper("-_.*", false);


  Long id;
  String label = "";
  String shape;
  String title;
  String image;

  public GraphNode()
  {
    this.shape = "image";

  }

  public GraphNode(Vertex v)
  {
    this();
    this.id = (Long) v.id();
    String vLabel = v.label();

    int vLabelSize = vLabel.length() + 1; // +1 for the dot.

    //    this.label = vLabel;
    //    "data:image/svg+xml;charset=utf-8,"+

    //    StringBuilder sb = new StringBuilder(vLabel).append("\n");
    StringBuilder sb = new StringBuilder();

    this.title = "<h1> title </h1>";
    Iterator<VertexProperty<Object>> properties = v.properties();
    while (properties.hasNext())
    {
      VertexProperty<Object> prop = properties.next();
      String label = prop.label();

      sb.append("<tr><td class=\"tg-yw4l\">");

      if (label.startsWith(vLabel))
      {
        sb.append(label.substring(vLabelSize));

      }
      else if (label.startsWith(METADATA))
      {
        sb.append(label.substring(METADATA.length()));

      }
      else
      {
        sb.append(label);
      }
      sb.append("</td><td class=\"tg-yw4l\">");

      sb.append(prop.value().toString()).append("</td></tr>");

    }

    StringBuilder svgSb = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"600\" height=\"700\">")
        .append(
            "<rect x=\"0\" y=\"0\" width=\"100%\" height=\"100%\" fill=\"#7890A7\" stroke-width=\"20\" stroke=\"#ffffff\" ></rect>")
        .append("<foreignObject x=\"15\" y=\"10\" width=\"100%\" height=\"100%\">")
        .append("<div xmlns=\"http://www.w3.org/1999/xhtml\" style=\"font-size:40px\">")
        .append("<style type=\"text/css\">\n")
        .append( "p {margin:0 0 1em}\n" + "table p {margin :0}\n" + ".wrap {\n" + "\tmargin:50px 0 0 2%;\n"
            + "\twidth:95%;\n" + "\tfloat:left;\n" + "\tposition:relative;\n" + "\theight:200px;\n"
            + "\toverflow:hidden;\n" + "\tpadding:25px 0 0;\n" + "\tbackground:green;\n" + "\tborder:1px solid #000;\n"
            + "}\n" + ".inner {\n" + "\tpadding:0 18px 0 0; \n" + "\theight:200px;\n" + "\toverflow:auto;\n" + "}\n"
            + "table {\n" + "\twidth:100%;\n" + "\tmargin:0 0 0 -1px;\n" + "\tborder-collapse:collapse;\n" + "}\n"
            + "td {\n" + "\tpadding:5px;\n" + "\tborder:1px solid #000;\n" + "\ttext-align:center;\n"
            + "\tbackground:yellow;\n" + "}\n" + "tfoot th, thead th {\n" + "\tfont-weight:bold;\n"
            + "\ttext-align:center;\n" + "\tborder:1px solid #000;\n" + "\tpadding:0 3px 0 5px;\n"
            + "\tbackground:green;\n" + "\tcolor:#fff;\n" + "}\n" + "thead th {border:none;}\n" + "thead tr p {\n"
            + "\tposition:absolute;\n" + "\ttop:0;\n" + "}\n" + ".last {\n" + "\tpadding-right:15px!important;\n" + "}")
        .append("</style>")
        .append("<div class=\"wrap\"<div class=\"inner\"><table>")
        .append("<thead>")
        .append("<tr><th>Property</th><th>Value</th></tr></thead><tbody>").append(sb)
        .append("</tbody></table></div>").append("</div></div></foreignObject></svg>");

    StringBuilder imageSb = new StringBuilder("data:image/svg+xml;charset=utf-8,");
    imageSb.append(percentEscaper.escape(svgSb.toString()));

    this.image = imageSb.toString();
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

  public String getImage()
  {
    return image;
  }

  public void setImage(String image)
  {
    this.image = image;
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
