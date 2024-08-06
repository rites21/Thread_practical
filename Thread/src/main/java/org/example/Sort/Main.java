package org.example.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
//        int[] arr= {5,10,4,9,7,8,6};
        List<Integer> arr=Arrays.asList(5,10,4,9,7,8,6);
        List<Integer> ans = fun(arr,0,arr.size()-1);
        System.out.println(arr.toString());
        System.out.println(ans.toString());
    }

    private static List<Integer> fun(List<Integer> arr, int lo, int hi) {
        if(lo>=hi){
            List<Integer> base = new ArrayList<>();
            base.add(arr.get(lo));
            System.out.println(base.toString());
            return base;
        }

        int mid = (lo+hi)/2;
        List<Integer> lsp = fun(arr,lo,mid);
        List<Integer> rsp = fun(arr,mid+1,hi);

        List<Integer> mergeArr = merge(lsp,rsp);
        System.out.println(mergeArr.toString());

        return mergeArr;
    }
    private static List<Integer> merge(List<Integer> arr1, List<Integer> arr2){
        int i =0;
        int j =0;
        int[] res = new int[arr1.size()+ arr2.size()];
        int ind = 0;

        while(i< arr1.size() && j < arr2.size()){
            if(arr1.get(i)<=arr2.get(j)){
                res[ind++] = arr1.get(i);
                i++;
            }else{
                res[ind++] = arr2.get(j);
                j++;
            }
        }
        while(i< arr1.size()){
            res[ind++] = arr1.get(i);
            i++;
        }

        while (j< arr2.size()){
            res[ind++] = arr2.get(j);
            j++;
        }

        List<Integer> list = new ArrayList<>();
        for(int a: res){
            list.add(a);
        }
//        System.out.println(res.toString());
//        System.out.println(list.toString());
        return list;



    }
}
