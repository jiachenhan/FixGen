package gumtree.spoon;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffImpl;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;

/**
 * Computes the differences between two CtElements.
 *
 * @author Matias Martinez, matias.martinez@inria.fr
 */
public class AstComparator {
	private final Factory factory;

	static {
		// default 0.3
		// 0.1 one failing much more changes
		// 0.2 one failing much more changes
		// 0.3 OK
		// 0.4 OK
		// 0.5
		// 0.6 OK
		// 0.7 1 failing
		// 0.8 2 failing
		// 0.9 two failing tests with more changes
		System.setProperty("gumtree.match.bu.sim", "0.3");

		// default 2
		// 0 is really bad for 211903 t_224542 225391 226622
		// 1 is required for t_225262 and t_213712 to pass
		System.setProperty("gumtree.match.gt.minh", "1");

		// default 1000
		// 0 fails
		// 1 fails
		// 10 fails
		// 100 OK
		// 1000 OK
		System.setProperty("gumtree.match.bu.size", "1000");
	}

	public AstComparator() {
		this(new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));
	}

	public AstComparator(Factory factory) {
		this.factory = factory;
		factory.getEnvironment().setNoClasspath(true);
	}

	/**
	 * compares two java files
	 */
	public Diff compare(File f1, File f2) throws Exception {
		return this.compare(getCtType(f1), getCtType(f2));
	}

	/**
	 * compares two snippet
	 */
	public Diff compare(String left, String right) {
		return compare(getCtType(left), getCtType(right));
	}

	/**
	 * compares two AST nodes
	 */
	public Diff compare(CtElement left, CtElement right) {
		final SpoonGumTreeBuilder scanner = new SpoonGumTreeBuilder();
		return new DiffImpl(scanner.getTreeContext(), scanner.getTree(left), scanner.getTree(right));
	}

	private CtType getCtType(File file) throws Exception {
		SpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);
		compiler.getFactory().getEnvironment().setLevel("OFF");
		compiler.addInputSource(SpoonResourceHelper.createResource(file));
		compiler.build();

		if (factory.Type().getAll().size() == 0) {
			return null;
		}

		return factory.Type().getAll().get(0);
	}

	private CtType<?> getCtType(String content) {
		SpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);
		compiler.addInputSource(new VirtualFile(content, "/test"));
		compiler.build();
		return factory.Type().getAll().get(0);
	}

	public static void main(String[] args) throws Exception {
//		if (args.length != 2) {
//			System.out.println("Usage: DiffSpoon <file_1>  <file_2>");
//			return;
//		}
		
		String f1 = "/Users/Jiajun/Code/Java/tmpspace/CommitExtractorByKeyWords/diffile/ant/0a51af299e_a8b79fd315/0a51af299e871318ffb6241565dfbf771fa503ec/locator.java";
		String f2 = "/Users/Jiajun/Code/Java/tmpspace/CommitExtractorByKeyWords/diffile/ant/0a51af299e_a8b79fd315/a8b79fd3155220929b46352bf45cc5c66521a04a/locator.java";
		
//		final Diff result = new AstComparator().compare("class A { public void m(){if(a>8) a++;}}", "class B { public void m(){if(a>9) a++;}}");
		
//		final Diff result = new AstComparator().compare(new File(args[0]), new File(args[1]));
		final Diff result = new AstComparator().compare(new File(f1), new File(f2));
		System.out.println(result.toString());
	}
}
