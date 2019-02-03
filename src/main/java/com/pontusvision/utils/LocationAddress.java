package com.pontusvision.utils;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import com.pontusvision.jpostal.AddressExpander;
import com.pontusvision.jpostal.AddressParser;
import com.pontusvision.jpostal.ParsedComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocationAddress {
  static final String ADDRESS_PARSER_DIR = "/opt/pontus/pontus-graph/current/datadir/libpostal";
  static final String ADDRESS_PARSER_DIR_OPT = "pg.jpostal.datadir";


  static AddressParser parser = AddressParser.getInstanceDataDir(System.getProperty(ADDRESS_PARSER_DIR_OPT,ADDRESS_PARSER_DIR ));
  static AddressExpander expander = AddressExpander.getInstanceDataDir(System.getProperty(ADDRESS_PARSER_DIR_OPT,ADDRESS_PARSER_DIR ));

  private Map<String, Set<String>> tokens = new HashMap<>();

  private LocationAddress() {
  }


  public static LocationAddress fromString(String strVal) {

    LocationAddress retVal = new LocationAddress();


    ParsedComponent[] res = parser.parseAddress(strVal);

    for (int i = 0,ilen = res.length; i < ilen; i++){
      ParsedComponent it = res[i];
      String label = it.getLabel();
      String value = it.getValue();

      Set<String> vals = retVal.tokens.computeIfAbsent(label,  k -> new HashSet<>() );

      // sb?.append("\n$label = $value")

      vals.add(value);

      String[] expansions = expander.expandAddress(value);

      for (int j = 0, jlen = expansions.length; j < jlen; j++){


        if (! "postcode".equals(label)) {

          vals.add(PostCode.format(expansions[j]));

        } else {

          vals.add(expansions[j]);


        }

      }

    }
    return retVal;

  }


  public GraphTraversal addPropsToGraphTraverser(GraphTraversal g, String propPrefix) {
    Set<Map.Entry<String, Set<String>>> tokensEntrySet = tokens.entrySet();

    GraphTraversal retVal = g;
    for (Map.Entry<String, Set<String>> tokenEntry: tokensEntrySet)
    {
      StringBuffer propName = new StringBuffer(propPrefix).append(tokenEntry.getKey());
      for (String val:tokenEntry.getValue())
      {
        retVal = retVal.property(propName, val);

      }
    }

    return retVal;
  }


  public  Map<String, Set<String>> getTokens() {
    return tokens;//.asImmutable()
  }
  public Set<String> getVals(String key) {
    return tokens.get(key);
  }

  public String toString() {
    return tokens.toString();
  }

}
