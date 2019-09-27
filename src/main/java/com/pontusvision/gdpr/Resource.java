package com.pontusvision.gdpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.SchemaStatus;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.tinkerpop.gremlin.process.traversal.P.*;
import static org.janusgraph.core.attribute.Text.textContainsFuzzy;

//import org.json.JSONArray;
//import org.json.JSONObject;

@Path("home") public class Resource
{

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;

  public Resource()
  {

  }

  @GET @Path("hello") @Produces(MediaType.TEXT_PLAIN) public String helloWorld()
  {
    return "Hello, world!";
  }

  Gson gson = new Gson();

  GsonBuilder gsonBuilder = new GsonBuilder();

  @POST @Path("gremlin") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply gremlin(GremlinRequest req)
  {
    ServerGremlinExecutor executor = App.gserver.getServerGremlinExecutor();
    try
    {
      Optional<CompiledScript> script = executor.getGremlinExecutor().compile(req.gremlin);

      script.get().eval(req.bindings);
    }
    catch (ScriptException e)
    {
      e.printStackTrace();
    }

    return null;

  }

  public static StringBuilder addDateFilter(StringBuilder sb, String colId, String dateFrom, String dateTo, String type)
  {

    if ("notEqual".equals(type))
    {
      // not equals currently implemented as less than or greter than:
      sb.append("( (").append("v.\"").append(colId).append("\":")
        .append("{ * TO ").append(dateFrom).append(" } )")
        .append(" OR ")
        .append("(").append("v.\"").append(colId).append("\":")
        .append("{ ").append(dateFrom).append(" TO * } ) )");
    }
    else
    {

      sb.append("v.\"").append(colId).append("\":");

      //    equals, greaterThan, lessThan, inRange, notEqual
    /*
     Ranges can be specified for date, numeric or string fields.
     Inclusive ranges are specified with square brackets [min TO max] and
     exclusive ranges with curly brackets {min TO max}.
     */

      if ("inRange".equals(type))
      {
        sb.append("[ ").append(dateFrom).append(" TO ").append(dateTo).append(" ]");
      }
      else if ("equals".equals(type))
      {
        sb.append("[ ").append(dateFrom).append(" TO ").append(dateFrom).append(" ]");
      }
      else if ("lessThan".equals(type))
      {
        sb.append("{ * TO ").append(dateFrom).append(" }");
      }
      else if ("greaterThan".equals(type))
      {
        sb.append("{ ").append(dateFrom).append(" TO * }");
      }

    }
    return sb;
  }

  public static StringBuilder addConditionFilter(StringBuilder sb, PVGridFilters filter)
  {
    if (filter.condition1 != null)
    {
      sb.append("(");
      if ("date".equalsIgnoreCase(filter.filterType))
      {
        addDateFilter(sb, filter.colId, filter.condition1.dateFrom, filter.condition1.dateTo, filter.condition1.type);
      }
      else
      {
        addTextFilter(sb, filter.colId, filter.condition1.type, filter.condition1.filter);
      }
      if (filter.condition2 != null)
      {
        sb.append(") ").append(filter.operator).append(" (");
        if ("date".equalsIgnoreCase(filter.filterType))
        {
          addDateFilter(sb, filter.colId, filter.condition2.dateFrom, filter.condition2.dateTo, filter.condition2.type);
        }
        else
        {
          addTextFilter(sb, filter.colId, filter.condition2.type, filter.condition2.filter);
        }
        //              sb.append(")");
      }
      sb.append(")");
    }
    return sb;

  }

  public static StringBuilder addFilter(StringBuilder sb, PVGridFilters filter)
  {
    if ("date".equalsIgnoreCase(filter.filterType))
    {
      return addDateFilter(sb, filter.colId, filter.dateFrom, filter.dateTo, filter.type);

    }
    else
    {
      return addTextFilter(sb, filter.colId, filter.type, filter.filter);
    }

  }

