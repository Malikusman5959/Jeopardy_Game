/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeopardy;

import java.util.ArrayList;

/**
 *
 * @author USMAN
 */
public class Jeopardy {
    static ArrayList<String> t = new ArrayList<String>(); 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        t.add("test");
        t.add("list");
        ArrayList<String>  test = new ArrayList<String>();
        test.add("a");
        test.add("b");
        test.add("c");
        test.add("b");
        test.add("b");
        test.add("b");
        test.add("a");
        test.add("a");
        
        t= removeDuplicates(test);
        for (int x =0 ; x< t.size();x++) { 
            System.out.println("VAlues :: " + t.get(x));
        } 
        
       
        
        
    }
   static ArrayList<String> removeDuplicates(ArrayList<String> list) 
    { 
  
        // Create a new ArrayList 
        ArrayList<String> newList = new ArrayList<String>(); 
  
        // Traverse through the first list 
        for (int x =0 ; x< list.size();x++) { 
  
            // If this element is not present in newList 
            // then add it 
            if (!newList.contains(list.get(x))) { 
  
                newList.add(list.get(x)); 
            } 
        } 
  
        // return the new list 
        return newList; 
    } 
  
    
}
