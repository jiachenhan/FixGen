[![Build Status](https://travis-ci.org/SpoonLabs/gumtree-spoon-ast-diff.svg?branch=master)](https://travis-ci.org/SpoonLabs/gumtree-spoon-ast-diff)

Gumtree Spoon AST Diff
======================

Computes the AST difference between two Spoon abstract syntax trees using the Gumtree algorithm.

If you use this, please cite:

[Fine-grained and Accurate Source Code Differencing][paper] (Jean-Rémy Falleri, Floréal Morandat, Xavier Blanc, Matias Martinez, Martin Monperrus), In Proceedings of the International Conference on Automated Software Engineering, 2014.

Usage
-----

The main class is used this way:

```bash
gumtree.spoon.AstComparator <file_1> <file_2>
```

Testing AST differencing
------------------------

gumtree-spoon-ast-diff is heavily tested. The testing of AST tree differencing is quite interesting.

There are cases where the oracle is pretty clear, for instance for the deletion of a node.

```java
// there is only one deletion at line 442
assertEquals(operations.size(), 1);
assertTrue(diff.containsOperation(operations, OperationKind.Delete, "Literal", "\"UTF-8\""));
assertEquals(442, result.changedNode().getPosition().getLine());

```

or for the addition of a single node

```java
assertEquals(operations.size(), 1);
assertTrue(diff.containsOperation(operations, OperationKind.Insert, "Invocation", "append"));

```

However, with the presence of moves, the answer is less clear. For instance, an insert+delete instead of a move is correct, although not optimal. However, in this case, there are still definitive oracles: for instance, you are sure that the change happened within a certain node

```java
// the change happened in System.out.println() at line 334
CtElement ancestor = result.commonAncestor();
assertTrue(ancestor instanceof CtInvocation);
assertEquals("println", ((CtInvocation)ancestor).getExecutable().getSimpleName());
assertEquals(344,ancestor.getPosition().getLine());

``` 

To conclude, for testing AST differencing, there is not always a unique and complete, it is a blend of assertions on:

* the number of changes
* the presence of certain changes
* the location of the change (node type and content)
* the location of the change (line number)

Download
--------

Download [the latest JAR][jar] or grab via Maven:

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon.labs</groupId>
    <artifactId>gumtree-spoon-ast-diff</artifactId>
    <version>1.0.0</version>
</dependency>
```

or Gradle:

```groovy
compile 'fr.inria.gforge.spoon.labs:gumtree-spoon-ast-diff:1.0.0'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

License
-------

    Copyright 2016 Matias Martinez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [paper]: http://hal.archives-ouvertes.fr/hal-01054552
 [jar]: https://search.maven.org/remote_content?g=fr.inria.gforge.spoon.labs&a=gumtree-spoon-ast-diff&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
