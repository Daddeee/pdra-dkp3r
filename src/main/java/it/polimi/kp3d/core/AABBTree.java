package it.polimi.kp3d.core;


import it.polimi.kp3d.domain.Cube;

import java.util.*;

public final class AABBTree implements Iterable<Cube> {

    public final class AABBTreeIterator implements Iterator<Cube> {
        final Stack<Node> stack;

        public AABBTreeIterator(Node root) {
            stack = new Stack<>();
            while (root != null) {
                stack.push(root);
                root = root.leftChild;
            }
        }

        public boolean hasNext() {
            return !stack.isEmpty();
        }

        public Cube next() {
            Node node = stack.pop();
            Cube result = node.aabb;
            if (node.rightChild != null) {
                node = node.rightChild;
                while (node != null) {
                    stack.push(node);
                    node = node.rightChild;
                }
            }
            return result;
        }
    }

    private static final class Node {
        Cube aabb;

        int height;
        Node parent;
        Node leftChild, rightChild;

        Node(Cube aabb) {
            this.aabb = aabb.clone();
        }

        boolean isLeaf() {
            return leftChild == null && rightChild == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(aabb, node.aabb);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aabb);
        }
    }

    private Node root = null;

    public Node getRoot() {
        return root;
    }

    @Override
    public Iterator<Cube> iterator() {
        return new AABBTreeIterator(root);
    }

    public AABBTree clone() {
        AABBTree cloned = new AABBTree();
        cloned.root = recursiveClone(this.root, null);
        return cloned;
    }

    public Node recursiveClone(Node visited, Node parent) {
        if (visited == null) return null;
        Node cloned = new Node(visited.aabb.clone());
        cloned.parent = parent;
        cloned.leftChild = recursiveClone(visited.leftChild, cloned);
        cloned.rightChild = recursiveClone(visited.rightChild, cloned);
        return cloned;
    }

    public void insert(final Cube aabb) {
        Node toInsert = new Node(aabb);

        if (root == null) {
            root = toInsert;
            return;
        }

        Node node = root;
        while (!node.isLeaf()) {
            Cube combinedAABB;
            Node child1 = node.leftChild;
            Node child2 = node.rightChild;

            long parentArea = node.aabb.getSurfaceArea();

            combinedAABB = node.aabb.minimumBoundingCuboid(toInsert.aabb);
            long combinedArea = combinedAABB.getSurfaceArea();

            // Cost of creating a new parent for this node and the new leaf
            long cost = 2 * combinedArea;

            // Minimum cost of pushing the leaf further down the tree
            long inheritanceCost = 2 * (combinedArea - parentArea);

            long cost1 = getDescendingCost(toInsert, child1, inheritanceCost);
            long cost2 = getDescendingCost(toInsert, child2, inheritanceCost);

            if (cost < cost1 && cost < cost2) {
                break;
            }

            if (cost1 < cost2) {
                node = child1;
            } else {
                node = child2;
            }
        }

        Node sibling = node;
        Node oldParent = node.parent;
        Node newParent = new Node(sibling.aabb.minimumBoundingCuboid(toInsert.aabb));
        newParent.parent = oldParent;
        newParent.height = sibling.height + 1;
        newParent.leftChild = sibling;
        newParent.rightChild = toInsert;
        sibling.parent = newParent;
        toInsert.parent = newParent;

        if (oldParent != null) {
            //The sibling was not the root, connect new parent
            if (oldParent.leftChild == sibling) {
                oldParent.leftChild = newParent;
            } else {
                oldParent.rightChild = newParent;
            }
        } else {
            root = newParent;
        }

        node = toInsert.parent;
        while (node != null) {
            node = balance(node);

            Node child1 = node.leftChild;
            Node child2 = node.rightChild;

            node.height = 1 + Math.max(child1.height, child2.height);
            node.aabb = child1.aabb.minimumBoundingCuboid(child2.aabb);

            node = node.parent;
        }
    }

    private long getDescendingCost(final Node toInsert, final Node child, final long inheritanceCost) {
        Cube aabb = toInsert.aabb.minimumBoundingCuboid(child.aabb);
        if (child.isLeaf()) {
            return aabb.getSurfaceArea() + inheritanceCost;
        } else {
            long oldArea = child.aabb.getSurfaceArea();
            long newArea = aabb.getSurfaceArea();
            return (newArea - oldArea) + inheritanceCost;
        }
    }


    private Node balance(Node a) {
        if (a.isLeaf() || a.height < 2)
            return a;

        Node b = a.leftChild;
        Node c = a.rightChild;

        int balance = c.height - b.height;

        // rotate c up
        if (balance > 1) {
            Node f = c.leftChild;
            Node g = c.rightChild;

            //swap a and c
            c.leftChild = a;
            c.parent = a.parent;
            a.parent = c;

            //a's old parent points to c
            if (c.parent != null) {
                if (c.parent.leftChild == a)
                    c.parent.leftChild = c;
                else
                    c.parent.rightChild = c;
            } else {
                root = c;
            }

            // rotate
            if (f.height > g.height) {
                c.rightChild = f;
                a.rightChild = g;
                g.parent = a;
                a.aabb = b.aabb.minimumBoundingCuboid(g.aabb);
                c.aabb = a.aabb.minimumBoundingCuboid(f.aabb);

                a.height = 1 + Math.max(b.height, g.height);
                c.height = 1 + Math.max(a.height, f.height);
            } else {
                c.rightChild = g;
                a.rightChild = f;
                f.parent = a;
                a.aabb = b.aabb.minimumBoundingCuboid(f.aabb);
                c.aabb = a.aabb.minimumBoundingCuboid(g.aabb);

                a.height = 1 + Math.max(b.height, f.height);
                c.height = 1 + Math.max(a.height, g.height);
            }

            return c;
        }

        // rotate b up
        if (balance < -1) {
            Node d = b.leftChild;
            Node e = b.rightChild;

            //Swap a and b
            b.leftChild = a;
            b.parent = a.parent;
            a.parent = b;

            // A's old parent should point to B
            if (b.parent != null) {
                if (b.parent.leftChild == a) {
                    b.parent.leftChild = b;
                } else {
                    b.parent.rightChild = b;
                }
            } else {
                root = b;
            }

            // Rotate
            if (d.height > e.height) {
                b.rightChild = d;
                a.leftChild = e;
                e.parent = a;
                a.aabb = c.aabb.minimumBoundingCuboid(e.aabb);
                b.aabb = a.aabb.minimumBoundingCuboid(d.aabb);

                a.height = 1 + Math.max(c.height, e.height);
                b.height = 1 + Math.max(a.height, d.height);
            } else {
                b.rightChild = e;
                a.leftChild = d;
                d.parent = a;
                a.aabb = c.aabb.minimumBoundingCuboid(d.aabb);
                b.aabb = a.aabb.minimumBoundingCuboid(e.aabb);

                a.height = 1 + Math.max(c.height, d.height);
                b.height = 1 + Math.max(a.height, e.height);
            }

            return b;
        }

        return a;
    }

    public List<Cube> getOverlapping(Cube aabb) {
        List<Cube> overlapping = new ArrayList<>();
        
        if (root == null)
            return overlapping;

        Stack<Node> s = new Stack<>();
        if (CubeUtils.overlap(aabb, root.aabb)){
            if (root.isLeaf())
                return List.of(root.aabb);
            s.push(root);
        }

        while (s.size() > 0) {
            Node cur = s.pop();
            if (CubeUtils.overlap(aabb, cur.leftChild.aabb)) {
                if (cur.leftChild.isLeaf())
                    overlapping.add(cur.leftChild.aabb);
                else
                    s.push(cur.leftChild);
            }

            if (CubeUtils.overlap(aabb, cur.rightChild.aabb)) {
                if (cur.rightChild.isLeaf())
                    overlapping.add(cur.rightChild.aabb);
                else
                    s.push(cur.rightChild);
            }
        }

        return overlapping;
    }

    public boolean overlaps(Cube aabb) {
        if (root == null)
            return false;

        Stack<Node> s = new Stack<>();
        if (CubeUtils.overlap(aabb, root.aabb)){
            if (root.isLeaf())
                return true;
            s.push(root);
        }

        while (s.size() > 0) {
            Node cur = s.pop();
            if (CubeUtils.overlap(aabb, cur.leftChild.aabb)) {
                if (cur.leftChild.isLeaf())
                    return true;
                s.push(cur.leftChild);
            }

            if (CubeUtils.overlap(aabb, cur.rightChild.aabb)) {
                if (cur.rightChild.isLeaf())
                    return true;
                s.push(cur.rightChild);
            }
        }

        return false;
    }

    public boolean overlaps(Cube aabb, Set<Cube> ignoreList) {
        if (root == null)
            return false;

        Stack<Node> s = new Stack<>();
        if (CubeUtils.overlap(aabb, root.aabb)){
            if (root.isLeaf())
                return !ignoreList.contains(root.aabb);
            s.push(root);
        }

        while (s.size() > 0) {
            Node cur = s.pop();
            if (CubeUtils.overlap(aabb, cur.leftChild.aabb)) {
                if (cur.leftChild.isLeaf() && !ignoreList.contains(cur.leftChild.aabb))
                    return true;
                if(!cur.leftChild.isLeaf())
                        s.push(cur.leftChild);
            }

            if (CubeUtils.overlap(aabb, cur.rightChild.aabb)) {
                if (cur.rightChild.isLeaf() && !ignoreList.contains(cur.rightChild.aabb))
                    return true;
                if(!cur.rightChild.isLeaf())
                    s.push(cur.rightChild);
            }
        }

        return false;
    }

    public List<Cube> getSupportingBoxes(Cube aabb, int tolerance) {
        if (root == null) {
            return Collections.emptyList();
        }

        List<Cube> supportingBoxes = new ArrayList<>();
        Stack<Node> s = new Stack<>();

        // if overlapping on X,Y
        addSupportingOrPushToStack(aabb, s, supportingBoxes, root, tolerance);

        while (s.size() > 0) {
            Node cur = s.pop();
            addSupportingOrPushToStack(aabb, s, supportingBoxes, cur.leftChild, tolerance);
            addSupportingOrPushToStack(aabb, s, supportingBoxes, cur.rightChild, tolerance);
        }

        return supportingBoxes;
    }

    private void addSupportingOrPushToStack(final Cube aabb, final Stack<Node> s, final List<Cube> supportingBoxes,
                                            final Node child1, int tolerance) {
        if (CubeUtils.overlapXY(aabb, child1.aabb)){
            if (child1.isLeaf()) {
                int diff = aabb.getZ() - child1.aabb.getMaxZ();
                if (0 <= diff && diff <= tolerance)
                    supportingBoxes.add(child1.aabb);
            } else {
                if (child1.aabb.getMaxZ() >= aabb.getZ() - tolerance && child1.aabb.getZ() < aabb.getZ())
                    s.push(child1);
            }
        }
    }

    public Cube getClosestPrecedentZ(Cube aabb) {
        if (root == null) {
            return null;
        }

        int minBound = aabb.getZ();
        int currentMax = 0;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapXY(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxZ();
            int rootMinBound = root.aabb.getZ();
            if (root.isLeaf()) {
                if (minBound >= rootMaxBound && rootMaxBound > currentMax) {
                    closest = root.aabb;
                    currentMax = closest.getMaxZ();
                }
            } else {
                if (minBound >= rootMinBound && rootMaxBound > currentMax)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapXY(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxZ();
                int leftChildMinBound = cur.leftChild.aabb.getZ();
                if (cur.leftChild.isLeaf()) {
                    if (minBound >= leftChildMaxBound && leftChildMaxBound > currentMax) {
                        closest = cur.leftChild.aabb;
                        currentMax = closest.getMaxZ();
                    }
                } else {
                    if (minBound >= leftChildMinBound && leftChildMaxBound > currentMax)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapXY(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxZ();
                int rightChildMinBound = cur.rightChild.aabb.getZ();
                if (cur.rightChild.isLeaf()) {
                    if (minBound >= rightChildMaxBound && rightChildMaxBound > currentMax) {
                        closest = cur.rightChild.aabb;
                        currentMax = closest.getMaxZ();
                    }
                } else {
                    if (minBound >= rightChildMinBound && rightChildMaxBound > currentMax)
                        s.push(cur.rightChild);
                }
            }
        }
        return closest;
    }

    public Cube getClosestPrecedentY(Cube aabb) {
        if (root == null) {
            return null;
        }

        int minBound = aabb.getY();
        int currentMax = 0;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapXZ(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxY();
            int rootMinBound = root.aabb.getY();
            if (root.isLeaf()) {
                if (minBound >= rootMaxBound && rootMaxBound > currentMax) {
                    closest = root.aabb;
                    currentMax = closest.getMaxY();
                }
            } else {
                if (minBound >= rootMinBound && rootMaxBound > currentMax)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapXZ(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxY();
                int leftChildMinBound = cur.leftChild.aabb.getY();
                if (cur.leftChild.isLeaf()) {
                    if (minBound >= leftChildMaxBound && leftChildMaxBound > currentMax) {
                        closest = cur.leftChild.aabb;
                        currentMax = closest.getMaxY();
                    }
                } else {
                    if (minBound >= leftChildMinBound && leftChildMaxBound > currentMax)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapXZ(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxY();
                int rightChildMinBound = cur.rightChild.aabb.getY();
                if (cur.rightChild.isLeaf()) {
                    if (minBound >= rightChildMaxBound && rightChildMaxBound > currentMax) {
                        closest = cur.rightChild.aabb;
                        currentMax = closest.getMaxY();
                    }
                } else {
                    if (minBound >= rightChildMinBound && rightChildMaxBound > currentMax)
                        s.push(cur.rightChild);
                }
            }
        }
        return closest;
    }

    public Cube getClosestPrecedentX(Cube aabb) {
        if (root == null) {
            return null;
        }

        int minBound = aabb.getX();
        int currentMax = 0;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapYZ(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxX();
            int rootMinBound = root.aabb.getX();
            if (root.isLeaf()) {
                if (minBound >= rootMaxBound && rootMaxBound > currentMax) {
                    closest = root.aabb;
                    currentMax = closest.getMaxX();
                }
            } else {
                if (minBound >= rootMinBound && rootMaxBound > currentMax)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapYZ(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxX();
                int leftChildMinBound = cur.leftChild.aabb.getX();
                if (cur.leftChild.isLeaf()) {
                    if (minBound >= leftChildMaxBound && leftChildMaxBound > currentMax) {
                        closest = cur.leftChild.aabb;
                        currentMax = closest.getMaxX();
                    }
                } else {
                    if (minBound >= leftChildMinBound && leftChildMaxBound > currentMax)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapYZ(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxX();
                int rightChildMinBound = cur.rightChild.aabb.getX();
                if (cur.rightChild.isLeaf()) {
                    if (minBound >= rightChildMaxBound && rightChildMaxBound > currentMax) {
                        closest = cur.rightChild.aabb;
                        currentMax = closest.getMaxX();
                    }
                } else {
                    if (minBound >= rightChildMinBound && rightChildMaxBound > currentMax)
                        s.push(cur.rightChild);
                }
            }
        }

        return closest;
    }

    public Cube getClosestSuccessorZ(Cube aabb) {
        if (root == null) {
            return null;
        }

        int maxBound = aabb.getMaxZ();
        int currentMin = Integer.MAX_VALUE;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapXY(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxZ();
            int rootMinBound = root.aabb.getZ();
            if (root.isLeaf()) {
                if (maxBound <= rootMinBound && rootMinBound < currentMin) {
                    closest = root.aabb;
                    currentMin = closest.getZ();
                }
            } else {
                if (maxBound <= rootMaxBound && rootMinBound < currentMin)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapXY(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxZ();
                int leftChildMinBound = cur.leftChild.aabb.getZ();
                if (cur.leftChild.isLeaf()) {
                    if (maxBound <= leftChildMinBound && leftChildMinBound < currentMin) {
                        closest = cur.leftChild.aabb;
                        currentMin = closest.getZ();
                    }
                } else {
                    if (maxBound <= leftChildMaxBound && leftChildMinBound < currentMin)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapXY(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxZ();
                int rightChildMinBound = cur.rightChild.aabb.getZ();
                if (cur.rightChild.isLeaf()) {
                    if (maxBound <= rightChildMinBound && rightChildMinBound < currentMin) {
                        closest = cur.rightChild.aabb;
                        currentMin = closest.getZ();
                    }
                } else {
                    if (maxBound <= rightChildMaxBound && rightChildMinBound < currentMin)
                        s.push(cur.rightChild);
                }
            }
        }
        return closest;
    }

    public Cube getClosestSuccessorY(Cube aabb) {
        if (root == null) {
            return null;
        }

        int maxBound = aabb.getMaxY();
        int currentMin = Integer.MAX_VALUE;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapXZ(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxY();
            int rootMinBound = root.aabb.getY();
            if (root.isLeaf()) {
                if (maxBound <= rootMinBound && rootMinBound < currentMin) {
                    closest = root.aabb;
                    currentMin = closest.getY();
                }
            } else {
                if (maxBound <= rootMaxBound && rootMinBound < currentMin)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapXZ(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxY();
                int leftChildMinBound = cur.leftChild.aabb.getY();
                if (cur.leftChild.isLeaf()) {
                    if (maxBound <= leftChildMinBound && leftChildMinBound < currentMin) {
                        closest = cur.leftChild.aabb;
                        currentMin = closest.getY();
                    }
                } else {
                    if (maxBound <= leftChildMaxBound && leftChildMinBound < currentMin)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapXZ(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxY();
                int rightChildMinBound = cur.rightChild.aabb.getY();
                if (cur.rightChild.isLeaf()) {
                    if (maxBound <= rightChildMinBound && rightChildMinBound < currentMin) {
                        closest = cur.rightChild.aabb;
                        currentMin = closest.getY();
                    }
                } else {
                    if (maxBound <= rightChildMaxBound && rightChildMinBound < currentMin)
                        s.push(cur.rightChild);
                }
            }
        }
        return closest;
    }

    public Cube getClosestSuccessorX(Cube aabb) {
        if (root == null) {
            return null;
        }

        int maxBound = aabb.getMaxX();
        int currentMin = Integer.MAX_VALUE;
        Cube closest = null;
        Stack<Node> s = new Stack<>();

        if (CubeUtils.overlapYZ(aabb, root.aabb)) {
            int rootMaxBound = root.aabb.getMaxX();
            int rootMinBound = root.aabb.getX();
            if (root.isLeaf()) {
                if (maxBound <= rootMinBound && rootMinBound < currentMin) {
                    closest = root.aabb;
                    currentMin = closest.getX();
                }
            } else {
                if (maxBound <= rootMaxBound && rootMinBound < currentMin)
                    s.push(root);
            }
        }

        while (s.size() > 0) {
            Node cur = s.pop();

            if (CubeUtils.overlapYZ(aabb, cur.leftChild.aabb)) {
                int leftChildMaxBound = cur.leftChild.aabb.getMaxX();
                int leftChildMinBound = cur.leftChild.aabb.getX();
                if (cur.leftChild.isLeaf()) {
                    if (maxBound <= leftChildMinBound && leftChildMinBound < currentMin) {
                        closest = cur.leftChild.aabb;
                        currentMin = closest.getX();
                    }
                } else {
                    if (maxBound <= leftChildMaxBound && leftChildMinBound < currentMin)
                        s.push(cur.leftChild);
                }
            }

            if (CubeUtils.overlapYZ(aabb, cur.rightChild.aabb)) {
                int rightChildMaxBound = cur.rightChild.aabb.getMaxX();
                int rightChildMinBound = cur.rightChild.aabb.getX();
                if (cur.rightChild.isLeaf()) {
                    if (maxBound <= rightChildMinBound && rightChildMinBound < currentMin) {
                        closest = cur.rightChild.aabb;
                        currentMin = closest.getX();
                    }
                } else {
                    if (maxBound <= rightChildMaxBound && rightChildMinBound < currentMin)
                        s.push(cur.rightChild);
                }
            }
        }
        return closest;
    }

    public Node find(Cube aabb) {
        return traverseFind(new Node(aabb), root);
    }

    public Node traverseFind(Node aabb, Node n) {
        if (n.equals(aabb)) return n;
        if (n.isLeaf()) return null;
        Node l = traverseFind(aabb, n.leftChild);
        if (l != null) return l;
        return traverseFind(aabb, n.rightChild);
    }

    public void remove(Cube aabb) {
        Node leaf = find(aabb);

        if (leaf.equals(root)) {
            root = null;
            return;
        }

        Node parent = leaf.parent;
        Node grandParent = parent.parent;
        Node sibling = (parent.leftChild == leaf) ? parent.rightChild : parent.leftChild;

        if (grandParent != null) {
            // Destroy parent and connect sibling to grandParent.
            if (grandParent.leftChild == parent) {
                grandParent.leftChild = sibling;
            } else {
                grandParent.rightChild = sibling;
            }
            sibling.parent = grandParent;

            // Adjust ancestor bounds.
            Node index = grandParent;
            while (index != null) {
                index = balance(index);

                Node child1 = index.leftChild;
                Node child2 = index.rightChild;

                index.aabb = child1.aabb.minimumBoundingCuboid(child2.aabb);
                index.height = 1 + Math.max(child1.height, child2.height);

                index = index.parent;
            }
        } else {
            root = sibling;
            sibling.parent = null;
        }
    }
}