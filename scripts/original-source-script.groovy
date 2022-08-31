// @melvingelbard - https://forum.image.sc/t/script-for-averaging-detection-measurement-data/48805/9?u=research_associate
// Source: https://forum.image.sc/t/script-for-averaging-detection-measurement-data/48805/9?u=research_associate

columnName = ["CK: Cell: Mean", "CD68: Cell: Mean"]    // Change this to your column of interest


imageName = getProjectEntry().getImageName()

//Save to the project folder
var path = buildFilePath(PROJECT_BASE_DIR, "Detection Summary measurements.csv")
//print "Mean: " + avg
//print "Std.Dev: " + Math.sqrt(sd/stats.length)
var pathObjects = getDetectionObjects()

var separator = ","
File file = new File(path)
header = "Image name,"
var exists = file.exists()
file.withWriterAppend { fw -> 
    if (!exists){
        columnName.each{
            header =header+[it+" Average", it+" Standard deviation"].join(separator)+separator
        }
        fw.writeLine(header)
    }
    dataLine = getProjectEntry().getImageName()+","
    columnName.each{
        
        stats = Histogram.getMeasurementValues(pathObjects, it);
        var avg = 0
        for (int i=0; i < stats.length; i++) {
            avg += stats[i]
        }
        avg /= stats.length
        
        var sd = 0
        for (int i=0; i < stats.length; i++) {
            sd = sd + Math.pow(stats[i] - avg, 2);
        }
        sd = Math.sqrt(sd/stats.length)
        dataLine = dataLine + [avg, sd].join(separator)+separator
    }
    
    fw.append(dataLine)
    fw.append(System.getProperty("line.separator"))      
}

print "Done!"
import qupath.lib.analysis.stats.Histogram