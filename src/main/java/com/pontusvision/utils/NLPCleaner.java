package com.pontusvision.utils;

import org.json.JSONArray;

import java.util.*;
import java.util.stream.Stream;

public class NLPCleaner
{
  public static boolean filterPassFunc(String s, Set<String> filterList)
  {
    if (filterList != null)
    {
      return s != null && s.length() > 2 && !filterList.contains(s);
    }

    return s != null && s.length() > 2;
  }

  public static String filter(Collection<String> listOfNames)
  {
    return filter(listOfNames,true, null);
  }
  public static String filter(Collection<String> listOfNames, boolean splitSpaces, Set<String> filterList)
  {

    Set<String> retVal = new HashSet<>();

    Stream<String> stringStream = filterList == null ?
        listOfNames.parallelStream().filter(s -> s != null && s.length() > 2) :
        listOfNames.parallelStream().filter(s -> filterPassFunc(s, filterList));

    stringStream.forEach(s -> {
      if (splitSpaces)
      {
        String[] parts = s.split("\\s");
        for (int i = 0, ilen = parts.length; i < ilen; i++)
        {
          String part = parts[i];
          if (filterPassFunc(part, filterList))
          {
            retVal.add(part.trim());
          }
        }
      }
      retVal.add(s.trim());
    });

    JSONArray array = new JSONArray();

    return array.put(retVal).toString();

  }

  public static void main(String[] args)
  {
    System.out.println(filter (Arrays.asList( args)));
  }
}
