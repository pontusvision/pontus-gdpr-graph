import org.apache.commons.math3.util.Pair
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.janusgraph.core.*
import org.janusgraph.core.schema.*
import static org.janusgraph.core.attribute.Text.*
import java.lang.*
import java.util.*
import java.text.*

def globals = [:]
globals << [g: graph.traversal()]
globals << [mgmt: graph.openManagement()]

def  addRandomUserData(graph, g, pg_dob, pg_metadataController, pg_metadataProcessor, pg_metadataLineage, pg_metadataRedaction, pg_metadataVersion, pg_metadataStatus, pg_metadataGDPRStatus, pg_metadataLineageServerTag, pg_metadataLineageLocationTag, pg_login_username, pg_login_sha256, pg_id_name, pg_id_value, pg_name_first, pg_name_last, pg_gender, pg_nat, pg_name_title, pg_email, pg_location_street, pg_location_city, pg_location_state, pg_location_postcode) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    try {
        dob = new  SimpleDateFormat("yyyy-MM-dd").parse((String) pg_dob)
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
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPRStatus", pg_metadataGDPRStatus).
                property("Metadata.LineageServerTag", pg_metadataLineageServerTag).
                property("Metadata.LineageLocationTag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Person").
                property("Person.FullName", pg_name_first + " " + pg_name_last).
                property("Person.LastName", pg_name_last).
                property("Person.Gender", pg_gender).
                property("Person.Nationality", pg_nat).
                property("Person.DateOfBirth", dob).
                property("Person.Title", pg_name_title).next()

         email = g.addV("Object.EmailAddress").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPRStatus", pg_metadataGDPRStatus).
                property("Metadata.LineageServerTag", pg_metadataLineageServerTag).
                property("Metadata.LineageLocationTag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.EmailAddress").
                property("Object.EmailAddress.Email", pg_email).next()


        credential = g.addV("Object.Credential").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPRStatus", pg_metadataGDPRStatus).
                property("Metadata.LineageServerTag", pg_metadataLineageServerTag).
                property("Metadata.LineageLocationTag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.Credential").
                property("Object.Credential.userId", pg_login_username).
                property("Object.Credential.login.sha256", pg_login_sha256).next()

        idCard = g.addV("Object.IdentityCard").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPRStatus", pg_metadataGDPRStatus).
                property("Metadata.LineageServerTag", pg_metadataLineageServerTag).
                property("Metadata.LineageLocationTag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Object.IdentityCard").
                property("Object.IdentityCard.id_name", pg_id_name).
                property("Object.IdentityCard.id_value", pg_id_value).next()


        location = g.addV("Location.Address").
                property("Metadata.Controller", pg_metadataController).
                property("Metadata.Processor", pg_metadataProcessor).
                property("Metadata.Lineage", pg_metadataLineage).
                property("Metadata.Redaction", pg_metadataRedaction).
                property("Metadata.Version", pg_metadataVersion).
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", pg_metadataStatus).
                property("Metadata.GDPRStatus", pg_metadataGDPRStatus).
                property("Metadata.LineageServerTag", pg_metadataLineageServerTag).
                property("Metadata.LineageLocationTag", pg_metadataLineageLocationTag).
                property("Metadata.Type", "Location.Address").
                property("Location.Address.Street", pg_location_street).
                property("Location.Address.City", pg_location_city).
                property("Location.Address.State", pg_location_state).
                property("Location.Address.PostCode", pg_location_postcode).next()





        g.addE("usesEmail").from(person).to(email).next()
        g.addE("hasCredential").from(person).to(credential).next()
        g.addE("hasIdCard").from(person).to(idCard).next()
        g.addE("lives").from(person).to(location).next()
        trans.commit()

    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally{
        trans.close()
    }
}

def  dumpData(LinkedList<Map<String, String>> listOfMaps) {

    StringBuilder strBuilder = new StringBuilder()
    for (Map<String, String> item in listOfMaps) {

        strBuilder.append(item.toString())

    }
    return strBuilder.toString()
}



