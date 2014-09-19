package jakiro.util;


public class Pair<L, R> {

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

    public Pair(L l, R r) {
        this.left = l;
        this.right = r;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<L, R>(left, right);
    }

}
