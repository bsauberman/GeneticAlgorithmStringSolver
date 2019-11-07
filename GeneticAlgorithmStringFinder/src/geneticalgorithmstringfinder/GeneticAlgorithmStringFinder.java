/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgorithmstringfinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;

/**
 *
 * @author bensauberman
 */
public class GeneticAlgorithmStringFinder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        // TODO code application logic here
        String target = "Cooper is a babe.";
        int nodeSize = 100;
        double mutationRate = 1;
        
        Node[] nodes = new Node[nodeSize];
        fillNodes(nodes, target.length());
        boolean hasFoundTarget = false;
        int genCount = 1;
        
        PrintWriter clearWriter = new PrintWriter("FitnessStats.txt");
        clearWriter.print("MIN, AVG, MAX");
        clearWriter.close();
        Thread.sleep((long)1000);
        while(!hasFoundTarget) { 
            System.out.println("\n\n\n\nGENERATION " + genCount);
            //Thread.sleep((long) 100);
            hasFoundTarget = checkForTarget(nodes, target);
           // Thread.sleep((long)15);
            double[] stats = calcFitness(nodes, target);
            System.out.println("MINFIT: " + stats[0] + "%");
            System.out.println("MAXFIT: " + stats[1] + "%");
            System.out.println("AVGFIT: " + stats[2] + "%");
            try{
                Writer output = new BufferedWriter(new FileWriter("FitnessStats.txt", true));

                output.append(stats[0]+",");
                output.append(stats[1]+",");
                output.append(stats[2]+",");
                output.close();
            } catch (IOException e) {
               // do something
            }            
            System.out.println("\n\n\n\n\n");
            discardBadNodes(nodes);
            printNodes(nodes);
            //discardBadNodes(nodes);
            System.out.println("\n\n\n\n\n");
            nodes = createChildNodes(nodes);
            addMutation(nodes, mutationRate);
            genCount++;
        }
        System.out.println("\n\n\n\n\n");
        System.out.println(target + " was found in " + genCount + " generations.");

    }
    
    public static void fillNodes(Node[] nodes, int wordSize) {
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(wordSize);
        }
    }
    
    public static boolean checkForTarget(Node[] nodes, String target) {
        for(int i = 0; i < nodes.length; i++) {
            if(nodes[i].toString().equals(target)) {
                return true;
            }
                
        }
        return false;
    }
    public static void printNode(Node node) {
        System.out.println(node.toString());
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("      " + df.format(node.getFitness()/node.getCharacters().length*100) +"%");
    }
    
    public static void printNodes(Node[] nodes) {
        for(int i = 0; i < nodes.length; i++) {
            if(nodes[i] != null) {
                System.out.println(nodes[i].toString());
                DecimalFormat df = new DecimalFormat("#.##");
                System.out.println("      " + df.format(nodes[i].getFitness()/nodes[i].getCharacters().length*100) +"%");
            } else {
                System.out.println("NULL");
                System.out.println("      NULL");
            }
        }
    }
    
    public static double[] calcFitness(Node[] nodes, String target) {//, double avgFitArr[], int arrCount) {
        nodes[0].setFitness(nodes[0].calcFitness(target));
        double minFit = nodes[0].getFitness();
        double maxFit = nodes[0].getFitness();
        for(int i = 1; i < nodes.length; i++) {
            nodes[i].setFitness(nodes[i].calcFitness(target));
            if(nodes[i].getFitness() < minFit) {
                minFit = nodes[i].getFitness();
            }
            if(nodes[i].getFitness() > maxFit) {
                maxFit = nodes[i].getFitness();
            }
        }
        
        /*for(int i = 0; i < nodes.length; i++) {
            nodes[i].addFitness(Math.abs(minFit));
        }*/
        DecimalFormat df = new DecimalFormat("#.##");
        double[] stats = new double[3];
        stats[0] = Double.parseDouble(df.format(minFit/target.length()*100));
        stats[1] = Double.parseDouble(df.format(maxFit/target.length()*100));
        stats[2] = Double.parseDouble(df.format(getAvgFit(nodes)/target.length() *100));
        return stats;
        /*avgFitArr[arrCount] = getAvgFit(nodes);
        return avgFitArr;*/     
    }
    
    public static double getAvgFit(Node[] nodes) {
        double avgFit = 0;
        int nullCount = 0;
        for(int i = 0; i < nodes.length; i++) {
            if(nodes[i] != null) {
                avgFit += nodes[i].getFitness();
            }
        }
        avgFit = avgFit/(nodes.length - nullCount);
        return avgFit;
    }
    
    public static double getTotalFit(Node[] nodes, boolean shouldExponentiate) {
        double totalFit = 0;
        for(int i = 0; i < nodes.length; i++) {
            if(nodes[i] != null) {
                if(shouldExponentiate) {
                    totalFit += Math.pow(nodes[i].getFitness(), 5);
                } else {
                    totalFit += nodes[i].getFitness();                    
                }
            }
        }
        return totalFit;   
    }
    
    public static void discardBadNodes(Node[] nodes) {
        double avgFit = getAvgFit(nodes);
        int discardCount = 0;
        for(int i = 0; i < nodes.length; i++) {
            if(nodes[i].getFitness() < avgFit) {
                nodes[i] = null;
                discardCount++;
            }
            if(discardCount > nodes.length * .75) {
                break;
            }
        }    
        System.out.println("\n\nDiscarded " + discardCount + " nodes.");
    }
    
    public static Node[] createChildNodes(Node[] nodes) {
        Node[] newNodes = new Node[nodes.length];
        double total = getTotalFit(nodes, true);
        for(int i = 0; i < nodes.length; i++) {      
            //FINDS PARENTS
            Node parent1 = null;
            Node parent2 = null;                 
            boolean foundParent1 = false, foundParent2 = false;
            while(!foundParent1) {
                int index = (int)(Math.random() * (nodes.length));
                double fitDeterminer = (Math.random() * total);
                if(nodes[index] != null && Math.pow(nodes[index].getFitness(),5) >= fitDeterminer) {
                    parent1 = nodes[index];
                    foundParent1 = true;
                }
            }
            while(!foundParent2) {
                int index = (int)(Math.random() * (nodes.length));
                int fitDeterminer = (int)(Math.random() * (total));
                if(nodes[index] != null && Math.pow(nodes[index].getFitness(),5) >= fitDeterminer && nodes[index] != parent1) {
                    parent2 = nodes[index];
                    foundParent2 = true;
                }
            }
            System.out.println(parent1.toString() + "     " + parent2.toString());


            //GENERATES CHILD
            int adder = 0;
            if(parent1.getCharLength() % 2 != 0) {
                adder = 1;
            }
            String[] characters = new String[(parent1.getCharLength()/2) + (parent2.getCharLength()/2) + adder];
            for(int j = 0; j < characters.length; j++) {
                /*double totalExpFitness = Math.pow(parent1.getFitness(), 3) + Math.pow(parent2.getFitness(), 3);
                int random = (int)(Math.random() * (total));
                if(Math.pow(parent1.getFitness(), 3) > random && j < parent1.getCharLength()-1) {
                    characters[j] = parent1.getCharacters()[j];
                } else {
                    characters[j] = parent2.getCharacters()[j];
                }               
            }*/
                if(j % 2 == 0) {
                    if(j < parent1.getCharLength()-1) {
                        characters[j] = parent1.getCharacters()[j];
                    } else {
                        characters[j] = parent2.getCharacters()[j];
                    }
                } else {
                    if(j < parent2.getCharLength()-1) {
                        characters[j] = parent2.getCharacters()[j];
                    } else {
                        characters[j] = parent1.getCharacters()[j];
                    }
                }
            }

            newNodes[i] = new Node(characters);
        } 
        return newNodes;
    }
    
    public static void addMutation(Node nodes[], double rate) {
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes[i].getCharLength(); j++) {
                int mutator = (int)(Math.random() * (100));
                if(mutator <= rate) {
                    String currStr = nodes[i].toString();
                    nodes[i].getCharacters()[j] = nodes[i].getRandomCharacter();
                    System.out.println("MUTATION ADDED! " + currStr + "  -->  " + nodes[i].toString());
                }
            }
           
        }
    }

}
