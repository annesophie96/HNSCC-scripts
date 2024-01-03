import qupath.lib.objects.PathObjects;
import java.lang.Math;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//********** Get Image Pixel Width and Height in microns **********
var server = getCurrentImageData().getServer();
var cal = server.getPixelCalibration();
String xUnit = cal.getPixelWidthUnit();
String yUnit = cal.getPixelHeightUnit();
double pixelWidth = cal.getPixelWidth().doubleValue();
double pixelHeight = cal.getPixelHeight().doubleValue();
//print(pixelWidth);
//print(pixelHeight);

//********** GET BUD GEOMETRY **********

//Get all annotation objects
def annotations = getAnnotationObjects();

//Initialize lists for Bud Center X, Y and Cell Count
def ArrayList<int> BudCellCount = new ArrayList<int>();
def ArrayList<double> BudCenterX = new ArrayList<double>();
def ArrayList<double> BudCenterY = new ArrayList<double>();

//Go through all annotation objects
annotations.each {
    
    //Get the Path Class
    String pathClassName = it.getPathClass();

    //Only treat the defined Path Classes
    if((pathClassName.equals("TB1cell")) || (pathClassName.equals("TB2cell")) || (pathClassName.equals("TB3cell")) || (pathClassName.equals("TB4cell"))) {
        
        //Extract all Center X and Y positions from the individual annotations
        for(int i=0;i<it.getROI().getAllPoints().size();i++) {
            BudCenterX.add(it.getROI().getAllPoints()[i].getX()*pixelWidth); //Convert pixel to microns
            BudCenterY.add(it.getROI().getAllPoints()[i].getY()*pixelHeight); //Convert pixel to microns
        }
    }
    
    //Fill Bud Cell Count 
    if(pathClassName.equals("TB1cell")) {
        for(int i=0;i<it.getROI().getAllPoints().size();i++) {
            BudCellCount.add(1);
        }
    }
    else if(pathClassName.equals("TB2cell")) {
        for(int i=0;i<it.getROI().getAllPoints().size();i++) {
            BudCellCount.add(2);
        }
    }
    else if(pathClassName.equals("TB3cell")) {
        for(int i=0;i<it.getROI().getAllPoints().size();i++) {
            BudCellCount.add(3);
        }
    }
    else if(pathClassName.equals("TB4cell")) {
        for(int i=0;i<it.getROI().getAllPoints().size();i++) {
            BudCellCount.add(4);
        }
    }
}

//print(BudCenterX);
//print(BudCenterY);
//print(BudCellCount);

//********** NON-PAIRED DISTANCES **********

//Initialize list for non-paired distances
def ArrayList<double> distNonPaired = new ArrayList<double>();

//Calculate non-paired distances
for(int i=0;i<BudCenterX.size();i++) {
   for(int j=i+1;j<BudCenterX.size();j++) {
       distNonPaired.add(getPtsDist(BudCenterX[i],BudCenterY[i],BudCenterX[j],BudCenterY[j]));
   }
}

//print(distNonPaired.size()) // Bud Count choose 2
//print(distNonPaired)
print("Average distance between buds [µm]: "+getAvg(distNonPaired));
print("Median distance between buds [µm]: "+getMedian(distNonPaired));

//********** PAIRWISE DISTANCES **********

//Initialize list of lists for pairwise distances
int numBuds = BudCenterX.size(); //number of buds

def ArrayList<double> tempDists = new ArrayList<double>();
def ArrayList<double> tempWeightedDists = new ArrayList<double>();

def ArrayList<double> minDists = new ArrayList<double>();
def ArrayList<double> minWeightedDists = new ArrayList<double>();

double currMin = 0;
double currDist = 0;
double currWeightedMin = 0;
double currWeightedDist = 0;

//Calculate pairwise distances
for(int i=0;i<numBuds;i++) {
   tempDists.clear();
   tempWeightedDists.clear();
   for(int j=0;j<numBuds;j++) {
       if(i!=j) {
           
           currDist = getPtsDist(BudCenterX[i],BudCenterY[i],BudCenterX[j],BudCenterY[j]);
           currWeightedDist = currDist/(BudCellCount[i]*BudCellCount[j]);
           
           tempDists.add(currDist);
           tempWeightedDists.add(currWeightedDist);
           
           if((j==1 && currMin==0) || (j==0 && currMin!=0) || (currDist < currMin)) {
              currMin = currDist;
           }
           
           if((j==1 && currWeightedMin==0) || (j==0 && currWeightedMin!=0) || (currWeightedDist < currWeightedMin)) {
              currWeightedMin = currWeightedDist;
           }
           
       }
   }
   minDists.add(currMin);
   minWeightedDists.add(currWeightedMin);
   //print(tempDists);
}

print("List of minimum pairwise distances [µm]: "+minDists);
print("Average minimum pairwise distance between buds [µm]: "+getAvg(minDists));
print("Median minimum pairwise distance between buds [µm]: "+getMedian(minDists));

print("List of minimum weighted pairwise distances [µm]: "+minWeightedDists);
print("Average minimum weighted pairwise distance between buds [µm]: "+getAvg(minWeightedDists));
print("Median minimum weighted pairwise distance between buds [µm]: "+getMedian(minWeightedDists));




//********** METHODS **********

//Calculate Distance of Two Points from Coordinates
public static double getPtsDist(double point1x, point1y, point2x, point2y) {
   return Math.sqrt(Math.pow((point2x-point1x),2)+Math.pow((point2y-point1y),2));
}

//Calculate Average of ArrayList
public static double getAvg(ArrayList list) {
   double sum = 0;
   for(int i=0;i<list.size();i++) {
       sum = sum + list.get(i);
   }
   return (sum / list.size());
}

//Calculate Median of ArrayList
public static double getMedian(ArrayList list) {
    Collections.sort(list);
    int length = list.size();
    if(length%2 == 1) {
        return list.get((int)Math.floor(length/2));
    }
    else {
        return ((list.get((int)(length/2)-1)+list.get((int)(length/2)))/2);
    }
}

//Print List of List in 2D
public static void print2D(ArrayList list) {
    Logger logger = LoggerFactory.getLogger(QuPathGUI.class);
    for(int i=0;i<list.size();i++) {
       String outLine = Arrays.deepToString(list.get(i).toArray());
       logger.info(outLine);
    }
}