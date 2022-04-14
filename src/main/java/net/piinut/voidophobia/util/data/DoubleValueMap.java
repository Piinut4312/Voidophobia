package net.piinut.voidophobia.util.data;

import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class DoubleValueMap<A, B, C> {

    private final Map<A, B> leftMap;
    private final Map<A, C> rightMap;

    public DoubleValueMap(){
        leftMap = new HashMap<>();
        rightMap = new HashMap<>();
    }

    public DoubleValueMap(A a, B b, C c){
        this();
        leftMap.put(a, b);
        rightMap.put(a, c);
    }

    public boolean containsKey(A a){
        //0=left. 1=right, 2=0&&1, 3=0||1
        boolean left = leftMap.containsKey(a);
        boolean right = rightMap.containsKey(a);

        return left && right;
    }

    public Pair<B, C> get(A key){
        return new Pair<>(leftMap.get(key), rightMap.get(key));
    }

    public void put(A key, B value1, C value2){
        this.leftMap.put(key, value1);
        this.rightMap.put(key, value2);
    }

}
