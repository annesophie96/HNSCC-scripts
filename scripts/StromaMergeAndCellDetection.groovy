def annotations = getAnnotationObjects()
def ArrayList<PathObject> temp = new ArrayList<PathObject>()
annotations.each { 
    if((it.getDisplayedName().equals("Stroma") || it.getDisplayedName().equals("Zellreiches Stroma") || it.getDisplayedName().equals("Lockeres Bindegewebe")) && it.getParent().getDisplayedName().equals("tumor") ){
        temp.add(it)
    }
}
getCurrentHierarchy().getSelectionModel().setSelectedObjects(temp, temp[0])
mergeSelectedAnnotations()
getSelectedObject().setName("Stroma Total")
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');