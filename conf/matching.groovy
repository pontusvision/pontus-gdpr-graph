import com.joestelmach.natty.DateGroup
import com.joestelmach.natty.Parser
import com.pontusvision.jpostal.AddressExpander
import com.pontusvision.jpostal.AddressParser
import com.pontusvision.jpostal.ParsedComponent
import com.pontusvision.utils.PostCode
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.concurrent.ConcurrentHashMap

// String.mixin(DateConvMixin2)


def benchmark = { closure ->
    start = System.nanoTime()
    closure.call()
    now = System.nanoTime()
    now - start
}
/// NOTE : look at groovy subsequences, then iterate, and check which ones are unique().size() == .size()

//def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["01/03/1933","11/03/1933","01/03/2011","01/01/1666"],"attribType":"java.util.Date","propName":"Person.Date_Of_Birth","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'
//def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["01/03/1933","11/03/1933","01/03/2011","01/01/1666"],"attribType":"java.util.Date","propName":"Person.Date_Of_Birth","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'
// def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'

//StringBuffer sb = new StringBuffer();
// String.mixin(PVConvMixin)

// try {
//matchPerson(jsonData,  sb ,  g , "Person")
// } catch (Throwable t){
//   sb?.append("\n Failed to match Person; err: $t")
// }
// g.V().has('Metadata.Type.Person',eq('Person'))
//     .has('Person.Last_Name',eq('owens'))
// .has('Person.Date_Of_Birth',eq (new Date("Tue Mar 01 00:00:00 UTC 2011")))
// g.V().has('Metadata.Type.Person',eq('Person'))
// .has('Person.Full_Name',eq('chris owens'))
// def test = [41005304, 122900712, 204845216, 122921056, 122896536, 81928200, 122884248, 81969160, 81960968, 122958056, 81944664, 40964344, 204869792, 41025784, 163868912, 122892360, 122925128, 122904672, 41005304, 40980728, 204820640, 81965144, 204857504, 41033976, 204882080, 81973256, 122892520, 81948760, 122888288, 122937496, 204853408, 122916960, 41005304, 41005304, 41005304, 41005304, 41005304]
// Map counts = test.countBy{ it }

// sb?.append("\n\ncounts = $counts")
//sb?.toString()


class LocationAddress {
    static final String ADDRESS_PARSER_DIR = '/opt/pontus/pontus-graph/current/datadir/libpostal';


    static AddressParser parser = AddressParser.getInstanceDataDir(ADDRESS_PARSER_DIR)
    static AddressExpander expander = AddressExpander.getInstanceDataDir(ADDRESS_PARSER_DIR)

    private Map<String, Set<String>> tokens = new HashMap<>();

    private LocationAddress(StringBuffer sb = null) {
        sb?.append("\nin LocationAddress constructor")
    }


    public static LocationAddress fromString(String strVal, StringBuffer sb = null) {

        sb?.append("\n\n\n\n #########################\n$strVal\n")

        LocationAddress retVal = new LocationAddress(sb)

        sb?.append("\nin LocationAddress fromString() - before parsing addr")

        ParsedComponent[] res = parser.parseAddress(strVal)
        sb?.append("\nin LocationAddress fromString() - after parsing addr; res= $res")

        res.each {
            String label = it.getLabel();
            String value = it.getValue();

            // sb?.append("\n$label = $value")

            String[] expansions = expander.expandAddress(value)

            expansions.each {
                Set<String> vals = retVal.tokens.computeIfAbsent(label, { k -> new HashSet<>() })

                if ("postcode".equals(label)) {

                    vals.add(PostCode.format(it))

                } else {
                    sb?.append("\n")?.append(label)?.append(":")?.append(it);

                    vals.add(it)


                }

            }

        }
        return retVal

    }


    def addPropsToGraphTraverser(g, String propPrefix, StringBuffer sb = null) {
        tokens.each { k, vals ->
            vals.each { val ->
                sb?.append("\nadding ${propPrefix}${k} = ${val}")
                g = g.property("${propPrefix}${k}" as String, val as String)
            }
        }
        return g
    }


    Map<String, Set<String>> getTokens() {
        return tokens;//.asImmutable()
    }

    Set<String> getVals(String key) {
        return tokens.get(key);
    }

    String toString() {
        return tokens.toString()
    }

}


class Convert<T> {
    private from
    private to


    public Convert(clazz) {
        from = clazz


    }

