import java.io.*;
import java.util.*;

public class TestSortedLinkedList {

    static void printList(SortedList<ZooAnimal> list) {
        list.forEach(x -> System.out.println(x.toString()));
    }

    public static void main(String[] args) {

        ZooAnimal[] subjects = {
            new ZooAnimal("jeff"  , "kangaroo", 13 , null) ,
            new ZooAnimal("sam"   , "kangaroo", 999, null) ,
            new ZooAnimal("mary"  , "lawyer"  , 1  , null) ,
            new ZooAnimal("bob"   , "lawyer"  , 2  , null) ,
            new ZooAnimal("gerald", "abcd"    , 1  , null) ,
            new ZooAnimal("donnie", "abcz"    , 3  , null) ,
            new ZooAnimal("kate"  , "d"       , 1  , null) ,
            new ZooAnimal("biff"  , "c"       , 143, null) ,
            new ZooAnimal("spike" , "b"       , 1  , null) ,
            new ZooAnimal("walter", "a"       , 4  , null) ,
        }

        SortedList<ZooAnimal> list = new MysterySortedListImplementation<ZooAnimal>();

        if(args.length == 1) {
            try{
                (new BufferedReader(new FileReader (new File(args[0]))))
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
            } catch(Exception e) {
                System.out.println("Error accessing provided file");
                System.exit(0);
            }
        } else {
            System.out.println("Please provide a zoo text file as an argument.");
            System.exit(0);
        }


    }
}
