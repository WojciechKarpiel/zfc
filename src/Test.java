public class Test {
    int hehe(Intf1 i){
        return 1;
    }
    int hehe(Intf2 i){
        return 2;
    }

    public static void main(String[] args) {
        Elo e = new Elo();
        Intf1 e1 =e;
        Intf2 e2=e;
        System.out.println(new Test().hehe(e1));
        System.out.println(new Test().hehe(e2));
    }
}

interface Intf1{}
interface Intf2{}
class Elo implements Intf1,Intf2{}

