package util;

public final class Common {
    private Common(){}
    public static void fail(){
        assertC(false);
    }
    public static void assertC(Boolean cond){
        if (!cond)
            throw new AssertionError();
    }
}