  public static StringBuilder addTextFilter(StringBuilder sb, String colId, String type, String filter)
  {
    sb.append("v.\"").append(colId).append("\":");

    if ("contains".equals(type) || "endsWith".equals(type))
    {
      sb.append('*');
    }
    if ("notEqual".equals(type))
    {
      sb.append("*!");
    }
    else if ("notContains".equals(type))
    {
      sb.append("*!*");
    }
    sb.append(filter);
    if ("contains".equals(type) || "startsWith".equals(type) || "notContains".equals(type))
    {
      sb.append('*');
    }
    return sb;
  }

  public static String getIndexMetadataTypeStr(String vertexType)
  {

    StringBuilder sb = new StringBuilder();

    sb.append("v.\"Metadata.Type.").append(vertexType).append("\":").append(vertexType);

    return sb.toString();
  }

  public static String getIndexSearchStr(RecordRequest req)
  {
    StringBuilder sb = new StringBuilder();

    boolean hasAgFilters = req.filters != null && req.filters.length > 0;

    if (hasAgFilters)
    {

      PVGridFilters[] filters = req.filters;
      sb.append("(");

      for (int i = 0, ilen = filters.length; i < ilen; i++)
      {
        PVGridFilters filter = filters[i];
        if (i > 0)
        {
          sb.append(") AND (");
        }

        /* when we have simple filters, the following format is used:
          [
            { colId: "Object_Notification_Templates_Label", filterType: "text", type: "contains", filter: "adfasdf"},
            { colId: "Object_Notification_Templates_Types", filterType: "text", type: "contains", filter: "aaa"}
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":null,"dateFrom":"1960-08-16","type":"equals","filterType":"date"}
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":null,"dateFrom":"1932-09-02","type":"greaterThan","filterType":"date"}
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"}
          ]
         */

        if (filter.operator == null)
        {
          addFilter(sb, filter);
        }
        /*
          When we have complex filters, the following format is used:
          [
            {
              colId: "Object_Notification_Templates_Label",
              condition1: {filterType: "text", type: "notContains", filter: "ddd"},
              condition2: {filterType: "text", type: "endsWith", filter: "aaaa"},
              filterType: "text",
              operator: "OR"
            },
            {
              colId: "Object_Notification_Templates_Types:{
              condition1: {filterType: "text", type: "notContains", filter: "aaaa"},
              condition2: {filterType: "text", type: "startsWith", filter: "bbbb"},
              filterType: "text",
              operator: "AND"
            }
            {
              "colId":"Person.Natural.Date_Of_Birth",
              "filterType":"date",
              "operator":"OR",
              "condition1":{"dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"},
              "condition2":{"dateTo":null,"dateFrom":"1974-08-02","type":"equals","filterType":"date"}
            }
            {
              "colId":"Person.Natural.Date_Of_Birth",
              "filterType":"date",
              "operator":"OR",
              "condition1":{"dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"},
              "condition2":{"dateTo":null,"dateFrom":"1974-08-02","type":"notEqual","filterType":"date"}
            }
          ]
         */
        else
        {
          addConditionFilter(sb, filter);
        }
      }
      sb.append(")");

    }
    else if (req.search != null && req.search.cols != null)
    {

      PVGridColumn[] cols = req.search.cols;

      for (int i = 0, ilen = cols.length; i < ilen; i++)
      {
        PVGridColumn col = cols[i];
        if (i > 0)
        {
          sb.append(" OR ");
        }
        sb.append("v.\"").append(col.id).append("\":").append(req.search.searchStr);
      }
    }
    return sb.toString();
          /*
          {
   "search":{
      "searchStr":"Mickey",
      "searchExact":true,
      "cols":[
         {
            "name":"Person Full_Name",
            "resizable":true,
            "sortable":true,
            "minWidth":30,
            "rerenderOnResize":false,
            "headerCssClass":null,
            "defaultSortAsc":true,
            "focusable":true,
            "selectable":true,
            "width":624,
            "id":"Person.Natural.Full_Name",
            "field":"Person.Natural.Full_Name"
         },
         {
            "name":"Person Nationality",
            "resizable":true,
            "sortable":true,
            "minWidth":30,
            "rerenderOnResize":false,
            "headerCssClass":null,
            "defaultSortAsc":true,
            "focusable":true,
            "selectable":true,
            "width":623,
            "id":"Person.Natural.Nationality",
            "field":"Person.Natural.Nationality"
         }
      ],
      "extraSearch":{
         "label":"Person.Natural",
         "value":"Person.Natural"
      }
   },
   "from":0,
   "to":600,
   "sortCol":null,
   "sortDir":"+asc"
}
           */

  }

