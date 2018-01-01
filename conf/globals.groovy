import org.apache.commons.math3.util.Pair
import org.apache.commons.math3.distribution.EnumeratedDistribution
import com.jayway.jsonpath.JsonPath

import org.janusgraph.core.*
import org.janusgraph.core.schema.*
import scala.xml.Atom

import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

import static org.janusgraph.core.attribute.Text.*
import java.lang.*
import java.util.*
import java.text.*

def globals = [:]
globals << [g: graph.traversal()]
globals << [mgmt: graph.openManagement()]

def addRandomUserData(graph, g, pg_dob, pg_metadataController, pg_metadataProcessor, pg_metadataLineage, pg_metadataRedaction, pg_metadataVersion, pg_metadataStatus, pg_metadataGDPRStatus, pg_metadataLineageServerTag, pg_metadataLineageLocationTag, pg_login_username, pg_login_sha256, pg_id_name, pg_id_value, pg_name_first, pg_name_last, pg_gender, pg_nat, pg_name_title, pg_email, pg_location_street, pg_location_city, pg_location_state, pg_location_postcode) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    try {
        dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) pg_dob)
    } catch (Throwable t) {
        dob = new Date("01/01/1666")

    }

    trans = graph.tx()
    try {
        trans.open()


        person = g.addV("Person").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Person").
                property("Person.Full_Name", pg_name_first + " " + pg_name_last).
                property("Person.Last_Name", pg_name_last).
                property("Person.Gender", pg_gender).
                property("Person.Nationality", pg_nat).
                property("Person.Date_Of_Birth", dob).
                property("Person.Title", pg_name_title).next()

        email = g.addV("Object.Email_Address").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Email_Address").
                property("Object.Email_Address.Email", pg_email).next()


        credential = g.addV("Object.Credential").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Credential").
                property("Object.Credential.userId", pg_login_username).
                property("Object.Credential.login.sha256", pg_login_sha256).next()

        idCard = g.addV("Object.Identity_Card").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Identity_Card").
                property("Object.Identity_Card.id_name", pg_id_name).
                property("Object.Identity_Card.id_value", pg_id_value).next()


        location = g.addV("Location.Address").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPR_Status", pg_metadataGDPRStatus).
                property("Metadata.Lineage_Server_Tag", pg_metadataLineageServerTag).
                property("Metadata.Lineage_Location_Tag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Location.Address").
                property("Location.Address.Street", pg_location_street).
                property("Location.Address.City", pg_location_city).
                property("Location.Address.State", pg_location_state).
                property("Location.Address.Post_Code", pg_location_postcode).next()





        g.addE("Uses_Email").from(person).to(email).next()
        g.addE("Has_Credential").from(person).to(credential).next()
        g.addE("Has_Id_Card").from(person).to(idCard).next()
        g.addE("Lives").from(person).to(location).next()
        trans.commit()

    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

def dumpData(LinkedList<Map<String, String>> listOfMaps) {

    StringBuilder strBuilder = new StringBuilder()
    for (Map<String, String> item in listOfMaps) {

        strBuilder.append(item.toString())

    }
    return strBuilder.toString()
}


def addCampaignAwarenessBulk(graph, g, LinkedList<Map<String, String>> listOfMaps) {

    def metadataCreateDate = new Date()
    def metadataUpdateDate = new Date()
    def trans = graph.tx()
    try {
        trans.open()


        awarenessCampaign = g.V().has("Metadata.Type", "Object.Awareness_Campaign")

        if (awarenessCampaign.hasNext()) {
            awarenessCampaign = awarenessCampaign.next()
        } else {
            awarenessCampaign = g.addV("Object.Awareness_Campaign").
                    property("Metadata.Controller", "Controller").
                    property("Metadata.Processor", "Processor").
                    property("Metadata.Lineage", "https://trainingcourses.com").
                    property("Metadata.Redaction", "/data/protection/officer").
                    property("Metadata.Version", 1).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", "new").
                    property("Metadata.GDPR_Status", "n/a").
                    property("Metadata.Lineage_Server_Tag", "AWS_AAA").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type", "Object.Awareness_Campaign").
                    property("Object.Awareness_Campaign.Description", "GDPR Training Course Winter 2017").
                    property("Object.Awareness_Campaign.URL", "https://trainingcourses.com").
                    property("Object.Awareness_Campaign.Start_Date", metadataCreateDate).
                    property("Object.Awareness_Campaign.Stop_Date", metadataUpdateDate).
                    next()
        }




        for (Map<String, String> item in listOfMaps) {


            try {
                dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person.Employee").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person.Employee").
                    property("Person.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Last_Name", item.get("pg_name_last")).
                    property("Person.Gender", item.get("pg_gender")).
                    property("Person.Nationality", item.get("pg_nat")).
                    property("Person.Date_Of_Birth", dob).
                    property("Person.Title", item.get("pg_name_title")).next()


            def probabilities = [
                    new Pair<String, Double>("Link Sent", (Double) 25.0),
                    new Pair<String, Double>("Reminder Sent", (Double) 30.0),
                    new Pair<String, Double>("Failed", (Double) 3.0),
                    new Pair<String, Double>("Passed", (Double) 60.0),
                    new Pair<String, Double>("Second  Reminder", (Double) 45.0)]
            def distribution = new EnumeratedDistribution<String>(probabilities.asList())



            trainingEvent = g.addV("Event.Training").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Event.Training_Event").
                    property("Event.Training.Status", distribution.sample()).next()


            g.addE("Event.Training.Awareness_Campaign")
                    .from(trainingEvent)
                    .to(awarenessCampaign)
                    .property("Metadata.Type", "Event.Training.Awareness_Campaign")
                    .property("Metadata.Create_Date", metadataCreateDate)
                    .next()

            g.addE("Event.Training.Person")
                    .from(trainingEvent)
                    .to(person)
                    .property("Metadata.Type", "Event.Training.Awareness_Campaign")
                    .property("Metadata.Create_Date", metadataCreateDate)
                    .next()


        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}


def addRandomUserDataBulk(graph, g, LinkedList<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        trans.open()


        for (Map<String, String> item in listOfMaps) {


            try {
                dob = new SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person").
                    property("Person.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Last_Name", item.get("pg_name_last")).
                    property("Person.Gender", item.get("pg_gender")).
                    property("Person.Nationality", item.get("pg_nat")).
                    property("Person.Date_Of_Birth", dob).
                    property("Person.Title", item.get("pg_name_title")).next()


            email = g.addV("Object.Email_Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Email_Address").
                    property("Object.Email_Address.Email", item.get("pg_email")).next()


            credential = g.addV("Object.Credential").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Credential").
                    property("Object.Credential.User_Id", item.get("pg_login_username")).
                    property("Object.Credential.Login_SHA256", item.get("pg_login_sha256")).next()

            idCard = g.addV("Object.Identity_Card").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Identity_Card").
                    property("Object.Identity_Card.Id_Name", item.get("pg_id_name")).
                    property("Object.Identity_Card.Id_Value", item.get("pg_id_value")).next()


            location = g.addV("Location.Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Location.Address").
                    property("Location.Address.Street", item.get("pg_location_street")).
                    property("Location.Address.City", item.get("pg_location_city")).
                    property("Location.Address.State", item.get("pg_location_state")).
                    property("Location.Address.Post_Code", item.get("pg_location_postcode")).next()




            g.addE("Uses_Email").from(person).to(email).next()
            g.addE("Has_Credential").from(person).to(credential).next()
            g.addE("Has_Id_Card").from(person).to(idCard).next()
            g.addE("Lives").from(person).to(location).next()
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
    objectDataProceduresLabel = createVertexLabel(mgmt, "Object.Data_Procedures")

    objectDataProceduresType = createProp(mgmt, "Object.Data_Procedures.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresProperty = createProp(mgmt, "Object.Data_Procedures.Property", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteURL = createProp(mgmt, "Object.Data_Procedures.Delete_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateURL = createProp(mgmt, "Object.Data_Procedures.Update_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteMechanism = createProp(mgmt, "Object.Data_Procedures.Delete_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateMechanism = createProp(mgmt, "Object.Data_Procedures.Update_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectDataProceduresTypeIdx = createMixedIdx(mgmt, "objectDataProceduresTypeIdx", objectDataProceduresType)
    objectDataProceduresPropertyIdx = createMixedIdx(mgmt, "objectDataProceduresPropertyIdx", objectDataProceduresProperty)
    objectDataProceduresDeleteURLIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteURLIdx", objectDataProceduresDeleteURL)
    objectDataProceduresUpdateURLIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateURLIdx", objectDataProceduresUpdateURL)
    objectDataProceduresDeleteMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteMechanismIdx", objectDataProceduresDeleteMechanism)
    objectDataProceduresUpdateMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateMechanismIdx", objectDataProceduresUpdateMechanism)

 */
def addRandomDataProcedures(graph, g) {

    def trans = graph.tx()
    try {
        trans.open();

        def randVal = new Random();
        def randValK = randVal.nextInt(5);

        def oneWeekInMs = 3600000 * 24 * 7;
        def eighteenWeeks = oneWeekInMs * 18;



        def probabilitiesRequestType = [
                new Pair<String, Double>("Automated", (Double) 97.0),
                new Pair<String, Double>("Manual", (Double) 3.0)];
        def distributionRequestType = new EnumeratedDistribution<String>(probabilitiesRequestType.asList());


        def types = new String[6];

        types[0] = "Person";
        types[1] = "Object.Email_Address";
        types[2] = "Object.Credential";
        types[3] = "Object.Identity_Card";
        types[4] = "Event.Consent";
        types[5] = "Event.Subject_Access_Request";


        for (def i = 0; i < types.length; i++){
            def typeStr = types[i];

            props = g.V().has('Metadata.Type',typeStr).range(0,1).properties().key().findAll{
                (!it.startsWith('Metadata'))
            }


            for (def j = 0; j < props.size(); j++) {


                def propStr = props.get(j);

                def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
                def updateMillis = createMillis + (long) (randVal.nextDouble() * eighteenWeeks)
                def metadataCreateDate = new Date((long) createMillis)
                def metadataUpdateDate = new Date((long) updateMillis)

                def dp = g.addV("Object.Data_Procedures").
                        property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                        property("Metadata.Version", "1").
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.Data_Procedures").
                        property("Object.Data_Procedures.Type", typeStr.replaceAll('[_.]',' ')).
                        property("Object.Data_Procedures.Property", propStr.replaceAll('[_.]',' ')).
                        property("Object.Data_Procedures.Delete_URL", 'https://api-gateway/delete-'+propStr.toLowerCase()).
                        property("Object.Data_Procedures.Delete_Mechanism", distributionRequestType.sample()).
                        property("Object.Data_Procedures.Update_URL", 'https://api-gateway/update-'+propStr.toLowerCase()).
                        property("Object.Data_Procedures.Update_Mechanism", distributionRequestType.sample()).
                        next()

                for (def k = 0; k < randValK; k++) {
                    def pia = g.V().has('Metadata.Type', 'Object.Privacy_Impact_Assessment').order().by(shuffle).range(0, 1).next()
                    g.addE("Has_Data_Procedures").from(pia).to(dp).next()
                }
            }


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}





def addRandomSARs(graph, g) {

    def trans = graph.tx()
    try {
        trans.open()

        def randVal = new Random()
        def randVal1 = randVal.nextInt(3000)

        def oneWeekInMs = 3600000 * 24 * 7
        def eighteenWeeks = oneWeekInMs * 18


        def probabilitiesStatus = [
                new Pair<String, Double>("New", (Double) 25.0),
                new Pair<String, Double>("Acknowledged", (Double) 30.0),
                new Pair<String, Double>("Reviewed ", (Double) 3.0),
                new Pair<String, Double>("Denied", (Double) 60.0),
                new Pair<String, Double>("Completed", (Double) 45.0)]
        def distributionStatus = new EnumeratedDistribution<String>(probabilitiesStatus.asList())

        def probabilitiesRequestType = [
                new Pair<String, Double>("Read", (Double) 90.0),
                new Pair<String, Double>("Update", (Double) 30.0),
                new Pair<String, Double>("Delete ", (Double) 23.0)]
        def distributionRequestType = new EnumeratedDistribution<String>(probabilitiesRequestType.asList())



        for (def i = 0; i < randVal1; i++){

            def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
            def updateMillis =  createMillis +  (long) (randVal.nextDouble() * eighteenWeeks)
            def metadataCreateDate = new Date((long)createMillis)
            def metadataUpdateDate = new Date((long)updateMillis )

            def stat = distributionStatus.sample()
            def sar = g.addV("Event.Subject_Access_Request").
                    property("Metadata.Controller", "ABC INC").
                    property("Metadata.Processor", "ABC INC").
                    property("Metadata.Lineage", "Random generator").
                    property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                    property("Metadata.Version", "1").
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", stat).
                    property("Metadata.GDPR_Status", "SAR").
                    property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                    property("Metadata.Lineage_Location_Tag", "GB").
                    property("Metadata.Type", "Event.Subject_Access_Request").
                    property("Event.Subject_Access_Request.Status", stat).
                    property("Event.Subject_Access_Request.Request_Type", distributionRequestType.sample()).
                    next()


            def employee = g.V().has('Metadata.Type','Person.Employee').order().by(shuffle).range(0,1).next()

            def person = g.V().has('Metadata.Type','Person').order().by(shuffle).range(0,1).next()


            g.addE("Made_SAR_Request").from(person).to(sar).next()
            g.addE("Assigned_SAR_Request").from(employee).to(sar).next()


        }








        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }


}


def addRandomChildUserDataBulk(graph, g, LinkedList<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        trans.open()

        randVal = new Random()

        def oneYearInMs = 3600000 * 24 * 365
        def eighteenYearsInMs = oneYearInMs * 18




        for (Map<String, String> item in listOfMaps) {


            try {

                dob = new Date(System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenYearsInMs))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person").
                    property("Person.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Last_Name", item.get("pg_name_last")).
                    property("Person.Gender", item.get("pg_gender")).
                    property("Person.Nationality", item.get("pg_nat")).
                    property("Person.Date_Of_Birth", dob).
                    property("Person.Title", item.get("pg_name_title")).next()


            email = g.addV("Object.Email_Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Email_Address").
                    property("Object.Email_Address.Email", item.get("pg_email")).next()


            credential = g.addV("Object.Credential").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Credential").
                    property("Object.Credential.User_Id", item.get("pg_login_username")).
                    property("Object.Credential.Login_SHA256", item.get("pg_login_sha256")).next()

            idCard = g.addV("Object.Identity_Card").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Identity_Card").
                    property("Object.Identity_Card.Id_Name", item.get("pg_id_name")).
                    property("Object.Identity_Card.Id_Value", item.get("pg_id_value")).next()


            location = g.addV("Location.Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.Create_Date", metadataCreateDate).
                    property("Metadata.Update_Date", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPR_Status", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.Lineage_Server_Tag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.Lineage_Location_Tag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Location.Address").
                    property("Location.Address.Street", item.get("pg_location_street")).
                    property("Location.Address.City", item.get("pg_location_city")).
                    property("Location.Address.State", item.get("pg_location_state")).
                    property("Location.Address.Post_Code", item.get("pg_location_postcode")).next()





            g.addE("Uses_Email").from(person).to(email).next()
            g.addE("Has_Credential").from(person).to(credential).next()
            g.addE("Has_Id_Card").from(person).to(idCard).next()
            g.addE("Lives").from(person).to(location).next()
        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}


def addDataBulk(graph, g, LinkedList<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        trans.open()

        randVal = new Random()

        def oneYearInMs = 3600000 * 24 * 365
        def eighteenYearsInMs = oneYearInMs * 18


        for (Map<String, String> item in listOfMaps) {

        }
        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }
}

def __addConsentForPrivacyNotice(graph, g, Vertex privNoticeVertex) {

    def randVal = new Random()
    def randVal1 = randVal.nextInt(3000)

    def oneWeekInMs = 3600000 * 24 * 7
    def eighteenWeeks = oneWeekInMs * 18


    def probabilitiesStatus = [
            new Pair<String, Double>("Consent Pending", (Double) 25.0),
            new Pair<String, Double>("Consent", (Double) 30.0),
            new Pair<String, Double>("No Consent ", (Double) 3.0)]
    def distributionStatus = new EnumeratedDistribution<String>(probabilitiesStatus.asList())



    for (def i = 0; i < randVal1; i++){

        def createMillis = System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenWeeks)
        def updateMillis =  createMillis +  (long) (randVal.nextDouble() * eighteenWeeks)
        def metadataCreateDate = new Date((long)createMillis)
        def metadataUpdateDate = new Date((long)updateMillis )

        /*
            orgLabel = createVertexLabel(mgmt, "Event.Consent")

    eventConsentDate = createProp(mgmt, "Event.Consent.Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentStatus = createProp(mgmt, "Event.Consent.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)

         */



        def stat = distributionStatus.sample()
        def consent = g.addV("Event.Consent").
                property("Metadata.Controller", "ABC INC").
                property("Metadata.Processor", "ABC INC").
                property("Metadata.Lineage", "Random generator").
                property("Metadata.Redaction", "/dataprotectionofficer/aaa").
                property("Metadata.Version", "1").
                property("Metadata.Create_Date", metadataCreateDate).
                property("Metadata.Update_Date", metadataUpdateDate).
                property("Metadata.Status", stat).
                property("Metadata.GDPR_Status", "SAR").
                property("Metadata.Lineage_Server_Tag", "AWS EUR1").
                property("Metadata.Lineage_Location_Tag", "GB").
                property("Metadata.Type", "Event.Consent").
                property("Event.Consent.Status", stat).
                property("Event.Consent.Date", metadataUpdateDate).
                next()


//        def employee = g.V().has('Metadata.Type','Person.Employee').order().by(shuffle).range(0,1).next()

        def person = g.V().has('Metadata.Type','Person').order().by(shuffle).range(0,1).next()


        g.addE("Consent").from(person).to(consent).next()
        g.addE("Has_Privacy_Notice").from(consent).to(privNoticeVertex).next()


    }




//    g.V().has("Metadata.Type", "Person").as("people")
//            .addE("Consent").property("Consent.Date", new Date())
//            .from("people").to(privNoticeVertex).next()


}

def __addPrivacyImpactAssessment(graph, g, Vertex privNoticeVertex) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()

//    trans = graph.tx()
//    try {
//        trans.open()


    pia = g.addV("Object.Privacy_Impact_Assessment").
            property("Metadata.Controller", "ABC Inc").
            property("Metadata.Processor", "ABC Inc").
            property("Metadata.Lineage", "oracle://jdbc://oracledb.com").
            property("Metadata.Redaction", "/data/protection/officer").
            property("Metadata.Version", "1").
            property("Metadata.Create_Date", metadataCreateDate).
            property("Metadata.Update_Date", metadataUpdateDate).
            property("Metadata.Status", "New").
            property("Metadata.GDPR_Status", "n/a").
            property("Metadata.Lineage_Server_Tag", "AWS_EUR_HOST3").
            property("Metadata.Lineage_Location_Tag", "GB").
            property("Metadata.Type", "Object.Privacy_Impact_Assessment").
            property("Object.Privacy_Impact_Assessment.Description", "PIA for project xyz.").
            property("Object.Privacy_Impact_Assessment.Start_Date", new Date()).
            property("Object.Privacy_Impact_Assessment.Delivery_Date", new Date(System.currentTimeMillis() + 3600 * 24 * 365)).
            property("Object.Privacy_Impact_Assessment.Risk_To_Individuals", "Low").
            property("Object.Privacy_Impact_Assessment.Intrusion_On_Privacy", "Low").
            property("Object.Privacy_Impact_Assessment.Risk_To_Corporation", "Low").
            property("Object.Privacy_Impact_Assessment.Risk_Of_Reputational_Damage", "Low").
            property("Object.Privacy_Impact_Assessment.Compliance_Check_Passed", "true").
            next()

    /*
        Identifying privacy and related risks
 Record the risks to individuals, including possible intrusions on
privacy where appropriate.
 Assess the corporate risks, including regulatory action,
reputational damage, and loss of public trust.
 Conduct a compliance check against the Data Protection Act
and other relevant legislation.
 Maintain a record of the identified risks.

     */

    g.V().has("Metadata.Type", "Person.Employee").range(0, 10).as("employees").addE("Approved_Compliance_Check").from("employees").to(pia).next()



    g.addE("Has_Privacy_Notice").from(pia).to(privNoticeVertex).next()

//        trans.commit()

//    } catch (Throwable t) {
//        trans.rollback()
//        throw t
//    } finally{
//        trans.close()
//    }
}

/*


 In particular, they should record the date of consent, the method of consent, who obtained consent, and exactly what
information was provided to the person consenting. They should not rely on a bought-in list unless the seller or list
broker can provide these details. Organisations may be asked to produce their records as evidence to demonstrate compliance in the event of a complaint.

Privacy Impact Assessment

Describing information flows
 Explain how information will be obtained, used, and retained –
there may be several options to consider. This step can be
based on, or form part of, a wider project plan.
 This process can help to identify potential ‘function creep’ -
unforeseen or unintended uses of the data (for example data
sharing)

Identifying privacy and related risks
 Record the risks to individuals, including possible intrusions on
privacy where appropriate.
 Assess the corporate risks, including regulatory action,
reputational damage, and loss of public trust.
 Conduct a compliance check against the Data Protection Act
and other relevant legislation.
 Maintain a record of the identified risks.


Identifying and evaluating privacy solutions
 Devise ways to reduce or eliminate privacy risks.
 Assess the costs and benefits of each approach, looking at the
impact on privacy and the effect on the project outcomes.
 Refer back to the privacy risk register until satisfied with the
overall privacy impact.


Signing off and recording the PIA outcomes
 Obtain appropriate signoff within the organisation.
 Produce a PIA report, drawing on material produced earlier
during the PIA.
 Consider publishing the report or other relevant information
about the process.

Integrating the PIA outcomes back into the project plan
 Ensure that the steps recommended by the PIA are
implemented.
 Continue to use the PIA throughout the project lifecycle when
appropriate


Internal Consultation (Stakeholders)

 Project management team
o The team responsible for the overall implementation of
a project will play a central role in the PIA process.
 Data protection officer
If an organisation has a dedicated DPO, they are likely to
have a close link to a PIA. Even if project managers are
responsible for individual PIAs, the DPO will be able to provide
specialist knowledge on privacy issues,
 Engineers, developers and designers
o The people who will be building a product need to have
a clear understanding of how to approach privacy
issues. They will also be able to suggest workable
privacy solutions to the risks which have been identified.
 Information technology (IT)
o Will be able to advise on security risks and solutions.
The role of IT is not limited to security, and might also
include discussions on the usability of any software.
 Procurement
o If the project will require systems or services to be
procured, the needs of the project need to be
established before procurement takes place.
 Potential suppliers and data processors
o If some of the project will be outsourced to a third
party, early engagement will help to understand which
options are available.
 Communications
o A PIA can become a useful part of a project’s
communication strategy. For example, involving
communications colleagues in the PIA can help to
establish a clear message to the public about a project
 Customer-facing roles
o It is important to consult with the people who will have
to use a new system or put a policy into practice. They
will be able to advise on whether the system will work
as intended.
 Corporate governance/compliance
o Colleagues who work on risk management for an
organisation should be able to integrate PIAs into their
work. Other areas of compliance can be included in the
PIA process.
 Researchers, analysts, and statisticians
o Information gathered by a new project may be used to
analysing customer behaviour or for other statistical
purposes. Where relevant, consulting with researchers
can lead to more effective safeguards such as
Anonymisation.
 Senior management
o It will be important to involve those with responsibility
for signing off or approving a project.



External consultation:
Effective external consultations should follow these principles:
 Timely – at the right stage and allow enough time for
responses.
 Clear and proportionate– in scope and focused.
 Reach and representative - ensure those likely to be effected
have a voice.
 Ask objective questions and present realistic options.
 Feedback – ensure that those participating get feedback at
the end of the process.
 */


def addLawfulBasisAndPrivacyNotices(graph, g) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        trans.open()

        def definitions = new String[6]


        definitions[0] = "Processing is necessary for the performance of a contract to which the data subject is party or in order to take steps at the request of the data subject prior to entering into a contract;"
        definitions[1] = "The data subject has given consent to the processing of his or her personal data for one or more specific purposes;"
        definitions[2] = "Processing is necessary for compliance with a legal obligation to which the controller is subject;"
        definitions[3] = "Processing is necessary in order to protect the vital interests of the data subject or of another natural person;"
        definitions[4] = "Processing is necessary for the performance of a task carried out in the public interest or in the exercise of official authority vested in the controller;"
        definitions[5] = "Processing is necessary for the purposes of the legitimate interests pursued by the controller or by a third party, except where such interests are overridden by the interests or fundamental rights and freedoms of the data subject which require protection of personal data, in particular where the data subject is a child."

        def lawfulBasisVertices = new Vertex[6]

        def ilen = definitions.length


        for (i = 0; i < ilen; i++) {


            lawfulBasis1 = g.V().has("Object.Lawful_Basis.Description", definitions[i])

            if (lawfulBasis1.hasNext()) {
                lawfulBasisVertices[i] = lawfulBasis1.next()
            } else {
                lawfulBasisVertices[i] = g.addV("Object.Lawful_Basis").
                        property("Metadata.Lineage", "https://gdpr-info.eu/art-6-gdpr/").
                        property("Metadata.Redaction", "/data/protection/officer").
                        property("Metadata.Version", 1).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", "new").
                        property("Metadata.GDPR_Status", "n/a").
                        property("Metadata.Lineage_Server_Tag", "AWS_AAA").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.Lawful_Basis").
                        property("Object.Lawful_Basis.Id", i).
                        property("Object.Lawful_Basis.Description", definitions[i]).
                        next()
            }

        }

        def privacyNoticeText = new String[2]


        privacyNoticeText[0] = new String('<div><p>Here at XYZ Inc,  we take your privacy seriously and will only use your personal information to administer your account and to provide the products and services you have requested from us.</p><p>However, from time to time we would like to contact you with details of other services we provide. If you consent to us contacting you for this purpose please tick to say how you would like us to contact you:</p><p style="padding-left: 30px;"><strong>Post</strong> ☐    <strong>Email</strong> ☐  <strong>Telephone</strong> ☐  </p><p style="padding-left: 30px;"><strong>Text message</strong> ☐  <strong>Automated call</strong> ☐<span><br/></span></p><p>We would also like to pass your details onto other businesses such as ABC Inc and CDF Inc, so that they can contact you by post with details of services that they provide. If you consent to us passing on your details for that purpose please tick to confirm:</p><p style="padding-left: 30px;"><strong>I agree</strong> ☐</p></div>')


        privacyNoticeText[1] = new String('<article class="content us-content"> <p class="strap">DFG Limited (We or us) are committed to protecting and respecting your privacy</p> <p>This policy (together with our <a href="/about-us/terms-of-use/">terms of use</a> and any other documents referred to in it) sets out the basis on which any personal information we collect from you, or that you provide to us through our website or via one of our call-centres, will be processed by us. Please read the following carefully to understand our views and practices regarding your personal information and how we will treat it. By visiting this website or providing your personal information to one of our call-centre operators, you are accepting and consenting to the practices described in this policy. Please note that this includes consenting to the processing of any sensitive personal information you provide, as described below.</p> <p>We may amend this privacy policy at any time. Any changes we may make will be posted on this page, so please check back frequently. Your continued use of our website(s) after posting will constitute your acceptance of, and agreement to, any changes.</p> <p>For the purpose of the Data Protection Act 1998 (the Act), the data controller is DFG Limited of The Cooperage, 5 Copper Row, London, SE1 2LH. DFG Limited is part of the ZPG Plc group of companies, comprising ZPG Plc and its subsidiaries (collectively “Zoopla” and, individually, a “ZPG Company”).</p> <h2>Information we may collect from you</h2> <p>We may collect and process the following information about you:</p> <ul> <li> <h3>Information you provide to us</h3> <p>You may provide us with information by filling in forms on our website(s) or by corresponding with us by phone, e-mail, live-chat or otherwise. This includes information you provide when you register for an account, search for a price comparison or quote, enter into a contract for the supply of services, enter a competition, promotion or survey and when you report a problem with our site. The personal information you provide may include your name, address, e-mail address and phone number, financial and credit card information and other information about yourself to enable us to provide you with our price comparison services, switching services or other related services. Depending on which products and services you are interested in, you may also need to provide us with certain categories of sensitive personal information, to allow us to provide you with quotes, including, for example, information about criminal convictions (e.g. in connection with a car insurance quote) or health (e.g. for an energy provide to assess whether you have any special requirements). By providing us with this information, you expressly consent to our use of your sensitive personal information in accordance with this privacy policy.</p> </li> <li> <h3>Information we collect about you</h3> <p>When you visit our website(s) we may automatically collect information about your computer, including your IP address, information about your visit, your browsing history, and how you use our website. This information may be combined with other information you provide to us, as described above.</p> </li> <li> <h3>Information we receive from other sources</h3> <p>We may receive information about you if you use any of the other websites we, or our group companies, operate or the other services we provide. We are also working closely with third parties (including, for example, business partners, service providers, advertising networks, analytics providers, and search information providers) and may receive information about you from them. This may be combined with other information you provide to us, as described above.</p> </li> </ul> <h2>How we use your personal information</h2> <p>We use personal information about you in connection with the following purposes:</p> <ul> <li> <p>Fulfiling your requests:</p> <ul> <li>to provide you with the information, products and services that you request from us or another ZPG Company, including providing you with price comparison quotes and switching services;</li> <li>to complete any transaction you are undertaking with us or another ZPG Company;</li> <li>to administer any promotion or competition that you enter via our website(s); and</li> <li>to allow you to participate in interactive features of our service, when you choose to do so.</li> </ul> </li> <li> <p>Marketing:</p> <ul> <li>to provide you with information about other goods and services we or other ZPG Companies offer that are similar to those that you have already purchased or enquired about;</li> <li>to provide you, or permit other ZPG Companies, or selected third parties to provide you, with information about goods or services that may interest you; </li> <li>to measure or understand the effectiveness of advertising we serve to you and others, and to deliver relevant advertising to you, which may be based on your activity on our website(s) or the website of another ZPG Company or third parties\' websites; and </li> <li>to make suggestions and recommendations to you and other users of our site about goods or services that may interest you or them, which may be based on your activity on our website(s) or the website of another ZPG Company or third parties\' websites.</li> </ul> </li> <li> <p>Service Improvements and account management:</p> <ul> <li>to ensure that content from our site is presented in the most effective manner for you and for your computer.</li> <li>to administer our site and for internal business administration and operations, including troubleshooting, data analysis, testing, research, statistical and survey purposes; </li> <li>to notify you about changes to our service and to send you service emails relating to your account; </li> <li>as part of our efforts to keep our site safe and secure; and </li> <li>to manage and operate your account with us. </li> </ul> </li> </ul> <h2>Sharing your personal information</h2> <p>We may share your personal information in connection the purposes described above with any other ZPG Company.</p> <p>We may also share your personal information with third parties in the following circumstances:</p> <ul> <li>Selected energy suppliers, insurers, insurance brokers, credit brokers and other related third parties, to enable us to obtain a quote for you or provide you with other related services. Their use of any data we provide will be governed by their own terms of use and privacy policy and by asking us to provide the relevant comparison switching or other services you are also agreeing to the relevant third parties terms and conditions and privacy policy. Information about the third parties that provide our insurance quote services and their terms and conditions and privacy policies can be found online when you obtain a quote or here. In order to provide you with a quote, some service providers may search external sources such as the electoral roll, county court judgments, bankruptcy registers, UK credit reference agencies and education sources, where relevant, to check and assess the information you provide when asking for a quote. These searches may be recorded by credit agencies but will not affect your credit standing.</li> <li>Business partners, suppliers and sub-contractors for the performance of any contract we enter into with them or you.</li> <li>Advertisers and advertising networks and other third parties that require the data to select and serve relevant adverts to you and others.</li> <li>Analytics and search engine providers that assist us in the improvement and optimisation of our site. Your personal information is generally shared in a form that does not directly identify you.</li> <li>We may also share your personal information with third parties:</li> <li>In the event that we sell or buy any business or assets, in which case we may disclose your personal data to the prospective seller or buyer of such business or assets, along with its professional advisers. If DFG Limited or substantially all of its assets are acquired by a third party, personal information held by it about its customers will be one of the transferred assets.</li> <li>If required in order to obtain professional advice.</li> <li>If we are under a duty to disclose or share your personal information in order to comply with any legal obligation, or in order to enforce or apply our terms of use and other agreements; or to protect the rights, property, or safety of DFG Limited, our customers, or others. This includes exchanging information with other companies and organisations for the purposes of fraud protection.</li> </ul> <p>We may also share with or sell to third parties aggregate information or information that does not personally identify you.</p> <h2>Where we store your personal information</h2> <p>The information that we collect from you may be transferred to, and stored in, a country outside the European Economic Area (EEA). It may also be processed by staff operating outside the EEA who work for us or for one of our suppliers. The laws in some countries may not provide as much legal protection for your information as in the EEA. By submitting your personal information, you agree to this transfer, storing or processing. We will take all steps reasonably necessary to ensure that your data is treated securely and in accordance with this privacy policy.</p> <p>Unfortunately, the transmission of information via the internet is not completely secure. Although we will do our best to protect your personal information, we cannot guarantee the security of your information transmitted to our site; any transmission is at your own risk. Once we have received your information, we will use strict procedures and security features to try to prevent unauthorised access.</p> <h2>Cookies</h2> <p>Our website uses cookies to distinguish you from other users of our website. This helps us to provide you with a good experience when you browse our website and also allows us to improve our site and to provide interest-based advertising. You may adjust the settings on your browser to refuse cookies but some of the services on our website(s) may not work if you do so. For detailed information on the cookies we use and the purposes for which we use them see our Cookie Policy.</p> <h2>Opting Out of Collection of Information by Third Parties</h2> <p>DFG Ltd third-party ad servers, ad network providers, and third-party advertisers (the Ad Providers) may provide you with advertisements that you may see on our website(s) or on other affiliated websites. To improve the relevancy and help measure the effectiveness of such advertisements, the Ad Providers may use cookies, web beacons, clear .gifs or similar technologies. These are used to record users\' activity, such as the pages visited, and to learn what types of information are of most interest to the users. Use of these technologies by Ad Providers is subject to their own privacy policies and is not covered by our privacy policy. For more information regarding the choices you have about these technologies (such as opting-out), click <a href="http://www.youronlinechoices.eu/">here</a>. </p> <h2>Your rights</h2> <p>You have the right to ask us not to process your personal information for marketing purposes. You can exercise your right to prevent such processing by checking or unchecking certain boxes on the forms we use to collect your data. You can also exercise the right at any time by contacting us at <a href="mailto:customer-services@DFG.com">customer-services@DFG.com</a></p> <p>Our site may, from time to time, contain links to and from the websites of our partner networks, advertisers and affiliates. If you follow a link to any of these websites, please note that these websites have their own privacy policies and that we do not accept any responsibility or liability for these policies. Please check these policies before you submit any personal information to these websites.</p> <h2>Access to information</h2> <p>The Act gives you the right to access personal information held about you by DFG Ltd. Your right of access can be exercised in accordance with the Act. Any access request may be subject to a fee of £10 to meet our costs in providing you with details of the information we hold about you.</p> <h2>DFG Insurance Comparison Service Providers</h2> <p>DFG\'s home insurance comparison service is provided by AZD Insurance Services Ltd, registered in England No. 7777777. AZD Insurance Services Ltd has its registered office at Nile Street, Burslem, Stoke-on-Trent ST6 2BA United Kingdom. AZD Insurance Services Ltd is authorised and regulated by the Financial Conduct Authority (FCA) (Registration number: 888888). By using the home insurance system you are also agreeing to AZD\'s <a href="https://www.AZDinsurance.co.uk/disclaimers/terms-of-business/DFG-terms-of-business">Terms &amp; Conditions</a> and <a href="https://www.AZDinsurance.co.uk/disclaimers/DFG-privacy-policy">Privacy Policy</a>.</p> <p>DFG Ltd life insurance comparisons service is provided by YYT Ltd who are authorised and regulated by the Financial Conduct Authority (501109). Registered Office; Cambrian Buildings, Mount Stuart Square, Cardiff CF10 5FL. By using the life insurance system you are also agreeing to YYT <a href="/life-insurance/quote/terms-no-header.aspx">Terms and Conditions</a> and <a href="/life-insurance/quote/privacy-no-header.aspx">Privacy Policy</a>.</p> <p>DFG\'s health insurance comparisons service is provided by YYT Ltd who are authorised and regulated by the Financial Conduct Authority (501109). Registered Office; Cambrian Buildings, Mount Stuart Square, Cardiff CF10 5FL. By using the health insurance system you are also agreeing to YYT <a href="/health-insurance/quote/terms-no-header.aspx">Terms and Conditions</a> and <a href="/health-insurance/quote/privacy-no-header.aspx">Privacy Policy</a>.</p> <p>Updated: 27 April 2017</p> </article>')

        /*

        What information is being collected? - InfoCollected
        Who is collecting it? - WhoIsCollecting
        How is it collected? - HowIsItCollected
          - Electronic Form (Two types: layering, just-in-time)
          - Telephone
          - Paper Form

        Why is it being collected? - WhyIsItCollected
        How will it be used? - HowWillItBeUsed
        Who will it be shared with? - WhoWillItBeShared
        What will be the effect of this on the individuals concerned? - EffectOnIndividuals
        Is the intended use likely to cause individuals to object or complain? - LikelyToComplain
        */

        def privacyNoticeVertices = new Vertex[2]


        ilen = privacyNoticeText.length
        for (i = 0; i < ilen; i++) {


            privacyNotice = g.V().has("Object.Privacy_Notice.Text", privacyNoticeText[i])

            if (privacyNotice.hasNext()) {
                privacyNoticeVertices[i] = privacyNotice.next()
            } else {
                privacyNoticeVertices[i] = g.addV("Object.Privacy_Notice").
                        property("Metadata.Lineage", "https://gdpr-info.eu/art-6-gdpr/").
                        property("Metadata.Redaction", "/data/protection/officer").
                        property("Metadata.Version", 1).
                        property("Metadata.Create_Date", metadataCreateDate).
                        property("Metadata.Update_Date", metadataUpdateDate).
                        property("Metadata.Status", "new").
                        property("Metadata.GDPR_Status", "n/a").
                        property("Metadata.Lineage_Server_Tag", "AWS_AAA").
                        property("Metadata.Lineage_Location_Tag", "GB").
                        property("Metadata.Type", "Object.Privacy_Notice").
                        property("Object.Privacy_Notice.Id", i).
                        property("Object.Privacy_Notice.Description", privacyNoticeText[i]).
                        property("Object.Privacy_Notice.Text", privacyNoticeText[i]).
                        property("Object.Privacy_Notice.Delivery_Date", metadataCreateDate).
                        property("Object.Privacy_Notice.Expiry_Date", metadataUpdateDate).
                        property("Object.Privacy_Notice.URL", "http://www.pontusvision.com").
                        property("Object.Privacy_Notice.Info_Collected", "[name,e-mail,phone]").
                        property("Object.Privacy_Notice.Who_Is_Collecting", "ABC inc").
                        property("Object.Privacy_Notice.How_Is_It_Collected", "Electronic Form - Layering").
                        property("Object.Privacy_Notice.Why_Is_It_Collected", "Marketing campaign 1234").
                        property("Object.Privacy_Notice.How_Will_It_Be_Used", "Used to contact customers for new services offered").
                        property("Object.Privacy_Notice.Who_Will_It_Be_Shared", "CDF ltd").
                        property("Object.Privacy_Notice.Effect_On_Individuals", "receive e-mail communication once per quarter").
                        property("Object.Privacy_Notice.Likely_To_Complain", "no").
                        next()
            }
        }


        def pnId0 = privacyNoticeVertices[0].id()
        def pnId1 = privacyNoticeVertices[1].id()

        g.addE("Has_Lawful_Basis_On").from(g.V(pnId0).next()).to(lawfulBasisVertices[0]).next()
        g.addE("Has_Lawful_Basis_On").from(g.V(pnId0).next()).to(lawfulBasisVertices[1]).next()

        g.addE("Has_Lawful_Basis_On").from(g.V(pnId1).next()).to(lawfulBasisVertices[2]).next()
        g.addE("Has_Lawful_Basis_On").from(g.V(pnId1).next()).to(lawfulBasisVertices[3]).next()


        __addConsentForPrivacyNotice(graph, g, g.V(pnId0).next())
        __addConsentForPrivacyNotice(graph, g, g.V(pnId1).next())


        __addPrivacyImpactAssessment(graph, g, g.V(pnId0).next())
        __addPrivacyImpactAssessment(graph, g, g.V(pnId1).next())



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally {
        trans.close()
    }

}


def createProp(mgmt, keyName, classType, org.janusgraph.core.Cardinality card) {

    if (!mgmt.containsPropertyKey(keyName)) {
        return mgmt.makePropertyKey(keyName).dataType(classType).cardinality(card).make();
    } else {
        return mgmt.getPropertyKey(keyName);
    }
}


def createCompIdx(mgmt, idxName, prop) {
    if (!mgmt.containsGraphIndex(idxName)) {
        return mgmt.buildIndex(idxName, Vertex.class).addKey(prop).buildCompositeIndex();
    } else {
        return mgmt.getGraphIndex(idxName);

    }
}

def createMixedIdx(mgmt, idxName, prop) {
    if (!mgmt.containsGraphIndex(idxName)) {
        return mgmt.buildIndex(idxName, Vertex.class).addKey(prop).buildMixedIndex("search");
    } else {
        return mgmt.getGraphIndex(idxName);

    }


}

def createVertexLabel(mgmt, labelName) {

    if (!mgmt.containsVertexLabel(labelName)) {
        return mgmt.makeVertexLabel(labelName).make()
    }
    return mgmt.getVertexLabel(labelName)
}

def createEdgeLabel(mgmt, labelName) {

    if (!mgmt.containsEdgeLabel(labelName)) {
        return mgmt.makeEdgeLabel(labelName).make()
    }
    return mgmt.getEdgeLabel(labelName)
}


def createIndicesPropsAndLabels(mgmt) {







    metadataController = createProp(mgmt, "Metadata.Controller", String.class, org.janusgraph.core.Cardinality.SET)
    metadataProcessor = createProp(mgmt, "Metadata.Processor", String.class, org.janusgraph.core.Cardinality.SET)
    metadataLineage = createProp(mgmt, "Metadata.Lineage", String.class, org.janusgraph.core.Cardinality.SET)
    metadataRedaction = createProp(mgmt, "Metadata.Redaction", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataVersion = createProp(mgmt, "Metadata.Version", Integer.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataCreateDate = createProp(mgmt, "Metadata.Create_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataUpdateDate = createProp(mgmt, "Metadata.Update_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataStatus = createProp(mgmt, "Metadata.Status", String.class, org.janusgraph.core.Cardinality.SET)
    metadataOrigId = createProp(mgmt, "Metadata.Orig_Id", UUID.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataGDPRStatus = createProp(mgmt, "Metadata.GDPR_Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataLineageServerTag = createProp(mgmt, "Metadata.Lineage_Server_Tag", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataLineageLocationTag = createProp(mgmt, "Metadata.Lineage_Location_Tag", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataType = createProp(mgmt, "Metadata.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)

    metadataLineageServerTagIdx = createCompIdx(mgmt, "metadataLineageServerTagIdx", metadataLineageServerTag)
    metadataLineageServerTagIdx = createCompIdx(mgmt, "metadataTypeIdx", metadataType)
    metadataLineageLocationTagIdx = createCompIdx(mgmt, "metadataLineageLocationTagIdx", metadataLineageLocationTag)
    metadataCreateDateIdx = createMixedIdx(mgmt, "metadataCreateDateMixedIdx", metadataCreateDate)
    metadataUpdateDateIdx = createMixedIdx(mgmt, "metadataUpdateDateMixedIdx", metadataUpdateDate)


    metadataGDPRStatusIdx = createMixedIdx(mgmt, "metadataGDPRStatusMixedIdx", metadataGDPRStatus)



    objectDataProceduresLabel = createVertexLabel(mgmt, "Object.Data_Procedures")

    objectDataProceduresType = createProp(mgmt, "Object.Data_Procedures.Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresProperty = createProp(mgmt, "Object.Data_Procedures.Property", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteURL = createProp(mgmt, "Object.Data_Procedures.Delete_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateURL = createProp(mgmt, "Object.Data_Procedures.Update_URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresDeleteMechanism = createProp(mgmt, "Object.Data_Procedures.Delete_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectDataProceduresUpdateMechanism = createProp(mgmt, "Object.Data_Procedures.Update_Mechanism", String.class, org.janusgraph.core.Cardinality.SINGLE)

    objectDataProceduresTypeIdx = createMixedIdx(mgmt, "objectDataProceduresTypeIdx", objectDataProceduresType)
    objectDataProceduresPropertyIdx = createMixedIdx(mgmt, "objectDataProceduresPropertyIdx", objectDataProceduresProperty)
    objectDataProceduresDeleteURLIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteURLIdx", objectDataProceduresDeleteURL)
    objectDataProceduresUpdateURLIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateURLIdx", objectDataProceduresUpdateURL)
    objectDataProceduresDeleteMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresDeleteMechanismIdx", objectDataProceduresDeleteMechanism)
    objectDataProceduresUpdateMechanismIdx = createMixedIdx(mgmt, "objectDataProceduresUpdateMechanismIdx", objectDataProceduresUpdateMechanism)






    eventTrainingLabel = createVertexLabel(mgmt, "Event.Training")

    eventTrainingStatus = createProp(mgmt, "Event.Training.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    metadataGDPRStatusIdx = createCompIdx(mgmt, "eventTrainingStatusIdx", eventTrainingStatus)
    metadataGDPRStatusIdx = createMixedIdx(mgmt, "eventTrainingStatusMixedIdx", eventTrainingStatus)




    eventSubjAccessReq = createVertexLabel(mgmt, "Event.Subject_Access_Request")

    eventSARStatus = createProp(mgmt, "Event.Subject_Access_Request.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    eventSARStatusIdx = createMixedIdx(mgmt, "eventSARStatusMixedIdx", eventSARStatus)
    eventSARStatusIdx = createCompIdx(mgmt, "eventSARStatusIdx", eventSARStatus)

    eventSARRequestType = createProp(mgmt, "Event.Subject_Access_Request.Request_Type", String.class, org.janusgraph.core.Cardinality.SINGLE)
    eventSARRequestTypeIdx = createMixedIdx(mgmt, "eventSARRequestTypeMixedIdx", eventSARRequestType)
    eventSARRequestTypeIdx = createCompIdx(mgmt, "eventSARRequestTypeIdx", eventSARRequestType)

    createEdgeLabel(mgmt,"Made_SAR_Request")
    createEdgeLabel(mgmt,"Assigned_SAR_Request")





    personLabel = createVertexLabel(mgmt, "Person")
    personDateOfBirth = createProp(mgmt, "Person.Date_Of_Birth", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    personFullName = createProp(mgmt, "Person.Full_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personLastName = createProp(mgmt, "Person.Last_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personGender = createProp(mgmt, "Person.Gender", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personNationality = createProp(mgmt, "Person.Nationality", String.class, org.janusgraph.core.Cardinality.SET)
    personPlaceOfBirth = createProp(mgmt, "Person.Place_Of_Birth", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personReligion = createProp(mgmt, "Person.Religion", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personEthnicity = createProp(mgmt, "Person.Ethnicity", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personMaritalStatus = createProp(mgmt, "Person.Marital_Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personHeight = createProp(mgmt, "Person.Height", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personNameQualifier = createProp(mgmt, "Person.Name_Qualifier", String.class, org.janusgraph.core.Cardinality.SINGLE)
    personTitle = createProp(mgmt, "Person.Title", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "personTitleMixedIdx", personTitle)
    createMixedIdx(mgmt, "personFullNameMixedIdx", personFullName)
    createMixedIdx(mgmt, "personLastNameMixedIdx", personLastName)
    createMixedIdx(mgmt, "personGenderMixedIdx", personGender)
    createMixedIdx(mgmt, "personNationalityMixedIdx", personNationality)
    createMixedIdx(mgmt, "personDateOfBirthMixedIdx", personDateOfBirth)


    objectEmailAddressLabel = createVertexLabel(mgmt, "Object.Email_Address")
    objectEmailAddressEmail = createProp(mgmt, "Object.Email_Address.Email", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectEmailAddressEmailMixedIdx", objectEmailAddressEmail)


    objectCredentialLabel = createVertexLabel(mgmt, "Object.Credential")
    objectCredentialUserId = createProp(mgmt, "Object.Credential.User_Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectCredentialLoginSha256 = createProp(mgmt, "Object.Credential.Login_SHA256", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectCredentialMixedIdx", objectCredentialUserId)


    objectIdentityCardLabel = createVertexLabel(mgmt, "Object.Identity_Card")
    objectIdentityCardIdName = createProp(mgmt, "Object.Identity_Card.Id_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectIdentityCardIdValue = createProp(mgmt, "Object.Identity_Card.Id_Value", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectIdentityCardIdNameMixedIdx", objectIdentityCardIdName)
    createMixedIdx(mgmt, "objectIdentityCardIdNameMixedIdx", objectIdentityCardIdName)

    locationAddressLabel = createVertexLabel(mgmt, "Location.Address")
    locationAddressStreet = createProp(mgmt, "Location.Address.Street", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressCity = createProp(mgmt, "Location.Address.City", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressState = createProp(mgmt, "Location.Address.State", String.class, org.janusgraph.core.Cardinality.SINGLE)
    locationAddressPostCode = createProp(mgmt, "Location.Address.Post_Code", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "locationAddressStreetMixedIdx", locationAddressStreet)
    createMixedIdx(mgmt, "locationAddressCityMixedIdx", locationAddressCity)
    createMixedIdx(mgmt, "locationAddressStateMixedIdx", locationAddressState)
    createMixedIdx(mgmt, "locationAddressPostCodeMixedIdx", locationAddressPostCode)



    objectPrivacyImpactAssessmentLabel = createVertexLabel(mgmt, "Object.Privacy_Impact_Assessment")
    objectPrivacyImpactAssessment0 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment1 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Start_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment2 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Delivery_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment3 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_To_Individuals", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment4 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Intrusion_On_Privacy", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment5 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_To_Corporation", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment6 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Risk_Of_Reputational_Damage", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyImpactAssessment7 = createProp(mgmt, "Object.Privacy_Impact_Assessment.Compliance_Check_Passed", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx0", objectPrivacyImpactAssessment0)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx1", objectPrivacyImpactAssessment1)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx2", objectPrivacyImpactAssessment2)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx3", objectPrivacyImpactAssessment3)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx4", objectPrivacyImpactAssessment4)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx5", objectPrivacyImpactAssessment5)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx6", objectPrivacyImpactAssessment6)
    createMixedIdx(mgmt, "objectPrivacyImpactAssessmentMixedIdx7", objectPrivacyImpactAssessment7)

    objectAwarenessCampaignLabel = createVertexLabel(mgmt, "Object.Awareness_Campaign")
    objectAwarenessCampaignDescription = createProp(mgmt, "Object.Awareness_Campaign.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignURL = createProp(mgmt, "Object.Awareness_Campaign.URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignStart_Date = createProp(mgmt, "Object.Awareness_Campaign.Start_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectAwarenessCampaignStop_Date = createProp(mgmt, "Object.Awareness_Campaign.Stop_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "objectAwarenessCampaignDescriptionMixedIdx", objectAwarenessCampaignDescription)
    createMixedIdx(mgmt, "objectAwarenessCampaignURLMixedIdx", objectAwarenessCampaignURL)
    createMixedIdx(mgmt, "objectAwarenessCampaignStart_DateMixedIdx", objectAwarenessCampaignStart_Date)
    createMixedIdx(mgmt, "objectAwarenessCampaignStop_DateMixedIdx", objectAwarenessCampaignStop_Date)



    objectLawfulBasisLabel = createVertexLabel(mgmt, "Object.Lawful_Basis")
    objectLawfulBasis0 = createProp(mgmt, "Object.Lawful_Basis.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectLawfulBasis1 = createProp(mgmt, "Object.Lawful_Basis.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "objectLawfulBasis0MixedIdx", objectLawfulBasis0)
    createMixedIdx(mgmt, "objectLawfulBasis1MixedIdx", objectLawfulBasis1)

    objectPrivacyNoticeLabel = createVertexLabel(mgmt, "Object.Privacy_Notice")
    objectPrivacyNotice00 = createProp(mgmt, "Object.Privacy_Notice.Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice01 = createProp(mgmt, "Object.Privacy_Notice.Description", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice02 = createProp(mgmt, "Object.Privacy_Notice.Text", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice03 = createProp(mgmt, "Object.Privacy_Notice.Delivery_Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice04 = createProp(mgmt, "Object.Privacy_Notice.Expiry_Date", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice05 = createProp(mgmt, "Object.Privacy_Notice.URL", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice06 = createProp(mgmt, "Object.Privacy_Notice.Info_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice07 = createProp(mgmt, "Object.Privacy_Notice.Who_Is_Collecting", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice08 = createProp(mgmt, "Object.Privacy_Notice.How_Is_It_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice09 = createProp(mgmt, "Object.Privacy_Notice.Why_Is_It_Collected", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice10 = createProp(mgmt, "Object.Privacy_Notice.How_Will_It_Be_Used", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice11 = createProp(mgmt, "Object.Privacy_Notice.Who_Will_It_Be_Shared", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice12 = createProp(mgmt, "Object.Privacy_Notice.Effect_On_Individuals", String.class, org.janusgraph.core.Cardinality.SINGLE)
    objectPrivacyNotice13 = createProp(mgmt, "Object.Privacy_Notice.Likely_To_Complain", String.class, org.janusgraph.core.Cardinality.SINGLE)

    createMixedIdx(mgmt, "objectPrivacyNotice00MixedIdx", objectPrivacyNotice00)
    createMixedIdx(mgmt, "objectPrivacyNotice01MixedIdx", objectPrivacyNotice01)
    createMixedIdx(mgmt, "objectPrivacyNotice02MixedIdx", objectPrivacyNotice02)
    createMixedIdx(mgmt, "objectPrivacyNotice03MixedIdx", objectPrivacyNotice03)
    createMixedIdx(mgmt, "objectPrivacyNotice04MixedIdx", objectPrivacyNotice04)
    createMixedIdx(mgmt, "objectPrivacyNotice05MixedIdx", objectPrivacyNotice05)
    createMixedIdx(mgmt, "objectPrivacyNotice06MixedIdx", objectPrivacyNotice06)
    createMixedIdx(mgmt, "objectPrivacyNotice07MixedIdx", objectPrivacyNotice07)
    createMixedIdx(mgmt, "objectPrivacyNotice08MixedIdx", objectPrivacyNotice08)
    createMixedIdx(mgmt, "objectPrivacyNotice09MixedIdx", objectPrivacyNotice09)
    createMixedIdx(mgmt, "objectPrivacyNotice10MixedIdx", objectPrivacyNotice10)
    createMixedIdx(mgmt, "objectPrivacyNotice11MixedIdx", objectPrivacyNotice11)
    createMixedIdx(mgmt, "objectPrivacyNotice12MixedIdx", objectPrivacyNotice12)
    createMixedIdx(mgmt, "objectPrivacyNotice13MixedIdx", objectPrivacyNotice13)

    personEmployee = createVertexLabel(mgmt, "Person.Employee")

    edgeLabel = createEdgeLabel(mgmt, "Uses_Email")
    edgeLabel = createEdgeLabel(mgmt, "Approved_Compliance_Check")
    edgeLabel = createEdgeLabel(mgmt, "Has_Credential")
    edgeLabel = createEdgeLabel(mgmt, "Has_Id_Card")
    edgeLabel = createEdgeLabel(mgmt, "Lives")
    edgeLabel = createEdgeLabel(mgmt, "Has_Lawful_Basis_On")
    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Awareness_Campaign")
    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Person")

    if (!mgmt.containsGraphIndex("eventTrainingAwareness_CampaignIdx")) {
        try {
            mgmt.buildEdgeIndex(edgeLabel, "eventTrainingAwareness_CampaignIdx", Direction.BOTH, metadataType, metadataCreateDate);
        } catch (e) {
        }
    }

    edgeLabel = createEdgeLabel(mgmt, "Event.Training.Person")

    if (!mgmt.containsGraphIndex("eventTrainingPersonIdx")) {
        try {
            mgmt.buildEdgeIndex(edgeLabel, "eventTrainingPersonIdx", Direction.BOTH, metadataType, metadataCreateDate);
        } catch (e) {
        }
    }
    edgeLabel = createEdgeLabel(mgmt, "Consent")
    edgeLabel = createEdgeLabel(mgmt, "Has_Privacy_Notice")

    orgLabel = createVertexLabel(mgmt, "Organisation")
    orgRegNumber = createProp(mgmt, "Organisation.Registration_Number", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgType = createProp(mgmt, "Organisation.Type", String.class, org.janusgraph.core.Cardinality.SET)
    orgName = createProp(mgmt, "Organisation.Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgShortName = createProp(mgmt, "Organisation.Short_Name", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgTaxId = createProp(mgmt, "Organisation.Tax_Id", String.class, org.janusgraph.core.Cardinality.SINGLE)
    orgSector = createProp(mgmt, "Organisation.Sector", String.class, org.janusgraph.core.Cardinality.SET)

    createMixedIdx(mgmt, "orgNameMixedMixedIdx", orgName)
    createMixedIdx(mgmt, "orgRegNumberMixedIdx", orgRegNumber)


    orgLabel = createVertexLabel(mgmt, "Event.Consent")

    eventConsentDate = createProp(mgmt, "Event.Consent.Date", Date.class, org.janusgraph.core.Cardinality.SINGLE)
    eventConsentStatus = createProp(mgmt, "Event.Consent.Status", String.class, org.janusgraph.core.Cardinality.SINGLE)
    createMixedIdx(mgmt, "eventConsentStatusMixedIdx", eventConsentStatus)
    createMixedIdx(mgmt, "eventConsentDateMixedIdx"  , eventConsentDate)



    mgmt.commit()

}


def  getPropsNonMetadataAsHTMLTableRows(g, vid, String origLabel){
    StringBuilder sb = new StringBuilder();


    g.V(vid).valueMap().next().forEach
    {
        origKey, origVal ->
            String val = origVal.get(0)
            String key = origKey.replaceAll('[_.]',' ')
            if (!key.startsWith('Metadata')){
                sb.append("<tr><td class='tg-yw4l'>" );
                if (origKey.startsWith(origLabel))
                {
                    sb.append(key.substring(origLabel.length() + 1))
                }
                else
                {
                    sb.append(key);
                }
                sb.append("</td><td class='tg-yw4l'>");
                sb.append(val).append("</td></tr>");
            }
    }


    return sb.toString().replaceAll('["]','\\\\"');
}

//mgmt = graph.openManagement()
//try {
//createIndicesPropsAndLabels(graph.openManagement())
//} catch (e){
//e.printStackTrace()
//}

def createAWSGraph() {


    def aws_instances = '{"Reservations":[{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.158","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-d24792ac66bede777","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"f1:7c:f3:88:76:93","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-037906b9","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.158"}],"PrivateDnsName":"ip-10-230-1-158.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-98901771","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.158"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-a4748762a1c47d803","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-3fce850eb8a551d7b","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"primary-data","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"test-pontus-primary-data2","Key":"Name"},{"Value":"pontus","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-2e47fba8e06c5e814","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.91","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-23f2fc8437c5b0a70","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"08:5b:4d:49:63:4f","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-56341130","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.91"}],"PrivateDnsName":"ip-10-230-1-91.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-059017ec","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.91"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-6652bdafe366011cb","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-99cf87c2414bb17bb","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-pontus-primary-data0","Key":"Name"},{"Value":"primary-data","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-76beda8d0c7c78d54","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:24.000Z","PrivateIpAddress":"10.230.28.10","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-b01a8e52","StateTransitionReason":"","InstanceId":"i-588148775d367bd40","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"ClientToken":"","SubnetId":"subnet-5b58edc0","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"57:af:e0:c9:bc:42","SourceDestCheck":true,"VpcId":"vpc-b01a8e52","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-7941ca16","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.10"}],"PrivateDnsName":"ip-10-230-28-10.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-f2de2891","AttachTime":"2017-12-15T13:56:12.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-5b58edc0","PrivateIpAddress":"10.230.28.10"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-cdb4a7c7ce1fcd22e","AttachTime":"2017-12-15T13:56:13.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-fa9a04c8a932afe05","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.24.165","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-f1d0db00fbf1795a1","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-165.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-59ee7a59"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"db:93:ff:f3:76:48","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-broker","NetworkInterfaceId":"eni-23fa2bbb","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-165.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.165"}],"PrivateDnsName":"ip-10-230-24-165.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-f976809a","AttachTime":"2017-12-15T19:23:44.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-59ee7a59"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.165"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-4f938882534b87ac2","AttachTime":"2017-12-15T19:23:44.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-28f2f78166eb44fd9","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"","Code":""},"State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-10-07T14:18:11.000Z","PublicIpAddress":"35.177.16.174","PrivateIpAddress":"10.227.100.199","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"","InstanceId":"i-8e40e8744964284c9","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-e98bf21d","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"b2:3c:a5:4a:a6:0f","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-faaa532b","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","PrivateIpAddress":"10.227.100.199","Primary":true,"Association":{"PublicIp":"35.177.16.174","PublicDnsName":"","IpOwnerId":"amazon"}}],"PrivateDnsName":"ip-10-227-100-199.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-ca73eeaa","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","PrivateIpAddress":"10.227.100.199","SubnetId":"subnet-e98bf21d","Association":{"PublicIp":"35.177.16.174","PublicDnsName":"","IpOwnerId":"amazon"}}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-d17f189e1e9b86893","AttachTime":"2017-08-07T19:23:10.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"bastion-server","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev-pontus-bastion-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-2aca8439372d5f0aa","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.30.4","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-02e7f84d","StateTransitionReason":"","InstanceId":"i-ff46e1a3ae996c9f8","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ext-logging-syslog","GroupId":"sg-652e8012"}],"ClientToken":"","SubnetId":"subnet-e0a83226","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"45:e6:bf:51:01:17","SourceDestCheck":true,"VpcId":"vpc-02e7f84d","Description":"private ip address for abc alpha dev stack ext-logging syslog","NetworkInterfaceId":"eni-8ea68414","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.4"}],"PrivateDnsName":"ip-10-230-30-4.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-a1d026c2","AttachTime":"2017-12-15T13:56:16.000Z"},"Groups":[{"GroupName":"abc-alpha-ext-logging-syslog","GroupId":"sg-652e8012"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-e0a83226","PrivateIpAddress":"10.230.30.4"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-3a18a09582a52ab00","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ext-logging-syslog-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-a8fff8a470ff13c7f","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.115","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-88a8318cb29ea6895","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"da:4e:f1:18:34:c5","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-e8082f2b","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.115"}],"PrivateDnsName":"ip-10-230-1-115.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-489116a1","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.115"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-68268cc11171faf56","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-e5853186fec707d25","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"primary-ambari-server","Key":"Type"},{"Value":"test","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"test-pontus-primary-ambari-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-a5c5cca1608c3a432","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.207","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-a17f4c60ba45da5ef","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"40:9a:61:52:09:9e","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-35b2f375","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.207"}],"PrivateDnsName":"ip-10-230-1-207.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5c9215b5","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.207"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-32fcd261c60375cb6","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-c9777ade8f63bf8af","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test","Key":"Environment"},{"Value":"secondary-master","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"test-pontus-secondary-master0","Key":"Name"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-2bd401672b327e791","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-12-06T10:22:47.000Z","PrivateIpAddress":"10.230.0.57","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-0274bb3d","StateTransitionReason":"User initiated (2017-12-22 15:55:04 GMT)","InstanceId":"i-da62dd3263625bcd5","EnaSupport":false,"ImageId":"ami-7876cb3e","PrivateDnsName":"ip-10-230-0-57.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"default","GroupId":"sg-d06f8d5a"}],"ClientToken":"","SubnetId":"subnet-3b2ce6a5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"1a:6f:12:c5:16:27","SourceDestCheck":true,"VpcId":"vpc-0274bb3d","Description":"Primary network interface","NetworkInterfaceId":"eni-d5632829","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.57"}],"SubnetId":"subnet-3b2ce6a5","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e452b287","AttachTime":"2017-12-06T10:22:47.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-d06f8d5a"}],"Ipv6Addresses":[],"OwnerId":"524174466850","PrivateIpAddress":"10.230.0.57"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-708fa021978c748df","AttachTime":"2017-12-06T10:22:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":0},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-12-06T10:22:47.000Z","PrivateIpAddress":"10.230.0.35","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-0274bb3d","StateTransitionReason":"User initiated (2017-12-22 15:55:04 GMT)","InstanceId":"i-4b009f9bb36069f38","EnaSupport":false,"ImageId":"ami-7876cb3e","PrivateDnsName":"ip-10-230-0-35.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"default","GroupId":"sg-d06f8d5a"}],"ClientToken":"","SubnetId":"subnet-3b2ce6a5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"67:49:fc:bd:3a:57","SourceDestCheck":true,"VpcId":"vpc-0274bb3d","Description":"Primary network interface","NetworkInterfaceId":"eni-d7bee226","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.35"}],"SubnetId":"subnet-3b2ce6a5","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e552b286","AttachTime":"2017-12-06T10:22:47.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-d06f8d5a"}],"Ipv6Addresses":[],"OwnerId":"524174466850","PrivateIpAddress":"10.230.0.35"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-fca8c8b10111b4406","AttachTime":"2017-12-06T10:22:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":1}],"ReservationId":"r-f9f93ccfd482bea18","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-09T14:57:23.000Z","PrivateIpAddress":"10.229.101.56","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-4f8718eb","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-ae839dbc4e7c1bda4","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"logging-pontus-sg","GroupId":"sg-8646cd6e"}],"ClientToken":"","SubnetId":"subnet-dbf83461","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"3d:44:91:d0:3e:b0","SourceDestCheck":true,"VpcId":"vpc-4f8718eb","Description":"","NetworkInterfaceId":"eni-df97237a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.229.101.56"}],"PrivateDnsName":"ip-10-229-101-56.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7c30351c","AttachTime":"2017-10-09T14:57:23.000Z"},"Groups":[{"GroupName":"logging-pontus-sg","GroupId":"sg-8646cd6e"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-dbf83461","PrivateIpAddress":"10.229.101.56"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-675d5a9e039fc45e7","AttachTime":"2017-10-09T14:57:23.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-6c319ba3b017fb73f","AttachTime":"2017-10-09T14:58:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"logging-pontus-secondary-master0","Key":"Name"},{"Value":"pontus","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"logging","Key":"Environment"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"secondary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-82a4a27b5754ea6df","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.77","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-87de6dd820cc018c5","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"03:90:f4:44:84:f1","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-7ce65a67","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.77"}],"PrivateDnsName":"ip-10-227-101-77.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0772ef67","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.77"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-11c3d8ea0537f90ce","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-90a009a0648fbcc74","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"primary-master","Key":"Type"},{"Value":"dev-pontus-primary-master0","Key":"Name"},{"Value":"dev_pontus_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-944964284fef0f2d9","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.24.6","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-d32cbb0e9150c8e7a","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-6.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-security","GroupId":"sg-8e557eac"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"b8:57:b5:c4:29:e0","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-security","NetworkInterfaceId":"eni-89f917be","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-6.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.6"}],"PrivateDnsName":"ip-10-230-24-6.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-75768016","AttachTime":"2017-12-15T19:23:42.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-security","GroupId":"sg-8e557eac"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.6"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-3c66c35ae9ec92b54","AttachTime":"2017-12-15T19:23:42.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-9c0e1bc10a1a0cc92","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.28.59","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-b01a8e52","StateTransitionReason":"","InstanceId":"i-3568ddf7ffa7207ea","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"ClientToken":"","SubnetId":"subnet-5b58edc0","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"f0:31:a5:7a:30:25","SourceDestCheck":true,"VpcId":"vpc-b01a8e52","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-24d3a732","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.59"}],"PrivateDnsName":"ip-10-230-28-59.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-65de2806","AttachTime":"2017-12-15T13:56:17.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-5b58edc0","PrivateIpAddress":"10.230.28.59"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-1189ada9e542f5340","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0f7b885e5c7410b33","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:09.000Z","PrivateIpAddress":"10.227.101.102","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-c9c0ae59511eff5e6","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"32:bc:05:b1:44:d6","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-c165e9b2","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.102"}],"PrivateDnsName":"ip-10-227-101-102.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6173ee01","AttachTime":"2017-08-07T19:23:09.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.102"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-ffc711429cac879a4","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-f96543f99e8447169","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-pontus-primary-data2","Key":"Name"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"primary-data","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-0fa5e44dffe000273","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.49","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-8073497ae4829b2d6","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"65:05:7b:58:17:7d","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-a616adb6","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.49"}],"PrivateDnsName":"ip-10-227-101-49.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-c973eea9","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.49"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-943c00d44d0439808","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-cb4dca63c2811fd56","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-pontus-primary-data0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"primary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-12e87148d6795c93d","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","PrivateIpAddress":"10.230.24.117","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-ef7e4dc3bc2d77a12","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-117.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"f6:95:a4:63:b3:cc","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-8b52f669","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-117.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.117"}],"PrivateDnsName":"ip-10-230-24-117.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-d309ffb0","AttachTime":"2017-12-15T19:24:21.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.117"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-53b25b54f7411f220","AttachTime":"2017-12-15T19:24:21.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-c27a298a9978b3d5f","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-11-30T14:15:42.000Z","PrivateIpAddress":"10.230.1.68","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-db9db554","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-486d04e784f349e82","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-230-1-68.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-access-pontus-sg","GroupId":"sg-458e4c4b"}],"ClientToken":"","SubnetId":"subnet-45ec7af9","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"a6:55:4a:0f:54:3b","SourceDestCheck":true,"VpcId":"vpc-db9db554","Description":"Primary network interface","NetworkInterfaceId":"eni-631a78f8","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.1.68"}],"SubnetId":"subnet-45ec7af9","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7d4f541d","AttachTime":"2017-11-30T14:15:42.000Z"},"Groups":[{"GroupName":"dev-access-pontus-sg","GroupId":"sg-458e4c4b"}],"Ipv6Addresses":[],"OwnerId":"524174466850","PrivateIpAddress":"10.230.1.68"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-24539cf5f17d2a88e","AttachTime":"2017-11-30T14:15:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-access-pontus-bastion-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-f937c366639f0a0f2","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.31.4","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-c3da6021","StateTransitionReason":"","InstanceId":"i-b3cda87eb9a61918d","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-int-logging-syslog","GroupId":"sg-eb4d9642"}],"ClientToken":"","SubnetId":"subnet-0a892a0c","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"25:a4:40:b1:a1:ba","SourceDestCheck":true,"VpcId":"vpc-c3da6021","Description":"private ip address for abc alpha dev stack int-logging syslog","NetworkInterfaceId":"eni-7bf536be","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.31.4"}],"PrivateDnsName":"ip-10-230-31-4.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-32d12751","AttachTime":"2017-12-15T13:56:16.000Z"},"Groups":[{"GroupName":"abc-alpha-int-logging-syslog","GroupId":"sg-eb4d9642"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-0a892a0c","PrivateIpAddress":"10.230.31.4"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-20a86fc15a1b1273e","AttachTime":"2017-12-15T13:56:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-int-logging-syslog-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-40210f27614f2cdce","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:31.000Z","PrivateIpAddress":"10.227.101.30","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-47aca74944b1f7685","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"e0:61:a4:d5:e3:fa","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-5171918a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.30"}],"PrivateDnsName":"ip-10-227-101-30.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8173eee1","AttachTime":"2017-08-07T19:23:31.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.30"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-1898f19d2b607a88d","AttachTime":"2017-08-07T19:23:32.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-24563dc88383fa4cb","AttachTime":"2017-08-07T19:26:20.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"primary-data","Key":"Type"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"dev","Key":"Environment"},{"Value":"dev-pontus-primary-data1","Key":"Name"},{"Value":"pontus","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"}],"AmiLaunchIndex":0}],"ReservationId":"r-687a801e7a76484ef","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-16T13:09:26.000Z","PrivateIpAddress":"10.228.101.50","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-10-24 10:21:46 GMT)","InstanceId":"i-12325e01f3b77f2e3","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"c2:25:f6:7b:4c:c3","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"","NetworkInterfaceId":"eni-4f4745bc","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.50"}],"PrivateDnsName":"ip-10-228-101-50.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-12d2eb72","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.50"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-0fd63d30d484825fb","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-aee64de65976e72d4","AttachTime":"2017-10-06T22:14:16.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"},{"Value":"ingest","Key":"Environment"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"ingest-pontus-secondary-data1","Key":"Name"},{"Value":"pontus","Key":"Project"}],"AmiLaunchIndex":0}],"ReservationId":"r-e5437fc7e055a2a81","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-30T14:41:12.000Z","PrivateIpAddress":"10.230.0.39","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-0274bb3d","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-f47834f634685d197","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-230-0-39.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"launch-wizard-2","GroupId":"sg-5dcd71ec"}],"ClientToken":"","SubnetId":"subnet-3b2ce6a5","InstanceType":"m4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"24:51:1c:03:a5:e5","SourceDestCheck":true,"VpcId":"vpc-0274bb3d","Description":"Primary network interface","NetworkInterfaceId":"eni-02c2cb06","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.39"}],"SubnetId":"subnet-3b2ce6a5","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-e3455e83","AttachTime":"2017-11-30T14:41:12.000Z"},"Groups":[{"GroupName":"launch-wizard-2","GroupId":"sg-5dcd71ec"}],"Ipv6Addresses":[{"Ipv6Address":"2a05:d01c:a43:ec00:1188:1033:49d0:8592"}],"OwnerId":"524174466850","PrivateIpAddress":"10.230.0.39"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-6ac30ad84af28bd0d","AttachTime":"2017-11-30T14:41:12.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-ci-pontus-ci-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-9db5ae7ef3f6f353d","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.253","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-ecbd96c48a1ef9875","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"8c:7e:87:b1:1d:ea","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-4dd50ff1","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.253"}],"PrivateDnsName":"ip-10-227-101-253.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6273ee02","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.253"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-4288db19cf6ccd247","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-cbfddfb6b22cc30fe","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"primary-ambari-server","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"dev-pontus-primary-ambari-server0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-23cd5fa086ecab02a","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-10-06T22:12:55.000Z","PrivateIpAddress":"10.228.100.72","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-10-06 22:53:17 GMT)","InstanceId":"i-6913a3d42064e7d15","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-676a6374","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"31:50:45:d7:81:80","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"","NetworkInterfaceId":"eni-87476444","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.100.72"}],"PrivateDnsName":"ip-10-228-100-72.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-52d2eb32","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-676a6374","PrivateIpAddress":"10.228.100.72"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-9e1fad4659a5ef9bd","AttachTime":"2017-10-06T22:12:55.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"ingest-pontus-bastion-server0","Key":"Name"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"bastion-server","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"ingest","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-6a50de703be303eb5","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:51.000Z","PrivateIpAddress":"10.230.1.58","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-8194a7ca513c91cdf","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"ff:f3:62:13:f7:62","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-e11249cc","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.58"}],"PrivateDnsName":"ip-10-230-1-58.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-509215b9","AttachTime":"2017-11-16T15:44:51.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.58"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-751e64f37ef3bafdd","AttachTime":"2017-11-16T15:44:51.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-603ad90ad0d61a1d7","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-pontus-primary-master0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"test","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"primary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-e44604d4da274ba7d","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","PrivateIpAddress":"10.230.30.141","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-d1a91b4f","StateTransitionReason":"","InstanceId":"i-49330847ecac200a6","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-usrdb-keycloak","GroupId":"sg-33a2936a"}],"ClientToken":"","SubnetId":"subnet-f8c64288","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"a2:6e:fb:c5:4f:f7","SourceDestCheck":true,"VpcId":"vpc-d1a91b4f","Description":"private ip address for abc alpha dev stack usrdb keycloak","NetworkInterfaceId":"eni-63863f3d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.141"}],"PrivateDnsName":"ip-10-230-30-141.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-eed1278d","AttachTime":"2017-12-15T13:56:02.000Z"},"Groups":[{"GroupName":"abc-alpha-usrdb-keycloak","GroupId":"sg-33a2936a"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-f8c64288","PrivateIpAddress":"10.230.30.141"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-4988a41d4cfc8123e","AttachTime":"2017-12-15T13:56:03.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-usrdb-keycloak-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-a3c9ca2def82471aa","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-22T11:34:57.000Z","PrivateIpAddress":"10.230.24.73","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-d2de141888317b541","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-73.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary_database","GroupId":"sg-33fcac39"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"e7:5c:4a:a0:82:e2","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary_database","NetworkInterfaceId":"eni-5b9decc7","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-73.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.73"}],"PrivateDnsName":"ip-10-230-24-73.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-6988ce80","AttachTime":"2017-12-22T11:34:57.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary_database","GroupId":"sg-33fcac39"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.73"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-07607f37be07bcca6","AttachTime":"2017-12-22T11:34:58.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-bc40f1f5f6b997328","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:29.000Z","PrivateIpAddress":"10.230.24.7","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-953d870f626f9bd54","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-7.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"c1:25:7c:c1:68:ff","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-c0ba099f","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-7.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.7"}],"PrivateDnsName":"ip-10-230-24-7.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-4109ff22","AttachTime":"2017-12-15T19:23:47.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.7"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-ed0d48db0d66869b6","AttachTime":"2017-12-15T19:23:47.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-8873ef5c2be9f84a1","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:31.000Z","PrivateIpAddress":"10.227.101.56","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-5b7a353bf7cdf9360","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"01:d0:c1:fb:56:73","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-7bf5c02d","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.56"}],"PrivateDnsName":"ip-10-227-101-56.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-9e70edfe","AttachTime":"2017-08-07T19:23:31.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.56"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-a194b15b7d5eafdbe","AttachTime":"2017-08-07T19:23:32.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-509276a5a92214b73","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"secondary-master","Key":"Type"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"dev-pontus-secondary-master0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-7e7ab546e04ab449f","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-07T14:46:37.000Z","PrivateIpAddress":"10.228.101.42","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-f0f718dcee3bfdcd4","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"b2:d5:26:11:05:d9","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"","NetworkInterfaceId":"eni-3fed24e7","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.42"}],"PrivateDnsName":"ip-10-228-101-42.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0cd2eb6c","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.42"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-bc2a9d4a4bc4f6557","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-f03373137091b9467","AttachTime":"2017-10-06T22:14:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"ingest","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"primary-master","Key":"Type"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"ingest-pontus-primary-flow0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-1d3853981e7babf97","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:51.000Z","PrivateIpAddress":"10.230.1.34","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-5b2f2fb2d1109edc5","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"da:89:4d:dc:ea:36","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-9862b37f","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.34"}],"PrivateDnsName":"ip-10-230-1-34.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-1a9017f3","AttachTime":"2017-11-16T15:44:51.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.34"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-42a19e83030dc2e24","AttachTime":"2017-11-16T15:44:51.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-e2dd800f7292c1c8d","AttachTime":"2017-11-16T15:47:02.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"pontus","Key":"Project"},{"Value":"primary-data","Key":"Type"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test-pontus-primary-data1","Key":"Name"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-3a12eff5d99f31ff0","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.24.71","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-468435a1b5fd171c0","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-71.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"b1:b1:7f:f5:ba:21","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-c85d32b7","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-71.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.71"}],"PrivateDnsName":"ip-10-230-24-71.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-72778111","AttachTime":"2017-12-15T19:23:47.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.71"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-66f4a8b1737bc9ba9","AttachTime":"2017-12-15T19:23:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-986babe8d571fcc3e","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-09T14:57:23.000Z","PrivateIpAddress":"10.229.101.111","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-4f8718eb","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-5c1b69395d9633439","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"logging-pontus-sg","GroupId":"sg-8646cd6e"}],"ClientToken":"","SubnetId":"subnet-dbf83461","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"65:51:72:f4:27:60","SourceDestCheck":true,"VpcId":"vpc-4f8718eb","Description":"","NetworkInterfaceId":"eni-60e02975","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.229.101.111"}],"PrivateDnsName":"ip-10-229-101-111.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-2f30354f","AttachTime":"2017-10-09T14:57:23.000Z"},"Groups":[{"GroupName":"logging-pontus-sg","GroupId":"sg-8646cd6e"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-dbf83461","PrivateIpAddress":"10.229.101.111"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-05ce3c4366719a95e","AttachTime":"2017-10-09T14:57:24.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-90c1826f9e7e112bc","AttachTime":"2017-10-09T14:58:43.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus","Key":"Project"},{"Value":"logging","Key":"Environment"},{"Value":"logging-pontus-primary-master0","Key":"Name"},{"Value":"primary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-a88f1053ff2a12d1c","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.24.216","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-5a12c33753502e3ee","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-216.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-secondary-security","GroupId":"sg-5da639f9"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"68:83:ca:5f:81:cb","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store secondary-security","NetworkInterfaceId":"eni-965da62e","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-216.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.216"}],"PrivateDnsName":"ip-10-230-24-216.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-d709ffb4","AttachTime":"2017-12-15T19:24:30.000Z"},"Groups":[{"GroupName":"abc-alpha-store-secondary-security","GroupId":"sg-5da639f9"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.216"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-9b44668bedf1e2749","AttachTime":"2017-12-15T19:24:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary-security-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-53a1ad07cfc888252","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T16:58:15.000Z","PrivateIpAddress":"10.230.9.191","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-af85a5a1","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-0235b038713fef22f","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-logging-pontus-sg","GroupId":"sg-6e1e40ef"}],"ClientToken":"","SubnetId":"subnet-68255bc4","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"ec:26:0d:e7:a2:84","SourceDestCheck":true,"VpcId":"vpc-af85a5a1","Description":"","NetworkInterfaceId":"eni-070e3e27","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.9.191"}],"PrivateDnsName":"ip-10-230-9-191.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-7fad2a96","AttachTime":"2017-11-16T16:58:15.000Z"},"Groups":[{"GroupName":"test-logging-pontus-sg","GroupId":"sg-6e1e40ef"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-68255bc4","PrivateIpAddress":"10.230.9.191"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-899a86050fd999227","AttachTime":"2017-11-16T16:58:16.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-06ae483731227b6f2","AttachTime":"2017-11-16T16:59:41.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"secondary-syslog","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"test-logging","Key":"Environment"},{"Value":"test-logging-pontus-secondary-syslog0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-0565ef956b0593b4a","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.101","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-d6ead489d0c26c00f","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"ba:eb:6f:2e:e4:f0","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-091e1a6b","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.101"}],"PrivateDnsName":"ip-10-227-101-101.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-6373ee03","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.101"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-e5d87195d1ae8f9e0","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-9742c4738fdcd0f85","AttachTime":"2017-08-07T19:25:49.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-pontus-secondary-data1","Key":"Name"},{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"secondary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-28a031119cc67a414","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.30.136","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-d1a91b4f","StateTransitionReason":"","InstanceId":"i-4f3b6139c50c926fc","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-usrdb-ranger","GroupId":"sg-14ba26e0"}],"ClientToken":"","SubnetId":"subnet-f8c64288","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"9f:5c:1c:aa:9e:b6","SourceDestCheck":true,"VpcId":"vpc-d1a91b4f","Description":"private ip address for abc alpha dev stack usrdb ranger","NetworkInterfaceId":"eni-1849c2d7","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.30.136"}],"PrivateDnsName":"ip-10-230-30-136.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-53de2830","AttachTime":"2017-12-15T13:56:02.000Z"},"Groups":[{"GroupName":"abc-alpha-usrdb-ranger","GroupId":"sg-14ba26e0"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-f8c64288","PrivateIpAddress":"10.230.30.136"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-51abc027bec508ca5","AttachTime":"2017-12-15T13:56:03.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-usrdb-ranger-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-f3c48dcf5e526c154","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-22T11:34:57.000Z","PrivateIpAddress":"10.230.24.231","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-79e2b2befa587df7e","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-231.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-secondary_database","GroupId":"sg-4c38edbb"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"8a:d7:19:4e:44:fa","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store secondary_database","NetworkInterfaceId":"eni-bf289dd4","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-231.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.231"}],"PrivateDnsName":"ip-10-230-24-231.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-dc8bcd35","AttachTime":"2017-12-22T11:34:57.000Z"},"Groups":[{"GroupName":"abc-alpha-store-secondary_database","GroupId":"sg-4c38edbb"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.231"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-5d6e8513d945e8b49","AttachTime":"2017-12-22T11:34:58.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-secondary_database-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-be5e52145e5349809","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-12-07T17:28:53.000Z","PrivateIpAddress":"10.228.101.92","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-fa7db95f3769c1275","EnaSupport":true,"ImageId":"ami-963e38ed","PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"8f:04:8d:59:1f:6c","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"Primary network interface","NetworkInterfaceId":"eni-460c2ae6","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.92"}],"PrivateDnsName":"ip-10-228-101-92.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5d64863e","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.92"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0452afe8593139f3d","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-ca6959925067bb030","AttachTime":"2017-12-11T13:08:42.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-ingest0","Key":"Name"}],"AmiLaunchIndex":2},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-12-07T17:28:53.000Z","PrivateIpAddress":"10.228.101.158","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-ba84755b2958ca93b","EnaSupport":true,"ImageId":"ami-963e38ed","PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"9f:21:62:21:97:71","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"Primary network interface","NetworkInterfaceId":"eni-db96be2a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.158"}],"PrivateDnsName":"ip-10-228-101-158.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5c64863f","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.158"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-43f175b6e94b98ea8","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-6c8537e70c5608fa1","AttachTime":"2017-12-11T13:13:18.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-ingest2","Key":"Name"}],"AmiLaunchIndex":1},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-12-07T17:28:53.000Z","PrivateIpAddress":"10.228.101.171","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-3cde7df9fa702ff70","EnaSupport":true,"ImageId":"ami-963e38ed","PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"0a:45:91:a4:c5:f1","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"Primary network interface","NetworkInterfaceId":"eni-1670f18e","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.171"}],"PrivateDnsName":"ip-10-228-101-171.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-53648630","AttachTime":"2017-12-07T17:28:53.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.171"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-3bdee8679464c7c5f","AttachTime":"2017-12-07T17:28:54.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-a801dc370298fbe1d","AttachTime":"2017-12-11T13:15:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-ingest1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-5c30663b1562cf25b","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-19T13:39:52.000Z","PrivateIpAddress":"10.230.29.23","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-1b7e20d7","StateTransitionReason":"","InstanceId":"i-adcb3243b35b92e92","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ci-jenkins","GroupId":"sg-d389cc4f"}],"ClientToken":"","SubnetId":"subnet-8d01689f","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"8d:4c:6b:8c:76:22","SourceDestCheck":true,"VpcId":"vpc-1b7e20d7","Description":"private ip address for abc alpha dev stack ci jenkins","NetworkInterfaceId":"eni-9f41ffbb","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.29.23"}],"PrivateDnsName":"ip-10-230-29-23.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-26847845","AttachTime":"2017-12-19T13:39:52.000Z"},"Groups":[{"GroupName":"abc-alpha-ci-jenkins","GroupId":"sg-d389cc4f"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-8d01689f","PrivateIpAddress":"10.230.29.23"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-13e7308f500d19f45","AttachTime":"2017-12-19T13:39:52.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ci-jenkins-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-9ccbfd58d101ba9c4","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.13","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-c1181d0cd2ea0d062","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"c6:5c:0e:22:ca:3d","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-d74ed12a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.13"}],"PrivateDnsName":"ip-10-230-1-13.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-99901770","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.13"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-70ed1e3d1a512a99a","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-d6b2b1cbcf6e96551","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"primary-security","Key":"Type"},{"Value":"test-pontus-primary-security0","Key":"Name"},{"Value":"pontus","Key":"Project"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-b5ce2b6cb7ff33df5","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.129","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-6de2e59731cacb75d","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"39:82:6d:46:0c:9f","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-344c1a65","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.129"}],"PrivateDnsName":"ip-10-230-1-129.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5d9215b4","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.129"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-1e7e1adbfb9c3dd60","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-04ec5eeeaa34a3f2e","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"test","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"secondary-master","Key":"Type"},{"Value":"test-pontus-secondary-master1","Key":"Name"},{"Value":"pontus_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-25f91ee0224a641bd","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-24T10:21:31.000Z","PrivateIpAddress":"10.228.101.192","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-a765feee597b962f9","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"ca:73:57:9a:da:a9","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"","NetworkInterfaceId":"eni-0132d3cb","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.192"}],"PrivateDnsName":"ip-10-228-101-192.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-a2d2ebc2","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.192"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-5fb4e1258ed69d77e","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-ac13a9551bb381648","AttachTime":"2017-10-06T22:14:16.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"ingest","Key":"Environment"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"ingest-pontus-primary-flow2","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-48755f47dcfb8faa3","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:50.000Z","PrivateIpAddress":"10.230.24.95","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-575c4bd0037fdca54","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-95.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"19:91:1c:37:76:e6","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-data","NetworkInterfaceId":"eni-d3e4f8e7","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-95.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.95"}],"PrivateDnsName":"ip-10-230-24-95.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-0108fe62","AttachTime":"2017-12-15T19:23:47.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-data","GroupId":"sg-e3d7641d"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.95"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-ee5abaec84e290184","AttachTime":"2017-12-15T19:23:48.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-data-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-ffaa8d2a826764f12","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:23.000Z","PrivateIpAddress":"10.230.24.70","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-a27b49e9d8d77cda4","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-70.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-59ee7a59"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"35:04:f0:90:4f:28","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-broker","NetworkInterfaceId":"eni-16ee9811","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-70.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.70"}],"PrivateDnsName":"ip-10-230-24-70.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-1209ff71","AttachTime":"2017-12-15T19:23:44.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-broker","GroupId":"sg-59ee7a59"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.70"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-8a54deef6882ab27d","AttachTime":"2017-12-15T19:23:44.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-broker-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-14e8f9f01aabcea21","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.128","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-ea2d834a6b45a36f2","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"ef:28:da:a8:73:a5","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-ff5f6d0a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.128"}],"PrivateDnsName":"ip-10-227-101-128.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8a73eeea","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.128"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-3c610f2ab36f004ac","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-88ba02ceb6b27d5dc","AttachTime":"2017-08-07T19:25:49.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"dev-pontus-secondary-data0","Key":"Name"},{"Value":"pontus","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-data","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-b3af5064483ea7d51","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-09-25T15:02:27.000Z","PrivateIpAddress":"10.227.101.44","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-10-06 22:53:17 GMT)","InstanceId":"i-90eee07e003cbd429","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"RJbpa1506351747264","SubnetId":"subnet-2b97f9d5","InstanceType":"d2.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"13:a7:ef:5f:70:70","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"Primary network interface","NetworkInterfaceId":"eni-5870f7cb","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.44"}],"PrivateDnsName":"ip-10-227-101-44.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-5bb8973b","AttachTime":"2017-09-25T15:02:27.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.44"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-efebe3564b7e8f068","AttachTime":"2017-09-25T15:02:28.000Z"}},{"DeviceName":"/dev/sde","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-2a2807625afcac687","AttachTime":"2017-09-25T15:02:28.000Z"}},{"DeviceName":"/dev/sdf","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-6ed1d521fb6351623","AttachTime":"2017-09-25T16:03:20.000Z"}},{"DeviceName":"/dev/sdg","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-0e5e18bbe83a51427","AttachTime":"2017-09-25T16:09:32.000Z"}},{"DeviceName":"/dev/sdh","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-01b3eba4f7b652703","AttachTime":"2017-09-25T16:10:29.000Z"}},{"DeviceName":"/dev/sdi","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-d3f055012d84705b8","AttachTime":"2017-09-25T16:11:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev","Key":"Environment"},{"Value":"dmcrypt","Key":"Name"},{"Value":"DMCRYPT","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-a461cd29134caa5c3","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:23.000Z","PrivateIpAddress":"10.230.28.57","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-b01a8e52","StateTransitionReason":"","InstanceId":"i-55ba6ec621f809b5c","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"ClientToken":"","SubnetId":"subnet-5b58edc0","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"2e:2f:18:4a:5f:1b","SourceDestCheck":true,"VpcId":"vpc-b01a8e52","Description":"private ip address for abc alpha dev stack ingest primary-flow","NetworkInterfaceId":"eni-ac96c839","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.28.57"}],"PrivateDnsName":"ip-10-230-28-57.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-f3de2890","AttachTime":"2017-12-15T13:56:12.000Z"},"Groups":[{"GroupName":"abc-alpha-ingest-primary-flow","GroupId":"sg-573d9993"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-5b58edc0","PrivateIpAddress":"10.230.28.57"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-27d8078d0d52219ba","AttachTime":"2017-12-15T13:56:13.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ingest-primary-flow-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-2c6fb20fada5c5b2e","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:25.000Z","PrivateIpAddress":"10.227.101.142","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"","InstanceId":"i-9e6e090f610a04054","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"c4.4xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"be:4d:a8:83:92:da","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-ea7fea28","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.142"}],"PrivateDnsName":"ip-10-227-101-142.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-9c70edfc","AttachTime":"2017-08-07T19:23:25.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.142"},{"Status":"in-use","MacAddress":"7f:2f:58:de:96:73","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"dev-pontus-test-nic-01","NetworkInterfaceId":"eni-2b56c892","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-104-104.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.104.104"}],"PrivateDnsName":"ip-10-227-104-104.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":1,"DeleteOnTermination":false,"AttachmentId":"eni-attach-498456a0","AttachTime":"2017-10-17T16:04:57.000Z"},"Groups":[{"GroupName":"default","GroupId":"sg-02d8c62c"},{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"},{"GroupName":"dev-pontus-kafka-sg","GroupId":"sg-55ca3719"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-622ef8fe","PrivateIpAddress":"10.227.104.104"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-9788115842c6eee07","AttachTime":"2017-08-07T19:23:26.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-4f415ea93a3f51347","AttachTime":"2017-08-07T19:26:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev-pontus-client-server0","Key":"Name"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"client-server","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"dev","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-edb45989ca414be39","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-11-17T14:28:49.000Z","PrivateIpAddress":"10.230.0.24","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-12-22 15:52:48 GMT)","InstanceId":"i-86450868bd2807f8e","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-50bfd176","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"09:0a:7a:f0:ae:73","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-ba99ca34","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.0.24"}],"PrivateDnsName":"ip-10-230-0-24.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-cf9e1926","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-50bfd176","PrivateIpAddress":"10.230.0.24"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-9be1c20d927e8b3e5","AttachTime":"2017-11-16T15:44:38.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"test","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"test-pontus-bastion-server0","Key":"Name"},{"Value":"bastion-server","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-c3801c1600b13275f","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:22:14.000Z","PrivateIpAddress":"10.230.24.67","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-bfae1e67125b9e591","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-67.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"a9:b8:5c:86:12:70","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-0fc4c318","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-67.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.67"}],"PrivateDnsName":"ip-10-230-24-67.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-d609ffb5","AttachTime":"2017-12-15T19:24:29.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.67"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-b8a600173abd49280","AttachTime":"2017-12-15T19:24:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-a19c985d74fb7d193","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-10-24T10:13:41.000Z","PrivateIpAddress":"10.228.101.187","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-531e0ce7","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-692ea761fe15fae47","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"ClientToken":"","SubnetId":"subnet-ebed3744","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"3d:f9:3c:92:0e:cc","SourceDestCheck":true,"VpcId":"vpc-531e0ce7","Description":"","NetworkInterfaceId":"eni-74218319","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.228.101.187"}],"PrivateDnsName":"ip-10-228-101-187.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-13d2eb73","AttachTime":"2017-10-06T22:12:55.000Z"},"Groups":[{"GroupName":"ingest-pontus-sg","GroupId":"sg-d662e752"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-ebed3744","PrivateIpAddress":"10.228.101.187"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-2cdad84c918efb4c5","AttachTime":"2017-10-06T22:12:55.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-da43b524728d045aa","AttachTime":"2017-10-06T22:14:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus","Key":"Project"},{"Value":"ingest-pontus-primary-flow1","Key":"Name"},{"Value":"ingest","Key":"Environment"},{"Value":"secondary-master","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-2823b3c9ef1636836","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.178","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-f5f6b75a2dbcffaa5","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"97:f1:86:e8:0e:8a","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-eddbb1ad","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.178"}],"PrivateDnsName":"ip-10-227-101-178.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-8973eee9","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.178"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-4d4bcf87cb8043346","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-017b7a39c3d0bab7c","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"pontus","Key":"Project"},{"Value":"dev","Key":"Environment"},{"Value":"primary-security","Key":"Type"},{"Value":"dev-pontus-primary-security0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"dev_pontus_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-e8ec59397b44446a1","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-12-04T15:00:09.000Z","PrivateIpAddress":"10.230.0.29","ProductCodes":[],"VpcId":"vpc-0274bb3d","StateTransitionReason":"User initiated (2017-12-22 15:55:04 GMT)","InstanceId":"i-e9e1ef8bc538ad003","EnaSupport":true,"ImageId":"ami-1b515f9d","PrivateDnsName":"ip-10-230-0-29.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"launch-wizard-3","GroupId":"sg-9faa1f12"}],"ClientToken":"","SubnetId":"subnet-3b2ce6a5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"09:8a:3a:1a:bd:3f","SourceDestCheck":true,"VpcId":"vpc-0274bb3d","Description":"Primary network interface","NetworkInterfaceId":"eni-41608242","PrivateIpAddresses":[{"Primary":true,"PrivateIpAddress":"10.230.0.29"}],"SubnetId":"subnet-3b2ce6a5","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-eed6308d","AttachTime":"2017-12-04T15:00:09.000Z"},"Groups":[{"GroupName":"launch-wizard-3","GroupId":"sg-9faa1f12"}],"Ipv6Addresses":[{"Ipv6Address":"2a05:d01c:a43:ec00:e5a8:6086:491b:df63"}],"OwnerId":"524174466850","PrivateIpAddress":"10.230.0.29"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/xvda","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-793c3d26b6874bddf","AttachTime":"2017-12-04T15:00:09.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","IamInstanceProfile":{"Id":"7U65XJM2K1KB5Y2Y3E5ZF","Arn":"arn:aws:iam::688370094220:instance-profile/terraform"},"RootDeviceName":"/dev/xvda","VirtualizationType":"hvm","AmiLaunchIndex":0}],"ReservationId":"r-6e063697e7da4f53b","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-16T15:21:56.000Z","PrivateIpAddress":"10.230.24.181","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-c167c969ff2446320","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-181.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"d4:fa:59:09:05:bc","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-master","NetworkInterfaceId":"eni-b3438ce3","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-181.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.181"}],"PrivateDnsName":"ip-10-230-24-181.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-4709ff24","AttachTime":"2017-12-15T19:24:29.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-master","GroupId":"sg-1db66803"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.181"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-397b4474d7796107f","AttachTime":"2017-12-15T19:24:29.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-master-az1-3","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-3b3a7673c9ca72837","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T15:44:38.000Z","PrivateIpAddress":"10.230.1.98","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-0d2d48a6701ea247d","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"41:55:09:fb:7b:02","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-1221071a","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.98"}],"PrivateDnsName":"ip-10-230-1-98.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-499116a0","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.98"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-9c6215c22088731e2","AttachTime":"2017-11-16T15:44:38.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-5a41a90d15764ecb3","AttachTime":"2017-11-16T15:47:17.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"pontus","Key":"Project"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"test-pontus-secondary-security0","Key":"Name"},{"Value":"secondary-security","Key":"Type"},{"Value":"test","Key":"Environment"}],"AmiLaunchIndex":0}],"ReservationId":"r-208e8a263e956ecc3","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-19T12:40:04.000Z","PrivateIpAddress":"10.230.24.185","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-705a476e","StateTransitionReason":"","InstanceId":"i-e5c484cf282896b5b","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-24-185.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-store-primary-ambari","GroupId":"sg-9cb42de3"}],"ClientToken":"","SubnetId":"subnet-fb39a4d2","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"cf:ff:cf:54:cc:c1","SourceDestCheck":true,"VpcId":"vpc-705a476e","Description":"private ip address for abc alpha dev stack store primary-ambari","NetworkInterfaceId":"eni-334ff838","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-24-185.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.24.185"}],"PrivateDnsName":"ip-10-230-24-185.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-0b9b6768","AttachTime":"2017-12-19T12:40:04.000Z"},"Groups":[{"GroupName":"abc-alpha-store-primary-ambari","GroupId":"sg-9cb42de3"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-fb39a4d2","PrivateIpAddress":"10.230.24.185"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-876ef442428734182","AttachTime":"2017-12-19T12:40:04.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-store-primary-ambari-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-6d83cc11898d6b648","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-16T16:58:15.000Z","PrivateIpAddress":"10.230.9.130","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-af85a5a1","StateTransitionReason":"User initiated (2017-11-16 17:43:36 GMT)","InstanceId":"i-29dec5e092faf1e3a","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-logging-pontus-sg","GroupId":"sg-6e1e40ef"}],"ClientToken":"","SubnetId":"subnet-68255bc4","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"fc:34:06:87:b5:94","SourceDestCheck":true,"VpcId":"vpc-af85a5a1","Description":"","NetworkInterfaceId":"eni-f85e6379","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.9.130"}],"PrivateDnsName":"ip-10-230-9-130.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0cad2ae5","AttachTime":"2017-11-16T16:58:15.000Z"},"Groups":[{"GroupName":"test-logging-pontus-sg","GroupId":"sg-6e1e40ef"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-68255bc4","PrivateIpAddress":"10.230.9.130"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-81e85afbe6024a237","AttachTime":"2017-11-16T16:58:16.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-37e2f2083de2ad303","AttachTime":"2017-11-16T16:59:41.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"test-logging","Key":"Environment"},{"Value":"pontus_devops","Key":"Owner"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"test-logging-pontus-primary-syslog0","Key":"Name"},{"Value":"pontus","Key":"Project"},{"Value":"primary-syslog","Key":"Type"}],"AmiLaunchIndex":0}],"ReservationId":"r-b956b50b318f30b2b","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","State":{"Code":16,"Name":"running"},"EbsOptimized":false,"LaunchTime":"2017-12-19T13:39:52.000Z","PrivateIpAddress":"10.230.29.28","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-1b7e20d7","StateTransitionReason":"","InstanceId":"i-d7b834171baa42125","EnaSupport":true,"ImageId":"ami-20060558","PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"abc-alpha-ci-artifactory","GroupId":"sg-3064bd6f"}],"ClientToken":"","SubnetId":"subnet-8d01689f","InstanceType":"r4.large","NetworkInterfaces":[{"Status":"in-use","MacAddress":"4e:7e:df:df:7d:49","SourceDestCheck":true,"VpcId":"vpc-1b7e20d7","Description":"private ip address for abc alpha dev stack ci artifactory","NetworkInterfaceId":"eni-6a726bdd","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.29.28"}],"PrivateDnsName":"ip-10-230-29-28.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":false,"AttachmentId":"eni-attach-21847842","AttachTime":"2017-12-19T13:39:52.000Z"},"Groups":[{"GroupName":"abc-alpha-ci-artifactory","GroupId":"sg-3064bd6f"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-8d01689f","PrivateIpAddress":"10.230.29.28"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-a0cba101e5d4044c7","AttachTime":"2017-12-19T13:39:52.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"abc-alpha-dev-ci-artifactory-az1-1","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-7a56de7c727d0e58a","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-08-07T19:23:10.000Z","PrivateIpAddress":"10.227.101.149","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:50:34 GMT)","InstanceId":"i-36a7d8b527aa79180","EnaSupport":false,"ImageId":"ami-1f97a223","PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"r4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"05:e8:5e:3c:fc:a4","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"","NetworkInterfaceId":"eni-fb5ba914","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.149"}],"PrivateDnsName":"ip-10-227-101-149.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-cb73eeab","AttachTime":"2017-08-07T19:23:10.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.149"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-071862f913c090da8","AttachTime":"2017-08-07T19:23:10.000Z"}},{"DeviceName":"/dev/xvdb","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-ca675466aaed7094c","AttachTime":"2017-08-07T19:25:59.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"terraform","Key":"BuiltBy"},{"Value":"secondary-security","Key":"Type"},{"Value":"dev_pontus_devops","Key":"Owner"},{"Value":"dev","Key":"Environment"},{"Value":"pontus","Key":"Project"},{"Value":"dev-pontus-secondary-security0","Key":"Name"}],"AmiLaunchIndex":0}],"ReservationId":"r-6d89d7056ee0c2964","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":true,"LaunchTime":"2017-11-17T14:29:43.000Z","PrivateIpAddress":"10.230.1.221","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-e48c9857","StateTransitionReason":"User initiated (2017-11-17 17:30:25 GMT)","InstanceId":"i-e08a108bb9b71fac3","EnaSupport":false,"ImageId":"ami-1c97a212","PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"ClientToken":"","SubnetId":"subnet-3bd0cb6b","InstanceType":"m4.xlarge","NetworkInterfaces":[{"Status":"in-use","MacAddress":"55:19:88:7f:2a:7d","SourceDestCheck":true,"VpcId":"vpc-e48c9857","Description":"","NetworkInterfaceId":"eni-e41e4abe","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.230.1.221"}],"PrivateDnsName":"ip-10-230-1-221.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-0e9116e7","AttachTime":"2017-11-16T15:44:38.000Z"},"Groups":[{"GroupName":"test-pontus-sg","GroupId":"sg-ba5e241c"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-3bd0cb6b","PrivateIpAddress":"10.230.1.221"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":true,"VolumeId":"vol-a47be863e416631fd","AttachTime":"2017-11-16T15:44:38.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","Tags":[{"Value":"client-server","Key":"Type"},{"Value":"pontus","Key":"Project"},{"Value":"test","Key":"Environment"},{"Value":"test-pontus-client-server0","Key":"Name"},{"Value":"terraform","Key":"BuiltBy"},{"Value":"pontus_devops","Key":"Owner"}],"AmiLaunchIndex":0}],"ReservationId":"r-c1df0cac121cbaad6","Groups":[],"OwnerId":"524174466850"},{"Instances":[{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-12-06T10:30:29.000Z","PrivateIpAddress":"10.227.101.91","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:55:04 GMT)","InstanceId":"i-264229d5a4d447d48","EnaSupport":false,"ImageId":"ami-7876cb3e","PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"a9:72:1d:28:ea:41","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"Primary network interface","NetworkInterfaceId":"eni-ce9d2820","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.91"}],"PrivateDnsName":"ip-10-227-101-91.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-d553b3b6","AttachTime":"2017-12-06T10:30:29.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.91"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-8a41069b2c5024667","AttachTime":"2017-12-06T10:30:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":0},{"Monitoring":{"State":"disabled"},"PublicDnsName":"","StateReason":{"Message":"Client.UserInitiatedShutdown: User initiated shutdown","Code":"Client.UserInitiatedShutdown"},"State":{"Code":80,"Name":"stopped"},"EbsOptimized":false,"LaunchTime":"2017-12-06T10:30:29.000Z","PrivateIpAddress":"10.227.101.27","ProductCodes":[{"ProductCodeId":"awee1f4ac842336951c35aef1","ProductCodeType":"marketplace"}],"VpcId":"vpc-504a50bf","StateTransitionReason":"User initiated (2017-12-22 15:55:04 GMT)","InstanceId":"i-4c823e301dfed6512","EnaSupport":false,"ImageId":"ami-7876cb3e","PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal","KeyName":"dev_deployment_key","SecurityGroups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"ClientToken":"","SubnetId":"subnet-2b97f9d5","InstanceType":"t2.micro","NetworkInterfaces":[{"Status":"in-use","MacAddress":"fb:1e:fc:75:1c:f5","SourceDestCheck":true,"VpcId":"vpc-504a50bf","Description":"Primary network interface","NetworkInterfaceId":"eni-a8837ad6","PrivateIpAddresses":[{"PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal","Primary":true,"PrivateIpAddress":"10.227.101.27"}],"PrivateDnsName":"ip-10-227-101-27.eu-west-2.compute.internal","Attachment":{"Status":"attached","DeviceIndex":0,"DeleteOnTermination":true,"AttachmentId":"eni-attach-d253b3b1","AttachTime":"2017-12-06T10:30:29.000Z"},"Groups":[{"GroupName":"dev-pontus-sg","GroupId":"sg-50df3201"}],"Ipv6Addresses":[],"OwnerId":"524174466850","SubnetId":"subnet-2b97f9d5","PrivateIpAddress":"10.227.101.27"}],"SourceDestCheck":true,"Placement":{"Tenancy":"default","GroupName":"","AvailabilityZone":"eu-west-2a"},"Hypervisor":"xen","BlockDeviceMappings":[{"DeviceName":"/dev/sda1","Ebs":{"Status":"attached","DeleteOnTermination":false,"VolumeId":"vol-3c187eb4fea80ceb5","AttachTime":"2017-12-06T10:30:30.000Z"}}],"Architecture":"x86_64","RootDeviceType":"ebs","RootDeviceName":"/dev/sda1","VirtualizationType":"hvm","AmiLaunchIndex":1}],"ReservationId":"r-56507e6f5a7021410","Groups":[],"OwnerId":"524174466850"}]}'




   JsonPath.parse(aws_instances).read('$.quote.id')

}

