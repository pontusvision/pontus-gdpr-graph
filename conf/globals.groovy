import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.apache.commons.math3.util.Pair
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.RelationType
import org.janusgraph.core.schema.*
import org.janusgraph.graphdb.types.vertices.JanusGraphSchemaVertex

LinkedHashMap globals = [:]
globals << [g: ((JanusGraph) graph).traversal() as GraphTraversalSource]
globals << [mgmt: ((JanusGraph) graph).openManagement() as JanusGraphManagement]

@CompileStatic
def loadSchema(JanusGraph graph, String... files) {
    StringBuffer sb = new StringBuffer()

    JanusGraphManagement mgmt = graph.openManagement();
    Map<String, PropertyKey> propsMap = [:]
    for (f in files) {
        try {

            def jsonFile = new File(f);

            if (jsonFile.exists()) {
                def jsonStr = jsonFile.text
                def json = new JsonSlurper().parseText(jsonStr)
                addVertexLabels(mgmt, json, sb)
                addEdgeLabels(mgmt, json, sb)
                propsMap << addpropertyKeys(mgmt, json, sb)
                addIndexes(mgmt, json['vertexIndexes'], false, propsMap, sb)
                addIndexes(mgmt, json['edgeIndexes'], true, propsMap, sb)
                sb.append("Loading File ${f}\n")

            } else {
                sb.append("NOT LOADING FILE ${f}\n")
            }

        } catch (Throwable t) {
            sb.append('Failed to load schema!\n').append(t);

        }
    }
    mgmt.commit()

    sb.append('Done!\n')
    return sb.toString()
}

def addIndexes(JanusGraphManagement mgmt, def json, boolean isEdge, Map<String, PropertyKey> propsMap, StringBuffer sb = null) {
    if (!json) {
        return
    }
    json.each {
        List<String> propertyKeys = it.propertyKeys
        if (!propertyKeys) {
            return
        }

        String name = it.name
        PropertyKey props = []
        propertyKeys.each { key ->
            def prop = propsMap[key]
            if (!prop) {
                throw new RuntimeException("Failed to create index - $name, because property - $key doesn't exist")
            }
            props << prop
        }

        def composite = it.composite
        boolean unique = it.unique
        def mixedIndex = it.mixedIndex
        String mapping = it.mapping

        if (mixedIndex && composite) {
            throw new RuntimeException("Failed to create index - $name, because it can't be both MixedIndex and CompositeIndex")
        }

        if (mapping && composite) {
            throw new RuntimeException("Failed to create index - $name, because it can't be CompositeIndex and have mapping")
        }

        def idxCreated
        if (mixedIndex) {
            idxCreated = createMixedIdx(mgmt, name, isEdge, propertyKeys, mapping, (Map<String, Object>) it.propertyKeysMappings)
        } else {
            idxCreated = createCompIdx(mgmt, name, isEdge, unique, props as PropertyKey[])
        }

        def status = idxCreated ? 'Success added' : 'Failed to add'
        sb?.append("$status index - $name\n")
    }
}

def addVertexLabels(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
    json['vertexLabels'].each {
        String name = it.name
        createVertexLabel(mgmt, name)
        sb?.append("Success added vertext label - $name\n")
    }
}

def addEdgeLabels(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
    json['edgeLabels'].each {
        def name = it.name
        createEdgeLabel(mgmt, name)
        sb?.append("Success added edge label - $name\n")
    }
}

Map<String, PropertyKey> addpropertyKeys(JanusGraphManagement mgmt, def json, StringBuffer sb = null) {
    Map<String, PropertyKey> map = [:]
    json['propertyKeys'].each {
        String name = it.name
        Class<?> typeClass = Class.forName(getClass(it.dataType))
        String cardinality = it.cardinality
        org.janusgraph.core.Cardinality card = cardinality == 'SET' ? org.janusgraph.core.Cardinality.SET : org.janusgraph.core.Cardinality.SINGLE
        def prop = createProp(mgmt, name, typeClass, card);
        sb?.append("Success added property key - $name\n")
        map[name] = prop
    }
    return map
}

String getClass(def type) {
    return (type == 'Date') ? "java.util.Date" : "java.lang.$type"
}


