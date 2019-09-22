package com.pontusvision.gdpr;

public class RecordRequest
{
  //    {
  //        searchStr: self.searchstr,
  //                from: from,
  //            to: to,
  //            sortBy: self.sortcol,
  //            sortDir: ((self.sortdir > 0) ? "+asc" : "+desc")
  //    }

  PVGridColumn  cols[];
  PVGridFilters filters[];
  PVGridSearch  search;
  Long          from;
  Long          to;
  String        sortCol;
  String        sortDir;
  String        dataType;
  String        customFilter;

  public String getCustomFilter()
  {
    return customFilter;
  }

  public void setCustomFilter(String customFilter)
  {
    this.customFilter = customFilter;
  }

  public Long getFrom()
  {
    return from;
  }

  public void setFrom(Long from)
  {
    this.from = from;
  }

  public Long getTo()
  {
    return to;
  }

  public void setTo(Long to)
  {
    this.to = to;
  }

  public String getSortCol()
  {
    return sortCol;
  }

  public void setSortCol(String sortCol)
  {
    this.sortCol = sortCol;
  }

  public String getSortDir()
  {
    return sortDir;
  }

  public void setSortDir(String sortDir)
  {
    this.sortDir = sortDir;
  }

  public PVGridSearch getSearch()
  {
    return search;
  }

  public void setSearch(PVGridSearch search)
  {
    this.search = search;
  }

  public String getDataType()
  {
    return dataType;
  }

  public void setDataType(String dataType)
  {
    this.dataType = dataType;
  }

  public PVGridColumn[] getCols()
  {
    return cols;
  }

  public void setCols(PVGridColumn[] cols)
  {
    this.cols = cols;
  }

  public PVGridFilters[] getFilters()
  {
    return filters;
  }

  public void setFilters(PVGridFilters[] filters)
  {
    this.filters = filters;
  }

}
