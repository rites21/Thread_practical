package example.DSAs_prac;

import java.util.*;

public class Uber_dsa_tree {

    public static double maxPoints(Node root, Node bStart) {

        // --------------------------------
        // Step 1: Calculate B's arrival time
        // --------------------------------

        Map<Node, Integer> bTime = new HashMap<>();

        Node curr = bStart;
        int time = 0;

        while (curr != null) {
            bTime.put(curr, time);
            curr = curr.parent;
            time++;
        }

        // --------------------------------
        // Step 2: DFS for A
        // --------------------------------

        return dfs(root, 0, bTime);
    }

    static double dfs(Node node, int timeA, Map<Node, Integer> bTime) {

        if (node == null) {
            return Double.NEGATIVE_INFINITY;
        }

        int timeB = bTime.getOrDefault(node, Integer.MAX_VALUE);

        double points;

        if (timeA < timeB) {
            // A reaches first
            points = node.value;
        } else if (timeA == timeB) {
            // Both reach together
            points = node.value / 2.0;
        } else {
            // B reaches first
            points = 0;
        }

        // A must continue downward.
        // If this is a leaf, this is the final score.
        if (node.left == null && node.right == null) {
            return points;
        }

        double left = dfs(node.left, timeA + 1, bTime);
        double right = dfs(node.right, timeA + 1, bTime);

        return points + Math.max(left, right);
    }

    public static void main(String[] args) {

        /*

                 10
                /  \
               5    8
              / \    \
             3   7    9

        B starts at 7

        B path:

        7 -> 5 -> 10

        */

        Node root = new Node(10);
        Node n5 = new Node(5);
        Node n8 = new Node(8);
        Node n3 = new Node(3);
        Node n7 = new Node(7);
        Node n9 = new Node(9);

        root.left = n5;
        root.right = n8;

        n5.parent = root;
        n8.parent = root;

        n5.left = n3;
        n5.right = n7;

        n3.parent = n5;
        n7.parent = n5;

        n8.right = n9;
        n9.parent = n8;

        Node bStart = n7;

        double answer = maxPoints(root, bStart);

        System.out.println(answer);
    }

    static class Node {
        int value;
        Node left;
        Node right;
        Node parent;

        Node(int value) {
            this.value = value;
        }
    }
}