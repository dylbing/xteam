public class Clear {
    Clear(){}
    public static void cls(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }catch(Exception E) {
            System.out.print(E);
        }
    }
}