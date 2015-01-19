import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class PathFinder extends MysteryUnweightedGraphImplementation {

    private final Map<String,Integer> articleToId;
    private final Map<Integer,String> idToArticle;

    private void process (String file, Consumer<String> consumer) {
        try {
            (new BufferedReader(new FileReader (new File(file))))
                .lines()
                .filter(line -> !line.equals("") && line.charAt(0) != '#')
                .forEach(consumer);
        } catch(Exception e) {
            System.err.println("Problem processing " + file + "...\n..." + e);
            System.exit(0);
        }
    }

    public PathFinder(String nodeFile, String edgeFile) {

        articleToId = new HashMap<String,Integer>();
        idToArticle = new HashMap<Integer,String>();

        process(nodeFile, line -> {
            Integer id = addVertex();
            articleToId.put(line, id);
            idToArticle.put(id, line);
        });
        process(edgeFile, line -> {
            String[] links = line.split("\t");
            addEdge(articleToId.get(links[0]), articleToId.get(links[1]));
        });
    }

    private Optional<Stream<String>> travel(String starticle, String endicle) {

        final Integer start = articleToId.get(starticle);
        final Integer end = articleToId.get(endicle);

        final Queue<Integer> q = new ArrayDeque<Integer>();

        final Map<Integer,Optional<Integer>> visited = new HashMap<Integer,Optional<Integer>>();
        idToArticle.keySet().forEach((Integer x) -> visited.put(x, Optional.empty()));

        return Stream.iterate(Optional.of(start), stepM ->
            stepM.flatMap(step -> {
                getNeighbors(step).forEach(neighbor -> {
                    if(!visited.get(neighbor).isPresent()) {
                        visited.put(neighbor, stepM);
                        q.add(neighbor);
                }});
                return Optional.ofNullable(q.poll());
        })).filter(stepM ->
            stepM.map(step -> step.equals(end)).orElse(true)
        ).findFirst().get().map(dummy -> {
            final Stream.Builder<Integer> bob = Stream.builder();
            Stream.iterate(end, step -> visited.get(step).get())
                  .peek(bob)
                  .allMatch(step -> !step.equals(start));
            return bob.build().map(x -> idToArticle.get(x));
    });}

    private Optional<Stream<String>> travelThrough(String start, String middle, String end) {
        return travel(start, middle).flatMap(x ->
                    travel(middle, end).map(y ->
                        Stream.concat(x, y.skip(1))
    ));}

    final private static Function<String,String> safeDecode = x -> {
        try { return URLDecoder.decode(x, "UTF-8"); }
        catch(Exception e) {
            System.err.println("Problem decoding " + x);
            return "(error)";
        }
    };

    public static void main(String[] args) {

        if(args.length != 2 && (args.length != 3 && args[2].equals("useIntermediateNode"))) {
            System.out.println("Please check your arguments");
            return;
        }
            
        final PathFinder finder = new PathFinder(args[0], args[1]);
        final String[] articles = finder.articleToId.keySet().toArray(new String[0]);

        if(articles.length == 0) {
            System.out.println("Empty vertex file. Unable to choose vertecies.");
            return;
        }

        final Iterator<String> randicles = (new Random())
            .ints(0, articles.length)
            .mapToObj(x -> articles[x])
            .iterator();

        final String a1 = randicles.next();
        final String a2 = randicles.next();
        final String a3 = randicles.next();

        final String message;
        final Optional<Stream<String>> path;

        if(args.length == 2) {
            path = finder.travel(a1, a3);
            message = "Path from " + safeDecode.apply(a1) + " to " + safeDecode.apply(a3);
        } else {
            path = finder.travelThrough(a1, a2, a3);
            message = "Path from " + safeDecode.apply(a1) + " through " + safeDecode.apply(a2) + " to " + safeDecode.apply(a3);
        }

        final Optional<String> arrowsM = path.map(p -> p
                .map(safeDecode)
                .reduce((x, y) -> y + " --> " + x)
                .get()
         );

        System.out.println("\n#\t" + message + ":\n#\t" +
            arrowsM.map(arrows ->
                "Length = " + (arrows.split(" --> ").length - 1) + "\n#\t" + arrows
            ).orElse("No path found :(") + "\n");
    }


    public int getShortestPathLength(String node1, String node2) {
        return travel(node1, node2)
                .map(x -> (int) x.count())
                .orElse(0) - 1;
    }

    final private static Function<Stream<String>,List<String>> listify = x -> {
        List<String> list = x.collect(Collectors.toList());
        Collections.reverse(list);
        return list;
    };

    public List<String> getShortestPath(String node1, String node2) {
        return travel(node1, node2).map(listify).orElse(null);
    }

    public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
        return travelThrough(node1, intermediateNode, node2).map(listify).orElse(null);
    }
}
