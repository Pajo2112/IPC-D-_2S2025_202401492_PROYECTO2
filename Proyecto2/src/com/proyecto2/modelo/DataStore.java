package com.proyecto2.modelo;

import java.io.*;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private User[] users;
    private Product[] products;
    private int userCount;
    private int productCount;

    public static final int MAX_USERS = 500;
    public static final int MAX_PRODUCTS = 2000;

    public DataStore() {
        users = new User[MAX_USERS];
        products = new Product[MAX_PRODUCTS];
        userCount = 0;
        productCount = 0;
        ensureDefaultAdmin();
    }

    private void ensureDefaultAdmin(){
        if (findUserByCode("admin") == null) {
            addUser(new User("admin","Administrador","IPC1A","ADMIN"));
        }
    }

    // Users
    public boolean addUser(User u){
        if (userCount >= MAX_USERS) return false;
        if (findUserByCode(u.getCodigo()) != null) return false;
        users[userCount++] = u;
        return true;
    }

    public User findUserByCode(String code){
        for (int i=0;i<userCount;i++){
            if (users[i].getCodigo().equalsIgnoreCase(code)) return users[i];
        }
        return null;
    }

    public boolean deleteUser(String code){
        for (int i=0;i<userCount;i++){
            if (users[i].getCodigo().equalsIgnoreCase(code)){
                for (int j=i;j<userCount-1;j++) users[j]=users[j+1];
                users[--userCount]=null;
                return true;
            }
        }
        return false;
    }

    public User[] listUsers(){
        User[] out = new User[userCount];
        for (int i=0;i<userCount;i++) out[i]=users[i];
        return out;
    }

    // Products
    public boolean addProduct(Product p){
        if (productCount >= MAX_PRODUCTS) return false;
        if (findProductByCode(p.getCodigo()) != null) return false;
        products[productCount++] = p;
        return true;
    }

    public Product findProductByCode(String code){
        for (int i=0;i<productCount;i++){
            if (products[i].getCodigo().equalsIgnoreCase(code)) return products[i];
        }
        return null;
    }

    public boolean updateProduct(String code, String nombre, String categoria, String atributo, int stock){
        Product p = findProductByCode(code);
        if (p == null) return false;
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setAtributo(atributo);
        p.setStock(stock);
        return true;
    }

    public boolean deleteProduct(String code){
        for (int i=0;i<productCount;i++){
            if (products[i].getCodigo().equalsIgnoreCase(code)){
                for (int j=i;j<productCount-1;j++) products[j]=products[j+1];
                products[--productCount]=null;
                return true;
            }
        }
        return false;
    }

    public Product[] listProducts(){
        Product[] out = new Product[productCount];
        for (int i=0;i<productCount;i++) out[i]=products[i];
        return out;
    }

    // Serialización
    public boolean saveToFile(String filename){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static DataStore loadFromFile(String filename){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof DataStore) return (DataStore) obj;
            else return new DataStore();
        } catch(Exception e){
            return new DataStore();
        }
    }
}

