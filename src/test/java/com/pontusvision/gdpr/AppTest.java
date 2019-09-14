package com.pontusvision.gdpr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSimpleFilters()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[2] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Full_Name";
        req.filters[0].type="contains";
        req.filters[0].filter="Leo";
        req.filters[0].filterType="text";


        req.filters[1] = new PVGridFilters();
        req.filters[1].colId="Person.Natural.Last_Name";
        req.filters[1].type="equals";
        req.filters[1].filter="Martins";
        req.filters[1].filterType="text";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Full_Name\":*Leo*) AND (v.\"Person.Natural.Last_Name\":Martins)" , idxSearch );
    }

    public void testComplexFilters()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[2] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Full_Name";
        req.filters[0].operator="OR";
        req.filters[0].condition1= new PVGridFilterCondition();
        req.filters[0].condition1.filter = "Renata";
        req.filters[0].condition1.type = "notContains";
        req.filters[0].condition1.filterType="text";
        req.filters[0].condition2= new PVGridFilterCondition();
        req.filters[0].condition2.filter = "Leonardo";
        req.filters[0].condition2.type = "notEquals";
        req.filters[0].condition2.filterType="text";


        req.filters[1] = new PVGridFilters();
        req.filters[1].colId="Person.Natural.Last_Name";
        req.filters[1].type="equals";
        req.filters[1].filter="Martins";
        req.filters[1].filterType="text";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "((v.\"Person.Natural.Full_Name\":*!*Renata*) OR (v.\"Person.Natural.Full_Name\":*!Leonardo)) AND (v.\"Person.Natural.Last_Name\":Martins)" , idxSearch );
    }
}
