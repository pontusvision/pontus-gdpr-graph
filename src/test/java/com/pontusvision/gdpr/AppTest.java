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


    public void testSingleFilter()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Full_Name";
        req.filters[0].type="contains";
        req.filters[0].filter="Leo";
        req.filters[0].filterType="text";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Full_Name\":*Leo*)" , idxSearch );
    }

    public void testSingleDateFilterInRange()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].type="inRange";
        req.filters[0].dateFrom="02-08-1972";
        req.filters[0].dateTo="02-08-1992";
        req.filters[0].filterType="date";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Date_Of_Birth\":[ 02-08-1972 TO 02-08-1992 ])" , idxSearch );
    }

    public void testSingleDateFilterNotEquals()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].type="notEquals";
        req.filters[0].dateFrom="02-08-1972";
        req.filters[0].dateTo="02-08-1992";
        req.filters[0].filterType="date";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(( (v.\"Person.Natural.Date_Of_Birth\":{ * TO 02-08-1972 } ) OR (v.\"Person.Natural.Date_Of_Birth\":{ 02-08-1972 TO * } ) ))" , idxSearch );
    }
    public void testSingleDateFilterEquals()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].type="equals";
        req.filters[0].dateFrom="02-08-1972";
        req.filters[0].dateTo="02-08-1992";
        req.filters[0].filterType="date";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Date_Of_Birth\":[ 02-08-1972 TO 02-08-1972 ])" , idxSearch );
    }
    public void testSingleDateFilterGreaterThan()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].type="greaterThan";
        req.filters[0].dateFrom="02-08-1972";
        req.filters[0].dateTo="02-08-1992";
        req.filters[0].filterType="date";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Date_Of_Birth\":{ 02-08-1972 TO * })" , idxSearch );
    }

    public void testSingleDateFilterLessThan()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[1] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].type="lessThan";
        req.filters[0].dateFrom="02-08-1972";
        req.filters[0].dateTo="02-08-1992";
        req.filters[0].filterType="date";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "(v.\"Person.Natural.Date_Of_Birth\":{ * TO 02-08-1972 })" , idxSearch );
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
        req.filters[0].condition2.type = "notEqual";
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

    public void testComplexFiltersStartsEndsWith()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[2] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Full_Name";
        req.filters[0].operator="OR";
        req.filters[0].condition1= new PVGridFilterCondition();
        req.filters[0].condition1.filter = "Renata";
        req.filters[0].condition1.type = "startsWith";
        req.filters[0].condition1.filterType="text";
        req.filters[0].condition2= new PVGridFilterCondition();
        req.filters[0].condition2.filter = "Leonardo";
        req.filters[0].condition2.type = "notEqual";
        req.filters[0].condition2.filterType="text";


        req.filters[1] = new PVGridFilters();
        req.filters[1].colId="Person.Natural.Last_Name";
        req.filters[1].type="endsWith";
        req.filters[1].filter="Martins";
        req.filters[1].filterType="text";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "((v.\"Person.Natural.Full_Name\":Renata*) OR (v.\"Person.Natural.Full_Name\":*!Leonardo)) AND (v.\"Person.Natural.Last_Name\":*Martins)" , idxSearch );
    }

    public void testComplexDateFilters()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[2] ;
        req.filters[0] = new PVGridFilters();
        req.filters[0].colId="Person.Natural.Date_Of_Birth";
        req.filters[0].operator="OR";
        req.filters[0].condition1= new PVGridFilterCondition();
        req.filters[0].condition1.dateFrom = "01-02-1999";
        req.filters[0].condition1.dateTo = "01-02-2009";
        req.filters[0].condition1.type = "inRange";
        req.filters[0].condition1.filterType="date";
        req.filters[0].condition2= new PVGridFilterCondition();
        req.filters[0].condition2.dateFrom = "11-02-1999";
        req.filters[0].condition2.dateTo = null;
        req.filters[0].condition2.type = "notEquals";
        req.filters[0].condition2.filterType="date";
        req.filters[0].filterType="date";


        req.filters[1] = new PVGridFilters();
        req.filters[1].colId="Person.Natural.Last_Name";
        req.filters[1].type="endsWith";
        req.filters[1].filter="Martins";
        req.filters[1].filterType="text";


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "((v.\"Person.Natural.Date_Of_Birth\":[ 01-02-1999 TO 01-02-2009 ]) OR (( (v.\"Person.Natural.Date_Of_Birth\":{ * TO 11-02-1999 } ) OR (v.\"Person.Natural.Date_Of_Birth\":{ 11-02-1999 TO * } ) ))) AND (v.\"Person.Natural.Last_Name\":*Martins)" , idxSearch );
    }

    public void testEmptyFilters()
    {
        RecordRequest req = new RecordRequest();
        req.dataType = "Person.Natural";
        req.filters = new PVGridFilters[0] ;


        String idxSearch = Resource.getIndexSearchStr(req);

        System.out.println (idxSearch);


        assertEquals( "" , idxSearch );
    }

}
