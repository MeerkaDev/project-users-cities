import entities.City;
import entities.User;
import jakarta.persistence.*;

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
        StringBuilder chosenCityId = null;
        while (true) {
            System.out.println("Выберите город (введите id города из списка ниже):");
            
            for (City city : cities) {
                System.out.println(city.getName() + " - " + city.getId());
            }
            
            chosenCityId = new StringBuilder(scanner.nextLine());
            
            TypedQuery<City> chosenCityQuery = manager.createQuery(
                "select c from City c where c.id = ?1", City.class
            ); // Переделать через параметры
            
            chosenCityQuery.setParameter(1, Integer.parseInt(chosenCityId.toString()));
            chosenCity = chosenCityQuery.getSingleResult();
            
            if (chosenCity != null) {
                break;
            } else {
                System.out.println("Некорректно введен id города, либо город с таким id не найден!");
                chosenCityId = null;
            }
        }
        
        StringBuilder chosenLogin = null;
        
        while (true) {
            System.out.println("Впишите уникальный логин пользователя:");
            
            chosenLogin = new StringBuilder(scanner.nextLine());
            
            TypedQuery<User> chosenLoginQuery = manager.createQuery(
                "select u from User u where u.login = ?1", User.class
            );
            
            chosenLoginQuery.setParameter(1, chosenLogin.toString());
            User existingUserWithLogin = chosenLoginQuery.getSingleResult();
            
            if (existingUserWithLogin == null) {
                break;
            } else {
                System.out.println("Пользователь с таким логином уже существует, выберите другой!");
                chosenLogin = null;
            }
        }
        
        System.out.println("Впишите имя пользователя");
        
        String chosenName = scanner.nextLine();
        
        User userToAdd = new User();
        userToAdd.setCity(chosenCity);
        userToAdd.setLogin(chosenLogin.toString());
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
        
        User userForCheck = checkCreatedUser.getSingleResult();
        
        System.out.println("Пользователь с логином " + userForCheck.getLogin() +
            " создан с id " + userForCheck.getId());
        manager.close();
    }
    
    public static void findAndEditUser(Scanner scanner, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        
        User userToEdit = null;
        StringBuilder loginToFind = null;
        
        while (true) {
            System.out.println("Впишите уникальный логин пользователя для поиска в БД:");
            
            loginToFind = new StringBuilder(scanner.nextLine());
            
            TypedQuery<User> findByLoginQuery = manager.createQuery(
                "select u from User u where u.login = ?1", User.class
            );
            
            findByLoginQuery.setParameter(1, loginToFind.toString());
            try {
                User foundUser = findByLoginQuery.getSingleResult();
                userToEdit = foundUser;
                break;
            } catch (NoResultException e) {
                System.out.println("Пользователь с таким логином не найден, впишите другой!");
                loginToFind = null;
            }
        }
        
        TypedQuery<City> cityTypedQuery = manager.createQuery(
            "select c from City c", City.class
        );
        
        List<City> cities = cityTypedQuery.getResultList();
        
        StringBuilder chosenCityId = null;
        City chosenCity = null;
        
        while (true) {
            System.out.println("Измените город с" + userToEdit.getCity().getName() +
                " на любой из списка ниже (для выбора впишите id города," +
                " для отказа от изменения ничего не вписывайте и нажмите Enter):");
            
            for (City city : cities) {
                System.out.println(city.getName() + " - " + city.getId());
            }
            
            chosenCityId = new StringBuilder(scanner.nextLine());
            
            if (chosenCityId.equals("")) {
                break;
            } else {
                TypedQuery<City> chosenCityQuery = manager.createQuery(
                    "select c from City c where c.id = ?1", City.class
                ); // Переделать через параметры
                
                chosenCityQuery.setParameter(1, Integer.parseInt(chosenCityId.toString()));
                chosenCity = chosenCityQuery.getSingleResult();
                
                if (chosenCity != null) {
                    userToEdit.setCity(chosenCity);
                    break;
                } else {
                    System.out.println("Некорректно введен id города, либо город с таким id не найден!");
                    chosenCityId = null;
                }
            }
        }
        
        System.out.println("Впишите новое имя пользователя. Старое имя - " + userToEdit.getName() +
            " (для отказа от изменения ничего не вписывайте и нажмите Enter):");
        
        String chosenName = scanner.nextLine();
        
        if (!chosenName.equals("")) {
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
