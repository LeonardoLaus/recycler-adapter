package ext.android.adapter.diff;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by roothost on 2018/3/20.
 */

public interface DiffCapable<P> {

    int DIFF_ITEM = 0;
    int DIFF_CONTENT = 1;

    boolean isSame(@Diff int diff, Object oldItem, Object newItem);

    P payloads(Object oldItem, Object newItem);

    @IntDef({DIFF_ITEM,
            DIFF_CONTENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Diff {

    }

    class DefaultDiffCapable<P> implements DiffCapable<P> {

        @Override
        public boolean isSame(@Diff int diff, Object oldItem, Object newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public P payloads(Object oldItem, Object newItem) {
            return null;
        }
    }
}