    static def from(clazz) {
        new Convert(clazz)
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


    T fromString(String data, Class<T> requiredType, StringBuffer sb = null) {

        if (requiredType == Date.class) {
            return data as Date

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

class PVConvMixin {

//    static List<String> dateFormats = [
//            "dd/MM/yyyy",
//            "dd/MM/yy",
//            "d M y",
//            "d-m-y",
//            "yyyy.MM.dd G 'at' HH:mm:ss z", //	2001.07.04 AD at 12:08:56 PDT
//            "EEE, MMM d, ''yy", //	Wed, Jul 4, '01
//            "h:mm a", //	12:08 PM
//            "hh 'o''clock' a, zzzz", //12 o'clock PM, Pacific Daylight Time
//            "K:mm a, z", //0:08 PM, PDT
//            "yyyyy.MMMMM.dd GGG hh:mm aaa", //	02001.July.04 AD 12:08 PM
//            "EEE, d MMM yyyy HH:mm:ss Z", //	Wed, 4 Jul 2001 12:08:56 -0700
//            "yyMMddHHmmssZ", //	010704120856-0700
//            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",  // 2001-07-04T12:08:56.235-0700
//            "yyyy-MM-dd'T'HH:mm:ss",  // 2001-07-04T12:08:56.235-0700
//            "M",
//            "y"
//
//
//    ];

//    private static List<SimpleDateFormat> dateFormatters = []


    static final def convert = StringGroovyMethods.&asType

    static Date invalidDate = new Date("01/01/1666")
    static Parser parser = new Parser();
    static {

        String.mixin(PVConvMixin)
    }

    static def asType(String self, Class cls, StringBuffer sb = null) {
        if (cls == Date) {

            List<DateGroup> dateGroup = parser.parse(self as String)
            Date retVal = null;

            if (!dateGroup.isEmpty()) {
                DateGroup dg = dateGroup.get(0)

                boolean isTimeInferred = dg.isTimeInferred();

                List<Date> dates = dg.getDates()

                sb?.append("\n\nConverting data $self; found $dates")
                dates.each {

                    retVal = it
                    if (isTimeInferred) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(retVal);
                        calendar.set(Calendar.HOUR_OF_DAY, 1);
                        calendar.set(Calendar.MINUTE, 1);
                        calendar.set(Calendar.SECOND, 1);
                        calendar.set(Calendar.MILLISECOND, 1);
                        retVal = calendar.getTime();

                    }
                }
                if (retVal == null) {
                    sb?.append("\nCould not find a conversion for this date $self")

                    return null;
                }
            }
            return retVal;


        } else if (cls == PostCode) {
            return new PostCode(self)
        } else return convert(self, cls)
    }


}

PVConvMixin dummy = null
String.mixin(PVConvMixin)


def class PVValTemplate {
    private static GStringTemplateEngine engine = new GStringTemplateEngine(PVValTemplate.class.getClassLoader())

    private static Map<String, Template> templateMap = new ConcurrentHashMap<>();

    static Template getTemplate(String templateName) {
        return templateMap.computeIfAbsent(templateName, { key -> engine.createTemplate(key) })
    }

}


class MatchReq<T> {

    private T attribNativeVal;
    private String attribVal;
    private Class attribType;
    private String propName;
    private String vertexName;

    private Closure predicate;
    private Convert<T> conv;
    private StringBuffer sb = null;

    private excludeFromSearch;

    static Closure convertPredicateFromStr(String predicateStr) {
        if ("eq".equals(predicateStr)) {
            return P.&eq
        } else if ("neq".equals(predicateStr)) {
            return P.&neq
        } else if ("gt".equals(predicateStr)) {
            return P.&gt
        } else if ("lt".equals(predicateStr)) {
            return P.&lt
        } else if ("gte".equals(predicateStr)) {
            return P.&gte
        } else if ("lte".equals(predicateStr)) {
            return P.&lte
        } else if ("textContains".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textContains
        } else if ("textContainsPrefix".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textContainsPrefix
        } else if ("textContainsRegex".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textContainsRegex
        } else if ("textContainsFuzzy".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textContainsFuzzy
        } else if ("textPrefix".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textPrefix
        } else if ("textRegex".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textRegex
        } else if ("textFuzzy".equals(predicateStr)) {
            return org.janusgraph.core.attribute.Text.&textFuzzy
        } else return P.eq;

    }

    MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String predicateStr, boolean excludeFromSearch = false, StringBuffer sb = null) {
        this.attribVal = attribVals
        this.attribType = attribType

        this.propName = propName
        this.vertexName = vertexName
        this.conv = new Convert<>(attribType)
        this.predicate = convertPredicateFromStr(predicateStr)

        this.sb = sb;

        this.excludeFromSearch = excludeFromSearch


        sb?.append("\n In MatchReq($attribVals, $attribType, $propName, $vertexName, $predicateStr)")
        convertToNativeFormat()
    }

    // int compareTo(Object other) {
    //     this.propName <=> other.propName
    // }

    protected void convertToNativeFormat() {

//        Convert.fromString("asdf", this.attribType);

        if (this.attribType == String) {
            this.attribNativeVal = this.attribVal;
        } else {
            this.attribNativeVal = conv.fromString(this.attribVal, this.attribType, this.sb)


        }
    }


    def getExcludeFromSearch() {
        return excludeFromSearch
    }

    void setExcludeFromSearch(excludeFromSearch) {
        this.excludeFromSearch = excludeFromSearch
    }


    T getAttribNativeVal() {
        return attribNativeVal
    }

    void setAttribNativeVal(T attribNativeVal) {
        this.attribNativeVal = attribNativeVal
    }

    String getAttribVal() {
        return attribVal
    }

    void setAttribVal(String attribVal) {
        this.attribVal = attribVal
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

    Closure getPredicate() {
        return predicate
    }

    void setPredicate(Closure predicate) {
        this.predicate = predicate
    }

    @Override
    String toString() {
        return propName + '=' + attribNativeVal
    }
}


def matchVertices(gTrav = g, List<MatchReq> matchReqs, int maxHitsPerType, StringBuffer sb = null) {


    HashMap<String, List<Long>> vertexListsByVertexName = new HashMap();

    HashMap<String, List<MatchReq>> matchReqByVertexName = new HashMap<>();


    matchReqs.each {
        List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() });
        matchReqList.push(it)
        vertexListsByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() })

    }


    matchReqByVertexName.each { k, v ->

        def gtrav = gTrav


        def matchReqByPropName = new HashMap<String, List<MatchReq>>()

        // HashMap<Object, MatchReq>  params = new HashMap<>();


        def subs = v.subsequences()

        // sb?.append("\n $subs")


        subs.each { it ->

            // WARNING: it.unique changes the list; DONT call it as the first arg!
            // Also, we should always this lambda
            // comparator here rather than at the class so
            // the subsequences can do its job without repetition

            if (it.size() == it.unique { entry -> entry.propName }.size()) {

                def searchableItems = it.findAll { it2 -> !it2.excludeFromSearch }

                if (searchableItems.size() > 0) {
                    sb?.append("\ng.V().has('Metadata.Type.")?.append(k)?.append("',eq('")?.append(k)?.append("')")
                    gtrav = gTrav.V().has("Metadata.Type." + k, eq(k)).clone()

                    searchableItems.each { it2 ->
                        gtrav = gtrav.has(it2.propName, it2.predicate(it2.attribNativeVal)).clone()
                        sb?.append("\n     .has('")?.append(it2.propName)?.append("',")
                                ?.append(it2.predicate)?.append(",'")?.append(it2.attribNativeVal)?.append("')")


                    }
                    vertexListsByVertexName.get(k).addAll(gtrav.range(0, maxHitsPerType).id().toList() as Long[])
                    sb?.append("\n $it")

                }


            }

        }

        sb?.append('\n')?.append(vertexListsByVertexName)?.append("\n")


    }




    return [vertexListsByVertexName, matchReqByVertexName];

}

