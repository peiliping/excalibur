package icesword.agent.util;

public class Triple<L, M, R> {

    private L left;

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    private R right;

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    private M middle;
    
    public M getMiddle() {
        return middle;
    }

    public Triple(L l, M m, R r) {
        this.left = l;
        this.middle = m;
        this.right = r;
    }

    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return new Triple<L, M, R>(left, middle, right);
    }
}
