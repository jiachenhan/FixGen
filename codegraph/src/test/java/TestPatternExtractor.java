import builder.PatternExtractor;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import model.GraphConfiguration;
import model.graph.node.actions.ActionNode;
import model.pattern.Pattern;
import org.junit.Test;
import utils.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestPatternExtractor {
    @Test
    public void testPatternExtractFromSinglePair() {
        CodeGraph change1 = constructActionGraph("73");
        CodeGraph change2 = constructActionGraph("74");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern73-74.dot");
            dot.toDotFile(dir1);
        }

        assertEquals(change1.getNodes().stream().filter(s -> s instanceof ActionNode).count(), 1);
        assertEquals(change2.getNodes().stream().filter(s -> s instanceof ActionNode).count(), 1);

    }

    /**
     * test for api-misuse
     */
    @Test
    public void testPatternExtractFromMultiplePairs1() {
        CodeGraph change1 = constructActionGraph("73");
        CodeGraph change2 = constructActionGraph("74");
        CodeGraph change3 = constructActionGraph("75");
        CodeGraph change4 = constructActionGraph("76");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        cgs.add(change3);
        cgs.add(change4);

        DotGraph dot1 = new DotGraph(change1, new GraphConfiguration(), 0);
        dot1.toDotFile(new File(System.getProperty("user.dir") + "/out/73.dot"));
        DotGraph dot2 = new DotGraph(change2, new GraphConfiguration(), 0);
        dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/74.dot"));
        DotGraph dot3 = new DotGraph(change3, new GraphConfiguration(), 0);
        dot3.toDotFile(new File(System.getProperty("user.dir") + "/out/75.dot"));
        DotGraph dot4 = new DotGraph(change4, new GraphConfiguration(), 0);
        dot4.toDotFile(new File(System.getProperty("user.dir") + "/out/76.dot"));

        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern73-76.dot");
            dot.toDotFile(dir1);
        }
    }

    @Test
    public void testPatternExtractFromMultiplePairs2() {
        CodeGraph change1 = constructActionGraph2("EmptyCheck/Genesis#26");
        CodeGraph change2 = constructActionGraph2("EmptyCheck/Genesis#69");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0);
            int patternIndex = combinedGraphs.indexOf(pat);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern_EmptyCheck" + patternIndex + ".dot");
            dot.toDotFile(dir1);
        }
    }

    /**
     * special for FixBench dataset
     */
    public static CodeGraph constructActionGraph2(String id) {
        String rootDir = "src/test/resources/FixBench/";
        File srcDir = new File(rootDir + id + "/new");
        assertEquals("src file directory should have one java file", 1, srcDir.listFiles().length);
        String srcName = srcDir.listFiles()[0].getName();
        String srcFile = rootDir + id + "/old/" + srcName;
        String dstFile = rootDir + id + "/new/" + srcName;
        String diffFile = rootDir + id + "/diff.diff";
        List<Pair<CodeGraph, CodeGraph>> changedCG = TestUtil.getCodeGraphPair(srcFile, dstFile, diffFile, srcName);
        if (changedCG.size()>1) {
            System.out.println("[ERROR]changed codegraph greater than 1");
            return null;
        } else {
            Pair<CodeGraph, CodeGraph> pair = changedCG.get(0);
            CodeGraph srcGraph = pair.getFirst();
            CodeGraph dstGraph = pair.getSecond();
            AstComparator diff = new AstComparator();
            Diff editScript1 = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));

            MappingStore mapStore = new MappingStore(srcGraph, dstGraph, editScript1);
            mapStore.init();
            srcGraph.addMappingStore(mapStore);

            srcGraph.addActions(editScript1);
            return srcGraph;
        }
    }

    /**
     * special for APImisuse dataset
     */
    public static CodeGraph constructActionGraph(String id) {
        String rootDir = "./src/test/resources/APImisuse/";
        String srcFile = rootDir + id + "/src.java";
        String dstFile = rootDir + id + "/tar.java";
        String diffFile = rootDir + id + "/diff.diff";
        Map<String, String> fileNameById = TestUtil.loadBuggyFileName(rootDir + "/bugs.csv");
        String srcName = fileNameById.get(id);
        List<Pair<CodeGraph, CodeGraph>> changedCG = TestUtil.getCodeGraphPair(srcFile, dstFile, diffFile, srcName);
        if (changedCG.size()>1) {
            System.out.println("[ERROR]changed codegraph greater than 1");
            return null;
        } else {
            Pair<CodeGraph, CodeGraph> pair = changedCG.get(0);
            CodeGraph srcGraph = pair.getFirst();
            CodeGraph dstGraph = pair.getSecond();
            AstComparator diff = new AstComparator();
            Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));
            MappingStore mapStore = new MappingStore(srcGraph, dstGraph, editScript);
            mapStore.init();
            srcGraph.addMappingStore(mapStore);
            srcGraph.addActions(editScript);
            return srcGraph;
        }
    }
}