def  addCampaignAwarenessBulk(graph, g, LinkedList<Map<String, String>> listOfMaps){

    def metadataCreateDate = new Date()
    def metadataUpdateDate = new Date()
    def  trans = graph.tx()
    try {
        trans.open()


        awarenessCampaign = g.V().has("Metadata.Type","Object.AwarenessCampaign")

        if (awarenessCampaign.hasNext()){
            awarenessCampaign = awarenessCampaign.next()
        }
        else{
            awarenessCampaign =  g.addV("Object.Awareness_Campaign").
                    property("Metadata.Controller", "Controller").
                    property("Metadata.Processor", "Processor").
                    property("Metadata.Lineage","https://trainingcourses.com").
                    property("Metadata.Redaction", "/data/protection/officer" ).
                    property("Metadata.Version", 1).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", "new" ).
                    property("Metadata.GDPRStatus", "n/a" ).
                    property("Metadata.LineageServerTag", "AWS_AAA").
                    property("Metadata.LineageLocationTag", "GB").
                    property("Metadata.Type", "Object.AwarenessCampaign").
                    property("Object.Awareness_Campaign.Description", "GDPR Training Course Winter 2017").
                    property("Object.Awareness_Campaign.Campaign_URL", "https://trainingcourses.com").
                    property("Object.Awareness_Campaign.Campaign_Start_Date", metadataCreateDate).
                    property("Object.Awareness_Campaign.Campaign_Stop_Date", metadataUpdateDate).
                    next()
        }




        for (Map<String, String> item in listOfMaps) {


            try {
                dob = new  SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person.Employee").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person.Employee").
                    property("Person.Employee.Full_Name", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.Employee.Last_Name", item.get("pg_name_last")).
                    property("Person.Employee.Gender", item.get("pg_gender")).
                    property("Person.Employee.Nationality", item.get("pg_nat")).
                    property("Person.Employee.Date_Of_Birth", dob).
                    property("Person.Employee.Title", item.get("pg_name_title")).next()


            def probabilities = [
                    new Pair<String, Double>("Link Sent", (Double)25.0),
                    new Pair<String, Double>("Reminder Sent", (Double)30.0),
                    new Pair<String, Double>("Failed", (Double)3.0),
                    new Pair<String, Double>("Passed", (Double)60.0),
                    new Pair<String, Double>("Second  Reminder", (Double)45.0)]
            def distribution = new EnumeratedDistribution<String>(probabilities.asList())



            trainingEvent = g.addV("Event.Training_event").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Event.Training_event").
                    property("Event.Training_event.Status",distribution.sample() ).next()


            g.addE("Event.Training_event.awarenessCampaign")
                    .from(trainingEvent)
                    .to(awarenessCampaign)
                    .property("Metadata.Type","Event.Training_event.awarenessCampaign")
                    .property("Metadata.CreateDate",metadataCreateDate)
                    .next()

            g.addE("Event.Training_event.person")
                    .from(trainingEvent)
                    .to(person)
                    .property("Metadata.Type","Event.Training_event.awarenessCampaign")
                    .property("Metadata.CreateDate",metadataCreateDate)
                    .next()




        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    }finally{
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
                dob = new  SimpleDateFormat("yyyy-MM-dd").parse((String) item.get("pg_dob"))
            } catch (Throwable t) {
                dob = new Date("01/01/1666")
            }




            person = g.addV("Person").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Person").
                    property("Person.FullName", item.get("pg_name_first") + " " + item.get("pg_name_last")).
                    property("Person.LastName", item.get("pg_name_last")).
                    property("Person.Gender", item.get("pg_gender")).
                    property("Person.Nationality", item.get("pg_nat")).
                    property("Person.DateOfBirth", dob).
                    property("Person.Title", item.get("pg_name_title")).next()


            email = g.addV("Object.EmailAddress").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.EmailAddress").
                    property("Object.EmailAddress.Email", item.get("pg_email")).next()


            credential = g.addV("Object.Credential").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.Credential").
                    property("Object.Credential.userId", item.get("pg_login_username")).
                    property("Object.Credential.login.sha256", item.get("pg_login_sha256")).next()

            idCard = g.addV("Object.IdentityCard").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Object.IdentityCard").
                    property("Object.IdentityCard.id_name", item.get("pg_id_name")).
                    property("Object.IdentityCard.id_value", item.get("pg_id_value")).next()


            location = g.addV("Location.Address").
                    property("Metadata.Controller", item.get("pg_metadataController")).
                    property("Metadata.Processor", item.get("pg_metadataProcessor")).
                    property("Metadata.Lineage", item.get("pg_metadataLineage")).
                    property("Metadata.Redaction", item.get("pg_metadataRedaction")).
                    property("Metadata.Version", item.get("pg_metadataVersion")).
                    property("Metadata.CreateDate", metadataCreateDate).
                    property("Metadata.UpdateDate", metadataUpdateDate).
                    property("Metadata.Status", item.get("pg_metadataStatus")).
                    property("Metadata.GDPRStatus", item.get("pg_metadataGDPRStatus")).
                    property("Metadata.LineageServerTag", item.get("pg_metadataLineageServerTag")).
                    property("Metadata.LineageLocationTag", item.get("pg_metadataLineageLocationTag")).
                    property("Metadata.Type", "Location.Address").
                    property("Location.Address.Street", item.get("pg_location_street")).
                    property("Location.Address.City", item.get("pg_location_city")).
                    property("Location.Address.State", item.get("pg_location_state")).
                    property("Location.Address.PostCode", item.get("pg_location_postcode")).next()





            g.addE("usesEmail").from(person).to(email).next()
            g.addE("hasCredential").from(person).to(credential).next()
            g.addE("hasIdCard").from(person).to(idCard).next()
            g.addE("lives").from(person).to(location).next()
        }



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    }finally{
        trans.close()
    }
}



