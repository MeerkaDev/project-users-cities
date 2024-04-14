import entities.City;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;

public class UsersCitiesEditorDB {
    public static void createUser(Scanner scanner, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
    
        // Запрос для получения списка городов
        TypedQuery<City> cityTypedQuery = manager.createQuery(
            "select c from City c", City.class
        );
        
        List<City> cities = cityTypedQuery.getResultList();
    
        City chosenCity = null;
        
        while (true) {
            System.out.println("Выберите город (введите id города из списка ниже):");
    
            for (City city : cities) {
                System.out.println(city.getName() + " - " + city.getId());
            }
    
            String chosenCityId = scanner.nextLine();
    
            TypedQuery<City> chosenCityQuery = manager.createQuery(
                "select c from City c where c.id = " + Integer.parseInt(chosenCityId), City.class
            ); // Переделать через параметры
    
            chosenCity = chosenCityQuery.getSingleResult();
        
            if (chosenCity != null) {
                break;
            } else {
                System.out.println("Некорректно введен id города, либо город с таким id не найден!");
            }
        }
        
        String chosenLogin = null;
    
        while (true) {
            System.out.println("Впишите уникальный логин пользователя");
        
            chosenLogin = scanner.nextLine();
        
            TypedQuery<City> chosenCityQuery = manager.createQuery(
                "select u from User u where u.login = ?1", City.class
            ); // Stop point. Continue from here next time.
        
            chosenCity = chosenCityQuery.getSingleResult();
        
            if (chosenCity != null) {
                break;
            } else {
                System.out.println("Некорректно введен id города, либо город с таким id не найден!");
            }
        }
    }
}
