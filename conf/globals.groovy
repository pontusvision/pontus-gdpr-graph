import com.hubspot.jinjava.Jinjava
import groovy.json.JsonSlurper
import org.apache.commons.math3.util.Pair
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.Cardinality
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.RelationType
import org.janusgraph.core.schema.JanusGraphIndex
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.Mapping
import org.janusgraph.core.schema.RelationTypeIndex
import org.janusgraph.graphdb.types.vertices.JanusGraphSchemaVertex

import java.text.SimpleDateFormat

def globals = [:]
globals << [g: graph.traversal()]
globals << [mgmt: graph.openManagement()]

// Thanks to yudong.cai@homeoffice.gsi.gov.uk for the load Schema options.
def loadSchema(String... files) {
    StringBuilder sb = new StringBuilder()

    def mgmt = graph.openManagement();
    def propsMap = [:]
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

def addIndexes(def mgmt, def json, boolean isEdge, def propsMap, sb) {
    if (!json) {
        return
    }
    json.each {
        def propertyKeys = it.propertyKeys
        if (!propertyKeys) {
            return
        }

        def name = it.name
        def props = []
        propertyKeys.each { key ->
            def prop = propsMap[key]
            if (!prop) {
                throw new RuntimeException("Failed to create index - $name, because property - $key doesn't exist")
            }
            props << prop
        }

        def composite = it.composite
        def unique = it.unique
        def mixedIndex = it.mixedIndex
        def mapping = it.mapping

        if (mixedIndex && composite) {
            throw new RuntimeException("Failed to create index - $name, because it can't be both MixedIndex and CompositeIndex")
        }

        if (mapping && composite) {
            throw new RuntimeException("Failed to create index - $name, because it can't be CompositeIndex and have mapping")
        }

        if (mixedIndex) {
            if (!mapping) {
                createMixedIdx(mgmt, name, isEdge, props as PropertyKey[])
            } else {
                def map = [:]
                map[propsMap[props.get(0)]] = mapping
//                System.out.println("Line 93 - About to add index - $name $isEdge \n")
                createMixedIdx(mgmt, name, isEdge, map)
            }

        } else {
            createCompIdx(mgmt, name, isEdge, unique, props as PropertyKey[])
        }

        sb.append("Success added index - $name\n")
    }
}

def addVertexLabels(def mgmt, def json, def sb) {
    json['vertexLabels'].each {
        def name = it.name
        createVertexLabel(mgmt, name)
        sb.append("Success added vertext label - $name\n")
    }
}

def addEdgeLabels(def mgmt, def json, def sb) {
    json['edgeLabels'].each {
        def name = it.name
        createEdgeLabel(mgmt, name)
        sb.append("Success added edge label - $name\n")
    }
}

def addpropertyKeys(def mgmt, def json, def sb) {
    def map = [:]
    json['propertyKeys'].each {
        def name = it.name
        def typeClass = Class.forName(getClass(it.dataType))
        def cardinality = it.cardinality
        def card = cardinality == 'SET' ? Cardinality.SET : Cardinality.SINGLE
        def prop = createProp(mgmt, name, typeClass, card);
        sb.append("Success added property key - $name\n")
        map[name] = prop
    }
    return map
}

def getClass(def type) {
    return (type == 'Date') ? "java.util.Date" : "java.lang.$type"
}


def createProp(mgmt, keyName, classType, Cardinality card) {

    try {
        def key = null;
        if (!mgmt.containsPropertyKey(keyName)) {
            key = mgmt.makePropertyKey(keyName).dataType(classType).cardinality(card).make();
            Long id = ((JanusGraphSchemaVertex) key).id();

            System.out.println("keyName = ${keyName}, keyID = " + id)
        } else {
            key = mgmt.getPropertyKey(keyName);
            Long id = ((JanusGraphSchemaVertex) key).id();
            System.out.println("keyName = ${keyName}, keyID = " + id)

        }
        return key;
    }
    catch (Throwable t) {
        t.printStackTrace();
    }
}

def createCompIdx(def mgmt, String idxName, boolean isUnique, PropertyKey... props) {
    createCompIdx(mgmt, idxName, false, isUnique, props)
}

def createCompIdx(def mgmt, String idxName, boolean isEdge, boolean isUnique, PropertyKey... props) {

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
}


def createCompIdx(mgmt, idxName, boolean isEdge, PropertyKey... props) {
    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)
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

}

def createCompIdx(def mgmt, def idxName, PropertyKey... props) {
    createCompIdx(mgmt, idxName, false, props)
}

def createMixedIdx(def mgmt, String idxName, boolean isEdge, Pair<PropertyKey, Mapping>... props) {
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
}


