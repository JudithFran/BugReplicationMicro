/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugrepmicroregclone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Judith
 */

class CodeFragment {

    int revision = -1;
    String filepath = "";
    int startline = -1, endline = -1;
    String changetype = "-1";

    String[] lines = new String[10000];

    public void getFragment() {
        
        String abs_filepath = InputParameters.pathSystem + revision + "/" + filepath;    
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abs_filepath)));
            String str = "";

            int line = 0;
            int i = 0;
            while ((str = br.readLine()) != null) {
                line++;
                if (line > endline) {
                    break;
                }
                if (line >= startline && line <= endline) {
                    lines[i] = str.trim();
                    //System.out.println(lines[i]);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("error.getFragment." + e);
        }

    }

    public void showFragment() {

        String abs_filepath = InputParameters.pathSystem + revision + "/" + filepath;
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(abs_filepath)));
            String str = "";

            System.out.println("\n" + revision + ": " + filepath + ", " + startline + " - " + endline);
            //System.out.println("---------------------------------------------------");
            int line = 0;
            int i = 0;
            while ((str = br.readLine()) != null) {
                line++;
                if (line > endline) {
                    break;
                }
                if (line >= startline && line <= endline) {
                    lines[i] = str.trim();
                    i++;
                    //System.out.println(str);
                }
            }
            //System.out.println("---------------------------------------------------");
        } catch (Exception e) {
            System.out.println("error.showFragment." + e);
        }
    }
}

public class BugReplicationMicroRegularClones {
    
    DBConnect db = new DBConnect();
    CompareChanges cc = new CompareChanges();
    
    int countRevR = 0;
    int countRevRepR = 0;
    
    int countRevM = 0;
    int countRevRepM = 0;
    
    int RQ4 = 0;
    