@CompileStatic
PropertyKey createProp(JanusGraphManagement mgmt, String keyName, Class<?> classType, org.janusgraph.core.Cardinality card) {

    try {
        PropertyKey key;
        if (!mgmt.containsPropertyKey(keyName)) {
            key = mgmt.makePropertyKey(keyName).dataType(classType).cardinality(card).make();
            Long id = ((JanusGraphSchemaVertex) key).id() as Long;

            System.out.println("keyName = ${keyName}, keyID = " + id)
        } else {
            key = mgmt.getPropertyKey(keyName);
            Long id = ((JanusGraphSchemaVertex) key).id() as Long;
            System.out.println("keyName = ${keyName}, keyID = " + id)

        }
        return key;
    }
    catch (Throwable t) {
        t.printStackTrace();
    }
    return null
}

@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, boolean isUnique, PropertyKey... props) {
    return createCompIdx(mgmt, idxName, false, isUnique, props)
}

@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, boolean isUnique, PropertyKey... props) {

    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
            if (isUnique) {
                ib.unique()
            }
            for (PropertyKey prop in props) {
                ib.addKey(prop);
//            ib.addKey(prop,Mapping.STRING.asParameter());
                System.out.println("creating Comp IDX ${idxName} for key ${prop}");

            }

            return ib.buildCompositeIndex();
        } else {
            return mgmt.getGraphIndex(idxName);
        }
    }
    catch (Throwable t) {
        t.printStackTrace();
    }
    return null
}

//JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, PropertyKey... props) {
//    try {
//        if (!mgmt.containsGraphIndex(idxName)) {
//            def clazz = isEdge ? Edge.class : Vertex.class
//            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
//            for (PropertyKey prop in props) {
//                ib.addKey(prop);
////            ib.addKey(prop,Mapping.STRING.asParameter());
//                System.out.println("creating Comp IDX ${idxName} for key ${prop}");
//
//            }
//
//            return ib.buildCompositeIndex();
//        } else {
//            return mgmt.getGraphIndex(idxName);
//        }
//    }
//    catch (Throwable t) {
//        t.printStackTrace();
//    }
//    return null
//
//}

@CompileStatic
JanusGraphIndex createCompIdx(JanusGraphManagement mgmt, String idxName, PropertyKey... props) {
    return createCompIdx(mgmt, idxName, false, props)
}

@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, Pair<PropertyKey, Mapping>... pairs) {
    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)

            for (Pair<PropertyKey, Mapping> pair in pairs) {

                PropertyKey prop = pair.getFirst();
                Mapping mapping = pair.getSecond();
                ib.addKey(prop, mapping.asParameter());
//            ib.addKey(prop,Mapping.STRING.asParameter());
                System.out.println("creating IDX ${idxName} for key ${prop}");

            }
            return ib.buildMixedIndex("search");
        } else {
            return mgmt.getGraphIndex(idxName);
        }
    }
    catch (Throwable t) {
        t.printStackTrace();
    }
    return null
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, Pair<PropertyKey, Mapping>... props) {
    return createMixedIdx(mgmt, idxName, false, props)
}

/*
    {
      "name": "by_O.vehicle_MixedIdx",
      "propertyKeys": [
        "O.vehicle.meta.m_create",
        "O.vehicle.meta.m_subtype",
        "O.vehicle.meta.m_owner",
        "O.vehicle.core.registrationNumber",
        "O.vehicle.meta.m_update",
        "O.vehicle.meta.m_source",
        "O.vehicle.core.vehicleType",
        "O.vehicle.meta.m_version",
        "O.vehicle.core.registrationCountry",
        "O.vehicle.meta.m_createBy",
        "O.vehicle.meta.m_use",
        "O.vehicle.core.objectType",
        "O.vehicle.meta.m_type",
        "O.vehicle.meta.m_correlationID",
        "O.vehicle.meta.m_identityId"
      ],
      "propertyKeysMappings": {
        "O.vehicle.core.registrationNumber": {
          "mapping": "TEXT"
        }
      },
      "composite": false,
      "unique": false,
      "indexOnly": null,
      "mixedIndex": "search",
      "mapping": null
    },

 */