/*
{
  "reqs": [
  	{
  		"attribVals":["Leo","Mark", "Mardy"],
  		"attribType": "String",
  		"propName": "Person.Full_Name",
  		"vertexName": "Person",
  		"predicateStr": "textFuzzy"
  	}
  	,{
  		"attribVals":["Martins","Zukker", "Silva"],
  		"attribType": "String",
  		"propName": "Person.Last_Name",
  		"vertexName": "Person",
  		"predicateStr": "textFuzzy"
  	}
  	,{
  		"attribVals":["01/03/1933","11/03/1933", "01/03/2011"],
  		"attribType": "java.util.Date",
  		"propName": "Person.Date_Of_Birth",
  		"vertexName": "Person",
  		"predicateStr": "eq"
  	}

  	,{
  		"attribVals":["SW1W 9QL","E14 4BB", "SW1W 3LL"],
  		"attribType": "String",
  		"propName": "Location.Address.Post_Code",
  		"vertexName": "Location.Address",
  		"predicateStr": "eq"
  	}
  ]
}
 */

def matchVertices(gTrav = g, String jsonData, String targetType = "Person", int maxHitsPerType, StringBuffer sb = null) {


    def jsonSlurper = new JsonSlurper();


    def object = jsonSlurper.parseText(jsonData);

    if (object.reqs instanceof List) {

        int size = object.reqs.size();
        List<MatchReq> matchReqs = new ArrayList<>(size)

        object.reqs.each { it ->

            if (it) {

                Class nativeType;
                if (it.attribType == null) {
                    nativeType = String.class
                } else {
                    nativeType = Class.forName((String) it.attribType)
                }

                def attribList
                if (it.attribVals instanceof String) {
                    attribList = JsonOutput.toJson(it.attribVals)
                } else { //if (it.attribVals instanceof Collection){
                    attribList = it.attribVals
                }


                attribList?.each { it2 ->

                    sb?.append("\nAdding new MatchReq ($it2, $nativeType, ${it.attribType})")
                    //    MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String predicateStr, StringBuffer sb = null) {

                    MatchReq mreq = new MatchReq(
                            (String) it2
                            , nativeType
                            , (String) it.propName
                            , (String) it.vertexName
                            , (String) it.predicate ?: "eq"
                            , (boolean) it.excludeFromSearch ? true : false
                            , sb
                    )

                    matchReqs.add(mreq)
                }
            }


        }
        return matchVertices(gTrav, matchReqs, maxHitsPerType, sb)


    }

    sb?.toString()

    return new Collections.EmptyMap<String, List<Long>>();
}


