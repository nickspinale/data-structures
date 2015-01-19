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

        // Queue to be used in the breadth-first search
        final Queue<Integer> q = new ArrayDeque<Integer>();

        // Keys of this map are all of the article id's. As articles are
        // added to q, their predecessor (the node (value) whose iteration
        // in the search added it) overwrites 'empty' as the articles 'visited'
        // value. In such a way, this map serves to allow us to both tell which
        // articles have been visited, and also backtrack once we have reached
        // the end.
        final Map<Integer,Optional<Integer>> visited = new HashMap<Integer,Optional<Integer>>();
        idToArticle.keySet().forEach((Integer x) -> visited.put(x, Optional.empty()));

        // Infinite (lazy evaluated) repitition of the breadth-first
        // iteration that we have talked about in class (and therefore
        // doesn't seem to warrant an explanation (aside from the
        // explanation of 'visited's purpose, which is detailed above)).
        // We start at the end so that when we 'backtrack' using visited,
        // we end up with the steps in the correct order.
        return Stream.iterate(Optional.of(end), (Optional<Integer> mstep) ->
                    mstep.flatMap((Integer step) -> {
                        getNeighbors(step).forEach((Integer neighbor) -> {
                            if (!visited.get(neighbor).isPresent()) {
                                visited.put(neighbor, mstep);
                                q.add(neighbor);
                            }
                        });
                        return Optional.ofNullable(q.poll());
                    }))

            // 'findFirst()' is a short-circuiting, terminal operation. So, this
            // combination is effectively a 'findFirstThat()' sort of thing.
            .filter((Optional<Integer> mstep) ->
                mstep.map((Integer step) ->
                    step.equals(start)
                )
                .orElse(true)
            )
            .findFirst()

            // This is an infinite stream, so findFirst could
            // never return empty. So, this generally frowned-upon
            // operation is safe under these circumstances.
            .get()

            // 'dummy' is not used in the body of its lambda. This construction is
            // analogous to the monadic '>>' (as opposed to '>>='). The 'phantom bind'.
            // Basically, the computation proceeds if the search stopped with a result,
            // rather than an empty q.
            .map((Integer dummy) -> {

                // It puzzles me why a 'takeWhile' (returning another stream)
                // doesn't already exist. However, like 'findFirst', 'allMatch'
                // is a short-circuiting terminal operation, so this works in the
                // same way as our 'takeFirstThat' sort of thing. The only values
                // that get peeked at (and thus added to the new stream by bob the
                // builder) are the ones up to and including the first occurence of
                // start.
                final Stream.Builder<Integer> bob = Stream.builder();
                Stream.iterate(start, (Integer step) -> visited.get(step).get())
                        .peek(bob)
                        .allMatch(step -> !step.equals(end));

                // This function takes and returns article names (becasue that way
                // other methods don't have to worry at all about id's), so we map
                // them back here.
                return bob.build().map(x -> idToArticle.get(x));
            });
    }

    /* Combines two searches where the end of one is the start of the other.
     * If either search is empty, this returns empty as well.
     * This looks pretty monadic. Yay Java, you did it (?)
     */
    private Optional<Stream<String>> travelThrough(String start, String middle, String end) {
        return travel(start, middle).flatMap(xi ->
                    travel(middle, end).map(yi ->
                        Stream.concat(xi, yi.skip(1))
    ));}

    /* A function object for decoding url-compatible strings. For the
     * sake of speed, strings are only decoded when they are about to
     * be displayed (rather than during the creation of 'articleToId'
     * and 'idToArticle', in which case ALL articles would be decoded).
     * An object rather than a method for use with streams.
     */
    private static final Function<String,String> safeDecode = x -> {
        try { return URLDecoder.decode(x, "UTF-8"); }
        catch (Exception e) {
            System.err.println("Problem decoding " + x);
            return "(error)";
        }
    };

    /* Given two files (one for articles and one for links), main finds and
     * pretty-prints the shortest path between two random articles (with another
     * (also random) one inbetween, if 'useIntermediateNode' was specified as
     * a third argument), or determines that no such path exists.
     */
    public static void main(String[] args) {

        // Input is valid in and only in these two cases
        if (args.length == 2 || args.length == 3 && args[2].equals("useIntermediateNode")) {

            // Instantiate this class
            final PathFinder finder = new PathFinder(args[0], args[1]);

            // Array of all articles. Set's 'toArray' requires a token array of the desired
            // return type, unless that type is Object.
            final String[] articles = finder.articleToId.keySet().toArray(new String[0]);

            // An iterator that generates random articles
            final Iterator<String> randicles = (new Random())
            // final Iterator<String> randicles =
            //     Stream.generate(() -> gen.nextInt(articles.length))
            //           .map(x -> articles[x])
            //           .iterator();
                .ints(0, articles.length)
                .mapToObj(x -> articles[x])
                .iterator();

            // Three random articles
            final String a1 = randicles.next();
            final String a2 = randicles.next();
            final String a3 = randicles.next();

            // The base message of the output
            final String message;

            // The path that will be formatted for output
            final Optional<Stream<String>> path;

            // Initialize message and path according to whether the user instructed
            // the use of an intermediate node
            if (args.length == 2) {
                path = finder.travel(a1, a3);
                message = "Path from " + safeDecode.apply(a1) + " to " + safeDecode.apply(a3);
            } else {
                path = finder.travelThrough(a1, a2, a3);
                message = "Path from " + safeDecode.apply(a1) + " through " + safeDecode.apply(a2) + " to " + safeDecode.apply(a3);
            }
            
            // The pretty-printed version of path (or its lack)
            final String arrows = path.map(p ->
                    p.map(safeDecode)
                     .reduce((x, y) -> x + " --> " + y)
                     // Generally frowned upon, but from 'tavel', it is clear
                     // that the path is never empty
                     .get()
            ).orElse("\nNo path found :(");

            // Information combined into final output
            System.out.println( message
                              + ", length = "
                              // This is obviously not ideal. While it appears to be
                              // sketchy, it is, surprisingly, absolutly guarenteed
                              // to be safe, under the condition that input files are
                              // formatted to be compatible with URL's (because such
                              // files would not contain any (non-commented) spaces.
                              // This approach was taken because count() is a terminal
                              // operation, and there is no nice way to copy a stream
                              // (to count one of them) - I suppose that makes sense.
                              // I just see this ugly counting strategy to be a trade-off
                              // of (what I consider to be) the otherwise very appropriate
                              // use of streams.
                              // TL;DR while it looks ugly, this split->length thing
                              // is actually safe, and is kinda the only way.
                              + arrows.split(" --> ").length
                              + "\n"
                              + arrows
                              );

        // An incorect number/combination of arguments must have been supplied
        } else System.err.println("Please check your arguments");
    }


    /**
     * Returns the length of the shortest path from node1 to node2. If no path exists,
     * returns -1.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return length of shortest path
     *
     * Just an int wrapper for travel()
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
     * 
     * Just a list wrapper for travel()
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
     *
     * Just a list wrapper for travelThrough()
     */
    public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
        return travelThrough(node1, intermediateNode, node2)
                .map(x -> x.collect(Collectors.toList()))
                .orElse(null);
    }
}