//(PropertyKey vs String representing a Mapping
@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, List<String> props, String mappingStr, Map<String, Object> propertyKeysMappings) {
    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)

            props.each { property ->
                PropertyKey propKey = mgmt.getPropertyKey(property)
                if (!propKey) {
                    throw new RuntimeException("$property not found")
                }


                def keyMapping = propertyKeysMappings ? propertyKeysMappings[property] : null
                if (keyMapping) {
                    String mappingVal = keyMapping['mapping']
                    Parameter mappingParam = Mapping.valueOf(mappingVal)?.asParameter()
                    if (!mappingParam) {
                        throw new RuntimeException("Cannot get mapping from $mappingVal for $property")
                    }

                    Map<String, String> analyzer = (Map<String, String>) keyMapping['analyzer']
                    if (analyzer) {
                        ib.addKey(propKey, mappingParam, Parameter.of(analyzer['name'], analyzer['value']))
                    } else {
                        ib.addKey(propKey, mappingParam)
                    }
                } else if (mappingStr) {
                    Mapping mapping = Mapping.valueOf(mappingStr)
                    if (!mapping) {
                        mapping = Mapping.DEFAULT
                    }
                    ib.addKey(propKey, mapping.asParameter());
                } else {
                    //mappingString not specified, depends on the property's type
                    if (propKey.dataType() == String.class) {
                        ib.addKey(propKey, Mapping.STRING.asParameter());
                    } else {
                        ib.addKey(propKey);
                    }
                }
            }
            System.out.println("creating IDX ${idxName} for key(s) - ${props}")
            return ib.buildMixedIndex("search")
        } else {
            return mgmt.getGraphIndex(idxName)
        }
    }
    catch (Throwable t) {
        t.printStackTrace();
    }

    return null
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, PropertyKey... props) {
    return createMixedIdx(mgmt, idxName, false, props)
}


@CompileStatic
JanusGraphIndex createMixedIdx(JanusGraphManagement mgmt, String idxName, boolean isEdge, PropertyKey... props) {
    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
            for (PropertyKey prop in props) {

                if (prop.dataType() == String.class) {
                    ib.addKey(prop, Mapping.STRING.asParameter());

                } else {
                    ib.addKey(prop);

                }

//                ib.addKey(prop,Mapping.TEXTSTRING.asParameter());
                System.out.println("creating IDX ${idxName} for key ${prop}");

            }
            return ib.buildMixedIndex("search");
        } else {
            return mgmt.getGraphIndex(idxName);

        }
    }
    catch (Throwable t) {
        t.printStackTrace();
    }


}


PropertyKey createVertexLabel(JanusGraphManagement mgmt, String labelName) {

    try {
        if (!mgmt.containsVertexLabel(labelName)) {
            mgmt.makeVertexLabel(labelName).make()
        }
        return createProp(mgmt, "Metadata.Type." + labelName, String.class, org.janusgraph.core.Cardinality.SINGLE);

    }
    catch (Throwable t) {
        t.printStackTrace();
    }
    return null;
}

@CompileStatic
def createEdgeLabel(JanusGraphManagement mgmt, String labelName) {

    try {
        if (!mgmt.containsEdgeLabel(labelName)) {
            return mgmt.makeEdgeLabel(labelName).make()
        }
        return mgmt.getEdgeLabel(labelName)
    }
    catch (Throwable t) {
        t.printStackTrace();
    }
}

boolean isASCII(String s) {
    for (int i = 0; i < s.length(); i++)
        if (s.charAt(i) > 127)
            return false;
    return true;
}


def getPropsNonMetadataAsHTMLTableRows(GraphTraversalSource g, Long vid, String origLabel) {
    StringBuilder sb = new StringBuilder();


    g.V(vid).valueMap().next().forEach { origKey, origVal ->
        String val = origVal.get(0)
        String key = origKey.replaceAll('[_.]', ' ')
        if (!key.startsWith('Metadata')) {
            sb.append("<tr><td class='tg-yw4l'>");
            if (key.endsWith("b64")) {
                val = new String(val.decodeBase64())
                key += ' (Decoded)'
            }
            val = val.replaceAll('\\\\"', '"')
            val = val.replaceAll('\"', '"')

            val = org.apache.commons.lang.StringEscapeUtils.escapeHtml(val);
            val = val.replaceAll("(\\r\\n|\\n)", "<br />");

            val = val.replaceAll('\\\\', '');

            if (!isASCII(val)) {
                val = val.replaceAll("\\p{C}", "?");
            }
            
            if (origKey.startsWith(origLabel)) {
                sb.append(key.substring(origLabel.length() + 1))
            } else {
                sb.append(key);
            }
            sb.append("</td><td class='tg-yw4l'>") //.append("<![CDATA[");
                    .append(val)
//                    .append("]]>")
                    .append("</td></tr>");
        }
    }


    return sb.toString().replaceAll('["]', '\\\\"').bytes.encodeBase64();
}


