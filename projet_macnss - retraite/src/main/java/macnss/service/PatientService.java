package macnss.service;
import macnss.dao.CompanyDAOImpl;
import macnss.dao.UserDAOImpl;
import macnss.model.Patient;
import macnss.model.User;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import macnss.util.tools;


public class PatientService {
    private final Connection connection;
    private final UserDAOImpl UserDAOImpl;
    private final CompanyDAOImpl CompanyDAOImpl;


    public PatientService(Connection connection) {
        this.connection = connection;
        this.UserDAOImpl = new UserDAOImpl(connection);
        this.CompanyDAOImpl = new CompanyDAOImpl(connection);
    }

    public void showMenu(User authenticatedUser, FileService FileService) {
        Patient patient = (Patient) authenticatedUser;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nClient Menu:");
            System.out.println("1. check your files state");
            System.out.println("2. check your Retirement status");
            System.out.println("3. Logout");

            System.out.print("Enter your choice: ");
            int choice = tools.tryParse(scanner.nextLine());
            while (choice < 1 || choice > 3) {
                System.out.print("Invalid, Enter your choice again: ");
                choice = tools.tryParse(scanner.nextLine());
            }
            switch (choice) {
                case 1 -> FileService.checkClientFiles(patient);
                case 2 -> checkRetirementStatus(patient);
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void checkRetirementStatus(Patient patient) {
        if (checkEmployeeRetirementStatus(patient) ) {
            System.out.println(" Retirement salary is : " + patient.getSalary());
        } else {
            System.out.println("vous navez pas encore retraiter");
        }
    }

    public boolean checkEmployeeRetirementStatus(Patient employee) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = LocalDate.parse(employee.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long yearsBetween = ChronoUnit.YEARS.between(birthDate, currentDate);
        if (yearsBetween >= 55) {
            int totalDaysWorked = employee.getDays();

            if (totalDaysWorked >= 1320) {
                double averageSalary = CompanyDAOImpl.calculateAverageSalary(employee.getId());
                int additionalDays = Math.max(totalDaysWorked - 3240, 0);
                double additionalPensionRate = (double) additionalDays / 216;

                double pensionRate = Math.min(50 + additionalPensionRate, 70);

                double retirementSalary = (pensionRate / 100) * averageSalary;
                if (retirementSalary >= 6000) {
                    retirementSalary = 6000;
                } else if (retirementSalary <= 1000) {
                    retirementSalary = 1000;
                }
                CompanyDAOImpl.updateRetirementStatusAndSalary(employee.getId(), retirementSalary);
                return true;

            }

        }
        return false;
    }
}

