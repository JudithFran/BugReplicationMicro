/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugrepmicroregclone;

import java.io.BufferedReader;
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
    
    public void bugReplication(){
        try{
            // --------------------------Implementing RQ1----------------------------
            
            int countFragmentR = 0;
            int countFragmentM = 0;
            int countRevision = 0;
            
            ArrayList<CodeFragment> bugRepR = new ArrayList<>();
            ArrayList<CodeFragment> bugRepM = new ArrayList<>();
            
            
            System.out.println("---------------------------------------Regular Clone Analysis Starts Here--------------------------------------------\n");
            //bugRepR = bugReplicationR();
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
    
}
