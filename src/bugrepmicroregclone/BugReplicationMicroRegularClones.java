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
        
        String abs_filepath = InputParameters.pathRegular + revision + "/" + filepath;    
        
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

        String abs_filepath = InputParameters.pathRegular + revision + "/" + filepath;
        
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
    
    public String getBugFixCommits() {
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
            System.out.println("error in getBugFixCommits = " + e);
        }

        return bugFixCommits;
    }
    
    public CodeFragment[][] getChangedBugFixCommits() {

        SingleChange[] changedBugFixCommits = new SingleChange[50000];
        SingleChange[][] changedBugFixCommits2D = new SingleChange[10000][10000];
        CodeFragment[][] changedBugFixCommits2DNew = new CodeFragment[10000][10000];

        try {

            String str = getBugFixCommits();
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
            
            //System.out.println("-----------------------------------------Micro Clone Analysis Starts Here--------------------------------------------\n");
            //bugRepM = bugReplicationM();
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
    
    public ArrayList<CodeFragment> bugReplicationR(){
        ArrayList<CodeFragment> bugRep = new ArrayList<>();
        try{          
            CodeFragment[][] cloneFragmentPair = new CodeFragment[10000][2];
            
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
                                System.out.println("////////////////////////////////////////////////////////////////////////////Replicated Bug Fixing Change Found////////////////////////////////////////////////////////////////////////////");
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
            
            System.out.println("\nTotal distinct number of replicated bug revision in regular code clone = " + countRevisionRep);
            
            float averageCountRegular = (float)bugRep.size()/countRevisionRep;
                        
            System.out.println("\nDistinct number of replicated bugs in regular clones per revision = " + averageCountRegular + "\n");
            
        }catch(Exception e){
            System.out.println("error in BugReplicationR = " + e);
            e.printStackTrace();
        }
        return bugRep;
    }
    
    public CodeFragment[][] isClonePair(){
        CodeFragment[][] cfp = new CodeFragment[10000][2];
        try{
            CodeFragment[][] changedBugFixCommits = new CodeFragment[10000][10000];
            changedBugFixCommits = getChangedBugFixCommits();
            
            CodeFragment[][] cfXmlFile = new CodeFragment[10000][10000];
            CodeFragment[] cfXmlFileMatch = new CodeFragment[10000];
            int x = 0;
            
            // Looping through the changed bug-fix commit 2D array
            for(int i = 0; i<changedBugFixCommits.length; i++){
                for(int j = 0; j<changedBugFixCommits.length; j++){
                    if(changedBugFixCommits[i][j] != null){
                        System.out.println("Revision number = " + changedBugFixCommits[i][j].revision);
                        cfXmlFile = xmlFileParse(changedBugFixCommits[i][j].revision);
                        
                        
                        // Looping through the xml file of each revision
                        for (int m = 0; m < cfXmlFile.length; m++) {
                            for (int n = 0; n < cfXmlFile.length; n++) {

                                if (cfXmlFile[m][n] != null){
                                    
                                    if(changedBugFixCommits[i][j].filepath.equals(cfXmlFile[m][n].filepath)){
                                        
                                        // Checking for matches with changed bug-fix commits with each line of xml file of a particular revision
                                        // Matches if line numbers of code fragment from changedBugFixCommits are overlapping with line numbers range of cfXmlFile
                                        if(((changedBugFixCommits[i][j].startline >= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFile[m][n].endline))
                                            ||((changedBugFixCommits[i][j].startline <= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline <= cfXmlFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].endline >= cfXmlFile[m][n].startline)) 
                                            ||((changedBugFixCommits[i][j].startline >= cfXmlFile[m][n].startline) && (changedBugFixCommits[i][j].endline >= cfXmlFile[m][n].endline) 
                                                && (changedBugFixCommits[i][j].startline <= cfXmlFile[m][n].endline))){
                                        
                                            System.out.println("*********************************************** File Name matched ***********************************************");
                                            
                                            //System.out.println("Matched CF from changedBugFixCommits["+i+"]["+j+"] = " + changedBugFixCommits[i][j].filepath + " Start Line = " 
                                                    //+ changedBugFixCommits[i][j].startline + " End Line = " + changedBugFixCommits[i][j].endline);
                                            
                                            //System.out.println("Matched CF1 from cfXmlFile["+m+"]["+n+"] = " + cfXmlFile[m][n].filepath + " Start Line = " + cfXmlFile[m][n].startline 
                                                //+ " End Line = " + cfXmlFile[m][n].endline);
                                            
                                            // Saving the matched entries into a separate 1D array
                                            cfXmlFileMatch[x] = cfXmlFile[m][n];
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
                        
                        classID1 = getClassID(cfXmlFileMatch[i]);
                        //System.out.println("classID1 = " + classID1);
                        
                        classID2 = getClassID(cfXmlFileMatch[j]);
                        //System.out.println("classID2 = " + classID2 + "\n");
                        
                        if(classID1 == classID2){
                            System.out.println("********************************************Pair Found********************************************");
                            cfp[x][0] = cfXmlFileMatch[i];
                            cfp[x][1] = cfXmlFileMatch[j];
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
    
    public CodeFragment getInstanceInNextRevision(CodeFragment cf) {
        try {
            CodeFragment instance = new CodeFragment();

            int crevision = cf.revision;
            int nrevision = crevision + 1;

            int nstartline = 999999999;
            int nendline = -1;

            int changed = 0;
            

            String cfilepath = InputParameters.pathRegular + crevision + "/" + cf.filepath; 
            String nfilepath = InputParameters.pathRegular + nrevision + "/" + cf.filepath; 
                    
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