def getTopHits(HashMap<String, List<Long>> vertexListsByVertexName, String targetType, int countThreshold, StringBuffer sb = null) {
    def ids = vertexListsByVertexName.get(targetType) as Long[];

    return getTopHits(ids, countThreshold, sb)

}


def getTopHits(Long[] ids, int countThreshold, StringBuffer sb = null) {

    Map<Long, Integer> counts = ids.countBy { it }

    counts = counts.sort { a, b -> b.value <=> a.value }

    List<Long> retVal = new ArrayList<>()
    counts.each { k, v ->
        if (v >= countThreshold) {
            retVal.add(k)
        }

    }


    return retVal

}


def getOtherTopHits(Map<String, List<Long>> vertexListsByVertexName, String targetType, int countThreshold, StringBuffer sb = null) {

    Set<Long> otherIdsSet = new HashSet<>();

    vertexListsByVertexName.each { k, v ->
        if (k != targetType) {
            otherIdsSet.addAll(getTopHits(v as Long[], countThreshold, sb))
        }
    }

    return otherIdsSet;

}


def findMatchingNeighboursFromSingleRequired(gTrav = g, Long requiredTypeId, Set<Long> otherIds, StringBuffer sb = null) {


    def foundIds = gTrav.V(otherIds)
            .both()
            .hasId(requiredTypeId).id()
            .toSet() as Long[]

    sb?.append("\n in findMatchingNeighboursFromSingleRequired() - foundIds = $foundIds")

    return foundIds

}

def findMatchingNeighbours(gTrav = g, Set<Long> requiredTypeIds, Set<Long> otherIds, StringBuffer sb = null) {


    def foundIds = gTrav.V(otherIds)
            .both()
            .hasId(within(requiredTypeIds)).id()
            .toSet() as Long[]

    sb?.append("\n$foundIds")

    return foundIds

}

/*

 */

def getMatchRequests(Map<String, String> currRecord, Object parsedRules, String rulesJsonStr, StringBuffer sb = null) {
    def binding = currRecord

    binding.put("original_request", rulesJsonStr)

    def rules = parsedRules

    List<MatchReq> matchReqs = new ArrayList<>(rules.vertices.size())

    rules.vertices.each { vtx ->

        String vertexName = vtx.label
        vtx.props.each { prop ->

            Class nativeType;

            if (prop.type == null) {
                nativeType = String.class
            } else {
                nativeType = Class.forName((String) prop.type)
            }

            String propName = prop.name

            String propVal = PVValTemplate.getTemplate((String) prop.val).make(binding)
            if (!"null".equals(propVal)) {
                String predicate = prop.predicate ?: "eq"

                MatchReq mreq = new MatchReq(
                        (String) propVal
                        , nativeType
                        , (String) propName
                        , (String) vertexName
                        , (String) predicate
                        , (boolean) prop.excludeFromSearch ? true : false
                        , sb
                )
                matchReqs.add(mreq)

            }


        }


    }
    return matchReqs;

}


