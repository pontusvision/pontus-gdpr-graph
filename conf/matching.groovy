import groovy.json.JsonSlurper
import org.apache.tinkerpop.gremlin.process.traversal.P
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.text.SimpleDateFormat

/// NOTE : look at groovy subsequences, then iterate, and check which ones are unique().size() == .size()


//def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["01/03/1933","11/03/1933","01/03/2011","01/01/1666"],"attribType":"java.util.Date","propName":"Person.Date_Of_Birth","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'
//def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"textFuzzy"},{"attribVals":["01/03/1933","11/03/1933","01/03/2011","01/01/1666"],"attribType":"java.util.Date","propName":"Person.Date_Of_Birth","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'
// def jsonData = '{"reqs":[{"attribVals":["chris owens","Mark","Mardy"],"attribType":"java.lang.String","propName":"Person.Full_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["Martins","Zukker","Silva", "owens"],"attribType":"java.lang.String","propName":"Person.Last_Name","vertexName":"Person","predicateStr":"eq"},{"attribVals":["SW1W 9QL","E14 4BB","60412"],"attribType":"java.lang.String","propName":"Location.Address.Post_Code","vertexName":"Location.Address","predicateStr":"eq"}]}'

//StringBuffer sb = new StringBuffer();
// String.mixin(DateConvMixin)

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