@CompileStatic
def describeSchema(JanusGraph graph, StringBuffer sb = new StringBuffer()) {
    def schema = new Schema(graph, sb)
    schema.describeAll()
    sb.toString()
}

def describeSchema(StringBuffer sb = new StringBuffer()) {
    def schema = new Schema(globals['graph'], sb)
    schema.describeAll()
    sb.toString()
}


@CompileStatic
class Schema {
    JanusGraph graph;
    StringBuffer sb;

    public Schema() {}

    @CompileDynamic
    public Schema(def graph, def sb) {
        this.graph = graph
        this.sb = sb
    }

    public void describeAll() {
        try {
            ensureMgmtOpen()
            printVertex(getVertex([]))
            printEdge(getEdge([]))
            printPropertyKey(getPropertyKey([]))
            printIndex(getIndex([]))
        } finally {
            ensureMgmtClosed()
        }
    }

    @CompileDynamic
    public Object getVertex(Object args) {
        if (args) {
            def result = getManagement().getVertexLabels().findAll {
                args.contains(it.name())
            }
            result.toSorted { a, b -> a.name() <=> b.name() }
            return result
        } else {
            def result = getManagement().getVertexLabels()
            return result.toSorted { a, b -> a.name() <=> b.name() }
        }
    }

    @CompileDynamic
    public void printVertex(Object args) {
        def pattern = "%-30s  | %11s | %6s\n"
        this.sb.append(String.format(pattern, '\nVertex Label', 'Partitioned', 'Static'))
        this.sb.append(String.format(pattern, '------------', '-----------', '------'))
        args.each {
            this.sb.append(String.format(pattern, it.name().take(30), it.isPartitioned(), it.isStatic()))
        }
        this.sb.append(String.format(pattern, '------------', '-----------', '------'))
        this.sb.append('\n')
    }

    @CompileDynamic
    public Object getIndex(Object args) {
        def result = []
        result.addAll(getManagement().getGraphIndexes(Vertex.class))
        result.addAll(getManagement().getGraphIndexes(Edge.class))
        result.addAll(getManagement().getGraphIndexes(PropertyKey.class))
        result.addAll(getRelation(RelationType.class, []).collect {
            getManagement().getRelationIndexes(it).flatten()
        }.flatten())
        if (args) {
            return result.findAll {
                args.contains(it.name())
            }
        }
        result = result.toSorted { a, b -> a.name() <=> b.name() }
        return result
    }

    @CompileDynamic
    public void printIndex(Object args) {
        def pattern = "%-50s | %9s | %16s | %6s | %15s | %40s | %20s\n"
        this.sb.append(String.format(pattern, 'Graph Index', 'Type', 'Element', 'Unique', 'Backing/Mapping', 'PropertyKey [dataType]', 'Status'))
        this.sb.append(String.format(pattern, '-----------', '----', '-------', '------', '-------', '-----------', '------'))
        args.findAll { it instanceof JanusGraphIndex }.each {
            def idxType = "Unknown"
            if (it.isCompositeIndex()) {
                idxType = "Composite"
            } else if (it.isMixedIndex()) {
                idxType = "Mixed"
            }
            def element = it.getIndexedElement().simpleName.take(16)
            this.sb.append(String.format(pattern, it.name().take(50), idxType, element, it.isUnique(), it.getBackingIndex().take(13), "", ""))
            it.getFieldKeys().each { fk ->
                def mapping = it.getParametersFor(fk).findAll { it.key == 'mapping' }[0]
                this.sb.append(String.format(pattern, "", "", "", "", mapping ? mapping : '', fk.name().take(40) + " [" + fk.dataType().getSimpleName() + "]", it.getIndexStatus(fk).name().take(20)))
            }
        }
        this.sb.append(String.format(pattern, '----------', '----', '-------', '------', '-------', '-----------', '------'))
        this.sb.append('\n')

        pattern = "%-50s | %20s | %10s | %10s | %10s | %20s\n"
        this.sb.append(String.format(pattern, 'Relation Index', 'Type', 'Direction', 'Sort Key', 'Sort Order', 'Status'))
        this.sb.append(String.format(pattern, '--------------', '----', '---------', '----------', '--------', '------'))
        args.findAll { it instanceof RelationTypeIndex }.each {
            def keys = it.getSortKey()
            this.sb.append(String.format(pattern, it.name().take(50), it.getType(), it.getDirection(), keys[0], it.getSortOrder(), it.getIndexStatus().name().take(20)))
            keys.tail().each { k ->
                this.sb.append(String.format(pattern, "", "", "", k, "", ""))
            }
        }
        this.sb.append(String.format(pattern, '--------------', '----', '---------', '----------', '--------', '------'))
        this.sb.append('\n')
    }

