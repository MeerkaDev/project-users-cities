import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Scanner;

public class UsersCitiesApplicationMain {
    
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        Scanner scanner = new Scanner(System.in);
    
        while (true) {
            System.out.println("""
                Чтобы выполнить действия введите числа соответствующие командам:
                - Создать пользователя [1]
                - Найти и редактировать пользователя [2]
                - Закрыть приложение [0]
                """);
        
            String command = scanner.nextLine();
        
            if (!command.isEmpty()) {
                try {
                    switch (Integer.parseInt(command)) {
                        case 1:
                            UsersCitiesEditorDB.createUser(scanner, factory);
                            break;
                        case 2:
                            UsersCitiesEditorDB.findAndEditUser(scanner, factory);
                            break;
                        case 0:
                            scanner.close();
                            factory.close();
                            break;
                    }
                
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат ввода числа! Попробуйте снова.");
                }
            }
        }
    }
}
