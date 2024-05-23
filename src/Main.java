
import java.util.Random;
import java.util.Scanner;

class Utils {

    static Random random = new Random();

    static void sleedRandomMS(int upper_bound) {
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Restaurant {

    int max_ord;
    int min_ord;
    int nOrd = 0;

    Restaurant(int max_ord, int min_ord) {
        this.max_ord = max_ord;
        this.min_ord = min_ord;
    }

    synchronized void put_order(String name) {
        while (nOrd >= max_ord) {
            System.out.println(name + "->" + "Can't take orders right now. Pending Orders:" + nOrd);
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted");
            }
        }
        ++nOrd;
        System.out.println(name + "-> " + "placed a new order, Pending Orders: " + nOrd);
        notify();
    }

    synchronized void cook_order(String name) {
        System.out.println(name + "->" + "Waiting for min orders to start cooking. Pending Orders: " + nOrd);
        while (nOrd < min_ord) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted");
            }
        }
        --nOrd;
        System.out.println(name + "-> " + "Cooked for an order, Pending Orders: " + nOrd);
        notify();
    }
}

class Cook extends Thread {

    String name;
    Restaurant restaurant;

    Cook(String name, Restaurant restaurant) {
        super(name);
        this.name = name;
        this.restaurant = restaurant;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                restaurant.cook_order(name);
                Utils.sleedRandomMS(80);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }
    }
}

class DeliveryMan extends Thread {

    String name;
    Restaurant restaurant;

    DeliveryMan(String name, Restaurant restaurant) {
        super(name);
        this.name = name;
        this.restaurant = restaurant;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                restaurant.put_order(name);
                Utils.sleedRandomMS(100);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }

        }
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Max Placeable Orders : ");
        int max_placeable_ord = scanner.nextInt(); // 100

        System.out.println("Enter Min Placed Orders to Cook : ");
        int min_placed_ord = scanner.nextInt(); // 10

        Restaurant restaurant = new Restaurant(max_placeable_ord, min_placed_ord);
        new Cook("Cook.1", restaurant);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new DeliveryMan("Del. Man.1", restaurant);
        new DeliveryMan("Del. Man.2", restaurant);

    }
}
