import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.RelationType
import org.janusgraph.core.schema.JanusGraphIndex
import org.janusgraph.core.schema.RelationTypeIndex

//action

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

try {
    createIndicesPropsAndLabels();
} catch (e) {
    e.printStackTrace()
}

