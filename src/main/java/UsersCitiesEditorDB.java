import entities.City;
import entities.User;
import jakarta.persistence.*;

import java.util.*;

public class UsersCitiesEditorDB {
    public static void createUser(Scanner scanner, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        
        // Запрос для получения списка городов
        TypedQuery<City> cityTypedQuery = manager.createQuery(
            "select c from City c", City.class
        );
        
        List<City> cities = cityTypedQuery.getResultList();
        
        City chosenCity;
        while (true) {
            System.out.println("Выберите город (введите id города из списка ниже):");
            for (int i = 1; i <= cities.size(); i++) {
                System.out.println(cities.get(i).getName() + " - " + i);
            }
    
            String chosenCityId = scanner.nextLine();
            
            try {
                int numberInList = Integer.parseInt(chosenCityId) - 1;
                if (numberInList < cities.size() && numberInList > 0) {
                    chosenCity = cities.get(numberInList);
                    break;
                } else {
                    System.out.println("Город с таким id не найден");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректно введен id города!");
            }
        }
        
        String chosenLogin;
        
        while (true) {
            System.out.println("Впишите уникальный логин пользователя:");
            
            chosenLogin = scanner.nextLine();
            
            TypedQuery<Long> userCountQuery = manager.createQuery(
                "select count(u.id) from User u where u.login = ?1", Long.class
            );
    
            userCountQuery.setParameter(1, chosenLogin);
            long userWithLoginCount = userCountQuery.getSingleResult();
            
            if (userWithLoginCount != 0) {
                System.out.println("Пользователь с таким логином уже существует, выберите другой!");
            } else {
                break;
            }
        }
        
        System.out.println("Впишите имя пользователя");
        
        String chosenName = scanner.nextLine();
        
        User userToAdd = new User();
        userToAdd.setCity(chosenCity);
        userToAdd.setLogin(chosenLogin);
        userToAdd.setName(chosenName);
        
        try {
            manager.getTransaction().begin();
            
            manager.persist(userToAdd);
            
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
        
        TypedQuery<User> checkCreatedUser = manager.createQuery(
            "select u from User u where u.login = ?1", User.class
        );
        checkCreatedUser.setParameter(1, chosenLogin);
        User userForCheck = checkCreatedUser.getSingleResult();
        
        System.out.println("Пользователь с логином " + userForCheck.getLogin() +
            " создан с id " + userForCheck.getId());
        manager.close();
    }
    
    public static void findAndEditUser(Scanner scanner, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        
        User userToEdit;
        
        while (true) {
            System.out.println("Впишите уникальный логин пользователя для поиска в БД:");
    
            String loginToFind = scanner.nextLine();
            
            TypedQuery<User> findByLoginQuery = manager.createQuery(
                "select u from User u where u.login = ?1", User.class
            );
            
            findByLoginQuery.setParameter(1, loginToFind);
            try {
                userToEdit = findByLoginQuery.getSingleResult();
                break;
            } catch (NoResultException e) {
                System.out.println("Пользователь с таким логином не найден, впишите другой!");
            }
        }
        
        TypedQuery<City> cityTypedQuery = manager.createQuery(
            "select c from City c", City.class
        );
        
        List<City> cities = cityTypedQuery.getResultList();
        
        while (true) {
            System.out.println("Измените город с " + userToEdit.getCity().getName() +
                " на любой из списка ниже (для выбора впишите id города," +
                " для отказа от изменения ничего не вписывайте и нажмите Enter):");
            for (int i = 1; i <= cities.size(); i++) {
                System.out.println(cities.get(i).getName() + " - " + i);
            }
    
            String chosenCityId = scanner.nextLine();
            
            if (chosenCityId.isEmpty()) {
                break;
            } else {
                try {
                    int numberInList = Integer.parseInt(chosenCityId) - 1;
                    if (numberInList < cities.size() && numberInList > 0) {
                        City chosenCity = cities.get(Integer.parseInt(chosenCityId) - 1);
                        userToEdit.setCity(chosenCity);
                        break;
                    } else {
                        System.out.println("Город с таким id не найден!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректно введен id города!");
                }
            }
        }
        
        System.out.println("Впишите новое имя пользователя. Старое имя - " + userToEdit.getName() +
            " (для отказа от изменения ничего не вписывайте и нажмите Enter):");
        
        String chosenName = scanner.nextLine();
        
        if (!chosenName.isEmpty()) {
            userToEdit.setName(chosenName);
        }
        
        try {
            manager.getTransaction().begin();
            
            manager.persist(userToEdit);
            
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
        manager.close();
    }
}
