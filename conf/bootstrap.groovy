try {
  mgmt = graph.openManagement()
  createIndicesPropsAndLabels(mgmt)
} catch (e){
  e.printStackTrace()
}

