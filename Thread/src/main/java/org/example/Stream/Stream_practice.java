package org.example.Stream;

import java.security.KeyStore;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Stream_practice {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
//        IntStream.iterate(n, i -> i >= 0, i -> i - 10)
//                .forEach(System.out::println);
        int sum = IntStream.rangeClosed(1,n).reduce(0, Integer::sum);
        System.out.println(sum);

        Map<String, Integer> serverData = new HashMap<>();
        serverData.put("Server 1", 5);
        serverData.put("Server 2", 4);
        serverData.put("Server 3", 5);
        serverData.put("Server 4", 6);
        serverData.put("Server 5", 2);
        serverData.put("Server 6", 4);

        Map<Integer,List<String>> ans = serverData.entrySet().stream().
                collect(Collectors.groupingBy(Map.Entry::getValue,Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        int[] nums = {1,2,3,2};
        Map<Integer,Integer> countMap = new HashMap<>();

        for(Integer number: nums){
            countMap.put(number,countMap.getOrDefault(number,0)+1);
        }
        int count = countMap.entrySet().stream().filter(entry ->entry.getValue()==1).mapToInt(Map.Entry::getKey).sum();
    }
}