def class Convert<T> {
    private from
    private to


    public List<String> dateFormats = DateConvMixin.dateFormats;

    private List<SimpleDateFormat> dateFormatters = [];


    public Convert(clazz) {
        from = clazz
        setDateFormats(this.dateFormats)

    }

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


    T fromString(String data, Class<T> requiredType, StringBuffer sb = null) {

        if (requiredType == Date.class) {

            int ilen = dateFormatters.size();
            for (int i = 0; i < ilen; i++) {
                try {
                    Date retVal = dateFormatters.get(i).parse(data);
                    sb?.append("\n Converted $data to Date")
                    return retVal as T;

                } catch (Throwable t) {
                    sb?.append("\n Failed to convert $data to Date: $t")
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

class DateConvMixin {

    static List<String> dateFormats = [
            "dd/MM/yyyy",
            "dd/MM/yy",
            "d M y",
            "d-m-y",
            "yyyy.MM.dd G 'at' HH:mm:ss z", //	2001.07.04 AD at 12:08:56 PDT
            "EEE, MMM d, ''yy", //	Wed, Jul 4, '01
            "h:mm a", //	12:08 PM
            "hh 'o''clock' a, zzzz", //12 o'clock PM, Pacific Daylight Time
            "K:mm a, z", //0:08 PM, PDT
            "yyyyy.MMMMM.dd GGG hh:mm aaa", //	02001.July.04 AD 12:08 PM
            "EEE, d MMM yyyy HH:mm:ss Z", //	Wed, 4 Jul 2001 12:08:56 -0700
            "yyMMddHHmmssZ", //	010704120856-0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",  // 2001-07-04T12:08:56.235-0700
            "yyyy-MM-dd'T'HH:mm:ss",  // 2001-07-04T12:08:56.235-0700
            "M",
            "y"


    ];


    private static List<SimpleDateFormat> dateFormatters = []


    static final def convert = StringGroovyMethods.&asType

    static {
        dateFormatters.clear()
        dateFormats.each {
            dateFormatters.add(new SimpleDateFormat(it))
        }

        String.mixin(DateConvMixin)
    }

    static def asType(String self, Class cls, StringBuffer sb = null) {
        if (cls == Date) {
            int ilen = dateFormatters.size();
            for (int i = 0; i < ilen; i++) {
                try {
                    Date retVal = dateFormatters.get(i).parse(self);
                    sb?.append("\n Successfully parsed $self")  // ignore

                    return retVal as Date;

                } catch (Throwable t) {
                    sb?.append("\nfailed to parseDate $self; err: $t")  // ignore
                }
            }

        } else convert(self, cls)
    }


}
//


class MatchReq<T>  {

    private T attribNativeVal;
    private String attribVal;
    private Class attribType;
    private String propName;
    private String vertexName;

    private Closure predicate;
    private Convert<T> conv;
    private StringBuffer sb = null;

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


    MatchReq(String attribVals, Class<T> attribType, String propName, String vertexName, String predicateStr, List<String> dateFormats, StringBuffer sb = null) {
        this.attribVal = attribVals
        this.attribType = attribType

        this.propName = propName
        this.vertexName = vertexName
        this.conv = new Convert<>(attribType)
        this.predicate = convertPredicateFromStr(predicateStr)

        this.sb = sb;

        sb?.append("\n In MatchReq($attribVals, $attribType, $propName, $vertexName, $predicateStr)")
        if (dateFormats != null) {
            this.conv.setDateFormats(dateFormats)
        }
        convertListToNativeFormat()
    }

    // int compareTo(Object other) {
    //     this.propName <=> other.propName
    // }

    protected void convertListToNativeFormat() {

//        Convert.fromString("asdf", this.attribType);

        if (this.attribType == String) {
            this.attribNativeVal = this.attribVal;
        } else {
            this.attribNativeVal = conv.fromString(this.attribVal, this.attribType, this.sb)


        }
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

def matchPerson(ArrayList<MatchReq> matchReqs, StringBuffer sb, g) {

    HashMap<String, List<MatchReq>> matchReqByVertexName = new HashMap<>();

    HashMap<String, List<Long>> vertexListsByVertexName = new HashMap();



    matchReqs.each {
        List<MatchReq> matchReqList = matchReqByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() });
        matchReqList.push(it)
        vertexListsByVertexName.computeIfAbsent(it.vertexName, { k -> new ArrayList<>() })

    }


    matchReqByVertexName.each { k, v ->

        def gtrav = g


        def matchReqByPropName = new HashMap<String, List<MatchReq>>()

        // HashMap<Object, MatchReq>  params = new HashMap<>();


        def subs = v.subsequences()

        // sb?.append("\n $subs")



        subs.each { it ->

            // WARNING: it.unique changes the list; DONT call it as the first arg!
            // Also, we should always this lambda
            // comparator here rather than at the class so
            // the subsequences can do its job without repetition

            if (it.size() == it.unique{ entry -> entry.propName }.size() ) {
                sb?.append("\ng.V().has('Metadata.Type.").append(k).append("',eq('").append(k).append("')")
                gtrav = g.V().has("Metadata.Type." + k, eq(k)).clone()

                it.each { it2 ->
                    gtrav = gtrav.has(it2.propName, it2.predicate(it2.attribNativeVal)).clone()
                    sb?.append("\n     .has('").append(it2.propName).append("',")
                            .append(it2.predicate).append(",'").append(it2.attribNativeVal).append("')")

                }

                vertexListsByVertexName.get(k).addAll(gtrav.id() as Long[])
                sb?.append("\n $it")

            }

        }

        sb?.append('\n').append(vertexListsByVertexName).append("\n")


    }

    return vertexListsByVertexName;

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

def matchPerson(String jsonData, StringBuffer sb = null, gTrav = g, String targetType = "Person") {


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
                }
                else {
                    nativeType = Class.forName((String) it.attribType)
                }

                def attribList = null
                if (it.attribVals instanceof String){
                    attribList = groovy.json.JsonOutput.toJson(it.attribVals)
                }
                else{ //if (it.attribVals instanceof Collection){
                    attribList = it.attribVals
                }


                attribList?.each{ it2 ->

                    sb?.append("\nAdding new MatchReq ($it2, $nativeType, ${it.attribType})")
                    MatchReq mreq = new MatchReq(
                            (String) it2
                            , nativeType
                            , (String) it.propName
                            , (String) it.vertexName
                            , (String) it.predicate ?: "eq"
                            , null
                            , sb
                    )

                    matchReqs.add(mreq)
                }
            }


        }
        matchEdges(matchPerson(matchReqs, sb, gTrav), targetType, sb)


    }

    sb?.toString()
}


def matchEdges(HashMap<String, List<Long>> vertexListsByVertexName, String targetType, StringBuffer sb) {

    def ids = vertexListsByVertexName.get(targetType) as Long[];

    Set<Long> idsSet = new HashSet<>(ids.length)

    idsSet.addAll(ids)
    Map counts = ids.countBy { it }

    counts = counts.sort { a, b -> b.value <=> a.value }

    Set<Long> otherIdsSet = new HashSet<>();

    vertexListsByVertexName.each { k, v -> if (!k.equals(targetType)) otherIdsSet.addAll(v) }


    def otherIds = otherIdsSet as Long[];


    def foundIds = g.V(otherIds)
            .both()
            .hasId(within(idsSet)).id()
            .toSet() as Long[]

    sb?.append("\n$foundIds")
    sb?.append("\n\n$counts")



    /* THIS will create a map of entries by count....  it'll come in handy for ranking the matches.
Map counts = certs.countBy { it }
counts.findAll { it.value == counts.values().max() }
or by an one-liner

certs.countBy { it }.groupBy { it.value }.max { it.key }.value.keySet()


 */

}

