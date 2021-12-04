package identify.BayesianNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static identify.Preprocessor.*;
import static identify.Preprocessor.cores;
import static identify.readSetting.getReader;

/**
 * An enumeration representing the two classes of interest.
 * 
 * DO NOT MODIFY
 */
public class Label {
    public static ArrayList<String> label_nick = new ArrayList<>();
    public static HashMap<String,Integer> labelHash = new HashMap<>();
    public static void initLabels(String fileName){
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp;
            while ((temp = sc.readLine()) != null) {
                labelHash.put(temp,label_nick.size());
                label_nick.add(temp);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void initLabelFromRaw(String fileName){
        try {
            BufferedReader sc = null;
            sc = getReader(fileName);
            String temp;
            while ((temp = sc.readLine()) != null) {
                String[]token = temp.split(",");
                String name = token[0];
                if(!labelHash.containsKey(name)){
                    labelHash.put(name,label_nick.size());
                    label_nick.add(name);
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getIndex(String label){
        for(int i=0;i <label_nick.size();i++){
            if(label_nick.get(i).equals(label)){
                return i;
            }
        }
        return -1;
    }
}