def createMixedIdx(def mgmt, String idxName, Pair<PropertyKey, Mapping>... props) {
    createMixedIdx(mgmt, idxName, false, props)
}


def createMixedIdx(def mgmt, String idxName, Map<PropertyKey, String> props) {
    createMixedIdx(mgmt, idxName, false, props)
}

//(PropertyKey vs String representing a Mapping
def createMixedIdx(def mgmt, String idxName, boolean isEdge, Map<PropertyKey, String> props) {
    try {
        if (!mgmt.containsGraphIndex(idxName)) {
            def clazz = isEdge ? Edge.class : Vertex.class
            JanusGraphManagement.IndexBuilder ib = mgmt.buildIndex(idxName, clazz)

            props.each { prop, mappingStr ->

                Mapping mapping = Mapping.valueOf(mappingStr)
                if (mapping == null) {
                    mapping = Mapping.DEFAULT
                }

                ib.addKey(prop, mapping.asParameter());
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


def createMixedIdx(def mgmt, String idxName, PropertyKey... props) {
    createMixedIdx(mgmt, idxName, false, props)
}


def createMixedIdx(def mgmt, String idxName, boolean isEdge, PropertyKey... props) {
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


def createVertexLabel(mgmt, String labelName) {

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

def createEdgeLabel(mgmt, labelName) {

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

def getPropsNonMetadataAsHTMLTableRows(g, Long vid, String origLabel) {
    StringBuilder sb = new StringBuilder();


    g.V(vid).valueMap().next().forEach { origKey, origVal ->
        String val = origVal.get(0)
        String key = origKey.replaceAll('[_.]', ' ')
        if (!key.startsWith('Metadata')) {
            sb.append("<tr><td class='tg-yw4l'>");
            if (origKey.startsWith(origLabel)) {
                sb.append(key.substring(origLabel.length() + 1))
            } else {
                sb.append(key);
            }
            sb.append("</td><td class='tg-yw4l'>");
            sb.append(val).append("</td></tr>");
        }
    }


    return sb.toString().replaceAll('["]', '\\\\"');
}

def describeSchema() {
    StringBuilder sb = new StringBuilder()
    def schema = new Schema(graph, sb)
    schema.describeAll()
    sb.toString()
}

class Schema {
    def graph
    StringBuilder sb

    public Schema() {}

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

    public Object getRelation(Object type, Object args) {
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

    public void printEdge(Object args) {
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

    public void printPropertyKey(Object args) {
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

    public def getManagement() {
        return openManagement()
    }

    public def openManagement() {
        graph.tx().rollback()
        return graph.openManagement()
    }

    public Object getGraph() {
        return this.graph
    }
}


def class Convert<T> {
    private from
    private to


    public List<String> dateFormats = [
            "d/m/y",
            "d M y",
            "d-m-y",
            "M",
            "y",
            "yyyy.MM.dd G 'at' HH:mm:ss z", //	2001.07.04 AD at 12:08:56 PDT
            "EEE, MMM d, ''yy", //	Wed, Jul 4, '01
            "h:mm a", //	12:08 PM
            "hh 'o''clock' a, zzzz", //12 o'clock PM, Pacific Daylight Time
            "K:mm a, z", //0:08 PM, PDT
            "yyyyy.MMMMM.dd GGG hh:mm aaa", //	02001.July.04 AD 12:08 PM
            "EEE, d MMM yyyy HH:mm:ss Z", //	Wed, 4 Jul 2001 12:08:56 -0700
            "yyMMddHHmmssZ", //	010704120856-0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"  // 2001-07-04T12:08:56.235-0700

    ];

    private List<SimpleDateFormat> dateFormatters = [];


    public Convert(clazz) { from = clazz }

    static def from(clazz) {
        new Convert(clazz)
    }

    List<String> getDateFormats() {
        return dateFormats
    }

    void setDateFormats(List<String> dateFormats) {
        this.dateFormats = dateFormats
        dateFormatters.clear();
        dateFormats.each {
            dateFormatters.add(new SimpleDateFormat(it));
        }

    }

    def to(clazz) {
        to = clazz
        return this
    }

    def using(closure) {
        def originalAsType = from.metaClass.getMetaMethod('asType', [] as Class[])
        from.metaClass.asType = { Class clazz ->
            if (clazz == to) {
                closure.setProperty('value', delegate)
                closure(delegate)
            } else {
                originalAsType.doMethodInvoke(delegate, clazz)
            }
        }
    }

    static T fromString(String data, Class<T> requiredType) {

        if (requiredType == Date.class) {

            int ilen = dateFormatters.size();
            for (int i = 0; i < ilen; i++) {
                try {
                    Date retVal = dateFormatters.get(i).parse(data);
                    return retVal as T;

                } catch (Throwable t) {
                    // ignore
                }
            }

        } else if (requiredType == String.class) {
            return data as T
        } else if (requiredType == Boolean.class) {
            return Boolean.valueOf(data) as T
        } else if (requiredType == Integer.class) {
            return Integer.valueOf(data) as T
        } else if (requiredType == Long.class) {
            return Long.valueOf(data) as T
        } else if (requiredType == Float.class) {
            return Float.valueOf(data) as T
        } else if (requiredType == Double.class) {
            return Double.valueOf(data) as T
        } else if (requiredType == Short.class) {
            return Short.valueOf(data) as T
        } else {
            return data as T
        }


    }
}


class MatchReq<T> {

    private List<T> attribNativeVals;
    private List<String> attribVals;
    private Class attribType;
    private String propName;
    private String vertexName;
    private String predicate;
    private Convert<T> conv;


    MatchReq(List<String> attribVals, Class<T> attribType, String propName, String vertexName, List<String> dateFormats) {
        this.attribVals = attribVals
        this.attribType = attribType
        this.propName = propName
        this.vertexName = vertexName
        this.conv = new Convert<>(attribType)

        if (dateFormats != null) {
            this.dateFormats = dateFormats;
        }
        convertListToNativeFormat();
    }

    protected void convertListToNativeFormat() {


//        Convert.fromString("asdf", this.attribType);


        attribVals.each {
            attribNativeVals.add(conv.fromString(it, this.attribType))
        }
    }

    List<String> getAttribVals() {
        return attribVals
    }

    void setAttribVals(List<String> attribVals) {
        this.attribVals = attribVals
    }

    Class getAttribType() {
        return attribType
    }

    void setAttribType(Class attribType) {
        this.attribType = attribType
    }

    String getPropName() {
        return propName
    }

    void setPropName(String propName) {
        this.propName = propName
    }

    String getVertexName() {
        return vertexName
    }

    void setVertexName(String vertexName) {
        this.vertexName = vertexName
    }
}

def renderReportInBase64(long pg_id, String pg_templateTextInBase64) {

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

    Jinjava jinJava = new Jinjava();
    return jinJava.render(new String(pg_templateTextInBase64.decodeBase64()), allData).bytes.encodeBase64().toString();

}

def matchPerson(List<MatchReq> matchReqs) {

    HashMap<String, List<MatchReq>> matchReqByVertexName = new HashMap<>();
    matchReqs.each {
        List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() });
        matchReqList.add(it)
    }

    matchReqByVertexName.each {
        g = g.V().has("Metadata.Type." + it.value.vertexName, eq(it.value.vertexName));
        it.value.attribNativeVals.each { it2 ->
            g = g.V().has(it.value.propName, it2)

        }


    }


}


def matchPerson(String jsonData) {


    def jsonSlurper = new groovy.json.JsonSlurper();


    def object = jsonSlurper.parseText(jsonData);

    if (object.reqs instanceof List) {
        List<MatchReq> matchReqs = new ArrayList<>(object.reqs.size());

        object.reqs.each {

            List<String> attribList = null;
            if (it.attribVals instanceof List) {
                attribList = it.attribVals;
            } else if (it.attribVals instanceof String) {
                def attrList = jsonSlurper.parseText((String) it.attribVals);

                if (attrList instanceof List) {
                    attribList = attrList;
                }

            } else {
                throw new Exception("Failed to read Attribute List");
            }

            Class nativeType;
            if (it.nativeType == null) {
                nativeType = String.class;
            } else {
                nativeType = Class.forName((String) it.nativeType);
            }


            MatchReq mreq = new MatchReq(attribList, nativeType, (String) it.propName, (String) it.vertexName);

            matchReqs.add(mreq);

        }
    }


}

def matchEdges() {

    def ids = [4096, 8192, 16384, 20480] as Long[];
    def otherIds = [4184, 24576, 16496, 4256] as Long[];


    g.V(ids)
            .both()
            .hasId(otherIds).id()
}
//
//{
//    reqs: [/* each of these objs will be AND'd together */
//            { /* each of these vals will be OR'd together */
//                attribVals: ["asdf","Leo","Leo Martins"]
//                ,attribType: "String"
//                ,propName: "Person.Full_Name"
//                ,vertexName: "Person"
//                ,predicate: "textContainsFuzzy"
//
//            }
//            , { /* each of these vals will be OR'd together */
//                attribVals: ["asdf","Leo","Leo Martins"]
//                ,attribType: "String"
//                ,propName: "Person.Full_Name"
//                ,vertexName: "Person"
//                ,predicate: "textContainsFuzzy"
//
//            }
//    ]
//}

