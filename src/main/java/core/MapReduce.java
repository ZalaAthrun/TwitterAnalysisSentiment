package core;

import entity.Sentiment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MapReduce {
    abstract class Mapper<E>{
        abstract List<E> map(String data);
    }
    abstract class Reducer<E,V,Z>{
        abstract Map<E,V> reduce(List<Z> data);
    }
}
