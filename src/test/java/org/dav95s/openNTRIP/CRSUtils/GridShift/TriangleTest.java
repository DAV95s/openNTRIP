package org.dav95s.openNTRIP.CRSUtils.GridShift;

import org.junit.Assert;
import org.junit.Test;

public class TriangleTest {

    @Test
    public void initTest1() {
        Node[] nodes = new Node[]{
                new Node(3328, 2641, 0, 0),
                new Node(3823, 2910, 0, 0),
                new Node(3285, 3454, 0, 0)
        };

        Triangle triangle = new Triangle(nodes);
        Assert.assertArrayEquals(new float[]{2641f, 3823f}, triangle.coords, 0);
        Assert.assertArrayEquals(new float[]{813f, 538f}, triangle.dimensions, 0);
    }

    @Test
    public void initTest2() {
        Node[] nodes = new Node[]{
                new Node(3328, -139, 0, 0),
                new Node(3823, 130, 0, 0),
                new Node(3285, 674, 0, 0)
        };

        Triangle triangle = new Triangle(nodes);
        Assert.assertArrayEquals(new float[]{-139f, 3823f}, triangle.coords, 0);
        Assert.assertArrayEquals(new float[]{813f, 538f}, triangle.dimensions, 0);
    }
    @Test
    public void initTest3() {
        Node[] nodes = new Node[]{
                new Node(-312, 1091, 0, 0),
                new Node(183, 1360, 0, 0),
                new Node(-355, 1904, 0, 0)
        };

        Triangle triangle = new Triangle(nodes);
        Assert.assertArrayEquals(new float[]{1091f, 183f}, triangle.coords, 0);
        Assert.assertArrayEquals(new float[]{813f, 538f}, triangle.dimensions, 0);
    }
    @Test
    public void initTest4() {
        Node[] nodes = new Node[]{
                new Node(-1275, -1755, 0, 0),
                new Node(-780, -1485, 0, 0),
                new Node(-1318, -942, 0, 0)
        };

        Triangle triangle = new Triangle(nodes);
        Assert.assertArrayEquals(new float[]{-1755f, -780f}, triangle.coords, 0);
        Assert.assertArrayEquals(new float[]{813f, 538f}, triangle.dimensions, 0);
    }

}