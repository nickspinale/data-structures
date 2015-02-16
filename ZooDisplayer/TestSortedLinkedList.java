import java.io.*;
import java.util.*;

/**
 * Testing module for SortedList.
 * By Nick Spinale
 */

public class TestSortedLinkedList {

    // Prints the current state of a SortedList
    static void printList(SortedList<ZooAnimal> list) {
        System.out.println("\nThe list's current state:");
        list.forEach(x -> System.out.println("- " + x.toString()));
    }

    // See the first print statements for an explanation
    public static void main(String[] args) {

        System.out.println("\nRunning SortedList tests.");
        System.out.println("Each full test run takes an array of zoo animals and tests all methods on it.");
        System.out.println("NOTE: A specific test that shows no results (e.g. 'Testing 'iterator()': _) does not indicate an error.");
        System.out.println("      These tests will explicitly mention issues.");

        // Tests are run on three different arrays. One with 10 elements,
        // one with 1 element, and one with 0 elements.

        ZooAnimal[] bigArr = {
            new ZooAnimal("kangaroo", "jeff"  , 13 , null) ,
            new ZooAnimal("kangaroo", "sam"   , 999, null) ,
            new ZooAnimal("lawyer"  , "mary"  , 1  , null) ,
            new ZooAnimal("lawyer"  , "bob"   , 2  , null) ,
            new ZooAnimal("abcd"    , "gerald", 1  , null) ,
            new ZooAnimal("abcz"    , "donnie", 3  , null) ,
            new ZooAnimal("d"       , "kate"  , 1  , null) ,
            new ZooAnimal("c"       , "biff"  , 143, null) ,
            new ZooAnimal("b"       , "spike" , 1  , null) ,
            new ZooAnimal("a"       , "walter", 4  , null) ,
        };

        runTests(bigArr);

        ZooAnimal[] singleton = { new ZooAnimal("chinchilla", "willy"  , 4328743 , null) };

        runTests(singleton);

        ZooAnimal[] empty = { };

        runTests(empty);

        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        System.out.println("Testing complete.\nExiting.\n");
    }


    // This is the testing method. It is very large, but also very liner.
    // If it were more complicated, it would have warrented some sort of subdivision
    // (into smaller methods), but because it is so linear, and because more methods
    // would have required the passing of arguments or mutable global variables,
    // this seemed like the better option.
    // There are not many comments in here becuase the print statements are pretty verbose.
    public static void runTests(ZooAnimal[] arr) {

        System.out.println("\n===================================");
        System.out.println("Running tests on array of length " + arr.length);
        System.out.println("===================================");

        SortedList<ZooAnimal> list = new SortedLinkedList();
        System.out.println("\nTest list initialized as empy.");

        System.out.println("\nAnimals to be added to test list:");
        for(int i = 0; i < arr.length; i++) {
            System.out.println("- " + arr[i].toString());
        }

        System.out.println("\nTesting 'add()' (adding all test animals to empty list)");
        for(int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
            System.out.println("- Added " + arr[i].toString());
        }

        printList(list);

        // Test all of these methods against one another
        System.out.println("\nTesting 'get()', 'getPosition()', and lookup");
        for(int i = 0; i < arr.length; i++) {
            ZooAnimal animal = arr[i];
            int pos = list.getPosition(animal);
            ZooAnimal animal2 = list.get(pos);
            System.out.println("- According to getPosition(), " + animal.toString() + " should be in position " + pos);
            System.out.println("  According to get(), " + animal2.toString() + " is in position " + pos);
            System.out.println(
                animal == animal2
              ? "  These two animals are the some, which is GOOD"
              : "  These two animals are not the some, which is BAD"
          );
        }

        System.out.println("\nTesting irregular 'get()' case");
        System.out.println("- get(1000) should throw an IndexOutOfBoundsException");
        boolean allgood = false;
        try {
            list.get(1000);
        } catch(IndexOutOfBoundsException e) {
            allgood = true;
        } catch(Exception f) {
            allgood = false;
        }
        System.out.println(allgood ? "  It DID, which is GOOD" : "  It DIDN'T, which is BAD");


        System.out.println("\nTesting 'contains()'");
        for(int i = 0; i < arr.length; i++) {
            ZooAnimal tmp = arr[i];
            System.out.println(
                list.contains(tmp)
              ? "- List contains (AND SHOULD CONTAIN) " + tmp.toString()
              : "- List does not contain (AND SHOULD NOT CONTAIN) " + tmp.toString()
            );
        }

        // Outsider is just a random animal that isn't already in the list
        ZooAnimal outsider = new ZooAnimal("lizard", "jim", 666, null);
        System.out.println(
            list.contains(outsider)
          ? "- List contains (BUT SHOULD NOT CONTAIN) " + outsider.toString()
          : "- List does not contain (AND SHOULD NOT CONTAIN) " + outsider.toString()
        );

        System.out.println("\nTesting 'size()'");
        System.out.println("- This list's length should be " + arr.length + ":");
        System.out.println("- This list's length is " + list.size());
        System.out.println(arr.length == list.size() ? "- This worked AS EXPECTED" : "This DID NOT WORK AS EXPECTED");

        System.out.println("\nTesting 'isEmpty()'");
        System.out.println("- This list is" + (arr.length == 0 ? "empty" : "not empty"));
        System.out.println("- According to 'isEmpty()', this list is " + (list.isEmpty() ? "empty" : "not empty"));
        System.out.println((arr.length == 0) == list.isEmpty() ? "- This worked AS EXPECTED" : "This DID NOT WORK AS EXPECTED");

        System.out.println("\nTesting 'toArray()'");
        Object[] arr2 = list.toArray();
        for(int i = 0; i < arr2.length; i++) {
            System.out.println("- " + arr2[i] + " should equal " + list.get(i));
            System.out.println(
                arr2[i] == list.get(i)
              ? "  It does, which is GOOD"
              : "  It doesn't, which is BAD"
            );
        }

        // Matches the output of the iterator against sequential calls to "get"
        System.out.println("\nTesting 'iterator()'");
        int i = 0;
        Iterator<ZooAnimal> iter = list.iterator();
        while(iter.hasNext()) {
            ZooAnimal e = iter.next();
            System.out.println("- " + e + " should equal " + list.get(i));
            System.out.println(
                e == list.get(i)
              ? "  It does, which is GOOD"
              : "  It doesn't, which is BAD"
            );
            i++;
        }

        // Try removing the lizard that doesnt exist in the list
        System.out.println("\nTesting 'remove()'");
        System.out.println("- Trying to remove " + outsider.toString() + ", which is not in the list.");
        boolean tmp = list.remove(outsider);
        System.out.println("  Remove returned " + tmp + ", which is " + (tmp ? "BAD" : "GOOD"));

        // Only test removing something if something exists to be removed
        if(arr.length > 0) {
            boolean tmp2 = list.remove(arr[0]);
            System.out.println("- Trying to remove " + arr[0].toString() + ", which is in the list.");
            System.out.println("  Remove returned " + tmp2 + ", which is " + (tmp2 ? "GOOD" : "BAD"));
        }

        printList(list);

        System.out.println("\nTesting 'clear()'");
        System.out.println("- Cleared list... it should be empty");
        System.out.println("- According to 'size()', it " + (list.size() == 0 ? "IS, which is GOOD" : "IS NOT, which is BAD"));

        System.out.println();
    }
}