    public String getBugFixCommits() {
        String bugFixCommits = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputParameters.systemName + " commitlog.txt")));
            
            String str = "";
            String prevString = "";
                
            int commit = 0;
            while ((str = br.readLine()) != null) {
                if (str.trim().length() == 0) {
                    continue;
                }

                if (prevString.contains("--------------------------------")) {
                    //this is the starting of a commit report.
                    //we need to know the commit number.
                    String str1 = str.trim().split("[ ]+")[0].trim();
                    str1 = str1.substring(1);
                    commit = Integer.parseInt(str1);
                    //System.out.println (commit);
                } else {
                    //according to the study of Mockus
                    if (str.toLowerCase().contains("bug") || str.toLowerCase().contains("fix") || str.toLowerCase().contains("fixup") || str.toLowerCase().contains("error") || str.toLowerCase().contains("fail")) 
                    //if (str.contains ("bug") || str.contains("fix") || str.contains ("fixup") || str.contains ("error") || str.contains ("fail"))
                    {
                        if (!bugFixCommits.contains(" " + commit + " ")) {
                            bugFixCommits += " " + commit + " ";
                        }
                    }                                          
                }
                prevString = str;
            }
            br.close();
            //System.out.println ("Revisions that were created because of a bug fix = " + bugFixCommits);

        } catch (Exception e) {
            System.out.println("error in getBugFixCommits = " + e);
        }

        return bugFixCommits;
    }
    
    // For RQ4 use this method
    
    public String getBugFixCommitsRQ4() {
        String bugFixCommits = "";
        try{
            String[] bugFixCommitsMockus = new String[10000];
            String[] bugFixCommitsLamkanfi = new String[10000];
            String[] bugFixCommitsTemp = new String[10000];
            
            String str1 = getBugFixCommitsMockus();
            System.out.println ("Revisions that were created because of a bug fix (Mockus) = " + str1);
            bugFixCommitsMockus = str1.trim().split("  ");
            
            for(int i = 0; i < bugFixCommitsMockus.length; i++)
                System.out.println ("Revisions that were created because of a bug fix in bugFixCommitsMockus["+i+"] array (Mockus) = " + bugFixCommitsMockus[i]);
            
            String str2 = getBugFixCommitsLamkanfi();
            System.out.println ("Revisions that were created because of a bug fix (Lamkanfi) = " + str2);
            bugFixCommitsLamkanfi = str2.trim().split("  ");
            
            for(int i = 0; i < bugFixCommitsLamkanfi.length; i++)
                System.out.println ("Revisions that were created because of a bug fix in bugFixCommitsLamkanfi["+i+"] array (Lamkanfi) = " + bugFixCommitsLamkanfi[i]);
            
            // Finding common commits in both arrays 
            
            for (int i = 0; i < bugFixCommitsMockus.length; i++) {
                for (int j = 0; j < bugFixCommitsLamkanfi.length; j++) {
                    if (bugFixCommitsMockus[i].equals(bugFixCommitsLamkanfi[j])) {
                        // got the duplicate element
                        //if (!bugFixCommits.contains(" " + bugFixCommitsMockus[i] + " ")) {
                            bugFixCommits += " " + bugFixCommitsMockus[i] + " ";
                        //}
                    }
                }
            } 
            System.out.println ("Revisions that were created because of a bug fix (Mockus and Lamkanfi) = " + bugFixCommits);
            
            bugFixCommitsTemp = bugFixCommits.trim().split("  ");
            
            for(int i = 0; i < bugFixCommitsTemp.length; i++)
                System.out.println ("Revisions that were created because of a bug fix in bugFixCommitsTemp["+i+"] array (Temp) = " + bugFixCommitsTemp[i]);
            
            
        } catch (Exception e) {
            System.out.println("error in getBugFixCommits = " + e);
        }

        return bugFixCommits;
    }
    
    public String getBugFixCommitsMockus() {
        String bugFixCommits = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputParameters.systemName + " commitlog.txt")));
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("brlcad commitlog.txt"))); // Have to make it variable
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ctags commitlog.txt"))); // Have to make it variable

            String str = "";
            String prevString = "";
                
            int commit = 0;
            while ((str = br.readLine()) != null) {
                if (str.trim().length() == 0) {
                    continue;
                }

                if (prevString.contains("--------------------------------")) {
                    //this is the starting of a commit report.
                    //we need to know the commit number.
                    String str1 = str.trim().split("[ ]+")[0].trim();
                    str1 = str1.substring(1);
                    commit = Integer.parseInt(str1);
                    //System.out.println (commit);
                } else {
                    //according to the study of Mockus
                    if (str.toLowerCase().contains("bug") || str.toLowerCase().contains("fix") || str.toLowerCase().contains("fixup") || str.toLowerCase().contains("error") || str.toLowerCase().contains("fail")) 
                    //if (str.contains ("bug") || str.contains("fix") || str.contains ("fixup") || str.contains ("error") || str.contains ("fail"))
                    {
                        if (!bugFixCommits.contains(" " + commit + " ")) {
                            bugFixCommits += " " + commit + " ";
                        }
                    }                                          
                }
                prevString = str;
            }
            br.close();
            //System.out.println ("Revisions that were created because of a bug fix = " + bugFixCommits);

        } catch (Exception e) {
            System.out.println("error in getBugFixCommitsMockus = " + e);
        }

        return bugFixCommits;
    }
    
    public String getBugFixCommitsLamkanfi() {
        String bugFixCommits = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputParameters.systemName + " commitlog.txt")));
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("brlcad commitlog.txt"))); // Have to make it variable
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ctags commitlog.txt"))); // Have to make it variable

            String str = "";
            String prevString = "";
            
            int commit = 0;
            while ((str = br.readLine()) != null) {
                if (str.trim().length() == 0) {
                    continue;
                }

                if (prevString.contains("--------------------------------")) {
                    //this is the starting of a commit report.
                    //we need to know the commit number.
                    String str1 = str.trim().split("[ ]+")[0].trim();
                    str1 = str1.substring(1);
                    commit = Integer.parseInt(str1);
                    //System.out.println (commit);
                } else {
                                            
                    // Severe bugs according to Lamkanfi
                    if (str.toLowerCase().contains("fault") || str.toLowerCase().contains("machin") || str.toLowerCase().contains("reboot") || str.toLowerCase().contains("reinstal") || str.toLowerCase().contains("lockup") || str.toLowerCase().contains("seemingli") || str.toLowerCase().contains("perman") || str.toLowerCase().contains("instantli") || str.toLowerCase().contains("segfault") || str.toLowerCase().contains("compil")
                        || str.toLowerCase().contains("hang") || str.toLowerCase().contains("freez") || str.toLowerCase().contains("deadlock") || str.toLowerCase().contains("thread") || str.toLowerCase().contains("slow") || str.toLowerCase().contains("anymor") || str.toLowerCase().contains("memori") || str.toLowerCase().contains("tick") || str.toLowerCase().contains("jvm") || str.toLowerCase().contains("adapt") 
                        || str.toLowerCase().contains("deadlock") || str.toLowerCase().contains("sigsegv") || str.toLowerCase().contains("relat") || str.toLowerCase().contains("caus") || str.toLowerCase().contains("snapshot") || str.toLowerCase().contains("segment") || str.toLowerCase().contains("core") || str.toLowerCase().contains("unexpectedli") || str.toLowerCase().contains("build") || str.toLowerCase().contains("loop")) 
                    {
                        if (!bugFixCommits.contains(" " + commit + " ")) {
                            bugFixCommits += " " + commit + " ";
                        }
                    }
                }
                prevString = str;
            }
            br.close();
            //System.out.println ("Revisions that were created because of a bug fix = " + bugFixCommits);

        } catch (Exception e) {
            System.out.println("error in getBugFixCommitsLamkanfi = " + e);
        }

        return bugFixCommits;
    }
    
    public CodeFragment[][] getChangedBugFixCommits() {

        SingleChange[] changedBugFixCommits = new SingleChange[50000];   
        SingleChange[][] changedBugFixCommits2D = new SingleChange[5000][5000]; // was 10000 before optimization
        CodeFragment[][] changedBugFixCommits2DNew = new CodeFragment[5000][5000];  // was 10000 before optimization

        try {
            String str = "";
            
            if (RQ4 == 0)
                str = getBugFixCommits();
            else if (RQ4 == 1)
                str = getBugFixCommitsRQ4();
            
            String[] bugFixCommits = new String[10000];  

            //SingleChange[] changes = new SingleChange[10000];
            SingleChange[] changes = db.getChangedRevisions();

            /*
            for(int j=0; changes[j] != null; j++){
            System.out.println("Revision [" + j + "]= " + changes[j].revision);
            }
             */
            bugFixCommits = str.split("  ");

            /*---------------------------------------- Preprocessing bugFixCommits Start ---------------------------------------------------------------*/
            String[] bugFixCommitsReverse = new String[10000];   
            int i = 0;
            for (int j = bugFixCommits.length - 1; j >= 0; j--) {
                bugFixCommitsReverse[i] = bugFixCommits[j];
                //System.out.println("Bug Fix Revision [" + i + "] in Reverse = " + bugFixCommitsReverse[i] + " Where j = " + j);
                i++;
            }
            int len = i;

            //Changing x to x-1 for revision numbers
            for (i = 0; i < len; i++) {
                bugFixCommitsReverse[i] = Integer.toString(Integer.parseInt(bugFixCommitsReverse[i].trim()) - 1);
                //System.out.println("Bug Fix Revision [" + i + "] decreased value by 1 = " + bugFixCommitsReverse[i]);
            }
            /*---------------------------------------- Preprocessing bugFixCommits End ---------------------------------------------------------------*/

            //Matching bug-fix commits with changed revisions and saving in 1D array
            
            int k = 0;
            for (i = 0; i < len; i++) {
                for (int j = 0; changes[j] != null; j++) {
                    if (bugFixCommitsReverse[i].equals(changes[j].revision)) {
                        changedBugFixCommits[k] = changes[j];
                        //System.out.println("Revision [" + k + "] in changedBugFixCommits = " + changedBugFixCommits[k].revision);
                        k++;
                    }
                }
            }
            
            //Matching bug-fix commits with changed revisions and saving in 2D array
            int a = 0, b = 0;
            for (i = 0; i < len; i++) {
                for (int j = 0; changes[j] != null; j++) {
                    if (bugFixCommitsReverse[i].equals(changes[j].revision)) {
                        changedBugFixCommits2D[a][b] = changes[j];
                        if(changes[j+1] != null){ 
                            if(changes[j].revision.equals(changes[j+1].revision)){
                                b++;
                                changedBugFixCommits2D[a][b] = changes[j+1];
                            }
                            else
                                a++;
                        }
                    }
                }
                b = 0;
            }
            // Changing the data type from SingleChange to CodeFragment of the 2D array          
            for(i = 0; i<changedBugFixCommits2D.length; i++){
                for(int j = 0; j<changedBugFixCommits2D.length; j++){
                    if(changedBugFixCommits2D[i][j] != null){
                        changedBugFixCommits2DNew[i][j] = new CodeFragment();
                        changedBugFixCommits2DNew[i][j].revision = Integer.parseInt(changedBugFixCommits2D[i][j].revision);
                        changedBugFixCommits2DNew[i][j].startline = Integer.parseInt(changedBugFixCommits2D[i][j].startline);
                        changedBugFixCommits2DNew[i][j].endline = Integer.parseInt(changedBugFixCommits2D[i][j].endline);
                        changedBugFixCommits2DNew[i][j].filepath = changedBugFixCommits2D[i][j].filepath;
                        changedBugFixCommits2DNew[i][j].changetype = changedBugFixCommits2D[i][j].changetype;
                    }
                }
            }
            
            int count = 0;
            for(i = 0; i<changedBugFixCommits2D.length; i++){
                for(int j = 0; j<changedBugFixCommits2D.length; j++){
                    if(changedBugFixCommits2D[i][j] != null){
                        System.out.println("getChangedBugFixCommits: getChangedBugFixCommits["+i+"]["+j+"].revision = " + changedBugFixCommits2D[i][j].revision 
                            + " Filepath = " + changedBugFixCommits2D[i][j].filepath + " Startline = " + changedBugFixCommits2D[i][j].startline 
                                + " Endline = " + changedBugFixCommits2D[i][j].endline + " Changetype = " + changedBugFixCommits2D[i][j].changetype);
                        count++;   
                    }
                }
            }
            System.out.println("Total number of changed bug-fix code fragments (CF) = " + count);
            
        } catch (Exception e) {
            System.out.println("error in getChangedBugFixCommits = " + e);
        }
        return changedBugFixCommits2DNew;
    }
    
    public void bugReplication(){
        try{
            // --------------------------Implementing RQ1----------------------------
            
            int countFragmentR = 0;
            int countFragmentM = 0;
            int countRevision = 0;
            
            ArrayList<CodeFragment> bugRepR = new ArrayList<>();
            ArrayList<CodeFragment> bugRepM = new ArrayList<>();
            
            
            System.out.println("---------------------------------------Regular Clone Analysis Starts Here--------------------------------------------\n");
            bugRepR = bugReplicationR();
            countFragmentR = bugRepR.size();
            
            System.out.println("-----------------------------------------Micro Clone Analysis Starts Here--------------------------------------------\n");
            bugRepM = bugReplicationM();
            countFragmentM = bugRepM.size();
            
            // Remove all duplicates
            bugRepR.removeAll(bugRepM);
            
            // Merge two arraylists
            bugRepR.addAll(bugRepM);
            
            System.out.println("\nThis is the array of replicated bugs after UNION operation in between Regular and Micro: ");
            for(int i=0; i<bugRepR.size(); i++){
                //System.out.println("\nThis is the array of replicated bugs after UNION operation in between Regular and Micro: i = " + i);
                bugRepR.get(i).getFragment();
                bugRepR.get(i).showFragment();
            }
            
            // Removing the duplicate values based on revisions only
            // Removing duplicate revisions so that we can count the distinct revisions only
            for(int i = 0; i < bugRepR.size(); i++){
                for(int j = i+1; j < bugRepR.size(); j++){
                    
                    if(bugRepR.get(i).revision == bugRepR.get(j).revision){
                        bugRepR.remove(j);
                        j--;                          
                    }
                }
            }
            
            System.out.println("\nThis is the array of replicated bugs after UNION operation in between Regular and Micro and after removing duplicate revision number: ");
            for(int i=0; i<bugRepR.size(); i++){
                //System.out.println("\nThis is the array of replicated bugs after UNION operation in between Regular and Micro and after removing duplicate revision number: i = " + i);
                bugRepR.get(i).getFragment();
                bugRepR.get(i).showFragment();
            }
            
            countRevision = bugRepR.size();
            
            // Answering RQ1
            // Calculating the average number of replicated bugs for both regular and micro clones
                        
            System.out.println("Total number of distinct clone fragments that experienced bug-replication in Regular clones = " + countFragmentR);
            System.out.println("Total number of distinct clone fragments that experienced bug-replication in Micro clones = " + countFragmentM);
            System.out.println("Total number of distinct revisions that experienced bug-replication in both Regular and Micro clones = " + countRevision);
            
            System.out.println("Distinct percentage of replicated bugs in regular clones per revision = " + (float) countFragmentR/countRevision);
            System.out.println("Distinct percentage of replicated bugs in micro clones per revision = " + (float) countFragmentM/countRevision);
            
    
        } catch(Exception e){
            System.out.println("error in BugReplication = " + e);
            e.printStackTrace();
        }
    }
    
    //-------------------------------------- This function implementing RQ2 ---------------------------------------
    
    public void bugReplicationRQ2(){
        try{
            
            // --------------------------Implementing RQ2 for Regular Clones----------------------------
            ArrayList<CodeFragment> bugRepR = new ArrayList<>();

            bugRepR = bugReplicationR();
  
            int classID1R = 0, classID2R = 0, countRepR = 1, nclonesR = 0, flagR = 0;
            
            for(int i = 0; i < bugRepR.size(); i++){
                for(int j = i+1; j < bugRepR.size(); j++){
                    //if(bugRepR.get(i).revision == bugRepR.get(j).revision && bugRepR.get(i).filepath.equals(bugRepR.get(j).filepath)){
                    if(bugRepR.get(i).revision == bugRepR.get(j).revision){
                        System.out.println("Revision number (R) = " + bugRepR.get(i).revision);
                        classID1R = getClassID(bugRepR.get(i));
                        classID2R = getClassID(bugRepR.get(j));
                        
                        if(classID1R == classID2R){
                            countRepR++;
                            i++;
                        }
                        flagR = 1;
                        
                    }
                    
                }
                if(flagR == 1){
                    nclonesR = nclonesR + getCloneNumber(bugRepR.get(i-1));
                    flagR = 0;
                }
                
                System.out.println("At i = " + i + " countRepR = " + countRepR);
                System.out.println("At i = " + i + " nclonesR = " + nclonesR);
                countRepR++;
            }
            
            // --------------------------Implementing RQ2 for Micro Clones----------------------------
            ArrayList<CodeFragment> bugRepM = new ArrayList<>();

            bugRepM = bugReplicationM();
  
            int classID1M = 0, classID2M = 0, countRepM = 1, nclonesM = 0, flagM = 0;
            
            for(int i = 0; i < bugRepM.size(); i++){
                for(int j = i+1; j < bugRepM.size(); j++){
                    //if(bugRepR.get(i).revision == bugRepR.get(j).revision && bugRepR.get(i).filepath.equals(bugRepR.get(j).filepath)){
                    if(bugRepM.get(i).revision == bugRepM.get(j).revision){
                        System.out.println("Revision number (M) = " + bugRepM.get(i).revision);
                        classID1M = getClassIDMicro(bugRepM.get(i));
                        classID2M = getClassIDMicro(bugRepM.get(j));
                        
                        if(classID1M == classID2M){
                            countRepM++;
                            i++;
                        }
                        flagM = 1;
                        
                    }
                    
                }
                if(flagM == 1){
                    nclonesM = nclonesM + getCloneNumberMicro(bugRepM.get(i-1));
                    flagM = 0;
                }
                
                System.out.println("At i = " + i + " countRepM = " + countRepM);
                System.out.println("At i = " + i + " nclonesM = " + nclonesM);
                countRepM++;
            }
        }catch(Exception e){
            System.out.println("Error in bugReplicationRQ2: " + e);
            e.printStackTrace();
        }
    }
    
    public int getCloneNumber(CodeFragment cf) {
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[1000][1000];
        int numClones = 0;
        try{
            
            //BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathRegular + cf.revision + "_blocks-blind-clones/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathRegular + cf.revision + "/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            
            String str = "";
            int i = -1;
            int j = -1;
            
            while((str = br.readLine()) != null){
                
                if(str.contains("<class ")){ 
                    numClones = Integer.parseInt(str.split("[ ]+")[2].trim().split("[\"]+")[1].trim());
                    i++;
                    j = -1;
                    continue;
                }
                
                if(str.contains("<source")){
                    j++;
                    cfFile[i][j] = new CodeFragment();
                    cfFile[i][j].revision = cf.revision;
                    
                    cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                    
                    cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                    
                    cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        
                    if (cfFile[i][j].filepath.contains("version-")) {
                        cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                        String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                        cfFile[i][j].filepath = filePath[1];

                        //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                            //+ " End Line = " + cfFile[i][j].endline);
                    }
                    
                    if(cf.filepath.equals(cfFile[i][j].filepath) && cf.startline == cfFile[i][j].startline && cf.endline == cfFile[i][j].endline)
                        return numClones;
                }               
            }
        }catch(Exception e){
            System.out.println("Error in getCloneNumber: " + e);
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getCloneNumberMicro(CodeFragment cf) {
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[5000][5000];
        int numClones = 0;
        try{
            
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathMicro + cf.revision + "_blocks-blind-clones/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            
            String str = "";
            int i = -1;
            int j = -1;
            
            while((str = br.readLine()) != null){
                
                if(str.contains("<class ")){  
                    numClones = Integer.parseInt(str.split("[ ]+")[2].trim().split("[\"]+")[1].trim());
                    i++;
                    j = -1;
                    continue;
                }
                
                if(str.contains("<source")){   
                    j++;
                    cfFile[i][j] = new CodeFragment();
                    cfFile[i][j].revision = cf.revision;
                    
                    cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                    
                    cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                    
                    cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        
                    if (cfFile[i][j].filepath.contains("version-")) {
                        cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                        String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                        cfFile[i][j].filepath = filePath[1];

                        //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                            //+ " End Line = " + cfFile[i][j].endline);
                    }
                    
                    if(cf.filepath.equals(cfFile[i][j].filepath) && cf.startline == cfFile[i][j].startline && cf.endline == cfFile[i][j].endline)
                        return numClones;
                }               
            }
        }catch(Exception e){
            System.out.println("Errro in getCloneNumberMicro: ");
            e.printStackTrace();
        }
        return 0;
    }
    
    //-------------------------------------- This function implementing RQ3 ---------------------------------------
    
    public void bugReplicationRQ3(){
        try{
            
            // --------------------------Implementing RQ3 for Regular Clones----------------------------
            
            bugReplicationR();
            
            System.out.println("Results of RQ3 Regular code clones:");
            System.out.println("Total Number of Distinct Bugs(revision) of code clones for Regular = " + countRevR);
            System.out.println("Total Distinct Number of Replicated Bug Revision in Regular code clone = " + countRevRepR);
            System.out.println("Percentage of Replicated Bugs in Regular code clones = " + (float) countRevRepR/countRevR*100 + "%\n");
            
            
            bugReplicationM();
            
            System.out.println("Results of RQ3 Micro code clones:");
            System.out.println("Total Number of Distinct Bugs(revision) of code clones for Micro = " + countRevM);
            System.out.println("Total Distinct Number of Replicated Bug Revision in Micro code clone = " + countRevRepM);
            System.out.println("Percentage of Replicated Bugs in Micro code clones = " + (float) countRevRepM/countRevM*100 + "%\n");
            
            
        }catch(Exception e){
            System.out.println("Error in bugReplicationRQ3: " + e);
            e.printStackTrace();
        }
    }
    
    //-------------------------------------- This function implementing RQ4 ---------------------------------------
    
    public void bugReplicationRQ4(){
        try{
            
            // --------------------------Implementing RQ4 for Regular Clones----------------------------
            
            RQ4 = 1;
            
        }catch(Exception e){
            System.out.println("Error in bugReplicationRQ4: " + e);
            e.printStackTrace();
        }
    }
    
    //-------------------------------------- This function implementing RQ5 ---------------------------------------
    
    public void bugReplicationRQ5(){
        try{
            // -----------------------------Implementing RQ5 for Regular Clones---------------------------------
            ArrayList<CodeFragment> bugRepR = new ArrayList<>();
            int lineNumberR = 0;
            int lineNumberRepR = 0;
            
            CodeFragment[][] changedBugFixCommits = new CodeFragment[500][500];   // was 10000 before optimization
            changedBugFixCommits = getChangedBugFixCommits();
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        //System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        lineNumberR += countLineNumber(changedBugFixCommits[i][j].revision);
                        //System.out.println("lineNumberR = " + lineNumberR);
                    }
                }
            }      
            
            bugRepR = bugReplicationR();
            
            for(int i=0; i<bugRepR.size(); i++){
                lineNumberRepR += countLineNumber(bugRepR.get(i).revision);
                //System.out.println("lineNumberRepR = " + lineNumberRepR);
            }
                   
            //System.out.println("******************************The Percentage of Line Coverage in Regular = " + (float) lineNumberRepR/lineNumberR*100 + "***********************************");
            
            // -----------------------------Implementing RQ5 for Micro Clones---------------------------------
            ArrayList<CodeFragment> bugRepM = new ArrayList<>();
            int lineNumberM = 0;
            int lineNumberRepM = 0;
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        //System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        lineNumberM += countLineNumberMicro(changedBugFixCommits[i][j].revision);
                        //System.out.println("lineNumberM = " + lineNumberM);
                    }
                }
            }      
            
            bugRepM = bugReplicationM();
            
            for(int i=0; i<bugRepM.size(); i++){
                lineNumberRepM += countLineNumberMicro(bugRepM.get(i).revision);
                //System.out.println("lineNumberRepM = " + lineNumberRepM);
            }
                   
            //System.out.println("******************************The Percentage of Line Coverage in Micro = " + (float) lineNumberRepM/lineNumberM*100 + "***********************************");
            
            System.out.println("Results for RQ5 is: \n");
            
            System.out.println("lineNumberR = " + lineNumberR);
            System.out.println("lineNumberRepR = " + lineNumberRepR);
            System.out.println("The Percentage of Line Coverage in Regular = " + (float) lineNumberRepR/lineNumberR*100);
            
            System.out.println("lineNumberM = " + lineNumberM);
            System.out.println("lineNumberRepM = " + lineNumberRepM);
            System.out.println("The Percentage of Line Coverage in Micro = " + (float) lineNumberRepM/lineNumberM*100);
            
        }catch(Exception e){
            System.out.println("Error in bugReplicationRQ5: " + e);
            e.printStackTrace();
        }
    }
    
    public int countLineNumber(int rev){
        int lineNumber = 0;
        try{
            // Here I use cfFile (a one dimensional array) to store each xml file. It will store each clone fragments (each source tag) in each row.
            CodeFragment[] cfFile = new CodeFragment[5000];

            File regularXmlFile = new File(InputParameters.pathRegular + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"); //All Type

            //if (regularXmlFile.exists() && rev <= 100) { // Tuning for feasible experiment for Jabref
            //if (regularXmlFile.exists() && rev <= 50) { // Tuning for feasible experiment for Carol    
            //if (regularXmlFile.exists() && rev <= 1025) { // Tuning for feasible experiment for Freecol
            if (regularXmlFile.exists()) {    
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(regularXmlFile))); // All Type

                String str;
                int i = 0;

                while((str = br.readLine()) != null){
                    if(str.contains("<source")){
                        cfFile[i] = new CodeFragment();
                        cfFile[i].revision = rev;

                        cfFile[i].filepath = str.split("[\"]+")[1].trim();
                        //System.out.println(" i = " + i + " cfFile[i].filepath = " + cfFile[i].filepath);

                        cfFile[i].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                        //System.out.println(" i = " + i + " cfFile[i].startline = " + cfFile[i].startline);

                        cfFile[i].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        //System.out.println(" i = " + i + " cfFile[i].endline = " + cfFile[i].endline);

                        int cloneSize = (cfFile[i].endline-cfFile[i].startline) + 1;
                        //System.out.println("cloneSize = " + cloneSize);

                        lineNumber += cloneSize;
                        //System.out.println("Total line numbers = " + lineNumber);

                        if (cfFile[i].filepath.contains("version-")) {
                            cfFile[i].filepath = cfFile[i].filepath.replaceAll(".ifdefed", "");

                            String[] filePath = cfFile[i].filepath.split("version-\\d*\\/");
                            cfFile[i].filepath = filePath[1];

                            //System.out.println("cfFile[" + i + "] = " + cfFile[i].filepath + " Start Line = " + cfFile[i].startline
                            //+ " End Line = " + cfFile[i].endline);
                        }
                        i++;
                    }
                }
            }           
        
        } catch(Exception e){
            System.out.println("Error in countLineNumber: " + e);
            e.printStackTrace();
        }
    return lineNumber;
    }
    
    public int countLineNumberMicro(int rev){
        int lineNumber = 0;
        try{
            // Here I use cfFile (a one dimensional array) to store each xml file. It will store each clone fragments (each source tag) in each row.
            CodeFragment[] cfFile = new CodeFragment[10000];

            File microXmlFile = new File(InputParameters.pathMicro + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"); //All Type

            //if (microXmlFile.exists() && rev <= 100) { // Tuning for feasible experiment for Jabref
            //if (microXmlFile.exists() && rev <= 50) { // Tuning for feasible experiment for Carol
            //if (microXmlFile.exists() && rev <= 1025) { // Tuning for feasible experiment for Freecol
            if (microXmlFile.exists()) {    
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(microXmlFile))); // All Type

                String str;
                int i = 0;

                while((str = br.readLine()) != null){
                    if(str.contains("<source")){
                        cfFile[i] = new CodeFragment();
                        cfFile[i].revision = rev;

                        cfFile[i].filepath = str.split("[\"]+")[1].trim();
                        //System.out.println(" i = " + i + " cfFile[i].filepath = " + cfFile[i].filepath);

                        cfFile[i].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                        //System.out.println(" i = " + i + " cfFile[i].startline = " + cfFile[i].startline);

                        cfFile[i].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        //System.out.println(" i = " + i + " cfFile[i].endline = " + cfFile[i].endline);

                        int cloneSize = (cfFile[i].endline-cfFile[i].startline) + 1;
                        //System.out.println("cloneSize = " + cloneSize);

                        lineNumber += cloneSize;
                        //System.out.println("Total line numbers = " + lineNumber);

                        if (cfFile[i].filepath.contains("version-")) {
                            cfFile[i].filepath = cfFile[i].filepath.replaceAll(".ifdefed", "");

                            String[] filePath = cfFile[i].filepath.split("version-\\d*\\/");
                            cfFile[i].filepath = filePath[1];

                            //System.out.println("cfFile[" + i + "] = " + cfFile[i].filepath + " Start Line = " + cfFile[i].startline
                            //+ " End Line = " + cfFile[i].endline);
                        }
                        i++;
                    }
                }
            }           
        
        } catch(Exception e){
            System.out.println("Error in countLineNumberMicro: " + e);
            e.printStackTrace();
        }
    return lineNumber;
    }
    
    public ArrayList<CodeFragment> bugReplicationR(){
        ArrayList<CodeFragment> bugRep = new ArrayList<>();
        try{          
            CodeFragment[][] cloneFragmentPair = new CodeFragment[5000][2]; // was 10000 before optimization
            
            cloneFragmentPair = isClonePair();
            
            if(cloneFragmentPair != null)
                for (int i = 0; i < cloneFragmentPair.length; i++) 
                    for (int j = 0; j < 2; j++) 
                        if(cloneFragmentPair[i][j] != null)
                            System.out.println("bugReplicationR: cfp["+i+"]["+j+"].revision = " + cloneFragmentPair[i][j].revision + " Filepath = " + cloneFragmentPair[i][j].filepath
                                + " Startline = " + cloneFragmentPair[i][j].startline + " Endline = " + cloneFragmentPair[i][j].endline);
            
            // For RQ3
            int countRevision = 1;
            if(cloneFragmentPair != null)
                for (int i = 0; cloneFragmentPair[i][0] != null; i++) 
                    if(cloneFragmentPair[i+1][0] != null)
                        if(cloneFragmentPair[i][0].revision != cloneFragmentPair[i+1][0].revision)
                            countRevision++;
                    
                    
            System.out.println("Total number of distinct bugs(revision) of code clones for Regular = " + countRevision);
            
            countRevR = countRevision;
            
            
            // Finding Replicated Bugs
            int numReplicatedBugFixCommits = 0;
            for(int x = 0; cloneFragmentPair[x][0] != null; x++){
                if(cloneFragmentPair[x][0] != null && cloneFragmentPair[x][1] != null){
                        CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];

                        cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[x][0]);
                        cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[x][1]);

                        if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                            if(isClonePairBinary(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                                numReplicatedBugFixCommits++;
                                System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found (Regular)////////////////////////////////////////////////////////////////////////////");
                                //System.out.println("numReplicatedBugFixCommits for Regular Clones = " + numReplicatedBugFixCommits);
                                
                                bugRep.add(cloneFragmentPair[x][0]);
                                bugRep.add(cloneFragmentPair[x][1]);
                            }
                        }
                }
            }
            System.out.println("Total Number of Pairs of Replicated Bug-Fix Commits for Regular Clones = " + numReplicatedBugFixCommits);
            
            System.out.println("\nThis is the array of replicated bugs: ");
            for(int i=0; i<bugRep.size(); i++){
                //System.out.println("\nThis is the array of replicated bugs: i = " + i);
                bugRep.get(i).getFragment();
                bugRep.get(i).showFragment();
            }
            
            // Removing the duplicate values
            for(int i = 0; i < bugRep.size(); i++){
                for(int j = i+1; j < bugRep.size(); j++){
                    
                    if(bugRep.get(i).revision == bugRep.get(j).revision && bugRep.get(i).filepath.equals(bugRep.get(j).filepath) 
                        && bugRep.get(i).startline == bugRep.get(j).startline && bugRep.get(i).endline == bugRep.get(j).endline){
                        bugRep.remove(j);
                        j--;                          
                    }
                }
            }
            
            System.out.println("\nThis is the array of replicated bugs after removing duplicate objects: ");
            for(int i=0; i<bugRep.size(); i++){
                //System.out.println("\nThis is the array of replicated bugs after removing duplicate objects: i = " + i);
                bugRep.get(i).getFragment();
                bugRep.get(i).showFragment();
            }
            
            System.out.println("\nTotal distinct number of replicated bugs in regular code clone = " + bugRep.size());
            
            // Counting the distinct number replicated bug in regular clones per revision
            int countRevisionRep = 1;
            for(int i = 0; i < bugRep.size()-1; i++)
                if(bugRep.get(i).revision != bugRep.get(i+1).revision)
                    countRevisionRep++;   
            
            // For RQ3
            System.out.println("\nTotal distinct number of replicated bug revision in regular code clone = " + countRevisionRep);
            
            countRevRepR = countRevisionRep;
            
            float averageCountRegular = (float)bugRep.size()/countRevisionRep;
                        
            System.out.println("\nDistinct number of replicated bugs in regular clones per revision = " + averageCountRegular + "\n");
            
        }catch(Exception e){
            System.out.println("error in BugReplicationR = " + e);
            e.printStackTrace();
        }
        return bugRep;
    }
    
    public ArrayList<CodeFragment> bugReplicationM(){
        ArrayList<CodeFragment> bugRep = new ArrayList<>();
        try{
            CodeFragment[][] cloneFragmentPair = new CodeFragment[50000][2];  
            
            cloneFragmentPair = isClonePairMicro();
            
            if(cloneFragmentPair != null)
                for (int i = 0; i < cloneFragmentPair.length; i++) 
                    for (int j = 0; j < 2; j++) 
                        if(cloneFragmentPair[i][j] != null)
                            System.out.println("bugReplicationM: After excluding cloneFragmentPair["+i+"]["+j+"].revision = " + cloneFragmentPair[i][j].revision + " Filepath = " 
                                + cloneFragmentPair[i][j].filepath + " Startline = " + cloneFragmentPair[i][j].startline + " Endline = " + cloneFragmentPair[i][j].endline);
            
            // For RQ3
            int countRevision = 1;
            if(cloneFragmentPair != null)
                for (int i = 0; cloneFragmentPair[i][0] != null; i++)
                    if(cloneFragmentPair[i+1][0] != null)
                        if(cloneFragmentPair[i][0].revision != cloneFragmentPair[i+1][0].revision)
                            countRevision++;
                    
            System.out.println("Total number of distinct bugs(revision) of code clones for Micro = " + countRevision);
            
            countRevM = countRevision;
            
            // Finding Replicated Bugs
            int numReplicatedBugFixCommits = 0;
            for(int x = 0; cloneFragmentPair[x][0] != null; x++){
                if(cloneFragmentPair[x][0] != null && cloneFragmentPair[x][1] != null){
                        CodeFragment[] cloneFragmentPairINR = new CodeFragment[2];
                        
                        cloneFragmentPairINR[0] = getInstanceInNextRevision(cloneFragmentPair[x][0]);
                        cloneFragmentPairINR[1] = getInstanceInNextRevision(cloneFragmentPair[x][1]);

                        if(cloneFragmentPairINR[0] != null && cloneFragmentPairINR[1] != null){
                            if(isClonePairBinaryMicro(cloneFragmentPairINR[0], cloneFragmentPairINR[1]) == 1){
                                numReplicatedBugFixCommits++;
                                System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found (Micro)////////////////////////////////////////////////////////////////////////////");
                                System.out.println("numReplicatedBugFixCommits for Micro Clones = " + numReplicatedBugFixCommits);
                                
                                bugRep.add(cloneFragmentPair[x][0]);
                                bugRep.add(cloneFragmentPair[x][1]);
                            }
                        }
                }
            }
            
            System.out.println("Total Number of Pairs of Replicated Bug-Fix Commits for Micro Clones = " + numReplicatedBugFixCommits);
            
            System.out.println("\nThis is the array of replicated bugs: ");
            for(int i=0; i<bugRep.size(); i++){
                System.out.println("\nThis is the array of replicated bugs: i = " + i);
                bugRep.get(i).getFragment();
                bugRep.get(i).showFragment();
            }
            
            // Removing the duplicate values
            for(int i = 0; i < bugRep.size(); i++){
                for(int j = i+1; j < bugRep.size(); j++){
                    
                    if(bugRep.get(i).revision == bugRep.get(j).revision && bugRep.get(i).filepath.equals(bugRep.get(j).filepath) 
                        && bugRep.get(i).startline == bugRep.get(j).startline && bugRep.get(i).endline == bugRep.get(j).endline){
                        bugRep.remove(j);
                        j--;                          
                    }
                }
            }
            
            System.out.println("\nThis is the array of replicated bugs after removing duplicate objects: ");
            for(int i=0; i<bugRep.size(); i++){
                System.out.println("\nThis is the array of replicated bugs after removing duplicate objects: i = " + i);
                bugRep.get(i).getFragment();
                bugRep.get(i).showFragment();
            }
            
            System.out.println("\nTotal distinct number of replicated bugs in micro code clone = " + bugRep.size());
            
            // Counting the distinct number replicated bug in micro clones per revision
            int countRevisionRep = 1;
            for(int i = 0; i < bugRep.size()-1; i++)
                if(bugRep.get(i).revision != bugRep.get(i+1).revision)
                    countRevisionRep++;   
            
            // for RQ3
            System.out.println("\nTotal distinct number of replicated bug revision in micro code clone = " + countRevisionRep);
            
            countRevRepM = countRevisionRep;
            
            float averageCountMicro = (float)bugRep.size()/countRevisionRep;
                        
            System.out.println("\nDistinct number of replicated bugs in micro clones per revision = " + averageCountMicro + "\n");
            
        }catch(Exception e){
            System.out.println("error in BugReplicationM = " + e);
            e.printStackTrace();
        }
        return bugRep;
    }
    
    public CodeFragment[][] isClonePair(){
        CodeFragment[][] cfp = new CodeFragment[5000][2];   // was 10000 before optimization
        try{
            CodeFragment[][] changedBugFixCommits = new CodeFragment[500][500];   // was 10000 before optimization
            changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment[][] cfFile = new CodeFragment[500][500];
            CodeFragment[] cfFileMatch = new CodeFragment[5000];
            int x = 0;
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        cfFile = fileRead(changedBugFixCommits[i][j].revision);
                        
                        
                        // Looping through the xml file of each revision
                        for (int m = 0; m < cfFile.length; m++) {
                            for (int n = 0; n < cfFile.length; n++) {

                                if (cfFile[m][n] != null){
                                    
                                    if(changedBugFixCommits[i][j].filepath.equals(cfFile[m][n].filepath)){
                                        
                                        // Checking for matches with changed bug-fix commits with each line of xml file of a particular revision
                                        // Matches if line numbers of code fragment from changedBugFixCommits are overlapping with line numbers range of cfFile
                                        if(((changedBugFixCommits[i][j].startline >= cfFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfFile[m][n].endline))
                                            ||((changedBugFixCommits[i][j].startline <= cfFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].endline >= cfFile[m][n].startline)) 
                                            ||((changedBugFixCommits[i][j].startline >= cfFile[m][n].startline) && (changedBugFixCommits[i][j].endline >= cfFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].startline <= cfFile[m][n].endline))){
                                        
                                            System.out.println("*********************************************** File Name matched (Regular) ***********************************************");
                                            
                                            //System.out.println("Matched CF from changedBugFixCommits["+i+"]["+j+"] = " + changedBugFixCommits[i][j].filepath + " Start Line = " 
                                                    //+ changedBugFixCommits[i][j].startline + " End Line = " + changedBugFixCommits[i][j].endline);
                                            
                                            //System.out.println("Matched CF1 from cfFile["+m+"]["+n+"] = " + cfFile[m][n].filepath + " Start Line = " + cfFile[m][n].startline 
                                                //+ " End Line = " + cfFile[m][n].endline);
                                            
                                            // Saving the matched entries into a separate 1D array
                                            cfFileMatch[x] = cfFile[m][n];
                                            x++;
                                        }
                                    }
                                }
                            }
                        }
                        
                        
                    }
                }
            }
            int len = 0;
            for(x = 0; cfFileMatch[x] != null; x++){
                System.out.println("cfFileMatch["+x+"] Revision = " + cfFileMatch[x].revision + " Filepath = " + cfFileMatch[x].filepath 
                    + " Startline = " + cfFileMatch[x].startline + " Endline = " + cfFileMatch[x].endline);
                len++;
            }
            System.out.println("len = " + len);
            
            // Delete duplicate values from cfFileMatch[x] array
            for(int i = 0; i < len; i++){
                for(int j = i+1; j < len; ){
                    if(cfFileMatch[i].revision == cfFileMatch[j].revision && cfFileMatch[i].filepath.equals(cfFileMatch[j].filepath) 
                            && cfFileMatch[i].startline == cfFileMatch[j].startline && cfFileMatch[i].endline == cfFileMatch[j].endline){
                        for(x = j; x < len; x++){
                            cfFileMatch[x] = cfFileMatch[x+1];
                        }
                        len--;    
                    }
                    else{
                        j++;
                    }
                }
            }
            
            System.out.println("After removing duplicate values: ");
            for(x = 0; cfFileMatch[x] != null; x++){
                System.out.println("cfFileMatch["+x+"] Revision = " + cfFileMatch[x].revision + " Filepath = " + cfFileMatch[x].filepath 
                    + " Startline = " + cfFileMatch[x].startline + " Endline = " + cfFileMatch[x].endline);
            }
            
            int classID1 = 0, classID2 = 0;
            x = 0;
            for(int i = 0; cfFileMatch[i] != null; i++){
                for(int j = i+1; cfFileMatch[j] != null; j++){
                    if(cfFileMatch[i].revision == cfFileMatch[j].revision){
                        System.out.println("Revision = " + cfFileMatch[i].revision);
                        
                        classID1 = getClassID(cfFileMatch[i]);
                        //System.out.println("classID1 in Regular = " + classID1);
                        
                        classID2 = getClassID(cfFileMatch[j]);
                        //System.out.println("classID2 in Regular = " + classID2 + "\n");
                        
                        if(classID1 == classID2){
                            System.out.println("********************************************Pair Found (Regular)********************************************");
                            cfp[x][0] = cfFileMatch[i];
                            cfp[x][1] = cfFileMatch[j];
                            x++;
                        }
                    }
                }
            }
            
        }catch (Exception e) {
            System.out.println("error in method isClonePair = " + e);
            e.printStackTrace();
        }
        return cfp;
    }
    
    public CodeFragment[][] isClonePairMicro(){
        CodeFragment[][] cfpMicro = new CodeFragment[50000][2]; 
        try{
            CodeFragment[][] changedBugFixCommits = new CodeFragment[500][500]; // was 10000 before optimization
            changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment[][] cfXmlFileMicro = new CodeFragment[5000][5000]; // was 10000 before optimization
            
            CodeFragment[] cfXmlFileMatch = new CodeFragment[10000];    // was 50000 before optimization
            
            CodeFragment[][] cfpReg = new CodeFragment[10000][2];   // was 50000 before optimization
            
            int x = 0;
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        cfXmlFileMicro = fileReadMicro(changedBugFixCommits[i][j].revision);
                        //System.out.println("");
                        
                        // Looping through the xml file of each revision
                        for (int m = 0; m < cfXmlFileMicro.length; m++) {
                            for (int n = 0; n < cfXmlFileMicro.length; n++) {

                                if (cfXmlFileMicro[m][n] != null){
                                    
                                    if(changedBugFixCommits[i][j].filepath.equals(cfXmlFileMicro[m][n].filepath)){
                                        
                                        // Checking for matches with changed bug-fix commits with each line of xml file of a particular revision
                                        // Matches if line numbers of code fragment from changedBugFixCommits are overlapping with line numbers range of cfXmlFileMicro
                                        if(((changedBugFixCommits[i][j].startline >= cfXmlFileMicro[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFileMicro[m][n].endline))
                                            ||((changedBugFixCommits[i][j].startline <= cfXmlFileMicro[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFileMicro[m][n].endline) 
                                                && (changedBugFixCommits[i][j].endline >= cfXmlFileMicro[m][n].startline)) 
                                            ||((changedBugFixCommits[i][j].startline >= cfXmlFileMicro[m][n].startline) && (changedBugFixCommits[i][j].endline >= cfXmlFileMicro[m][n].endline) 
                                                && (changedBugFixCommits[i][j].startline <= cfXmlFileMicro[m][n].endline))){
                                        
                                            System.out.println("*********************************************** File Name matched (Micro) ***********************************************");
                                            
                                            System.out.println("Matched CF from changedBugFixCommits["+i+"]["+j+"] = " + changedBugFixCommits[i][j].filepath + " Start Line = " 
                                                + changedBugFixCommits[i][j].startline + " End Line = " + changedBugFixCommits[i][j].endline);
                                            
                                            System.out.println("Matched CF1 from cfXmlFileMicro["+m+"]["+n+"] = " + cfXmlFileMicro[m][n].filepath + " Start Line = " 
                                                + cfXmlFileMicro[m][n].startline + " End Line = " + cfXmlFileMicro[m][n].endline);
                                            
                                            // Saving the matched entries into a separate 1D array
                                            cfXmlFileMatch[x] = cfXmlFileMicro[m][n];
                                            x++;
                                        }
                                    }
                                }
                            }
                        }
                        
                        
                    }
                }
            }
            int len = 0;
            for(x = 0; cfXmlFileMatch[x] != null; x++){
                System.out.println("cfXmlFileMatch["+x+"] Revision = " + cfXmlFileMatch[x].revision + " Filepath = " + cfXmlFileMatch[x].filepath 
                    + " Startline = " + cfXmlFileMatch[x].startline + " Endline = " + cfXmlFileMatch[x].endline);
                len++;
            }
            System.out.println("len = " + len);
            
            // Delete duplicate values from cfXmlFileMatch[x] array
            for(int i = 0; i < len; i++){
                for(int j = i+1; j < len; ){
                    if(cfXmlFileMatch[i].revision == cfXmlFileMatch[j].revision && cfXmlFileMatch[i].filepath.equals(cfXmlFileMatch[j].filepath) 
                            && cfXmlFileMatch[i].startline == cfXmlFileMatch[j].startline && cfXmlFileMatch[i].endline == cfXmlFileMatch[j].endline){
                        for(x = j; x < len; x++){
                            cfXmlFileMatch[x] = cfXmlFileMatch[x+1];
                        }
                        len--;    
                    }
                    else{
                        j++;
                    }
                }
            }
            
            System.out.println("After removing duplicate values: ");
            for(x = 0; cfXmlFileMatch[x] != null; x++){
                System.out.println("cfXmlFileMatch["+x+"] Revision = " + cfXmlFileMatch[x].revision + " Filepath = " + cfXmlFileMatch[x].filepath 
                    + " Startline = " + cfXmlFileMatch[x].startline + " Endline = " + cfXmlFileMatch[x].endline);
            }
            
            int classID1 = 0, classID2 = 0;
            x = 0;
            for(int i = 0; cfXmlFileMatch[i] != null; i++){
                for(int j = i+1; cfXmlFileMatch[j] != null; j++){
                    if(cfXmlFileMatch[i].revision == cfXmlFileMatch[j].revision){
                        System.out.println("Revision = " + cfXmlFileMatch[i].revision);
                        
                        classID1 = getClassIDMicro(cfXmlFileMatch[i]);
                        //System.out.println("classID1 in Micro = " + classID1);
                        
                        classID2 = getClassIDMicro(cfXmlFileMatch[j]);
                        //System.out.println("classID2 in Micro = " + classID2 + "\n");
                        
                        if(classID1 == classID2){
                            System.out.println("********************************************Pair Found (Micro)********************************************");
                            cfpMicro[x][0] = cfXmlFileMatch[i];
                            cfpMicro[x][1] = cfXmlFileMatch[j];
                            x++;
                            System.out.println("x = " + x);
                        }
                        
                    }
                }
            }
            
        cfpReg = isClonePair();   
        
        if(cfpMicro != null)
                for (int i = 0; i < cfpMicro.length; i++) 
                    for (int j = 0; j < 2; j++) 
                        if(cfpMicro[i][j] != null)
                            System.out.println("isClonePairMicro: Before excluding cfpMicro["+i+"]["+j+"].revision = " + cfpMicro[i][j].revision + " Filepath = " 
                                + cfpMicro[i][j].filepath + " Startline = " + cfpMicro[i][j].startline + " Endline = " + cfpMicro[i][j].endline);
        
        // Eliminating micro-clone pairs which reside in regular clone pairs****************************IMPORTANT**********************************************
        for(int i = 0; cfpMicro[i][0] != null; i++){
            for(int j = 0; cfpReg[j][0] != null; j++){
                if(cfpMicro[i][0].filepath.equals(cfpReg[j][0].filepath) && cfpMicro[i][1].filepath.equals(cfpReg[j][1].filepath)){
                    if( (cfpMicro[i][0].startline >= cfpReg[j][0].startline && cfpMicro[i][0].endline <= cfpReg[j][0].endline) 
                      &&(cfpMicro[i][1].startline >= cfpReg[j][1].startline && cfpMicro[i][1].endline <= cfpReg[j][1].endline) ){
                        
                        System.out.println("isClonePairMicro: Deleted Pair cfpMicro["+i+"][0].revision = " + cfpMicro[i][0].revision + " Filepath = " 
                                + cfpMicro[i][0].filepath + " Startline = " + cfpMicro[i][0].startline + " Endline = " + cfpMicro[i][0].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpReg["+j+"][0].revision = " + cfpReg[j][0].revision + " Filepath = " 
                                + cfpReg[j][0].filepath + " Startline = " + cfpReg[j][0].startline + " Endline = " + cfpReg[j][0].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpMicro["+i+"][1].revision = " + cfpMicro[i][1].revision + " Filepath = " 
                                + cfpMicro[i][1].filepath + " Startline = " + cfpMicro[i][1].startline + " Endline = " + cfpMicro[i][1].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpReg["+j+"][1].revision = " + cfpReg[j][1].revision + " Filepath = " 
                                + cfpReg[j][1].filepath + " Startline = " + cfpReg[j][1].startline + " Endline = " + cfpReg[j][1].endline);
                        for(x = i; cfpMicro[x][0] != null && cfpMicro[x+1][0] != null; x++){
                            cfpMicro[x][0] = cfpMicro[x+1][0];
                            cfpMicro[x][1] = cfpMicro[x+1][1];
                        }
                    }
                }
                else if(cfpMicro[i][0].filepath.equals(cfpReg[j][1].filepath) && cfpMicro[i][1].filepath.equals(cfpReg[j][0].filepath)){
                    if( (cfpMicro[i][0].startline >= cfpReg[j][1].startline && cfpMicro[i][0].endline <= cfpReg[j][1].endline) 
                      &&(cfpMicro[i][1].startline >= cfpReg[j][0].startline && cfpMicro[i][1].endline <= cfpReg[j][0].endline) ){
                        
                        System.out.println("isClonePairMicro: Deleted Pair cfpMicro["+i+"][0].revision = " + cfpMicro[i][0].revision + " Filepath = " 
                                + cfpMicro[i][0].filepath + " Startline = " + cfpMicro[i][0].startline + " Endline = " + cfpMicro[i][0].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpReg["+j+"][0].revision = " + cfpReg[j][0].revision + " Filepath = " 
                                + cfpReg[j][0].filepath + " Startline = " + cfpReg[j][0].startline + " Endline = " + cfpReg[j][0].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpMicro["+i+"][1].revision = " + cfpMicro[i][1].revision + " Filepath = " 
                                + cfpMicro[i][1].filepath + " Startline = " + cfpMicro[i][1].startline + " Endline = " + cfpMicro[i][1].endline);
                        System.out.println("isClonePairMicro: Deleted Pair cfpReg["+j+"][1].revision = " + cfpReg[j][1].revision + " Filepath = " 
                                + cfpReg[j][1].filepath + " Startline = " + cfpReg[j][1].startline + " Endline = " + cfpReg[j][1].endline);
                        
                        for(x = i; cfpMicro[x][0] != null && cfpMicro[x+1][0] != null; x++){
                            cfpMicro[x][0] = cfpMicro[x+1][0];
                            cfpMicro[x][1] = cfpMicro[x+1][1];
                        }
                    }
                }
            }
        }
            
        }catch (Exception e) {
            System.out.println("error in method isClonePairMicro = " + e);
            e.printStackTrace();
        }
        return cfpMicro;
    }
    
    public CodeFragment[][] fileRead(int rev){
        
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[1000][1000];
        try{
            //File regularXmlFile = new File(InputParameters.pathRegular + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"); //All Type
            File regularXmlFile = new File(InputParameters.pathRegular + rev + "/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"); //All Type
            
            //if (regularXmlFile.exists() && rev <= 100) { // Tuning for feasible experiment for Jabref
            //if (regularXmlFile.exists() && rev <= 50) { // Tuning for feasible experiment for Carol
            //if (regularXmlFile.exists() && rev <= 1025) { // Tuning for feasible experiment for Freecol    
            if (regularXmlFile.exists()) { 
                BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (regularXmlFile))); // All Type           
                //BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathRegular + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
                String str = "";
                int i = -1;
                int j = -1;
            
                while((str = br.readLine()) != null){
                
                    if(str.contains("<class ")){  
                        i++;
                        j = -1;
                        continue;
                    }
                
                    if(str.contains("<source")){
                    
                        j++;    
                    
                        cfFile[i][j] = new CodeFragment();
                        cfFile[i][j].revision = rev;
                    
                        cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                        //System.out.println(" i = " + i + " j = " + j + " cfFile[i][j].filepath = " + cfFile[i][j].filepath);
                    
                        cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                        //System.out.println(" i = " + i + " j = " + j + " cfFile[i][j].startline = " + cfFile[i][j].startline);
                    
                        cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        //System.out.println(" i = " + i + " j = " + j + " cfFile[i][j].endline = " + cfFile[i][j].endline);
                        
                        if (cfFile[i][j].filepath.contains("version-")) {
                            cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                            String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                            cfFile[i][j].filepath = filePath[1];

                            //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                                //+ " End Line = " + cfFile[i][j].endline);
                        }                      
                    }               
                }
            }
            
        } catch(Exception e){
            System.out.println("Error in method fileRead()." + e);
            e.printStackTrace();
        }
        return cfFile;
    
    }
    
    public CodeFragment[][] fileReadMicro(int rev){
        
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[5000][5000];
        try{
            File microXmlFile = new File(InputParameters.pathMicro + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"); //All Type
            
            //if (microXmlFile.exists() && rev <= 100) { // Tuning for feasible experiment for Jabref
            //if (microXmlFile.exists() && rev <= 50) { // Tuning for feasible experiment for Carol
            //if (microXmlFile.exists() && rev <= 1025) { // Tuning for feasible experiment for Freecol
            if (microXmlFile.exists()) {     
                BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathMicro + rev + "_blocks-blind-clones/version-" + rev + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            
                String str = "";
                int i = -1;
                int j = -1;
            
                while((str = br.readLine()) != null){
                
                    if(str.contains("<class ")){  
                        i++;
                        j = -1;
                        continue;
                    }
                
                    if(str.contains("<source")){
                        
                        j++;
                        cfFile[i][j] = new CodeFragment();
                        cfFile[i][j].revision = rev;
                    
                        cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                    
                        cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                    
                        cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        
                        if (cfFile[i][j].filepath.contains("version-")) {
                            cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                            String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                            cfFile[i][j].filepath = filePath[1];

                            //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                                //+ " End Line = " + cfFile[i][j].endline);
                        }                      
                    }               
                }
            }
        
        } catch(Exception e){
            System.out.println("error in method fileReadMicro()." + e);
            e.printStackTrace();
        }
        return cfFile;
    
    }
    
    public int getClassID(CodeFragment cf){
        
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[1000][1000];
        int classID = 0;
        try{
            
            //BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathRegular + cf.revision + "_blocks-blind-clones/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathRegular + cf.revision + "/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            String str = "";
            int i = -1;
            int j = -1;
            
            while((str = br.readLine()) != null){
                
                if(str.contains("<class ")){ 
                    classID = Integer.parseInt(str.split("[ ]+")[1].trim().split("[\"]+")[1].trim());
                    i++;
                    j = -1;
                    continue;
                }
                
                if(str.contains("<source")){
                    //classID = i;
                    j++;
                    cfFile[i][j] = new CodeFragment();
                    cfFile[i][j].revision = cf.revision;
                    
                    cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                    
                    cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                    
                    cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        
                    if (cfFile[i][j].filepath.contains("version-")) {
                        cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                        String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                        cfFile[i][j].filepath = filePath[1];

                        //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                            //+ " End Line = " + cfFile[i][j].endline);
                    }
                    
                    if(cf.filepath.equals(cfFile[i][j].filepath) && cf.startline == cfFile[i][j].startline && cf.endline == cfFile[i][j].endline)
                        return classID;
                }               
            }
        
        } catch(Exception e){
            System.out.println("Error in method getClassID()." + e);
            e.printStackTrace();
        }
        return 0;
    
    }
    
    public int getClassIDMicro(CodeFragment cf){
        
        // In this method I use cfFile (a two dimensional array) to store each xml file. In first dimension it will store the class number (classID) 
        // and in second dimension it will store each clone fragments (nclones in source tags).
        CodeFragment[][] cfFile = new CodeFragment[5000][5000];
        int classID = 0;
        try{
            
            BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (InputParameters.pathMicro + cf.revision + "_blocks-blind-clones/version-" + cf.revision + "_blocks-blind-clones-0.30-classes.xml"))); // All Type
            
            String str = "";
            int i = -1;
            int j = -1;
            
            while((str = br.readLine()) != null){
                
                if(str.contains("<class ")){  
                    classID = Integer.parseInt(str.split("[ ]+")[1].trim().split("[\"]+")[1].trim());
                    i++;
                    j = -1;
                    continue;
                }
                
                if(str.contains("<source")){
                    //classID = i;    
                    j++;
                    cfFile[i][j] = new CodeFragment();
                    cfFile[i][j].revision = cf.revision;
                    
                    cfFile[i][j].filepath = str.split("[\"]+")[1].trim();
                    
                    cfFile[i][j].startline = Integer.parseInt(str.split("[\"]+")[3].trim());
                    
                    cfFile[i][j].endline = Integer.parseInt(str.split("[\"]+")[5].trim());
                        
                    if (cfFile[i][j].filepath.contains("version-")) {
                        cfFile[i][j].filepath = cfFile[i][j].filepath.replaceAll(".ifdefed", "");
                                
                        String[] filePath = cfFile[i][j].filepath.split("version-\\d*\\/");
                        cfFile[i][j].filepath = filePath[1];

                        //System.out.println("cfFile[" + i + "][" + j + "] = " + cfFile[i][j].filepath + " Start Line = " + cfFile[i][j].startline 
                            //+ " End Line = " + cfFile[i][j].endline);
                    }
                    
                    if(cf.filepath.equals(cfFile[i][j].filepath) && cf.startline == cfFile[i][j].startline && cf.endline == cfFile[i][j].endline)
                        return classID;
                }               
            }
        
        } catch(Exception e){
            System.out.println("Error in method getClassIDMicro()." + e);
            e.printStackTrace();
        }
        return 0;
    
    }
    
    public int isClonePairBinary(CodeFragment cf1, CodeFragment cf2){
        int pair = 0;
        try{
            int classID1 = 0, classID2 = 0;
            
            classID1 = getClassID(cf1);

            classID2 = getClassID(cf2);
            
            if(classID1 == classID2){
                pair = 1;
            }
        } catch(Exception e){
            System.out.println("error in method isClonePairBinary()." + e);
            e.printStackTrace();
        }
        return pair;
    
    }
    
    public int isClonePairBinaryMicro(CodeFragment cf1, CodeFragment cf2){
        int pair = 0;
        try{
            int classID1 = 0, classID2 = 0;
            
            classID1 = getClassIDMicro(cf1);

            classID2 = getClassIDMicro(cf2);
            
            if(classID1 == classID2){
                pair = 1;
            }
        } catch(Exception e){
            System.out.println("error in method isClonePairBinaryMicro()." + e);
            e.printStackTrace();
        }
        return pair;
    
    }
    
    public CodeFragment getInstanceInNextRevision(CodeFragment cf) {
        try {
            CodeFragment instance = new CodeFragment();

            int crevision = cf.revision;
            int nrevision = crevision + 1;

            int nstartline = 999999999;
            int nendline = -1;

            int changed = 0;
            

            String cfilepath = InputParameters.pathSystem + crevision + "/" + cf.filepath; 
            String nfilepath = InputParameters.pathSystem + nrevision + "/" + cf.filepath; 
                    
            File file = new File(nfilepath);
            if (!file.exists()) {
                return null;
            }

            String[][] filecompare = cc.compareFiles(cfilepath, nfilepath);

            for (int i = 0; filecompare[i][0] != null; i++) {
                String ln = filecompare[i][0].trim();
                if (ln.length() == 0) {
                    continue;
                }
                int line = Integer.parseInt(ln);
                if (line > cf.endline) {
                    break;
                }
                if (line == 509) {
                    int a = 10;
                }
                if (line >= cf.startline && line <= cf.endline) {
                    String nln = filecompare[i][2].trim(); // SHOWING NULL POINTER EXCEPTION HERE
                    if (nln.trim().length() > 0) {
                        int nline = Integer.parseInt(nln);
                        if (nstartline > nline) {
                            nstartline = nline;
                        }
                        if (nendline < nline) {
                            nendline = nline;
                        }
                    }
                    if (!filecompare[i][1].trim().equals(filecompare[i][3].trim())) {
                        if (filecompare[i][1].trim().length() > 0 || filecompare[i][3].trim().length() > 0) {
                            changed = 1;
                        }
                    }
                }
            }

            if (nendline == -1) {
                return null;
            }

            instance.revision = nrevision;
            instance.filepath = cf.filepath;
            instance.startline = nstartline;
            instance.endline = nendline;
            instance.changetype = cf.changetype;

            return instance;

        } catch (Exception e) {
            return null;
        }
    }
    
}
