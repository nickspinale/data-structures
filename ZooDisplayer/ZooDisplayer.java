import java.util.*;
import java.io.*;

/**
 * Reads input from a file and displays a list of ZooAnimals as either text or pictures.
 * Input is formatted as animal-species,animal-name,birth-day,image-file-location
 * and the user must specify both input file and "text" or "picture" mode. Handles incorrectly
 * formatted files by skipping problem lines.
 */
public class ZooDisplayer{

    SortedList<ZooAnimal> list;

    public ZooDisplayer(String filePath) {

        list = new MysterySortedListImplementation();

        if(filePath != null) {
            try{
                (new BufferedReader(new FileReader (new File(filePath))))
                    .lines()
                    .forEach(line -> {
                        String[] params = line.split(",",-1);
                        // Make sure that line has correct number of fields.
                        if(params.length != 4){
                            System.out.println("Error in parsing. Skipping to next line.");
                        } else {
                            try{
                                int birthYear = Integer.parseInt(params[2]);
                                EzImage pic = new EzImage(new File(params[3]));
                                list.add(new ZooAnimal(params[0], params[1], birthYear, pic));
                            // Catch incorrectly formatted number.
                            } catch(NumberFormatException e){
                                System.out.println("Error in parsing age. Skipping to next line.");
                            // Catch issue with creating EzImage.
                            } catch(IOException e){
                                System.out.println("Error in finding picture file. Skipping to next line.");
                            }
                        }
                    });
            } catch(FileNotFoundException e) {
                System.out.println("Issue accessing file. Proceeding without file input.");
            }
        }
    }

    public void displayZooAsText(){
        list.forEach(e -> System.out.println(e.toString()));
    }

    public void displayZooAsPicture(){
        EzImage result = list.get(0).getPic();
        for(int i = 1; i < list.size(); i++){
            result = result.appendToRight(list.get(i).getPic());
        }
        result.show("Zoo");
    }

    public void addAnimal(ZooAnimal animal) {
        list.add(animal);
    }

    /**
     * Generates list of zoo animals based on input file (first argument), and displays it according to
     * specified mode (second argument).
     */
    public static void main(String[] args){

        ZooDisplayer zoo = null;

        if(args.length == 0) {
            zoo = new ZooDisplayer(null);
        } else if (args.length == 1) {
            zoo = new ZooDisplayer(args[0]);
        } else {
            System.out.println("Incorrect number of arguments. Please try again with only 2.");
            System.exit(0);
        }

        Scanner in = new Scanner(System.in);
        
        while(true) {
            String command = in.nextLine();
            if(command.equals("add animal")) {
                System.out.println("What is the species of the animal you're adding?");
                String species = in.nextLine();
                System.out.println("What is the animal's name");
                String name = in.nextLine();
                System.out.println("And in what year was " + name + " born?");
                int year = askInt(in);
                System.out.println("Where can I find a picture of " + name + "?");
                EzImage img = askImg(in);
                zoo.addAnimal(new ZooAnimal(species, name, year, img));
            } else if(command.equals("remove animal")) {
                System.out.println("Which animal would you like to remove?");
                String name = in.nextLine();
                int spot = -1;
                for(int i = 0; i < zoo.list.size(); i++) {
                    if(zoo.list.get(i).getName().equals(name)) {
                        zoo.list.remove(zoo.list.get(i));
                    }
                }
                System.out.println(name + (spot < 0 ?  " was not found in the zoo." : " successfully removed."));
            } else if(command.equals("display text")) {
                zoo.displayZooAsText();
            } else if(command.equals("display picture")) {
                zoo.displayZooAsPicture();
            } else if(command.equals("exit")) {
                System.out.println("Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Unrecognize command: " + command);
                System.out.println("Valid commands: add animal, remove animal, display text, display picture, and exit.");
            }
        }
    }

    private static int askInt(Scanner in) {
        try { return in.nextInt(); }
        catch(Exception e) {
            System.out.println("Error... Please enter an integer:");
            in.nextLine();
            return askInt(in);
        }
    }

    private static EzImage askImg(Scanner in) {
        try { return new EzImage(in.nextLine()); }
        catch(Exception e) {
            System.out.println("Error... Please enter valid path (e.g. \"/home/gerald/pictures/sammy.jpg\"):");
            return askImg(in);
        }
    }
}
