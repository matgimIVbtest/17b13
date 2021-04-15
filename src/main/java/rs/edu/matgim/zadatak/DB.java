package rs.edu.matgim.zadatak;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    String connectionString = "jdbc:sqlite:src/main/java/KompanijaZaPrevoz.db";
    
    Connection con;
    
    public DB() throws SQLException {
        this.con = DriverManager.getConnection(connectionString);
        this.con.setAutoCommit(false);
    }

    public void printFirma() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Firma");
            while (rs.next()) {
                int IdFil = rs.getInt("IdFir");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");
                String Tel1 = rs.getString("Tel1");
                String Tel2 = rs.getString("Tel2");

                System.out.println(String.format("%d\t%s\t%s\t%s\t%s", IdFil, Naziv, Adresa, Tel1, Tel2));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    
    public void printNajzasutpljenijaRelacija() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT MestoOd, MestoDo, COUNT(MestoOd) AS br FROM Putovanje Group by MestoDo ORDER BY br DESC LIMIT 1");
            while (rs.next()) {
                String mestoOd = rs.getString("MestoOd");
                String mestoDo = rs.getString("MestoDo");
                int br = rs.getInt("br");

                System.out.println(String.format("%s\t%s", mestoOd, mestoDo, br));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    
    public void update(int idPut) throws SQLException{
        Statement stmt = con.createStatement();
        
        String query = "UPDATE Putovanje SET Status = 'P' WHERE IDPut=" + idPut;
        stmt.execute(query);
        
        
        
        String query3 = "DELETE FROM SePrevozi WHERE IDPut = " + idPut;
        stmt.execute(query3);
        
        stmt.close();
    }
    
    public int mehanicar(int idKam, int idPut) throws SQLException{
        int br = 0;
        Statement stmt = con.createStatement();
        String query = "SELECT IDZap FROM Zaposlen WHERE IDZap NOT IN (SELECT IDZap FROM Popravlja)";
        ResultSet rs = stmt.executeQuery(query);
        
        PreparedStatement ps;
        ps = con.prepareStatement("INSERT INTO Popravlja VALUES (0, ?, " + idKam + ")");
        //stmt.setInt(1, id);
        
        while(rs.next()){
            int idZap = rs.getInt("IDZap");
            ps.setInt(1, idZap);
            ps.execute();
            br++;
        }
        
        String query1 = "SELECT BrPopravljanja FROM Kamion WHERE IDKam=" + idKam;
        ResultSet rs2 = stmt.executeQuery(query1);
        rs2.next();
        int brp = rs2.getInt("BrPopravljanja");
        rs2.close();
        String query2 = "UPDATE Kamion SET BrPopravljanja = " + brp+1 + " WHERE IDKam=" + idKam;
        stmt.execute(query2);
        
        return br;
    }
    
    
    public int zadatak(int idPut) throws SQLException{
        
        try {
            
            update(idPut);
            
            Statement stmt = con.createStatement();
            String query = "SELECT IDKam FROM Putovanje WHERE IDPut=" + idPut;
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            int idKam = rs.getInt("IDKam");
            rs.close();
            stmt.close();
            
            int br = mehanicar(idKam, idPut);
            System.out.println("Uspesna realizacija");
            con.commit();
            return br;
            
        } catch (SQLException ex) {
            System.out.println("Dogodila se greska.");
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            con.rollback();
            return 0;
        }
        
    }
    
    
    public void close() throws SQLException{
        con.close();
    }
    

}


//SELECT       `column`,
//             COUNT(`column`) AS `value_occurrence` 
//    FROM     `my_table`
//    GROUP BY `column`
//    ORDER BY `value_occurrence` DESC
//    LIMIT    1;