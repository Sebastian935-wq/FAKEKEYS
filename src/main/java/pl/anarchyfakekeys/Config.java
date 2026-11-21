package pl.anarchyfakekeys;

public final class Config {
    public static final int[] QTY = {1, 16, 64};
    public static int qtyIdx = 0;
    public static boolean allFake = false;

    public static int qty() { return QTY[qtyIdx]; }
    private Config() {}
}
