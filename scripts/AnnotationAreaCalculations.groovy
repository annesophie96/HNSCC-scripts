import qupath.lib.gui.measure.ObservableMeasurementTableData

def ob = new ObservableMeasurementTableData();
def annotations = getAnnotationObjects()

 // This line creates all the measurements
ob.setImageData(getCurrentImageData(),  annotations);

double stromaTotalT = 0
double stromaT = 0
double zellreichesStromaT = 0
double lockeresBindegewebeT = 0
double stromaTotalP = 0
double stromaP = 0
double zellreichesStromaP = 0
double lockeresBindegewebeP = 0
double tumor = 0
double necrosis = 0
double verhornung = 0

annotations.each { 
    if(it.getDisplayedName().equals("Stroma") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        stromaT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Zellreiches Stroma") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        zellreichesStromaT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Lockeres Bindegewebe") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        lockeresBindegewebeT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Stroma") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        stromaP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Zellreiches Stroma") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        zellreichesStromaP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Lockeres Bindegewebe") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        lockeresBindegewebeP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Tumor") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        tumor=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Necrosis") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        necrosis=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Verhornung") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        verhornung=ob.getNumericValue(it, "Area µm^2")
    }
}
print(stromaT+" "+zellreichesStromaT+" "+lockeresBindegewebeT+" "+stromaP+" "+zellreichesStromaP+" "+lockeresBindegewebeP+" "+tumor+" "+necrosis+" "+verhornung)
stromaTotalT=stromaT+zellreichesStromaT+lockeresBindegewebeT
stromaTotalP=stromaP+zellreichesStromaP+lockeresBindegewebeP

print("Tumor Area    "+tumor+" µm^2")
print("Stroma Total Area in Tumor   "+stromaTotalT+" µm^2")
print("Tumor/(Tumor + Stroma Total in Tumor)    "+100*tumor/(tumor+stromaTotalT)+" %")
print("Necrosis/(Tumor + Necrosis)    "+100*necrosis/(tumor+necrosis)+" %")
print("Zellreiches Stroma/ Stroma Total in Tumor    "+100*zellreichesStromaT/stromaTotalT+" %")
print("Stroma/ Stroma Total in Tumor    "+100*stromaT/stromaTotalT+" %")
print("Verhornung/(Tumor + Verhornung)    "+100*verhornung/(tumor+verhornung)+" %")

print("Stroma Total Area in Peritumor   "+stromaTotalP+" µm^2")
print("Tumor/(Tumor + Stroma Total in Peritumor)    "+100*tumor/(tumor+stromaTotalP)+" %")
print("Zellreiches Stroma/ Stroma Total in Peritumor    "+100*zellreichesStromaP/stromaTotalP+" %")
print("Stroma/ Stroma Total in Peritumor    "+100*stromaT/stromaTotalP+" %")

//add for the last two, comparation to not stroma total but to total peritumor area
//export csv 