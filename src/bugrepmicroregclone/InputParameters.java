/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugrepmicroregclone;

/**
 *
 * @author Judith
 */
public class InputParameters {
    
    public static String systemName = "", pathSystem = "", pathRegular = "", pathMicro = "", programmingLanguage = "";
    public static int lastRevision;
    
    public void setParameters(String sysName){
        try{
            int flag = 0;
        
        if(sysName.equals("Ctags")){
                systemName = sysName;
                lastRevision = 774;
                pathSystem = "F:/Systems_Only/Ctags/Repository/version-";
                //pathRegular = "C:/RegularClones/Ctags/Repository/version-";
                pathRegular = "F:/RegularClones/Ctags/version-";
                pathMicro = "C:/MicroClones/Systems/Ctags/Repository/version-";
                programmingLanguage = "C";
        }
        else if(sysName.equals("Brlcad")){
                systemName = sysName;
                lastRevision = 735;
                pathSystem = "F:/Systems_Only/Brlcad/Repository/version-";
                //pathRegular = "C:/RegularClones/Brlcad/Repository/version-";
                pathRegular = "F:/RegularClones/Brlcad/version-";
                pathMicro = "C:/MicroClones/Systems/Brlcad/Repository/version-";
                programmingLanguage = "C";
        }
        else if(sysName.equals("MonoOSC")){
                systemName = sysName;
                lastRevision = 315;
                pathSystem = "F:/Systems_Only/MonoOSC/Repository/version-";
                //pathRegular = "C:/RegularClones/Monoosc/Repository/version-";
                pathRegular = "F:/RegularClones/Monoosc/version-";
                pathMicro = "C:/MicroClones/Systems/Monoosc/Repository/version-";
                programmingLanguage = "C#";
        }
        else if(sysName.equals("Freecol")){
                systemName = sysName;
                lastRevision = 1950;
                pathSystem = "F:/Systems_Only/Freecol/Repository/version-";
                //pathRegular = "C:/RegularClones/Freecol/Repository/version-";
                pathRegular = "F:/RegularClones/Freecol/version-";
                pathMicro = "C:/MicroClones/Systems/Freecol/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Carol")){
                systemName = sysName;
                lastRevision = 1700;
                pathSystem = "F:/Systems_Only/Carol/Repository/version-";
                //pathRegular = "C:/RegularClones/Carol/Repository/version-";
                pathRegular = "F:/RegularClones/Carol/version-";
                pathMicro = "C:/MicroClones/Systems/Carol/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Jabref")){
                systemName = sysName;
                lastRevision = 1545;
                pathSystem = "F:/Systems_Only/Jabref/Repository/version-";
                //pathRegular = "C:/RegularClones/Jabref/Repository/version-";
                pathRegular = "F:/RegularClones/Jabref/version-";
                pathMicro = "C:/MicroClones/Systems/Jabref/Repository/version-";
                programmingLanguage = "Java";
        }
        else if(sysName.equals("Select")){
            System.out.println("Please select a valid subject system.");
            flag = 1;
        }    
        
        if(flag == 0)
            System.out.println("This is inside setParameters systemName = " + systemName + " Programming language = " + programmingLanguage + " Regular path = " + pathRegular 
                + " Micro path = " + pathMicro + " Last revision = " + lastRevision);
        
        }catch(Exception e){
            System.out.println("error in method setParameters = " + e);
            e.printStackTrace();
        }
    }
    
    public void getParameters(){
        try{
            System.out.println("Inside getParameters, System name = " + systemName);
        }catch(Exception e){
            System.out.println("error in method getParameters = " + e);
            e.printStackTrace();
        }
    }
    
}
