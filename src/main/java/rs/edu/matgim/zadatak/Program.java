package rs.edu.matgim.zadatak;

import java.sql.SQLException;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws SQLException {

        DB _db = new DB();
        //_db.printFirma();
        _db.printNajzasutpljenijaRelacija();
        
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        
        _db.zadatak(id);
        _db.close();
        
    }
}
