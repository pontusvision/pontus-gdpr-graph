package com.pontusvision.gdpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.schema.JanusGraphManagement;
//import org.json.JSONArray;
//import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static org.janusgraph.core.attribute.Text.textContainsFuzzy;

@Path("home") public class Resource
{

  public Resource()
  {

  }

  @GET @Path("hello") @Produces(MediaType.TEXT_PLAIN) public String helloWorld()
  {
    return "Hello, world!";
  }

  Gson gson = new Gson();

  GsonBuilder gsonBuilder = new GsonBuilder();



  @POST @Path("records") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply records( RecordRequest req)
  {

    if (req.search != null && req.search.cols != null)
    {

      String[] vals = new String[req.search.cols.length];


      for (int i = 0, ilen = req.search.cols.length; i < ilen; i++)
      {
        vals[i] = req.search.cols[i].id;

      }

      try
      {

        GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person");
        String searchStr = req.search.getSearchStr();


        if (StringUtils.isNotEmpty(searchStr))
        {

          resSet.has("Person.FullName", textContainsFuzzy(searchStr));

        }
        else
        {
          resSet.has("Metadata.Type", req.search.extraSearch[0].value);

        }

        resSet.valueMap(true,vals)
            .range(req.from, req.to);

        List<Map<String, Object>> res = resSet.toList();

        String[] recs = new String[res.size()];
        ObjectMapper objMapper = new ObjectMapper();

        for (int i = 0, ilen = res.size(); i < ilen; i++)
        {
          Map<String, Object> map = res.get(i);

          //          recs[i] = new Record();
          Map<String, String> rec = new HashMap<>();
          for (Map.Entry<String, Object> entry : map.entrySet())
          {
            Object val = entry.getValue();
            if (val instanceof ArrayList)
            {
              ArrayList<Object> arrayList = (ArrayList) val;

              String val2 = arrayList.get(0).toString();

              rec.put(entry.getKey(), val2);

            }
            else
            {
              rec.put(entry.getKey(), val.toString());

            }

          }

          recs[i] = objMapper.writeValueAsString(rec);
        }

        Long count = StringUtils.isEmpty(searchStr)?
            App.g.V()
                .has("Metadata.Type",  req.search.extraSearch[0].value).range(req.from, req.to + req.to-req.from).propertyMap(vals).count().toList()
                .get(0)+req.from:

        App.g.V()
            .has("Person.FullName", textContainsFuzzy(searchStr)).range(req.from,  req.to + req.to-req.from).propertyMap(vals).count().toList()
            .get(0)+req.from;

        //        Long count = Long.parseLong(countStr);

        RecordReply reply = new RecordReply(req.from, req.to, count, recs);

        return reply;
      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }
    String[] recs = new String[0];

    return new RecordReply(req.from, req.to, 0L, null);

  }

  @POST @Path("graph") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public GraphReply graph (GraphRequest greq)
  {

    Set<Vertex> outNodes = App.g.V(Long.parseLong(greq.graphId)).to(Direction.OUT).toSet();
    Set<Vertex> inNodes = App.g.V(Long.parseLong(greq.graphId)).to(Direction.IN).toSet();

    Set<Edge> outEdges = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.OUT).toSet();
    Set<Edge> inEdges = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.IN).toSet();

    GraphReply retVal = new GraphReply(inNodes, outNodes, inEdges, outEdges);

    return retVal;
  }


  @POST @Path("vertex_labels") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public VertexLabelsReply vertexLabels(String str)
  {
    JanusGraphManagement mgt = App.graph.openManagement();


    VertexLabelsReply reply = new VertexLabelsReply(mgt.getVertexLabels());

    mgt.commit();
    return reply;
  }

  @POST @Path("country_data_count") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public CountryDataReply countryDataCount(CountryDataRequest req)
  {
    if (req != null)
    {


      String searchStr = req.searchStr;

      //      GraphTraversal g =
      try
      {
          GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person");
          //        Boolean searchExact = req.search.getSearchExact();

          CountryDataReply data = new CountryDataReply();

          List<Map<String, Long>> res =
              StringUtils.isNotEmpty(searchStr)?
              resSet.has("Person.FullName", textContainsFuzzy(searchStr)).values("Person.Nationality").groupCount().toList():
              resSet.has("Person.Nationality").values("Person.Nationality").groupCount().toList();

          if (res.size() == 1)
          {
            data.countryData.putAll(res.get(0));
          }


          return data;

      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }

    return new CountryDataReply();


  }


  @POST @Path("node_property_names") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public NodePropertyNamesReply nodeProperties(VertexLabelsReply req)
  {

    try
    {
      if (req != null && req.labels != null)
      {

        //        String[] labels = new String[req.labels.length - 1];
        //        String label0 = req.labels[0].value;
        GraphTraversal g = App.g.V();
        for (int i = 0, ilen = req.labels.length; i < ilen; i++)
        {
          g = g.has("Metadata.Type", req.labels[i].value).range(0,1);

          //          labels[i] = (req.labels[i + 1].value);

        }

        NodePropertyNamesReply reply = new NodePropertyNamesReply(g.properties().label().toSet()
            //            App.g.V().hasLabel(label0, labels).properties().label().toSet()
        );
        return reply;


      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    return new NodePropertyNamesReply(Collections.EMPTY_SET);
  }

  @POST @Path("edge_labels") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public EdgeLabelsReply edgeLabels(String str)
  {
    //        GraphTraversal<Edge,String> trav = App.g.E().label().dedup();

    EdgeLabelsReply reply = new EdgeLabelsReply(App.g.E().label().toSet());
    return reply;
  }

  @GET @Path("param") @Produces(MediaType.TEXT_PLAIN) public String paramMethod(@QueryParam("name") String name,
                                                                                @HeaderParam("AUTHORIZATION") String auth)
  {
    return "Hello, " + name + " AUTHORIZATION" + auth;
  }


}