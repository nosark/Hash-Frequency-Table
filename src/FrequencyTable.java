/**
 * Created by kylenosar on 10/17/14.
 */
public interface FrequencyTable<K> {
    void click(K key);
    int count(K key);
}
