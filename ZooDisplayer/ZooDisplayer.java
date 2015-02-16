import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Reads input from a file and displays a list of ZooAnimals as either text or pictures.
 * Input is formatted as animal-species,animal-name,birth-day,image-file-location
 * and the user must specify both input file and "text" or "picture" mode. Handles incorrectly
 * formatted files by skipping problem lines.
 * By Nick Spinale
 */

public class ZooDisplayer{

    // This list will hold this particular zoo displayer's list of animals
    SortedList<ZooAnimal> list;

    /**
     * Construct a new ZooDisplayer containing the animals in the file at filePath.
     * If filePath is null, constructs an empty ZooDisplayer.
     * @param filePath path to the file from which to load zoo animals
     */
    public ZooDisplayer(String filePath) {

        // Initialize list as empty
        list = new SortedLinkedList();

        // If we got a file, parse and process it
        if(filePath != null) {
            try{
                (new BufferedReader(new FileReader (new File(filePath))))
                    .lines()
                    .forEach(line -> {
                        String[] params = line.split(",",-1);
                        // Make sure that line has correct number of fields.
                        if(params.length != 4) {
                            System.out.println("Error in parsing. Skipping to next line.");
                        } else {
                            try {
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

    /**
     * Prints a text version of the zoo that lists the animals' names, species, and ages.
     */
    public void displayZooAsText(){
        list.forEach(e -> System.out.println(e.toString()));
    }

    /**
     * Displays a picture of the zoo where each animal's picture is concatenated to the
     * right of the previous animal.
     */
    public void displayZooAsPicture(){
        Iterable<ZooAnimal> iterable = () -> list.iterator();
        StreamSupport.stream(iterable.spliterator(), false)
                     .map(e -> e.getPic())
                     .reduce((x, y) -> x.appendToRight(y))
                     .ifPresent(pic -> pic.show("Zoo"));
    }

    /**
     * Adds animal to the zoo.
     * @param animal
     */
    public void addAnimal(ZooAnimal animal) {
        list.add(animal);
    }

    /**
     * Begins an interactive zoo fun session.
     * User may pass a file as a list of zoo animals (in the same format as in
     * last zoo animals assignment).
     */
    public static void main(String[] args){

        // This is the zoo with which the user will play
        ZooDisplayer zoo = null;

        if(args.length == 0) {
            zoo = new ZooDisplayer(null);
        } else if (args.length == 1) {
            zoo = new ZooDisplayer(args[0]);
        } else {
            System.out.println("Incorrect number of arguments. Please try again with only 2.");
            System.exit(0);
        }

        final Scanner in = new Scanner(System.in);
        // Spaces are not appropriate tokens here.
        in.useDelimiter("\n");

        while(true) {

            // Prompt for and get command
            System.out.print("Enter a command: ");
            final String command = in.next();

            if(command.equals("add animal")) {

                System.out.println("What is the species of the animal you're adding?");
                final String species = in.next();

                System.out.println("What is the animal's name");
                final String name = in.next();

                System.out.println("And in what year was " + name + " born?");
                final int year = askInt(in);

                System.out.println("Where can I find a picture of " + name + "?");
                final EzImage img = askImg(in);

                zoo.addAnimal(new ZooAnimal(species, name, year, img));

            } else if(command.equals("remove animal")) {

                System.out.println("Which animal would you like to remove?");
                final String name = in.next();

                // For knowing whether we actually found the animal
                boolean changed = false;

                // Since we can only assume that sortedlist uses "==",
                // we have to get a reference to the element we want
                // to remove in order to remove it.
                // Also, note that this removes all animals with the
                // given name. That seems to make the most sense.
                for(ZooAnimal e : zoo.list) {
                    if(e.getName().equals(name)) {
                        zoo.list.remove(e);
                        changed = true;
                    }
                }

                System.out.println(name + (changed ?  " was not found in the zoo." : " successfully removed."));

            // The other responses are self-explanatory

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

    // Prompts user for an integer until it gets one
    private static int askInt(Scanner in) {
        try {
            return in.nextInt();
        }
        catch(Exception e) {
            System.out.println("Error... Please enter an integer:");
            // Flush buffer, so that we don't recurse on the same token forever
            in.next();
            return askInt(in);
        }
    }

    // Prompts user for an image filename until it gets one that it can convert
    // to an EzImage
    private static EzImage askImg(Scanner in) {
        try {
            return new EzImage(in.next());
        } catch(Exception e) {
            System.out.println(e + "Error... Please enter valid path (e.g. \"/home/gerald/pictures/sammy.jpg\"):");
            // Flush buffer, so that we don't recurse on the same token forever
            in.next();
            return askImg(in);
        }
    }
}
