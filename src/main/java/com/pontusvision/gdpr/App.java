package com.pontusvision.gdpr;

//import com.netflix.astyanax.connectionpool.exceptions.ThrottledException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.jsr223.GremlinScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.server.GraphManager;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
//import org.mortbay.jetty.servlet.ServletHolder;
import org.janusgraph.core.*;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.jhades.JHades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
//import javax.ws.rs.core.Application;

/**
 * Hello world!
 */
public class App {
    static {
        // hook slf4j up to netty internal logging
//        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }


    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static JanusGraph graph;
    public static JanusGraphManagement graphMgmt;
    public static GraphTraversalSource g;
    public static Graph initGraph(String confFile) {
//        String confFile = args.length == 0? "conf/janusgraph-hbase-es.properties" : args[0];
        JanusGraph graph = JanusGraphFactory.open(confFile);
        graph.tx().rollback(); // Never create new indices while a transaction is active

        JanusGraphManagement mgmt = graph.openManagement();
        try {
            mgmt.getVertexLabels();







            PropertyKey metadataOwner = mgmt.makePropertyKey("Metadata.Owner").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataLineage = mgmt.makePropertyKey("Metadata.Lineage").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey metadataRedaction = mgmt.makePropertyKey("Metadata.Redaction").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataVersion = mgmt.makePropertyKey("Metadata.Version").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataCreateDate = mgmt.makePropertyKey("Metadata.CreateDate").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataUpdateDate = mgmt.makePropertyKey("Metadata.UpdateDate").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataStatus = mgmt.makePropertyKey("Metadata.Status").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey metadataOrigId = mgmt.makePropertyKey("Metadata.OrigId").dataType(UUID.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey metadataGDPRStatus = mgmt.makePropertyKey("Metadata.GDPRStatus").dataType(String.class).cardinality(Cardinality.SINGLE).make();


            VertexLabel partyPersonLabel = mgmt.makeVertexLabel("Party.Person").make();

            PropertyKey partyPersonDateOfBirth = mgmt.makePropertyKey("Party.Person.DateOfBirth").dataType(Date.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonFullName = mgmt.makePropertyKey("Party.Person.FullName").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonEmailAddress = mgmt.makePropertyKey("Party.Person.EmailAddress").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonGender = mgmt.makePropertyKey("Party.Person.Gender").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonNationality = mgmt.makePropertyKey("Party.Person.Nationality").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey partyPersonPlaceOfBirth = mgmt.makePropertyKey("Party.Person.PlaceOfBirth").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonReligion = mgmt.makePropertyKey("Party.Person.Religion").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonEthnicity = mgmt.makePropertyKey("Party.Person.Ethnicity").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonMaritalStatus = mgmt.makePropertyKey("Party.Person.MaritalStatus").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonHeight = mgmt.makePropertyKey("Party.Person.Height").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonNameQualifier = mgmt.makePropertyKey("Party.Person.NameQualifier").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyPersonTitle = mgmt.makePropertyKey("Party.Person.Title").dataType(String.class).cardinality(Cardinality.SINGLE).make();

            PropertyKey partyOrgRegNumber = mgmt.makePropertyKey("Party.Org.RegNumber").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgType = mgmt.makePropertyKey("Party.Org.Type").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey partyOrgName = mgmt.makePropertyKey("Party.Org.Name").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgShortName = mgmt.makePropertyKey("Party.Org.ShortName").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgTaxId = mgmt.makePropertyKey("Party.Org.TaxId").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey partyOrgSector = mgmt.makePropertyKey("Party.Org.Sector").dataType(String.class).cardinality(Cardinality.SET).make();


            mgmt.makeEdgeLabel("Metadata.isNext");

            mgmt.makeEdgeLabel("Party.person.isParent");
            mgmt.makeEdgeLabel("Party.person.isPartner");
            mgmt.makeEdgeLabel("Party.person.isMarried");
            mgmt.makeEdgeLabel("Party.person.isDivorced");
            mgmt.makeEdgeLabel("Party.person.hasAlias");
            mgmt.makeEdgeLabel("Party.person.isWidowed");
            mgmt.makeEdgeLabel("Party.person.isFriend");
            mgmt.makeEdgeLabel("Party.person.isDating");
            mgmt.makeEdgeLabel("Party.person.worksAt");
            mgmt.makeEdgeLabel("Party.person.earns");

            mgmt.makeEdgeLabel("Party.person.lives");
            mgmt.makeEdgeLabel("Party.person.isAt");
            mgmt.makeEdgeLabel("Party.person.studied");
            mgmt.makeEdgeLabel("Party.person.has");
            mgmt.makeEdgeLabel("Party.person.owns");
            mgmt.makeEdgeLabel("Party.person.rents");

            VertexLabel objectCredentialLabel = mgmt.makeVertexLabel("Object.Credential").make();
            PropertyKey objectCredentialUsername = mgmt.makePropertyKey("Object.Credential.Username").dataType(String.class).cardinality(Cardinality.SINGLE).make();
            PropertyKey objectCredentialQuestions = mgmt.makePropertyKey("Object.Credential.Questions").dataType(String.class).cardinality(Cardinality.SET).make();
            PropertyKey objectCredentialAnswers = mgmt.makePropertyKey("Object.Credential.Answers").dataType(String.class).cardinality(Cardinality.SET).make();


            VertexLabel partyOrgLabel = mgmt.makeVertexLabel("Party.Organization").make();
            VertexLabel objectDocument = mgmt.makeVertexLabel("Object.Document").make();
            VertexLabel objectAccount = mgmt.makeVertexLabel("Object.Account").make();

            VertexLabel locationPhysicalAddressLabel = mgmt.makeVertexLabel("Location.PhysicalAddress").make();
            VertexLabel locationIPAddressLabel = mgmt.makeVertexLabel("Location.IPAddress").make();
            VertexLabel locationPhoneNumberLabel = mgmt.makeVertexLabel("Location.PhoneNumber").make();

            VertexLabel eventLoginLabel = mgmt.makeVertexLabel("Event.Login").make();
            VertexLabel eventLogoutLabel = mgmt.makeVertexLabel("Event.Logout").make();


            mgmt.buildIndex("nameAndAge", Vertex.class).addKey(partyPersonFullName).addKey(partyPersonDateOfBirth).buildMixedIndex("search");
        /*
        The example above defines a mixed index containing the property keys name and age.
        The definition refers to the indexing backend name search so that JanusGraph knows which
        configured indexing backend it should use for this particular index. The search parameter
        specified in the buildMixedIndex call must match the second clause in the JanusGraph
        configuration definition like this: index.search.backend If the index was named solrsearch
        then the configuration definition would appear like this: index.solrsearch.backend.


        */

            mgmt.commit();

        } catch (Exception e) {
            logger.info("Did not set schema changes; error was {}", e.getMessage());

        }
        return graph;
    }

    public  static Configuration getDefaultConfigs() throws ConfigurationException {
        DefaultConfigurationBuilder confBuilder = new DefaultConfigurationBuilder();

        Configuration conf = confBuilder.getConfiguration(false);
        conf.setProperty("port", 8182);
        conf.setProperty("nioPoolSize", 2);
        conf.setProperty("workerPoolSize", 2);
//                    conf.setProperty("username", "root");
//                    conf.setProperty("password", "pa55word");
//                    conf.setProperty("jaasEntry", "tinkerpop");
//                    conf.setProperty("protocol", "GSSAPI");
        conf.setProperty("hosts", "127.0.0.1");
//                conf.setProperty("serializer.className", "GryoMessageSerializerV3d0");
//                conf.setProperty("serializer.config", "");
//        conf.setProperty("connectionPool.channelizer", "org.apache.tinkerpop.gremlin.driver.Channelizer.WebSocketChannelizer");
        conf.setProperty("connectionPool.enableSsl", false);
        conf.setProperty("connectionPool.trustCertChainFile", "");
        conf.setProperty("connectionPool.minSize", 2);
        conf.setProperty("connectionPool.maxSize", 2);
        conf.setProperty("connectionPool.minSimultaneousUsagePerConnection", 2);
        conf.setProperty("connectionPool.maxSimultaneousUsagePerConnection", 2);
        conf.setProperty("connectionPool.maxInProcessPerConnection", 2);
        conf.setProperty("connectionPool.minInProcessPerConnection", 2);
        conf.setProperty("connectionPool.maxSimultaneousUsagePerConnection", 2);
        conf.setProperty("connectionPool.maxWaitForConnection", 200000);
        conf.setProperty("connectionPool.maxContentLength", 200000);
        conf.setProperty("connectionPool.reconnectInterval", 200000);
        conf.setProperty("connectionPool.resultIterationBatchSize", 200000);
        conf.setProperty("connectionPool.keepAliveInterval", 1800000);
        return  conf;
    }


    public static void main(String[] args) {
        new JHades().overlappingJarsReport();
        Server server = new Server(3001);

        try {
            ResourceConfig config = new ResourceConfig();
            config.packages("com.pontusvision.gdpr");
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));


            ServletContextHandler context = new ServletContextHandler(server, "/*");
            context.addServlet(servlet, "/*");


            server.start();

            String file = args.length == 0 ? "conf/gremlin-server.yml" : args[0];
            String graphConfFile = args.length != 2 ? "conf/janusgraph-hbase-es.properties" : args[1];

            final Settings settings;
            try {
                settings = Settings.read(file);
            } catch (Exception ex) {
                logger.error("Configuration file at {} could not be found or parsed properly. [{}]", file, ex.getMessage());
                ex.printStackTrace();
                return;
            }

            logger.info("Configuring Gremlin Server from {}", file);
            final GremlinServer gserver = new GremlinServer(settings);
            CompletableFuture<ServerGremlinExecutor> c = gserver.start().exceptionally(t -> {
                logger.error("Gremlin Server was unable to start and will now begin shutdown: {}", t.getMessage());
                gserver.stop().join();
                return null;
            });


            ServerGremlinExecutor sge = c.get();

            GraphManager graphMgr = sge.getGraphManager();
            Set<String> graphNames = graphMgr.getGraphNames();

            for (String graphName : graphNames) {
                logger.debug("Found Graph: " + graphName);
                graph = (JanusGraph) graphMgr.getGraph(graphName);
                graphMgmt = graph.openManagement();
                g = graph.traversal();
            }

//            graph = graphMgr.getGraph("graph");


//            initGraph(graphConfFile);

//            Graph graph = initGraph(graphConfFile);
//            ServerGremlinExecutor executor = gserver.getServerGremlinExecutor();
//            executor.getGremlinExecutor().eval("graph = TinkerGraph.open()\n" +
//                    "g = graph.traversal()\n");

//            gserver.getServerGremlinExecutor().getGraphManager().putGraph("graph", graph);
//            gserver.getServerGremlinExecutor().getGraphManager().putTraversalSource("g", graph.traversal());
//
//            gserver.



//            Configuration conf = getDefaultConfigs();
//            Cluster cluster = Cluster.open(conf);
//
//            Client unaliasedClient = cluster.connect();
//
//            Client client = unaliasedClient; //.alias("g1");
//
//
//            ResultSet res = client.submit("[1,2,3,4]");
//
//            logger.debug(res.toString());
//



            server.join();
            c.join();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}
