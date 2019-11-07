/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgorithmstringfinder;

/**
 *
 * @author bensauberman
 */
public class Node {
    private String[] characters;
    private double fitness= 0;
    private double probability = 0;
    
    public Node(int wordSize) {
        //int wordSize = (int)(Math.random() * (20));
        fillCharacters(wordSize);           
    }
    
    public Node(String[] characters) {
        this.characters = characters;
    }
    
    private void fillCharacters(int wordSize) {
        this.characters = new String[wordSize];

        for(int i = 0; i < wordSize; i++) {
            characters[i] = getRandomCharacter();
        }
    }   
    
    public String getRandomCharacter() {
        return String.valueOf((char)(32 + (int)(Math.random() * (126-32+1))));
    }
    
    @Override
    public String toString() {
        String word = "";
        for(int i = 0; i < characters.length; i++) {
            word = word.concat(characters[i]);
        }
        return word;
    }
    
    public double calcFitness(String target) {
        fitness = 0;
        for(int i = 0; i < characters.length; i++) {
            if(i >= target.length()) {
                //this.fitness -= (3 * (i - target.length()));
            }
            if(this.characters[i].equals(target.substring(i, i+1))) {
                this.fitness += 1;
            }             
        }
        

        /*if(target.length() > this.characters.length) {
            this.fitness -= (3 * (target.length() - this.characters.length));
        }
        if(fitness < 0) {
            fitness = 0;
        }*/
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public void addFitness(double fitness) {
        this.fitness += fitness;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public int getCharLength() {
        return characters.length;
    }
    
    public String[] getCharacters() {
        return characters;
    }
}