def getTopHit(g, Long[] potentialHitIDs, int numHitsRequiredForMatch, HashMap<String, List<Long>> matchIdsByVertexType, String vertexTypeStr, Map<String, List<EdgeRequest>> edgeReqsByVertexType, StringBuffer sb = null) {

    sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; potentialHitIDs = ${potentialHitIDs} ")
    Long[] topHits = getTopHits(potentialHitIDs as Long[], numHitsRequiredForMatch, sb)

    sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; topHits = ${topHits} ")

    Long topHit = null
    Integer numEdgesRequired = edgeReqsByVertexType.get(vertexTypeStr)?.size()

    if (numEdgesRequired != null && numEdgesRequired > 0) {
        if (topHits.size() > 0) {
            // Sanity check: we now have one or more candidates, so let's check
            // if this has conns to other vertices in our little world
            def otherTopHits = getOtherTopHits(matchIdsByVertexType, vertexTypeStr, 1, sb)

            int ilen = topHits.size()

            for (int i = 0; i < ilen; i++) {

                Long[] tempTopHits = findMatchingNeighboursFromSingleRequired(g, topHits[i] as Long, otherTopHits as Set<Long>, sb)
                if (tempTopHits?.size() > 0) {
                    topHit = tempTopHits[0]
                    break
                }
            }


        }
    } else {
        if (topHits.size() > 0) {
            topHit = topHits[0]
        }

    }

    sb?.append("\nIn getTopHit() -- vertType = ${vertexTypeStr} ; topHit  = ${topHit} ")

    return topHit;

}

def addNewVertexFromMatchReqs(g, String vertexTypeStr, List<MatchReq> matchReqsForThisVertexType, StringBuffer sb = null) {

    def localTrav = g

    localTrav = localTrav.addV(vertexTypeStr)
            .property('Metadata.Type.' + vertexTypeStr, vertexTypeStr)
            .property('Metadata.Type', vertexTypeStr)

    matchReqsForThisVertexType.each { it ->
        localTrav = localTrav.property(it.getPropName(), it.attribNativeVal)
    }

    Long retVal = localTrav.next().id() as Long

    sb?.append("\n in addNewVertexFromMatchReqs() - added new vertex of type ${vertexTypeStr}; id = ${retVal}")
    return retVal


}


def updateExistingVertexWithMatchReqs(g, Long vertexId, List<MatchReq> matchReqsForThisVertexType, StringBuffer sb = null) {

    def localTrav = g
    def deletionTrav = g
    sb?.append("\n in updateExistingVertexWithMatchReqs() - about to start Updating vertex of id ${vertexId}; ${matchReqsForThisVertexType}")

    localTrav = localTrav.V(vertexId)

    matchReqsForThisVertexType.each { it ->
        String propName = it.getPropName();
        sb?.append("\n in updateExistingVertexWithMatchReqs() - updating new vertex of id = ${vertexId} prop=${propName} val = it.attribNativeVal")

        deletionTrav.V(vertexId).properties(it.getPropName()).drop().iterate()
        localTrav = localTrav.property(propName, it.attribNativeVal)
    }
    localTrav.iterate()

    // Long retVal = localTrav.next().id() as Long

    sb?.append("\n in updateExistingVertexWithMatchReqs() - updated vertex of id ${vertexId}")
    // return retVal


}


class EdgeRequest {

    String label;
    String fromVertexLabel;
    String toVertexLabel;

    EdgeRequest(String label, String fromVertexLabel, String toVertexLabel) {
        this.label = label
        this.fromVertexLabel = fromVertexLabel
        this.toVertexLabel = toVertexLabel
    }

    String getLabel() {
        return label
    }

    void setLabel(String label) {
        this.label = label
    }

    String getFromVertexLabel() {
        return fromVertexLabel
    }

    void setFromVertexLabel(String fromVertexLabel) {
        this.fromVertexLabel = fromVertexLabel
    }

    String getToVertexLabel() {
        return toVertexLabel
    }

