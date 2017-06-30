

package misc.LanguageTools;

import javax.annotation.Generated;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.impl.hash.HashConfigWrapper;
import com.koloboke.collect.impl.hash.LHash;
import com.koloboke.collect.impl.hash.LHashCapacities;
import com.koloboke.collect.impl.Maths;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Generated(value = "com.koloboke.compile.processor.KolobokeCollectionProcessor")
@SuppressFBWarnings(value = { "IA_AMBIGUOUS_INVOCATION_OF_INHERITED_OR_OUTER_METHOD" , "ES_COMPARING_PARAMETER_STRING_WITH_EQ" })
@SuppressWarnings(value = { "all" , "unsafe" , "deprecation" , "overloads" , "rawtypes" })
final class KolobokeWordCountMap extends WordCountMap {
    KolobokeWordCountMap(int expectedSize) {
        this.init(DEFAULT_CONFIG_WRAPPER, expectedSize);
    }

    static void verifyConfig(HashConfig config) {
        if ((config.getGrowthFactor()) != 2.0) {
            throw new IllegalArgumentException(((((((config + " passed, HashConfig for a hashtable\n") + "implementation with linear probing must have growthFactor of 2.0.\n") + "A Koloboke Compile-generated hashtable implementation could have\n") + "a different growth factor, if the implemented type is annotated with\n") + "@com.koloboke.compile.hash.algo.openaddressing.QuadraticProbing or\n") + "@com.koloboke.compile.hash.algo.openaddressing.DoubleHashing"));
        } 
    }

    int[] values;

    String[] set;

    public final boolean isEmpty() {
        return (size()) == 0;
    }

    private HashConfigWrapper configWrapper;

    int size;

    private int maxSize;

    boolean keyEquals(@Nonnull
    String a, @Nullable
    String b) {
        return a.equals(b);
    }

    public int capacity() {
        return set.length;
    }

    int keyHashCode(@Nonnull
    String key) {
        return key.hashCode();
    }

    public final int size() {
        return size;
    }

    int index(Object key) {
        if (key == null)
            throw new NullPointerException(("This hashtable implementation doesn\'t allow null keys.\n" + ("A Koloboke Compile-generated hashtable implementation could contain\n" + ("null keys if the implemented type is annotated with\n" + "@com.koloboke.compile.NullKeyAllowed"))));
        
        String k = ((String) (key));
        String[] keys = set;
        int capacityMask;
        int index;
        String cur;
        if ((cur = keys[(index = (LHash.SeparateKVObjKeyMixing.mix(keyHashCode(k))) & (capacityMask = (keys.length) - 1))]) == k) {
            return index;
        } else {
            if (cur == null) {
                return -1;
            } else {
                if (keyEquals(k, cur)) {
                    return index;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                            return index;
                        } else if (cur == null) {
                            return -1;
                        } else if (keyEquals(k, cur)) {
                            return index;
                        } 
                    }
                }
            }
        }
    }

    public int defaultValue() {
        return 0;
    }

    final void init(HashConfigWrapper configWrapper, int size) {
        KolobokeWordCountMap.verifyConfig(configWrapper.config());
        KolobokeWordCountMap.this.configWrapper = configWrapper;
        KolobokeWordCountMap.this.size = 0;
        internalInit(targetCapacity(size));
    }

    private void internalInit(int capacity) {
        assert Maths.isPowerOf2(capacity);
        maxSize = maxSize(capacity);
        allocateArrays(capacity);
    }

    private int maxSize(int capacity) {
        return !(isMaxCapacity(capacity)) ? configWrapper.maxSize(capacity) : capacity - 1;
    }

    @Override
    public int getInt(String key) {
        int index = index(key);
        if (index >= 0) {
            return values[index];
        } else {
            return defaultValue();
        }
    }

    private void _UpdatableSeparateKVObjLHashSO_allocateArrays(int capacity) {
        set = ((String[]) (new String[capacity]));
    }

    final void initForRehash(int newCapacity) {
        internalInit(newCapacity);
    }

    void allocateArrays(int capacity) {
        _UpdatableSeparateKVObjLHashSO_allocateArrays(capacity);
        values = new int[capacity];
    }

    final void postInsertHook() {
        if ((++(size)) > (maxSize)) {
            int capacity = capacity();
            if (!(isMaxCapacity(capacity))) {
                rehash((capacity << 1));
            } 
        } 
    }

    boolean doubleSizedArrays() {
        return false;
    }

    private int targetCapacity(int size) {
        return LHashCapacities.capacity(configWrapper, size, doubleSizedArrays());
    }

    private boolean isMaxCapacity(int capacity) {
        return LHashCapacities.isMaxCapacity(capacity, doubleSizedArrays());
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    @Override
    public String toString() {
        if (KolobokeWordCountMap.this.isEmpty())
            return "{}";
        
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        String[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            String key;
            if ((key = ((String) (keys[i]))) != null) {
                sb.append(' ');
                sb.append((key != ((Object) (KolobokeWordCountMap.this)) ? key : "(this Map)"));
                sb.append('=');
                sb.append(vals[i]);
                sb.append(',');
                if ((++elementCount) == 8) {
                    int expectedLength = (sb.length()) * ((size()) / 8);
                    sb.ensureCapacity((expectedLength + (expectedLength / 2)));
                } 
            } 
        }
        sb.setCharAt(0, '{');
        sb.setCharAt(((sb.length()) - 1), '}');
        return sb.toString();
    }

    void rehash(int newCapacity) {
        String[] keys = set;
        int[] vals = values;
        initForRehash(newCapacity);
        String[] newKeys = set;
        int capacityMask = (newKeys.length) - 1;
        int[] newVals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            String key;
            if ((key = ((String) (keys[i]))) != null) {
                int index;
                if ((newKeys[(index = (LHash.SeparateKVObjKeyMixing.mix(keyHashCode(key))) & capacityMask)]) != null) {
                    while (true) {
                        if ((newKeys[(index = (index - 1) & capacityMask)]) == null) {
                            break;
                        } 
                    }
                } 
                newKeys[index] = key;
                newVals[index] = vals[i];
            } 
        }
    }

    @Override
    public int addValue(String key, int value) {
        if (key == null)
            throw new NullPointerException(("This hashtable implementation doesn\'t allow null keys.\n" + ("A Koloboke Compile-generated hashtable implementation could contain\n" + ("null keys if the implemented type is annotated with\n" + "@com.koloboke.compile.NullKeyAllowed"))));
        
        String[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        String cur;
        keyPresent : if ((cur = keys[(index = (LHash.SeparateKVObjKeyMixing.mix(keyHashCode(key))) & (capacityMask = (keys.length) - 1))]) != key) {
            keyAbsent : if (cur != null) {
                if (keyEquals(key, cur)) {
                    break keyPresent;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == null) {
                            break keyAbsent;
                        } else if (keyEquals(key, cur)) {
                            break keyPresent;
                        } 
                    }
                }
            } 
            int newValue = (defaultValue()) + value;
            keys[index] = key;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        } 
        int newValue = (vals[index]) + value;
        vals[index] = newValue;
        return newValue;
    }

    KolobokeWordCountMap(HashConfig hashConfig, int expectedSize) {
        this.init(new HashConfigWrapper(hashConfig), expectedSize);
    }

    static class Support {    }

    static final HashConfigWrapper DEFAULT_CONFIG_WRAPPER = new HashConfigWrapper(HashConfig.getDefault());
}