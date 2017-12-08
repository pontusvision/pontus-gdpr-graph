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





def addRandomChildUserDataBulk(graph, g, LinkedList<Map<String, String>> listOfMaps) {

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()
    trans = graph.tx()
    try {
        trans.open()

        randVal = new Random()

        def oneYearInMs = 3600000*24*365
        def eighteenYearsInMs = oneYearInMs * 18




        for (Map<String, String> item in listOfMaps) {


            try {

                dob = new  Date(System.currentTimeMillis() - (long) (randVal.nextDouble() * eighteenYearsInMs))
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
    }finally{
        trans.close()
    }
}

def __addConsentForPrivacyNotice(graph, g, Vertex privNoticeVertex){

    g.has("Metadata.Type","Person").as("people")
            .addE("Consent").property("Consent.Date",new Date())
            .from("people").to(privNoticeVertex).next()



}

def __addPrivacyImpactAssessment(graph, g, Vertex privNoticeVertex){

    metadataCreateDate = new Date()
    metadataUpdateDate = new Date()

    trans = graph.tx()
    try {
        trans.open()


        pia = g.addV("Object.Privacy_Impact_Assessment").
                property("Metadata.Controller", "ABC Inc").
                property("Metadata.Processor", "ABC Inc").
                property("Metadata.Lineage", "oracle://jdbc://oracledb.com").
                property("Metadata.Redaction", "/data/protection/officer").
                property("Metadata.Version", "1").
                property("Metadata.CreateDate", metadataCreateDate).
                property("Metadata.UpdateDate", metadataUpdateDate).
                property("Metadata.Status", "New").
                property("Metadata.GDPRStatus", "n/a").
                property("Metadata.LineageServerTag", "AWS_EUR_HOST3").
                property("Metadata.LineageLocationTag", "GB").
                property("Metadata.Type", "Object.Privacy_Impact_Assessment").
                property("Object.Privacy_Impact_Assessment.Description", "PIA for project xyz.").
                property("Object.Privacy_Impact_Assessment.Start_Date", new Date()).
                property("Object.Privacy_Impact_Assessment.Delivery_Date", new Date(System.currentTimeMillis()+3600*24*365)).
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

        g.has("Metadata.Type","Person.Employee").range(0,10).as("employees").addE("ApprovedComplianceCheck").from("employees").to(pia).next()



        g.addE("hasPrivacyNotice").from(pia).to(privNoticeVertex).next()

        trans.commit()

    } catch (Throwable t) {
        trans.rollback()
        throw t
    } finally{
        trans.close()
    }
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


        for (i = 0; i< ilen; i++)
        {


            lawfulBasis1 = g.V().has("Object.Lawful_Basis.Description",definitions[i])

            if (lawfulBasis1.hasNext()){
                lawfulBasisVertices[i] = lawfulBasis1.next()
            }
            else{
                lawfulBasisVertices[i] =  g.addV("Object.Lawful_Basis").
                        property("Metadata.Lineage","https://gdpr-info.eu/art-6-gdpr/").
                        property("Metadata.Redaction", "/data/protection/officer" ).
                        property("Metadata.Version", 1).
                        property("Metadata.CreateDate", metadataCreateDate).
                        property("Metadata.UpdateDate", metadataUpdateDate).
                        property("Metadata.Status", "new" ).
                        property("Metadata.GDPRStatus", "n/a" ).
                        property("Metadata.LineageServerTag", "AWS_AAA").
                        property("Metadata.LineageLocationTag", "GB").
                        property("Metadata.Type", "Object.Lawful_Basis").
                        property("Object.Lawful_Basis.Id", i).
                        property("Object.Lawful_Basis.Description", definitions[i]).
                        next()
            }

        }

        def privacyNoticeText = new String[2]


        privacyNoticeText[0] = new String('<div><p>Here at XYZ Inc,  we take your privacy seriously and will only use your personal information to administer your account and to provide the products and services you have requested from us.</p><p>However, from time to time we would like to contact you with details of other services we provide. If you consent to us contacting you for this purpose please tick to say how you would like us to contact you:</p><p style="padding-left: 30px;"><strong>Post</strong> ☐ &nbsp; &nbsp;<strong>Email</strong> ☐&nbsp; &nbsp; <strong>Telephone</strong>&nbsp;☐ &nbsp; &nbsp;</p><p style="padding-left: 30px;"><strong>Text message</strong>&nbsp;☐ &nbsp; &nbsp;<strong>Automated call</strong>&nbsp;☐<span><br></span></p><p>We would also like to pass your details onto other businesses such as ABC Inc and CDF Inc, so that they can contact you by post with details of services that they provide. If you consent to us passing on your details for that purpose please tick to confirm:</p><p style="padding-left: 30px;"><strong>I agree</strong>&nbsp;☐</p></div>');


        privacyNoticeText[1] = new String ('<article class="content us-content"> <p class="strap">DFG Limited (We or us) are committed to protecting and respecting your privacy</p> <p>This policy (together with our <a href="/about-us/terms-of-use/">terms of use</a> and any other documents referred to in it) sets out the basis on which any personal information we collect from you, or that you provide to us through our website or via one of our call-centres, will be processed by us. Please read the following carefully to understand our views and practices regarding your personal information and how we will treat it. By visiting this website or providing your personal information to one of our call-centre operators, you are accepting and consenting to the practices described in this policy. Please note that this includes consenting to the processing of any sensitive personal information you provide, as described below.</p> <p>We may amend this privacy policy at any time. Any changes we may make will be posted on this page, so please check back frequently. Your continued use of our website(s) after posting will constitute your acceptance of, and agreement to, any changes.</p> <p>For the purpose of the Data Protection Act 1998 (the Act), the data controller is DFG Limited of The Cooperage, 5 Copper Row, London, SE1 2LH. DFG Limited is part of the ZPG Plc group of companies, comprising ZPG Plc and its subsidiaries (collectively “Zoopla” and, individually, a “ZPG Company”).</p> <h2>Information we may collect from you</h2> <p>We may collect and process the following information about you:</p> <ul> <li> <h3>Information you provide to us</h3> <p>You may provide us with information by filling in forms on our website(s) or by corresponding with us by phone, e-mail, live-chat or otherwise. This includes information you provide when you register for an account, search for a price comparison or quote, enter into a contract for the supply of services, enter a competition, promotion or survey and when you report a problem with our site. The personal information you provide may include your name, address, e-mail address and phone number, financial and credit card information and other information about yourself to enable us to provide you with our price comparison services, switching services or other related services. Depending on which products and services you are interested in, you may also need to provide us with certain categories of sensitive personal information, to allow us to provide you with quotes, including, for example, information about criminal convictions (e.g. in connection with a car insurance quote) or health (e.g. for an energy provide to assess whether you have any special requirements). By providing us with this information, you expressly consent to our use of your sensitive personal information in accordance with this privacy policy.</p> </li> <li> <h3>Information we collect about you</h3> <p>When you visit our website(s) we may automatically collect information about your computer, including your IP address, information about your visit, your browsing history, and how you use our website. This information may be combined with other information you provide to us, as described above.</p> </li> <li> <h3>Information we receive from other sources</h3> <p>We may receive information about you if you use any of the other websites we, or our group companies, operate or the other services we provide. We are also working closely with third parties (including, for example, business partners, service providers, advertising networks, analytics providers, and search information providers) and may receive information about you from them. This may be combined with other information you provide to us, as described above.</p> </li> </ul> <h2>How we use your personal information</h2> <p>We use personal information about you in connection with the following purposes:</p> <ul> <li> <p>Fulfiling your requests:</p> <ul> <li>to provide you with the information, products and services that you request from us or another ZPG Company, including providing you with price comparison quotes and switching services;</li> <li>to complete any transaction you are undertaking with us or another ZPG Company;</li> <li>to administer any promotion or competition that you enter via our website(s); and</li> <li>to allow you to participate in interactive features of our service, when you choose to do so.</li> </ul> </li> <li> <p>Marketing:</p> <ul> <li>to provide you with information about other goods and services we or other ZPG Companies offer that are similar to those that you have already purchased or enquired about;</li> <li>to provide you, or permit other ZPG Companies, or selected third parties to provide you, with information about goods or services that may interest you; </li> <li>to measure or understand the effectiveness of advertising we serve to you and others, and to deliver relevant advertising to you, which may be based on your activity on our website(s) or the website of another ZPG Company or third parties\' websites; and </li> <li>to make suggestions and recommendations to you and other users of our site about goods or services that may interest you or them, which may be based on your activity on our website(s) or the website of another ZPG Company or third parties\' websites.</li> </ul> </li> <li> <p>Service Improvements and account management:</p> <ul> <li>to ensure that content from our site is presented in the most effective manner for you and for your computer.</li> <li>to administer our site and for internal business administration and operations, including troubleshooting, data analysis, testing, research, statistical and survey purposes; </li> <li>to notify you about changes to our service and to send you service emails relating to your account; </li> <li>as part of our efforts to keep our site safe and secure; and </li> <li>to manage and operate your account with us. </li> </ul> </li> </ul> <h2>Sharing your personal information</h2> <p>We may share your personal information in connection the purposes described above with any other ZPG Company.</p> <p>We may also share your personal information with third parties in the following circumstances:</p> <ul> <li>Selected energy suppliers, insurers, insurance brokers, credit brokers and other related third parties, to enable us to obtain a quote for you or provide you with other related services. Their use of any data we provide will be governed by their own terms of use and privacy policy and by asking us to provide the relevant comparison switching or other services you are also agreeing to the relevant third parties terms and conditions and privacy policy. Information about the third parties that provide our insurance quote services and their terms and conditions and privacy policies can be found online when you obtain a quote or here. In order to provide you with a quote, some service providers may search external sources such as the electoral roll, county court judgments, bankruptcy registers, UK credit reference agencies and education sources, where relevant, to check and assess the information you provide when asking for a quote. These searches may be recorded by credit agencies but will not affect your credit standing.</li> <li>Business partners, suppliers and sub-contractors for the performance of any contract we enter into with them or you.</li> <li>Advertisers and advertising networks and other third parties that require the data to select and serve relevant adverts to you and others.</li> <li>Analytics and search engine providers that assist us in the improvement and optimisation of our site. Your personal information is generally shared in a form that does not directly identify you.</li> <li>We may also share your personal information with third parties:</li> <li>In the event that we sell or buy any business or assets, in which case we may disclose your personal data to the prospective seller or buyer of such business or assets, along with its professional advisers. If DFG Limited or substantially all of its assets are acquired by a third party, personal information held by it about its customers will be one of the transferred assets.</li> <li>If required in order to obtain professional advice.</li> <li>If we are under a duty to disclose or share your personal information in order to comply with any legal obligation, or in order to enforce or apply our terms of use and other agreements; or to protect the rights, property, or safety of DFG Limited, our customers, or others. This includes exchanging information with other companies and organisations for the purposes of fraud protection.</li> </ul> <p>We may also share with or sell to third parties aggregate information or information that does not personally identify you.</p> <h2>Where we store your personal information</h2> <p>The information that we collect from you may be transferred to, and stored in, a country outside the European Economic Area (EEA). It may also be processed by staff operating outside the EEA who work for us or for one of our suppliers. The laws in some countries may not provide as much legal protection for your information as in the EEA. By submitting your personal information, you agree to this transfer, storing or processing. We will take all steps reasonably necessary to ensure that your data is treated securely and in accordance with this privacy policy.</p> <p>Unfortunately, the transmission of information via the internet is not completely secure. Although we will do our best to protect your personal information, we cannot guarantee the security of your information transmitted to our site; any transmission is at your own risk. Once we have received your information, we will use strict procedures and security features to try to prevent unauthorised access.</p> <h2>Cookies</h2> <p>Our website uses cookies to distinguish you from other users of our website. This helps us to provide you with a good experience when you browse our website and also allows us to improve our site and to provide interest-based advertising. You may adjust the settings on your browser to refuse cookies but some of the services on our website(s) may not work if you do so. For detailed information on the cookies we use and the purposes for which we use them see our Cookie Policy.</p> <h2>Opting Out of Collection of Information by Third Parties</h2> <p>DFG Ltd third-party ad servers, ad network providers, and third-party advertisers (the Ad Providers) may provide you with advertisements that you may see on our website(s) or on other affiliated websites. To improve the relevancy and help measure the effectiveness of such advertisements, the Ad Providers may use cookies, web beacons, clear .gifs or similar technologies. These are used to record users\' activity, such as the pages visited, and to learn what types of information are of most interest to the users. Use of these technologies by Ad Providers is subject to their own privacy policies and is not covered by our privacy policy. For more information regarding the choices you have about these technologies (such as opting-out), click <a href="http://www.youronlinechoices.eu/">here</a>. </p> <h2>Your rights</h2> <p>You have the right to ask us not to process your personal information for marketing purposes. You can exercise your right to prevent such processing by checking or unchecking certain boxes on the forms we use to collect your data. You can also exercise the right at any time by contacting us at <a href="mailto:customer-services@DFG.com">customer-services@DFG.com</a></p> <p>Our site may, from time to time, contain links to and from the websites of our partner networks, advertisers and affiliates. If you follow a link to any of these websites, please note that these websites have their own privacy policies and that we do not accept any responsibility or liability for these policies. Please check these policies before you submit any personal information to these websites.</p> <h2>Access to information</h2> <p>The Act gives you the right to access personal information held about you by DFG Ltd. Your right of access can be exercised in accordance with the Act. Any access request may be subject to a fee of £10 to meet our costs in providing you with details of the information we hold about you.</p> <h2>DFG Insurance Comparison Service Providers</h2> <p>DFG\'s home insurance comparison service is provided by AZD Insurance Services Ltd, registered in England No. 7777777. AZD Insurance Services Ltd has its registered office at Nile Street, Burslem, Stoke-on-Trent ST6 2BA United Kingdom. AZD Insurance Services Ltd is authorised and regulated by the Financial Conduct Authority (FCA) (Registration number: 888888). By using the home insurance system you are also agreeing to AZD\'s <a href="https://www.AZDinsurance.co.uk/disclaimers/terms-of-business/DFG-terms-of-business">Terms &amp; Conditions</a> and <a href="https://www.AZDinsurance.co.uk/disclaimers/DFG-privacy-policy">Privacy Policy</a>.</p> <p>DFG Ltd life insurance comparisons service is provided by YYT Ltd who are authorised and regulated by the Financial Conduct Authority (501109). Registered Office; Cambrian Buildings, Mount Stuart Square, Cardiff CF10 5FL. By using the life insurance system you are also agreeing to YYT <a href="/life-insurance/quote/terms-no-header.aspx">Terms and Conditions</a> and <a href="/life-insurance/quote/privacy-no-header.aspx">Privacy Policy</a>.</p> <p>DFG\'s health insurance comparisons service is provided by YYT Ltd who are authorised and regulated by the Financial Conduct Authority (501109). Registered Office; Cambrian Buildings, Mount Stuart Square, Cardiff CF10 5FL. By using the health insurance system you are also agreeing to YYT <a href="/health-insurance/quote/terms-no-header.aspx">Terms and Conditions</a> and <a href="/health-insurance/quote/privacy-no-header.aspx">Privacy Policy</a>.</p> <p>Updated: 27 April 2017</p> </article>')


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
        for (i = 0; i< ilen; i++)
        {


            privacyNotice = g.V().has("Object.Privacy_Notice.Text",privacyNoticeText[i])

            if (privacyNotice.hasNext()){
                privacyNoticeVertices[i] = privacyNotice.next()
            }
            else{
                privacyNoticeVertices[i] =  g.addV("Object.Privacy_Notice").
                        property("Metadata.Lineage","https://gdpr-info.eu/art-6-gdpr/").
                        property("Metadata.Redaction", "/data/protection/officer" ).
                        property("Metadata.Version", 1).
                        property("Metadata.CreateDate", metadataCreateDate).
                        property("Metadata.UpdateDate", metadataUpdateDate).
                        property("Metadata.Status", "new" ).
                        property("Metadata.GDPRStatus", "n/a" ).
                        property("Metadata.LineageServerTag", "AWS_AAA").
                        property("Metadata.LineageLocationTag", "GB").
                        property("Metadata.Type", "Object.Privacy_Notice").
                        property("Object.Privacy_Notice.Id", i).
                        property("Object.Privacy_Notice.Text", privacyNoticeText[i]).
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


        g.addE("HasLawfulBasisOn").from(privacyNoticeVertices[0]).to(lawfulBasisVertices[0])
        g.addE("HasLawfulBasisOn").from(privacyNoticeVertices[0]).to(lawfulBasisVertices[1])

        g.addE("HasLawfulBasisOn").from(privacyNoticeVertices[1]).to(lawfulBasisVertices[2])
        g.addE("HasLawfulBasisOn").from(privacyNoticeVertices[1]).to(lawfulBasisVertices[3])


        __addConsentForPrivacyNotice(graph,g, privacyNoticeVertices[0])
        __addConsentForPrivacyNotice(graph,g, privacyNoticeVertices[1])


        __addPrivacyImpactAssessment(graph,g, privacyNoticeVertices[0])
        __addPrivacyImpactAssessment(graph,g, privacyNoticeVertices[1])



        trans.commit()
    } catch (Throwable t) {
        trans.rollback()
        throw t
    }finally{
        trans.close()
    }

}
