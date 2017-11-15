import org.janusgraph.core.*
import org.janusgraph.core.schema.*
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
        dob = new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String) pg_dob)
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

def dumpData(ArrayList<Map<String, String>> listOfMaps) {

    StringBuilder strBuilder = new StringBuilder()
    for (Map<String, String> item in listOfMaps) {

        strBuilder.append(item.toString())

    }
    return strBuilder.toString()
}

def addRandomUserDataBulk(graph, g, List<Map<String, String>> listOfMaps) {

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



