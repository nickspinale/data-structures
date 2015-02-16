/**
 * A class that represents a single animal, keeping track of a picture,
 * age, name, and species.
 * Nick Spinale and Micah Nacht CS 201
 */
public class ZooAnimal implements Comparable<ZooAnimal>{

    //Initialize class variables.
    private String species;
    private String name;
    private int birthYear;
    private EzImage picture;

    /**
     * Construct an instance of the class. Every animal is different so there is
     * no default constructor.
     */
    public ZooAnimal(String species, String name, int birthYear, EzImage picture){
        this.species = species;
        this.name = name;
        this.birthYear = birthYear;
        this.picture = picture;
    }

    /**
     * Returns a picture of the animal.
     */
    public EzImage getPic(){
        return picture;
    }

    /**
     * Returns the species of the animal as a String.
     */
    public String getSpecies(){
        return species;
    }

    /**
     * Returns the name of the animal as a String.
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the year the animal was born as an int.
     */
    public int getYear(){
        return birthYear;
    }

    /**
     * Compares one animal to another animal. Returns a positive number if this animal comes
     * after other. Sorts first alphabetically by species, then alphabetically by name, then
     * finally by age.
     */
    public int compareTo(ZooAnimal other){
        int comparison = species.compareTo(other.getSpecies());
        if(comparison != 0){
            return comparison;
        }
        comparison= name.compareTo(other.getName());
        if(comparison != 0){
            return comparison;
        }
        return birthYear - other.getYear();
    }

    /**
     * Returns a string displaying the animal's name, species and age.
     */
    public String toString(){
        return "" + name + " " + species + " " + (2015 - birthYear);
    }
}
