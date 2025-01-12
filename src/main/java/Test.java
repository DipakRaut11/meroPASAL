import lombok.Data;

@Data
public class Test {
    private String testField;

    public static void main(String[] args) {
        Test obj = new Test();
        obj.setTestField("Hello");
        System.out.println(obj.getTestField());
    }
}
