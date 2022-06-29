package client;

import java.util.Scanner;

public class ClientAdministrator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientMenu menu = new ClientMenu(scanner);
        int select;

        do {
            menu.printMenu();

            while(!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Enter an integer from 1 to 4");
                menu.printMenu();
                scanner.next();
            }

            select = scanner.nextInt();
            menu.selectOption(select);
        }while (select != 4);

        scanner.close();
        System.out.println("Administrator client stopped");
    }

}
