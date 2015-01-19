import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Nick Spinale 
 * CS 201, HW 6
 * January 30, 2015.
 *
 * This is an unweighted, directed graph implementation specifically
 * designed for the analysis of a subset of wikipedia's internal link
 * structure.
 *
 * IMPORTANT NOTE: While this class does not look like 'typical Java',
 * it is in no way obfuscated, neither intentionally nor otherwise.
 * It just takes advantage of the expressive power of some of Java 8's
 * new features (namely, streams, optional values, new-style iterables,
 * and functional interfaces). In the spirit of functional programming,
 * this file has 0 explicit loops, and all variables are declared as final.
 * Furthermore, in the spirit of 'provability', this file has 0 explicitly
 * recursive functions.
 */

public class PathFinder extends MysteryUnweightedGraphImplementation {

    // Maps for article name - integer id pairs.
    private final Map<String,Integer> articleToId;
    private final Map<Integer,String> idToArticle;

    /**
     * Constructs a PathFinder that represents the graph with nodes (vertices) specified as in
     * nodeFile and edges specified as in edgeFile.
     * @param nodeFile name of the file with the node names
     * @param edgeFile name of the file with the edge names
     */
    public PathFinder(String nodeFile, String edgeFile) {

        // Maps to match articles with their id's in the graph, and visa versa
        articleToId = new HashMap<String,Integer>();
        idToArticle = new HashMap<Integer,String>();

        try {
            (new BufferedReader(new FileReader (new File(nodeFile))))
                .lines()
                // Ignore empty lines and comments
                .filter(x -> !x.equals("") && x.charAt(0) != '#')
                // Populate articles initialized above
                .forEach(x -> {
                    Integer id = addVertex();
                    articleToId.put(x, id);
                    idToArticle.put(id, x);
                });
        } catch (Exception e) {
            // Buffered reader initialization failure indicates issues with file
            System.err.println("Problem with file: " + nodeFile + ".../n..." + e);
        }

        try {
            (new BufferedReader(new FileReader (new File(edgeFile))))
                .lines()
                // Ignore empty lines and comments
                .filter(x -> !x.equals("") && x.charAt(0) != '#')
                // Parse and add links to graph
                .forEach(x -> {
                    String[] articles = x.split("\t");
                    addEdge(articleToId.get(articles[0]), articleToId.get(articles[1]));
                });
        } catch (Exception e) {
            // Buffered reader initialization failure indicates issues with file
            System.err.println("Problem with file: " + edgeFile + ".../n..." + e);
        }
    }

    /* Core search method. Takes two articles (as names), and returns the
     * shortest path between them (as a stream of article names), if such
     * a path exists
     */
    private Optional<Stream<String>> travel(String starticle, String endicle) {

        // Input's representation within the graph
        final Integer start = articleToId.get(starticle);
        final Integer end = articleToId.get(endicle);

        // For 
        final Map<Integer,Integer> visited = new HashMap<Integer,Integer>();
        idToArticle.keySet().forEach(x -> visited.put(x, null));

        final Queue<Integer> q = new ArrayDeque<Integer>();

        return
            Optional.ofNullable(
                Stream.iterate(end, step -> {
                    getNeighbors(step).forEach(neighbor -> {
                        if (visited.get(neighbor) == null) {
                            visited.put(neighbor, step);
                            q.add(neighbor);
                        }});
                    return q.poll();
                }).filter(step -> step.equals(start) || step == null)
                .findFirst()
                .get()
            ).map(dummy -> {
                final Stream.Builder<Integer> bob = Stream.builder();
                Stream.iterate(start, step -> visited.get(step))
                    .peek(bob)
                    .allMatch(step -> !step.equals(end));
                return bob.build().map(id -> idToArticle.get(id));
            });
    }

    private Optional<Stream<String>> travelThrough(String start, String middle, String end) {
        return travel(start, middle).flatMap(xi ->
                    travel(middle, end).map(yi ->
                        Stream.concat(xi, yi.skip(1))
    ));}

    private static final Function<String,String> safeDecode = x -> {
        try { return URLDecoder.decode(x, "UTF-8"); }
        catch (Exception e) {
            System.err.println("Problem decoding " + x);
            return "(error)";
        }
    };

    public static void main(String[] args) {

        // final PathFinder finder = new PathFinder(args[0], args[1]);
        // finder.travel("Honey", "Beetle").get().forEach(x -> System.out.println(x));

        if (args.length == 2 || args.length == 3 && args[2].equals("useIntermediateNode")) {

            final PathFinder finder = new PathFinder(args[0], args[1]);

            final String[] articles = finder.articleToId.keySet().toArray(new String[0]);
            final Iterator<String> randicles = (new Random())
                .ints(0, articles.length)
                .mapToObj(x -> articles[x])
                .iterator();

            final String a1 = randicles.next();
            final String a2 = randicles.next();
            final String a3 = randicles.next();

            final String message;
            final Optional<Stream<String>> path;

            if (args.length == 2) {
                path = finder.travel(a1, a3);
                message = "Path from " + safeDecode.apply(a1) + " to " + safeDecode.apply(a3);
            } else {
                path = finder.travelThrough(a1, a2, a3);
                message = "Path from " + safeDecode.apply(a1) + " through " + safeDecode.apply(a2) + " to " + safeDecode.apply(a3);
            }
            
            final String arrows = path.map(p ->
                    p.map(safeDecode)
                     .reduce((x, y) -> x + " --> " + y)
                     .get()
            ).orElse("\nNo path found :(");

            System.out.println( message
                              + ", length = "
                              + arrows.split(" --> ").length
                              + "\n"
                              + arrows
                              );

        } else System.err.println("Please check your arguments");
    }


    /**
     * Returns the length of the shortest path from node1 to node2. If no path exists,
     * returns -1.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return length of shortest path
     */
    public int getShortestPathLength(String node1, String node2) {
        return travel(node1, node2)
                .map(x -> (int) x.count())
                .orElse(0) - 1;
    }

    /**
     * Returns a shortest path from node1 to node2, represented as list that has node1 at
     * position 0, node2 in the final position, and the names of each node on the path
     * (in order) in between.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return list of the names of nodes on the shortest path
     */
    public List<String> getShortestPath(String node1, String node2) {
        return travel(node1, node2)
                .map(x -> x.collect(Collectors.toList()))
                .orElse(null);
    }

    /**
     * Returns a shortest path from node1 to node2 that includes the node intermediateNode.
     * This may not be the absolute shortest path between node1 and node2, but should be
     * a shortest path given the constraint that intermediateNodeAppears in the path.
     * @param node1 name of the starting article node
     * @param intermediateNode name of the middle article node
     * @param node2 name of the ending article node
     * @return list that has node1 at position 0, node2 in the final position, and the names of each node
     *      on the path (in order) in between.
     */
    public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
        return travelThrough(node1, intermediateNode, node2)
                .map(x -> x.collect(Collectors.toList()))
                .orElse(null);
    }
}