    void setToVertexLabel(String toVertexLabel) {
        this.toVertexLabel = toVertexLabel
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof EdgeRequest)) return false

        EdgeRequest that = (EdgeRequest) o

        if (fromVertexLabel != that.fromVertexLabel) return false
        if (label != that.label) return false
        if (toVertexLabel != that.toVertexLabel) return false

        return true
    }

    int hashCode() {
        int result
        result = (label != null ? label.hashCode() : 0)
        result = 31 * result + (fromVertexLabel != null ? fromVertexLabel.hashCode() : 0)
        result = 31 * result + (toVertexLabel != null ? toVertexLabel.hashCode() : 0)
        return result
    }

    String toString() {
        return "${label} = ($fromVertexLabel)->($toVertexLabel)"
    }
}


def parseEdges(def rules) {

    Map<String, List<EdgeRequest>> edgeReqsByVertexName = new HashMap<>()
    Set<EdgeRequest> edgeReqs = new HashSet<>()

    rules.edges.each { it ->
        String fromVertexLabel = it.fromVertexLabel
        String toVertexLabel = it.toVertexLabel
        String label = it.label

        EdgeRequest req = new EdgeRequest(label, fromVertexLabel, toVertexLabel);

        edgeReqs.add(req)
        fromEdgeList = edgeReqsByVertexName.computeIfAbsent(fromVertexLabel, { k -> new ArrayList<EdgeRequest>() })
        fromEdgeList.add(req)
        toEdgeList = edgeReqsByVertexName.computeIfAbsent(toVertexLabel, { k -> new ArrayList<EdgeRequest>() })
        toEdgeList.add(req)

    }

    return [edgeReqsByVertexName, edgeReqs]
}

def createEdges(gTrav, Set<EdgeRequest> edgeReqs, Map<String, Long> finalVertexIdByVertexName, StringBuffer sb = null) {

    edgeReqs.each { it ->

        sb?.append("\n in createEdges; edgeReq = $it ")

        sb?.append("\n in createEdges; finalVertexIdByVertexName = $finalVertexIdByVertexName ")

        Long fromId = finalVertexIdByVertexName.get(it.fromVertexLabel)
        Long toId = finalVertexIdByVertexName.get(it.toVertexLabel)

        sb?.append("\n in createEdges; from=$fromId; to=$toId ")


        def foundIds = gTrav.V(toId)
                .both()
                .hasId(within(fromId)).id()
                .toSet() as Long[]

        sb?.append("\n in createEdges $foundIds")

        if (foundIds.size() == 0) {
            def fromV = gTrav.V(fromId)
            def toV = gTrav.V(toId)

            gTrav.addE(it.label).from(fromV).to(toV).next()
        }


    }
}