    public Object getPropertyKey(Object args) {
        return getRelation(PropertyKey.class, args)
    }

    public Object getEdge(Object args) {
        return getRelation(EdgeLabel.class, args)
    }

    @CompileDynamic
    Object getRelation(Object type, Object args) {
        def result = []
        result.addAll(getManagement().getRelationTypes(type))

        if (args) {
            return result.findAll {
                args.contains(it.name())
            }
        }
        result = result.toSorted { a, b -> a.name() <=> b.name() }
        return result
    }

    @CompileDynamic
    void printEdge(Object args) {
        def pattern = "%-50s | %15s | %15s | %15s | %15s\n"
        this.sb.append(String.format(pattern, 'Edge Name', 'Type', 'Directed', 'Unidirected', 'Multiplicity'))
        this.sb.append(String.format(pattern, '---------', '----', '--------', '-----------', '------------'))
        args.each {
            def relType = "Unknown"
            if (it.isEdgeLabel()) {
                relType = 'Edge'
            } else if (it.isPropertyKey()) {
                relType = 'PropertyKey'
            }

            this.sb.append(String.format(pattern, it.name().take(50), relType, it.isDirected(), it.isUnidirected(), it.multiplicity()))
        }
        this.sb.append(String.format(pattern, '---------', '----', '--------', '-----------', '------------'))
        this.sb.append('\n')
    }

    @CompileDynamic
    void printPropertyKey(Object args) {
        def pattern = "%-50s | %15s | %15s | %20s\n"
        this.sb.append(String.format(pattern, 'PropertyKey Name', 'Type', 'Cardinality', 'Data Type'))
        this.sb.append(String.format(pattern, '----------------', '----', '-----------', '---------'))
        args.each {
            def relType = "Unknown"
            if (it.isEdgeLabel()) {
                relType = 'Edge'
            } else if (it.isPropertyKey()) {
                relType = 'PropertyKey'
            }

            this.sb.append(String.format(pattern, it.name().take(50), relType, it.cardinality(), it.dataType()))
        }
        this.sb.append(String.format(pattern, '----------------', '----', '-----------', '---------'))
        this.sb.append('\n')
    }

    private void ensureMgmtOpen() {
        def mgmt = getManagement()
        if (!mgmt.isOpen()) {
            openManagement()
        }
    }

    private void ensureMgmtClosed() {
        def mgmt = getManagement()
        if (mgmt.isOpen()) {
            mgmt.rollback()
        }
    }

    JanusGraphManagement getManagement() {
        return openManagement()
    }

    JanusGraphManagement openManagement() {
        graph.tx().rollback()
        return graph.openManagement()
    }

    JanusGraph getGraph() {
        return this.graph
    }
}


def renderReportInBase64(long pg_id, String pg_templateTextInBase64, GraphTraversalSource g = g) {

    def context = g.V(pg_id).valueMap()[0].collectEntries { key, val ->
        [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
    };

    def neighbours = g.V(pg_id).both().valueMap().toList().collect { item ->
        item.collectEntries { key, val ->
            [key.replaceAll('[.]', '_'), val.toString() - '[' - ']']
        }
    };

    def allData = new HashMap<>();

    allData.put('context', context);
    allData.put('connected_data', neighbours);

    com.hubspot.jinjava.Jinjava jinJava = new com.hubspot.jinjava.Jinjava();
    return jinJava.render(new String(pg_templateTextInBase64.decodeBase64()), allData).bytes.encodeBase64().toString();

}