  @POST @Path("records") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply records(RecordRequest req)
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

        String searchStr = req.search.getSearchStr();
        String dataType  = req.dataType; //req.search.extraSearch[0].value;

        Long count = StringUtils.isEmpty(searchStr) ?
            App.graph.indexQuery(dataType + ".MixedIdx", getIndexMetadataTypeStr(dataType)).vertexTotals() :
            //            App.g.V()
            //                 .has("Metadata.Type." + dataType, P.eq(dataType))
            //                 .range(req.from, req.to + req.to - req.from)
            //                 .count().toList()
            //                 .get(0) + req.from :
            App.graph.indexQuery(dataType + ".MixedIdx", getIndexSearchStr(req)).vertexTotals();

        GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person.Natural");

        if (count > 0)
        {

          if (StringUtils.isNotEmpty(searchStr))
          {

            int        limit    = req.to.intValue() - req.from.intValue();
            List<Long> vertices = new ArrayList<>(limit);

            App.graph.indexQuery(req.search.extraSearch[0].value + ".MixedIdx", getIndexSearchStr(req))
                     .vertexStream().forEachOrdered(jgvr -> vertices.add(jgvr.getElement().longId()));

            resSet = App.g.V(vertices);

            //          resSet.has("Person.Natural.FullName", textContainsFuzzy(searchStr));

          }
          else
          {
            resSet = resSet.has("Metadata.Type." + dataType, dataType);

          }

          if (StringUtils.isNotEmpty(req.sortCol))
          {
            resSet = resSet.order().by(req.sortCol, "+asc".equalsIgnoreCase(req.sortDir) ? Order.incr : Order.decr);
          }
          resSet.valueMap(true, vals)
                .range(req.from, req.to);

          List<Map<String, Object>> res = resSet.toList();

          String[]     recs      = new String[res.size()];
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
          RecordReply reply = new RecordReply(req.from, req.to, count, recs);

          return reply;

        }
        //        Long count = Long.parseLong(countStr);

        RecordReply reply = new RecordReply(req.from, req.to, count, new String[0]);