def ingestDataUsingRules(graph, g, List<Map<String, String>> listOfMaps, String jsonRules, StringBuffer sb = null) {

    def jsonSlurper = new JsonSlurper()
    def rules = jsonSlurper.parseText(jsonRules)

    def (edgeReqsByVertexName, edgeReqs) = parseEdges(rules.updatereq)
    trans = graph.tx()
    try {
        if (!trans.isOpen()) {
            trans.open()
        }

        for (Map<String, String> item in listOfMaps) {

            def matchReqs = getMatchRequests(item, rules.updatereq, jsonRules, sb)
            def (matchIdsByVertexType, vertexListsByVertexName) = matchVertices(g, matchReqs, 10, sb);

            Map<String, Long> finalVertexIdByVertexName = new HashMap<>();
            matchIdsByVertexType.each { vertexTypeStr, potentialHitIDs ->

                List<MatchReq> matchReqsForThisVertexType = vertexListsByVertexName.get(vertexTypeStr)
                int numHitsRequiredForMatch = matchReqsForThisVertexType?.size()

                if (numHitsRequiredForMatch > 0) {
                    numHitsRequiredForMatch += (numHitsRequiredForMatch - 1)
                }

                Long topHit = getTopHit(g
                        , potentialHitIDs as Long[]
                        , (int) numHitsRequiredForMatch
                        , (HashMap<String, List<Long>>) matchIdsByVertexType
                        , (String) vertexTypeStr
                        , (Map<String, List<EdgeRequest>>) edgeReqsByVertexName
                        , sb)

                if (topHit != null) {

                    updateExistingVertexWithMatchReqs(g, topHit, matchReqsForThisVertexType, sb)
                    finalVertexIdByVertexName.put((String) vertexTypeStr, topHit)
                } else {
                    Long newVertexId = addNewVertexFromMatchReqs(g, (String) vertexTypeStr, matchReqsForThisVertexType, sb)
                    finalVertexIdByVertexName.put((String) vertexTypeStr, newVertexId)

                }


            }

            createEdges(g, (Set<EdgeRequest>) edgeReqs, (Map<String, Long>) finalVertexIdByVertexName, sb)


        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

/*


//NOTE: FOR SOME REASON, THE Object.Insurance_Policy is not working as expected!!!  check whether it's the flag
// to exclude from search that is causing trouble!!!


def jsonSlurper = new JsonSlurper()
def listOfMaps = jsonSlurper.parseText '''
[ {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "MICHAEL",
  "pg_LastName" : "PLATINI",
  "pg_ZipCode" : "B6 7NP",
  "pg_City" : "Birmingham",
  "pg_NumOfMarketingEmailSent" : "15",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "8",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "kiddailey@hotmail.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "18/10/1969",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "JUDY",
  "pg_LastName" : "CAMEROON",
  "pg_ZipCode" : "B60 1DX",
  "pg_City" : "Bromsgrove",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "knorr@live.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : "yeugo@hotmail.com",
  "pg_PermissionToContactSecondary" : "No",
  "pg_DateofBirth" : "04/12/1972",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "SACHIN",
  "pg_LastName" : "KUMAR",
  "pg_ZipCode" : "B742NH",
  "pg_City" : "Coldfield",
  "pg_NumOfMarketingEmailSent" : "11",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "13",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "mbswan@optonline.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "12/09/1973",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "CORY",
  "pg_LastName" : "RHODES",
  "pg_ZipCode" : "DE75 7PQ",
  "pg_City" : "Heanor",
  "pg_NumOfMarketingEmailSent" : "10",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "7",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "dieman@yahoo.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "05/04/1975",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "98497047",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "VVAP"
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "MICKEY",
  "pg_LastName" : "CRISTINO",
  "pg_ZipCode" : "NE70 7QG",
  "pg_City" : "Belford",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "14",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "jaxweb@sbcglobal.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "31/08/1976",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "10330435",
  "pg_PolicyType" : "Non- Renewable",
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "WUFP"
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "HERMAN",
  "pg_LastName" : "STONE",
  "pg_ZipCode" : "HS8 5QX",
  "pg_City" : "South Uist",
  "pg_NumOfMarketingEmailSent" : "13",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "hermanab@live.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "13/08/1979",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "JOHN",
  "pg_LastName" : "SMITH",
  "pg_ZipCode" : "PA15 4SY",
  "pg_City" : "Greenock",
  "pg_NumOfMarketingEmailSent" : "15",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "10",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "retoh@optonline.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "08/04/1973",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "10330434",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "RIKR"
}, {
  "pg_ExistingCustomer" : "YES",
  "pg_FirstName" : "TRACY",
  "pg_LastName" : "NOAH",
  "pg_ZipCode" : "CM2 9HX",
  "pg_City" : "Chemsford",
  "pg_NumOfMarketingEmailSent" : "14",
  "pg_NumOpened" : "8",
  "pg_NumOfBrandEnagementEmailSent" : "9",
  "pg_NumTotalClickThrough" : "10",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "tromey@mac.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "26/10/1982",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : "49949479",
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : "Open",
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : "JUDV"
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "JOHN",
  "pg_LastName" : "DAILEY",
  "pg_ZipCode" : "BH8 1  HM",
  "pg_City" : "Bournemouth",
  "pg_NumOfMarketingEmailSent" : "12",
  "pg_NumOpened" : "10",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "13",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "sabren@icloud.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : "kuaoiio@gmail.com",
  "pg_PermissionToContactSecondary" : "Yes",
  "pg_DateofBirth" : "20/11/1984",
  "pg_MailBounced" : "2",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "KEITH",
  "pg_LastName" : "SAUNDERS",
  "pg_ZipCode" : "PH34 3ET",
  "pg_City" : "Speam Bridge",
  "pg_NumOfMarketingEmailSent" : "12",
  "pg_NumOpened" : "9",
  "pg_NumOfBrandEnagementEmailSent" : "10",
  "pg_NumTotalClickThrough" : "11",
  "pg_OpenOnDevice" : "Desktop",
  "pg_PrimaryEmailAddress" : "shazow@yahoo.com",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "22/01/1987",
  "pg_MailBounced" : "1",
  "pg_Sex" : "Male",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
}, {
  "pg_ExistingCustomer" : "NO",
  "pg_FirstName" : "MICHELLE",
  "pg_LastName" : "DAVIDSON",
  "pg_ZipCode" : "SG13 7EJ",
  "pg_City" : "Hertford",
  "pg_NumOfMarketingEmailSent" : "11",
  "pg_NumOpened" : "10",
  "pg_NumOfBrandEnagementEmailSent" : "8",
  "pg_NumTotalClickThrough" : "14",
  "pg_OpenOnDevice" : "Mobile",
  "pg_PrimaryEmailAddress" : "moxfulder@sbcglobal.net",
  "pg_PermissionToContactPrimary" : "Yes",
  "pg_SecondaryEmailID" : null,
  "pg_PermissionToContactSecondary" : null,
  "pg_DateofBirth" : "27/01/1987",
  "pg_MailBounced" : "0",
  "pg_Sex" : "Female",
  "pg_Unsubscribed" : "No",
  "pg_SpamReported" : "No",
  "pg_Policynumber" : null,
  "pg_PolicyType" : null,
  "pg_PolicyStatus" : null,
  "pg_ProspectStatus" : "Active",
  "pg_ClientManager" : null
} ]
'''



def rulesStr =  '''
{
  "updatereq":
  {

   ,"vertices":
	[
	  {
	    "label": "Person"
	   ,"props":
		[
		  {
		    "name": "Person.Full_Name"
		   ,"val": "${pg_FirstName?.toUpperCase() } ${pg_LastName?.toUpperCase()}"
		   ,"predicate": "textContains"
		  }
		 ,{
		 	"name": "Person.Last_Name"
		   ,"val": "${pg_LastName?.toUpperCase()}"
		  }
		 ,{
		 	"name": "Person.Date_Of_Birth"
		   ,"val": "${pg_DateofBirth}"
		   ,"type": "java.util.Date"
		  }
		 ,{
		 	"name": "Person.Gender"
		   ,"val": "${pg_Sex}"
		  }
		]
	  }
	 ,{
	    "label": "Location.Address"
	,"props":
		[
		  {
		    "name": "Location.Address.parser.postcode"
		   ,"val": "${pg_ZipCode}"
		  }
		 ,{
		 	"name": "Location.Address.parser.city"
		   ,"val": "${pg_City?.toLowerCase()}"
		  }
		 ,{
		 	"name": "Location.Address.Post_Code"
		   ,"val": "${pg_ZipCode}"
		   ,"excludeFromSearch": true
		  }
		]

	  }
	 ,{
	    "label": "Object.Email_Address"
		,"props":
		[
		  {
		    "name": "Object.Email_Address.Email"
		   ,"val": "${pg_PrimaryEmailAddress}"
		  }
		]

	  }
	 ,{
	    "label": "Object.Insurance_Policy"
		,"props":
		[
		  {
		    "name": "Object.Insurance_Policy.Number"
		   ,"val": "${pg_Policynumber}"
		  }
		 ,{
		    "name": "Object.Insurance_Policy.Type"
		   ,"val": "${pg_PolicyType}"
		  }
		 ,{
		 	"name": "Object.Insurance_Policy.Status"
		   ,"val": "${pg_PolicyStatus}"
		   ,"excludeFromSearch": true
		  }

		]

	  }

	]
   ,"edges":
    [
      { "label": "Uses_Email", "fromVertexLabel": "Person", "toVertexLabel": "Object.Email_Address" }
	 ,{	"label": "Lives", "fromVertexLabel": "Person", "toVertexLabel": "Location.Address"  }
	 ,{	"label": "Has_Policy", "fromVertexLabel": "Person", "toVertexLabel": "Object.Insurance_Policy"  }
    ]
  }
}
'''
// edgeLabel = createEdgeLabel(mgmt, "Has_Policy")

StringBuffer sb = new StringBuffer ()

// trigger the String.Mixin() call in the static c-tor

// sb.append("${PostCode.format(pg_ZipCode)}")
try{
    ingestDataUsingRules(graph, g, listOfMaps, rulesStr, sb)
}
catch (Throwable t){
    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)
    // Arrays.stream( t.getStackTrace())
    //               .map( { s -> s.toString() })
    //               .collect(Collectors.joining("\n"));

    sb.append("\n$t\n$stackTrace")


}
sb.toString()

// g.E().drop().iterate()
// g.V().drop().iterate()

// describeSchema()
// g.V()
*/