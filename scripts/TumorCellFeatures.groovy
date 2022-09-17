import qupath.lib.analysis.stats.Histogram
import java.lang.Math
import java.util.Iterator
import java.time.*

//Select Data
columnName = ["Nucleus: Area", "Nucleus: Max caliper"]    // Change this to your column of interest
numVar = columnName.size

//Setting up Exports
imageName = getProjectEntry().getImageName()

String PATH = PROJECT_BASE_DIR

String directoryName = buildFilePath(PROJECT_BASE_DIR,"/csv");

File directory = new File(directoryName);
    if (! directory.exists()){
        directory.mkdir();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
    }

var rawPath = buildFilePath(PROJECT_BASE_DIR, "/csv/"+imageName+"_RawDetectionMeasurements.csv")
var path = buildFilePath(PROJECT_BASE_DIR, "/csv/TumorCellFeatures.csv")
var separator = ";"

//Creating Files
File file = new File(path)

File rawFile = new File(rawPath)
if(rawFile.exists()){
    try{
        if(rawFile.delete()){
            print("Previous raw data file deleted!")
        }
        else{
            print("WARNING: Previous raw data file delete failed!")
        }
    }
    catch(Exception e){
        e.printStackTrace()
    }
}
rawFile = new File(rawPath)

header = "Image name"+separator
rawHeader=""

var exists = file.exists()


//Select Objects
tic=Instant.now() //Start Timer

boolean delInst = false
var pathObjects = getDetectionObjects()
Iterator<PathObject> it = pathObjects.iterator();
t=0
while(it.hasNext()) {
  PathObject i = it.next();
  t++
  delInst=false
  for (int j=0; j<numVar; j++){
      List<Double> tempVal = new ArrayList<Double>(1);
      tempVal.add(i)
      if(Histogram.getMeasurementValues(tempVal,columnName[j])[0].isNaN()){delInst=true}
  }
  //if (t%10000==0){ Dialogs.showPlainNotification('Progress',String.valueOf(100*t/maxData)+"%")}
  if(!(i.getParent().getDisplayedName().equals("Tumor")) || !(i.getPathClass().toString().matches("tumorcells")) || delInst==true) {
    it.remove();
  }
}

toc=Instant.now() //Stop Timer
print("Elapsed time: "+Duration.between(tic,toc).toMillis()+" ms")

//Start Calculations
tic=Instant.now() //Start Timer

maxData=pathObjects.size()
if(maxData==0){
print("ERROR: No data matching conditions! Please retest slide: "+imageName)
return}

List<List<Double>> mesVal = new ArrayList<ArrayList<Double>>(numVar);
for (int i=0; i<numVar; i++){
    mesVal.add(Histogram.getMeasurementValues(pathObjects, columnName[i]))
}

List<Double> mesLnAvg = new ArrayList<Double>(Collections.nCopies(numVar, 0));
List<Double> mesLnSD = new ArrayList<Double>(Collections.nCopies(numVar, 0));

for (int j=0; j<numVar; j++){
    for (int i=0; i < maxData; i++) {
        mesLnAvg[j] += Math.log(mesVal[j][i])
    }
    mesLnAvg[j] /= maxData
    mesLnAvg[j] = Math.exp(mesLnAvg[j])
}

toc=Instant.now() //Stop Timer
print("Elapsed time: "+Duration.between(tic,toc).toMillis()+" ms")
tic=Instant.now() //Start Timer

for (int j=0; j<numVar; j++){
    for (int i=0; i < maxData; i++) {
        mesLnSD[j] = mesLnSD[j]+Math.pow(Math.log(mesVal[j][i])-Math.log(mesLnAvg[j]),2)
    }
    mesLnSD[j] = Math.sqrt(mesLnSD[j]/maxData)
    mesLnSD[j] = Math.exp(mesLnSD[j])
}

toc=Instant.now() //Stop Timer
print("Elapsed time: "+Duration.between(tic,toc).toMillis()+" ms")

//Start Exports
tic=Instant.now() //Start Timer

file.withWriterAppend { fw -> 
    if (!exists){
        columnName.each{
            header =header+[it+" AVG_S", it+" SD_S"].join(separator)+separator
        }
        fw.writeLine(header)
    }
    line=getProjectEntry().getImageName()+separator
    for (int j=0; j<numVar; j++){
        line = line + mesLnAvg[j] + separator + mesLnSD[j] + separator
    }
    line = line + System.getProperty("line.separator")
    fw.append(line)
}

toc=Instant.now() //Stop Timer
print("Elapsed time: "+Duration.between(tic,toc).toMillis()+" ms")
tic=Instant.now() //Start Timer

rawFile.withWriterAppend { fw -> 
    columnName.each{
        rawHeader = rawHeader+it+separator
    }
    fw.writeLine(rawHeader)
    
    k=0
    while(k<maxData){
        line=""
        for (int j=0; j<numVar; j++){
            line = line + mesVal[j][k] + separator
        }
        line = line + System.getProperty("line.separator")
        fw.append(line) 
        k++
        //if (k%10000==0){ Dialogs.showPlainNotification('Progress',String.valueOf(100*k/maxData)+"%")}
    }
}

toc=Instant.now() //Stop Timer
print("Elapsed time: "+Duration.between(tic,toc).toMillis()+" ms")

print "Done!"