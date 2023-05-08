package com.betaTest.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/04/10/16:02
 */
public class LoadStrategy {

    public class Node {
        private String ip;

        private Integer weight;

        private Integer currentWeight;

        public Node(String ip, Integer weight) {
            this.ip = ip;
            this.weight = weight;
            this.currentWeight = weight;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public Integer getCurrentWeight() {
            return currentWeight;
        }

        public void setCurrentWeight(Integer currentWeight) {
            this.currentWeight = currentWeight;
        }
    }

    List<Node> servers = Arrays.asList(
            new Node("192.168.1.1", 1),
            new Node("192.168.1.2", 2),
            new Node("192.168.1.3", 3),
            new Node("192.168.1.4", 4));
    private Integer totalWeight;

    public LoadStrategy() {
        this.totalWeight = servers.stream()
                .mapToInt(Node::getWeight)
                .reduce((a, b) -> a + b).getAsInt();
    }


    public String getServer() {
        Node node = servers.stream().max(Comparator.comparingInt(Node::getCurrentWeight)).get();
        node.setCurrentWeight(node.getCurrentWeight() - totalWeight);
        servers.forEach(server -> server.setCurrentWeight(server.getCurrentWeight() + server.getWeight()));
        return node.getIp();
    }


//    public static void main(String[] args) {
//        com.bataTest.test.LoadStrategy roundRobinTest = new com.bataTest.test.LoadStrategy();
//        for (int i = 0; i < 10; i++) {
//            String server = roundRobinTest.getServer();
//            System.out.println("select server: " + server);
//        }
//    }
}

