/**
 * how many comments?
 * what should lists be if no path?
 * printing numbers weird, ok?
 *
 */

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class PathFinder extends MysteryUnweightedGraphImplementation implements Supplier<String> {

    private Map<String,Integer> articleToId;
    private Map<Integer,String> idToArticle;
    private String[] articles;

    private static Random gen = new Random();

    /**
     * Constructs a PathFinder that represents the graph with nodes (vertices) specified as in
     * nodeFile and edges specified as in edgeFile.
     * @param nodeFile name of the file with the node names
     * @param edgeFile name of the file with the edge names
     */
    public PathFinder(String nodeFile, String edgeFile) {

        articleToId = new HashMap<String,Integer>();
        idToArticle = new HashMap<Integer,String>();

        try {

            (new BufferedReader(new FileReader (new File(nodeFile)))).lines()
                .filter(x -> !x.equals("") && x.charAt(0) != '#')
                .forEach(x -> {
                    Integer id = addVertex();
                    articleToId.put(x, id);
                    idToArticle.put(id, x);
                });

            (new BufferedReader(new FileReader (new File(edgeFile)))).lines()
                .filter(x -> !x.equals("") && x.charAt(0) != '#')
                .forEach(x -> {
                    String[] articles = x.split("\t");
                    addEdge(articleToId.get(articles[0]), articleToId.get(articles[1]));
                });

        } catch (Exception e) {
            System.err.println("There is a problem with the input files:\n" + e);
        }

        articles = articleToId.keySet().toArray(new String[0]);
    }

    private Optional<Stream<String>> travel(String starticle, String endicle) {

        Integer start = articleToId.get(starticle);
        Integer end = articleToId.get(endicle);

        Map<Integer,Integer> visited = new HashMap<Integer,Integer>();
        idToArticle.keySet().forEach(x -> visited.put(x, null));

        Queue<Integer> q = new ArrayDeque<Integer>();
        for (Integer curr = end; curr != null; curr = q.poll()) {

            if (curr.equals(start)) {
                Stream.Builder<Integer> bob = Stream.builder();
                Stream.iterate(start, x -> visited.get(x))
                    .peek(bob)
                    .allMatch(x -> !x.equals(end));
                return Optional.of(bob.build().map(x -> idToArticle.get(x)));
            }
                    
            // Feels a bit hacky, but only final (or 'effectivly final')
            // vars can be in lambdas.
            final Integer tmp = curr;
            getNeighbors(curr).forEach(x -> {
                if (visited.get(x) == null) {
                    visited.put(x, tmp);
                    q.add(x);
                }
            });
        }
        return Optional.empty();
    }

    private Optional<Stream<String>> travelThrough(String start, String middle, String end) {
        return travel(start, middle).flatMap(xi ->
                    travel(middle, end).map(yi ->
                        Stream.concat(xi, yi.skip(1))
                    )
               );
    }

    public String get() { return articles[gen.nextInt(articles.length)]; }

    private static Function<String,String> safeDecode = x -> {
        try { return URLDecoder.decode(x, "UTF-8"); }
        catch (Exception e) {
            System.err.println("Problem decoding " + x);
            return "(error)";
        }
    };

    static String prettyPath(Optional<Stream<String>> path) {
        return path.map(x ->
                x.map(safeDecode)
                 .reduce((y, z) -> y + " --> " + z)
                 // Generally frowned upon, but it is clear enough
                 // that travel never returns an empty stream
                 .get()
            ).orElse("No path found.");
    }

    // TODO print length of path
    public static void main(String[] args) {
        try {
            PathFinder test = new PathFinder(args[0], args[1]);
            String a1 = test.get();
            String a2 = test.get();
            String message = "Path from " + safeDecode.apply(a1) + " to " + safeDecode.apply(a2);
            String path = null;
            if (args.length == 2) {
                path = prettyPath(test.travel(a1, a2));
            } else if (args.length == 3 && args[2].equals("useIntermediateNode")) {
                String a3 = test.get();
                message += " through " + safeDecode.apply(a3);
                path = prettyPath(test.travelThrough(a1, a3, a2));
            } else throw new RuntimeException();
            System.out.println(message + ", length = " + 0 + "\n" + path);
        } catch (Exception e) {
            System.err.println(e);
        }
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