        return reply;

      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }

    return new RecordReply(req.from, req.to, 0L, new String[0]);

  }

  @POST @Path("agrecords") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public RecordReply agrecords(RecordRequest req)
  {

    if (req.cols != null && req.dataType != null)
    {

      Set<String> valsSet = new HashSet<>();
      Set<String> reportButtonsSet = new HashSet<>();


      for (int i = 0, ilen = req.cols.length; i < ilen; i++)
      {
        if (!req.cols[i].id.startsWith("@"))
        {
          valsSet.add(req.cols[i].id);
        }
        else{
          reportButtonsSet.add(req.cols[i].id);
        }

      }


      String[] vals = valsSet.toArray(new String[valsSet.size()]);

      try
      {
        String dataType = req.dataType; //req.search.extraSearch[0].value;

        boolean hasFilters = req.filters != null && req.filters.length > 0;

        Long count = !hasFilters ?
            App.graph.indexQuery(dataType + ".MixedIdx", getIndexMetadataTypeStr(dataType)).vertexTotals() :
            //            App.g.V()
            //                 .has("Metadata.Type." + dataType, P.eq(dataType))
            //                 .range(req.from, req.to + req.to - req.from)
            //                 .count().toList()
            //                 .get(0) + req.from :
            App.graph.indexQuery(dataType + ".MixedIdx", getIndexSearchStr(req)).vertexTotals();

        GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person.Natural");

        if (count > 0)
        {

          if (hasFilters)
          {
            int        limit    = req.to.intValue() - req.from.intValue();
            List<Long> vertices = new ArrayList<>(limit);

            App.graph.indexQuery(dataType + ".MixedIdx", getIndexSearchStr(req))
                     .vertexStream().forEachOrdered(jgvr -> vertices.add(jgvr.getElement().longId()));

            resSet = App.g.V(vertices);

          }
          else
          {
            resSet = resSet.has("Metadata.Type." + dataType, eq(dataType));

          }

          if (StringUtils.isNotEmpty(req.customFilter))
          {
            if ("unmatchedEvents".equalsIgnoreCase(req.customFilter))
            {
              resSet = resSet.where(__.inE().count().is(eq(1)));
            }
            else if ("children".equalsIgnoreCase(req.customFilter))
            {
              long ageThresholdMs = (long) (System.currentTimeMillis() - (3600000L * 24L * 365L * 18L));
              Date dateThreshold  = new java.util.Date(ageThresholdMs);
              resSet = resSet.where(__.values("Person.Natural.Date_Of_Birth").is(gte(dateThreshold)));
            }
            else if (req.customFilter.startsWith("hasNeighbourId:"))
            {
              long neighbourId = Long.parseLong(req.customFilter.split(":")[1]);
              resSet = resSet.where(__.both().hasId(neighbourId));

            }
            GraphTraversal resSet2 = resSet.asAdmin().clone();
            count = (Long) resSet2.count().next();

          }

          if (StringUtils.isNotEmpty(req.sortCol))
          {
            resSet = resSet.order().by(req.sortCol, "+asc".equalsIgnoreCase(req.sortDir) ? Order.incr : Order.decr);
          }

          resSet = resSet.valueMap(true, vals)
                         .range(req.from, req.to);

          List<Map<String, Object>> res = resSet.toList();

          String[]     recs      = new String[res.size()];
          ObjectMapper objMapper = new ObjectMapper();

          for (int i = 0, ilen = res.size(); i < ilen; i++)
          {
            Map<String, Object> map = res.get(i);
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
          RecordReply reply = new RecordReply(req.from, req.to, count, recs);

          return reply;

        }

        RecordReply reply = new RecordReply(req.from, req.to, count, new String[0]);

        return reply;

      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }

    }

    return new RecordReply(req.from, req.to, 0L, new String[0]);

  }

  @POST @Path("graph") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
  public GraphReply graph(GraphRequest greq)
  {

    Set<Vertex> outNodes = App.g.V(Long.parseLong(greq.graphId)).to(Direction.OUT).toSet();
    Set<Vertex> inNodes  = App.g.V(Long.parseLong(greq.graphId)).to(Direction.IN).toSet();
    Vertex      v        = App.g.V(Long.parseLong(greq.graphId)).next();

    Set<Edge> outEdges = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.OUT).toSet();
    Set<Edge> inEdges  = App.g.V(Long.parseLong(greq.graphId)).toE(Direction.IN).toSet();

    GraphReply retVal = new GraphReply(v, inNodes, outNodes, inEdges, outEdges);

    return retVal;
  }

  Map<String, Pattern> compiledPatterns = new HashMap<>();

  @GET @Path("vertex_prop_values") @Produces(MediaType.APPLICATION_JSON)
  public FormioSelectResults getVertexPropertyValues(
      @QueryParam("search") String search
      , @QueryParam("limit") Long limit
      , @QueryParam("skip") Long skip

  )
  {
    //    final  String bizCtx = "BizCtx";
    //
    //    final AtomicBoolean matches = new AtomicBoolean(false);
    //
    //    keycloakSecurityContext.getAuthorizationContext().getPermissions().forEach(perm -> perm.getClaims().forEach(
    //        (s, strings) -> {
    //          if (bizCtx.equals(s)){
    //            strings.forEach( allowedVal -> {
    //              Pattern patt = compiledPatterns.computeIfAbsent(allowedVal, Pattern::compile);
    //              matches.set(patt.matcher(search).matches());
    //
    //            }  );
    //          }
    //        }));
    //
    //    if (matches.get()){

    if (limit == null)
    {
      limit = 100L;
    }

    if (skip == null)
    {
      skip = 0L;
    }

    List<Map<String, Object>> querRes = App
        .g.V()
          .has(search, neq(""))
          .limit(limit + skip)
          .skip(skip)
          .as("matches")
          .match(
              __.as("matches").values(search).as("val")
              , __.as("matches").id().as("id")
          )
          .select("id", "val")
          .toList();

    List<ReactSelectOptions> selectOptions = new ArrayList<>(querRes.size());

    for (Map<String, Object> res : querRes)
    {
      selectOptions.add(new ReactSelectOptions(res.get("val").toString(), res.get("id").toString()));
    }

    FormioSelectResults retVal = new FormioSelectResults(selectOptions);

    return retVal;

    //    }
    //
    //    return new FormioSelectResults();

  }

  @POST @Path("vertex_labels") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)

  public VertexLabelsReply vertexLabels(String str)
  {
    try
    {
      JanusGraphManagement mgt = App.graph.openManagement();

      VertexLabelsReply reply = new VertexLabelsReply(mgt.getVertexLabels());

      mgt.commit();
      return reply;
    }
    catch (Exception e)
    {

    }
    return new VertexLabelsReply();

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
        GraphTraversal resSet = App.g.V(); //.has("Metadata.Type", "Person.Natural");
        //        Boolean searchExact = req.search.getSearchExact();

        CountryDataReply data = new CountryDataReply();

        List<Map<String, Long>> res =
            StringUtils.isNotEmpty(searchStr) ?
                resSet.has("Person.Natural.FullName", textContainsFuzzy(searchStr)).values("Person.Natural.Nationality")
                      .groupCount()
                      .toList() :
                resSet.has("Person.Natural.Nationality").values("Person.Natural.Nationality").groupCount().toList();

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
        //        GraphTraversal g = App.g.V();
        //        for (int i = 0, ilen = req.labels.length; i < ilen; i++)
        //        {
        //          g = g.has("Metadata.Type", req.labels[i].value).range(0, 1);
        //
        //          //          labels[i] = (req.labels[i + 1].value);
        //
        //        }

        Set<String>     props = new HashSet<>();
        final String    label = req.labels[0].value;
        JanusGraphIndex idx   = App.graph.openManagement().getGraphIndex(label + ".MixedIdx");

        App.graph.openManagement().getRelationTypes(PropertyKey.class).forEach(
            propertyKey ->
            {
              if (propertyKey.isPropertyKey())
              {
                String currLabel = propertyKey.name();

                if (currLabel.startsWith(label))
                {
                  String labelPrefix = "#";
                  try
                  {
                    SchemaStatus status = idx.getIndexStatus(propertyKey);
                    if (!status.isStable())
                    {
                      labelPrefix = "";
                    }
                  }
                  catch (Throwable t)
                  {
                    labelPrefix = "";
                  }

                  props.add(labelPrefix + currLabel);
                }

              }
            }
        );

        List<Map<String, Object>> notificationTemplates = App.g.V()
           .has("Object.Notification_Templates.Types", eq(label))
           .valueMap("Object.Notification_Templates.Label" ,"Object.Notification_Templates.Text" )
           .toList();

        notificationTemplates.forEach(map -> {
          props.add("@"+map.get("Object.Notification_Templates.Label")+"@"+map.get("Object.Notification_Templates.Text"));

        });


        NodePropertyNamesReply reply = new NodePropertyNamesReply(props);
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
    Iterable<EdgeLabel> labels = App.graph.openManagement().getRelationTypes(EdgeLabel.class);

    Set<String> labelNames = new HashSet<>();

    labels.forEach(label -> labelNames.add(label.name()));

    EdgeLabelsReply reply = new EdgeLabelsReply(labelNames);

    return reply;
  }

  @GET @Path("param") @Produces(MediaType.TEXT_PLAIN) public String paramMethod(@QueryParam("name") String name,
                                                                                @HeaderParam("AUTHORIZATION") String auth)
  {
    return "Hello, " + name + " AUTHORIZATION" + auth;
  }

}